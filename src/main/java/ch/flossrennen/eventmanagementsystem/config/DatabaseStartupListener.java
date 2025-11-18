package ch.flossrennen.eventmanagementsystem.config;

import ch.flossrennen.eventmanagementsystem.service.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Environment post processor that ensures PostgreSQL Docker container is running
 * BEFORE Spring Boot application context starts, when running in development mode.
 * This runs very early in the Spring Boot lifecycle, before the database connection is attempted.
 */
@Component
@Slf4j
public class DatabaseStartupListener implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Only auto-start PostgreSQL in development mode, not in production
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = Arrays.asList(activeProfiles).contains("prod");
        
        if (isProduction) {
            log.info("Running in production mode - skipping Docker PostgreSQL auto-start");
            return;
        }

        log.info("Checking PostgreSQL availability before application startup...");
        
        try {
            // Create DockerService instance directly since Spring context is not yet available
            DockerService dockerService = new DockerService();
            boolean dockerAvailable = dockerService.isDockerAvailable();
            
            if (!dockerAvailable) {
                log.warn("Docker is not available. Assuming PostgreSQL is running externally on localhost:5432");
                return;
            }

            boolean postgresEnsured = dockerService.ensurePostgresContainer();
            
            if (postgresEnsured) {
                log.info("PostgreSQL is ready for connections at localhost:5432");
            } else {
                log.warn("Could not ensure PostgreSQL container. Please start it manually.");
            }
        } catch (Exception e) {
            log.error("Error during PostgreSQL startup check: {}", e.getMessage());
            log.warn("Application will continue, but database connection may fail.");
        }
    }
}
