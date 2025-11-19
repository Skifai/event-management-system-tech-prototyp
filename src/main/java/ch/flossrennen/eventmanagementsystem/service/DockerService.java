package ch.flossrennen.eventmanagementsystem.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Service for managing Docker containers, specifically PostgreSQL database instances.
 * Can be used both as a Spring bean and instantiated directly for early initialization.
 */
@Service
public class DockerService {

    private static final Logger log = LoggerFactory.getLogger(DockerService.class);

    private static final String POSTGRES_CONTAINER_NAME = "event-management-db-container";
    private static final String POSTGRES_IMAGE = "postgres:17-alpine";
    private static final String POSTGRES_DB = "eventmanagement";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "postgres";
    private static final int POSTGRES_PORT = 5432;

    private DockerClient dockerClient;

    /**
     * Initialize Docker client.
     */
    private void initDockerClient() {
        if (dockerClient == null) {
            try {
                DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .build();

                DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                        .dockerHost(config.getDockerHost())
                        .sslConfig(config.getSSLConfig())
                        .maxConnections(100)
                        .connectionTimeout(Duration.ofSeconds(30))
                        .responseTimeout(Duration.ofSeconds(45))
                        .build();

                dockerClient = DockerClientImpl.getInstance(config, httpClient);
                log.info("Docker client initialized successfully");
            } catch (Exception e) {
                log.warn("Failed to initialize Docker client: {}", e.getMessage());
                dockerClient = null;
            }
        }
    }

    /**
     * Check if Docker is available.
     */
    public boolean isDockerAvailable() {
        try {
            initDockerClient();
            if (dockerClient != null) {
                dockerClient.pingCmd().exec();
                return true;
            }
        } catch (Exception e) {
            log.warn("Docker is not available: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Check if PostgreSQL container exists and is running.
     */
    public boolean isPostgresContainerRunning() {
        if (!isDockerAvailable()) {
            return false;
        }

        try {
            List<Container> containers = dockerClient.listContainersCmd()
                    .withNameFilter(Arrays.asList(POSTGRES_CONTAINER_NAME))
                    .withStatusFilter(Arrays.asList("running"))
                    .exec();

            return !containers.isEmpty();
        } catch (Exception e) {
            log.error("Error checking PostgreSQL container status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if PostgreSQL container exists (running or stopped).
     */
    public boolean postgresContainerExists() {
        if (!isDockerAvailable()) {
            return false;
        }

        try {
            List<Container> containers = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withNameFilter(Arrays.asList(POSTGRES_CONTAINER_NAME))
                    .exec();

            return !containers.isEmpty();
        } catch (Exception e) {
            log.error("Error checking if PostgreSQL container exists: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Start existing PostgreSQL container.
     */
    public boolean startPostgresContainer() {
        if (!isDockerAvailable()) {
            return false;
        }

        try {
            log.info("Starting existing PostgreSQL container: {}", POSTGRES_CONTAINER_NAME);
            dockerClient.startContainerCmd(POSTGRES_CONTAINER_NAME).exec();
            
            // Wait a bit for container to be ready
            Thread.sleep(3000);
            
            log.info("PostgreSQL container started successfully");
            return true;
        } catch (Exception e) {
            log.error("Error starting PostgreSQL container: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create and start a new PostgreSQL container.
     */
    public boolean createAndStartPostgresContainer() {
        if (!isDockerAvailable()) {
            log.warn("Docker is not available. Cannot create PostgreSQL container.");
            return false;
        }

        try {
            log.info("Pulling PostgreSQL image: {}", POSTGRES_IMAGE);
            dockerClient.pullImageCmd(POSTGRES_IMAGE)
                    .start()
                    .awaitCompletion();

            log.info("Creating PostgreSQL container: {}", POSTGRES_CONTAINER_NAME);

            PortBinding portBinding = new PortBinding(
                    Ports.Binding.bindPort(POSTGRES_PORT),
                    ExposedPort.tcp(POSTGRES_PORT)
            );

            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withPortBindings(portBinding)
                    .withRestartPolicy(RestartPolicy.unlessStoppedRestart());

            CreateContainerResponse container = dockerClient.createContainerCmd(POSTGRES_IMAGE)
                    .withName(POSTGRES_CONTAINER_NAME)
                    .withEnv(
                            "POSTGRES_USER=" + POSTGRES_USER,
                            "POSTGRES_PASSWORD=" + POSTGRES_PASSWORD,
                            "POSTGRES_DB=" + POSTGRES_DB
                    )
                    .withExposedPorts(ExposedPort.tcp(POSTGRES_PORT))
                    .withHostConfig(hostConfig)
                    .exec();

            log.info("Starting PostgreSQL container");
            dockerClient.startContainerCmd(container.getId()).exec();

            // Wait for container to be ready
            Thread.sleep(5000);

            log.info("PostgreSQL container created and started successfully");
            return true;
        } catch (Exception e) {
            log.error("Error creating PostgreSQL container: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Ensure PostgreSQL container is running. This method will:
     * 1. Check if container exists and is running - do nothing
     * 2. Check if container exists but stopped - start it
     * 3. If container doesn't exist - create and start it
     */
    public boolean ensurePostgresContainer() {
        if (!isDockerAvailable()) {
            log.warn("Docker is not available. Assuming PostgreSQL is running externally.");
            return false;
        }

        if (isPostgresContainerRunning()) {
            log.info("PostgreSQL container is already running");
            return true;
        }

        if (postgresContainerExists()) {
            log.info("PostgreSQL container exists but is not running. Starting it...");
            return startPostgresContainer();
        }

        log.info("PostgreSQL container does not exist. Creating and starting it...");
        return createAndStartPostgresContainer();
    }
}
