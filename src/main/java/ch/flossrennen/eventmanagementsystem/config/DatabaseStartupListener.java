package ch.flossrennen.eventmanagementsystem.config;

import ch.flossrennen.eventmanagementsystem.service.DockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

/**
 * Environment post processor that verifies PostgreSQL Docker container is running
 * BEFORE Spring Boot application context starts.
 * This runs very early in the Spring Boot lifecycle, before the database connection is attempted.
 * 
 * NOTE: This listener no longer automatically creates or starts containers.
 * Containers must be started manually using:
 *   - IDEA Run Configuration: "Start Databases"
 *   - Command line: docker compose -f docker-compose.db.yml up -d
 * 
 * This class only checks if the required container is running and provides helpful
 * error messages if it is not.
 * 
 * Note: This class is loaded via spring.factories and instantiated before the Spring context,
 * so it cannot use @Component or @Slf4j annotations.
 */
public class DatabaseStartupListener implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DatabaseStartupListener.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = Arrays.asList(activeProfiles).contains("prod");
        
        String expectedContainer = isProduction ? 
            "event-management-db-prod-container" : 
            "event-management-db-container";
        String expectedPort = isProduction ? "5433" : "5432";
        String profileName = isProduction ? "production" : "development";
        
        log.info("Running in {} mode - checking for PostgreSQL container: {}", profileName, expectedContainer);
        
        try {
            // Create DockerService instance directly since Spring context is not yet available
            DockerService dockerService = new DockerService();
            boolean dockerAvailable = dockerService.isDockerAvailable();
            
            if (!dockerAvailable) {
                log.warn("⚠️  Docker is not available. Please ensure:");
                log.warn("   1. Docker Desktop is running");
                log.warn("   2. OR PostgreSQL is running externally on localhost:{}", expectedPort);
                return;
            }

            boolean isRunning = dockerService.isPostgresContainerRunning(expectedContainer);
            
            if (isRunning) {
                log.info("✅ PostgreSQL container '{}' is running and ready on port {}", expectedContainer, expectedPort);
            } else {
                log.error("❌ PostgreSQL container '{}' is not running!", expectedContainer);
                log.error("   Please start the database containers first:");
                log.error("   - Using IDEA: Run 'Start Databases' configuration");
                log.error("   - Using command line: docker compose -f docker-compose.db.yml up -d");
                log.error("   Application startup will likely fail without the database.");
            }
        } catch (Exception e) {
            log.error("Error during PostgreSQL availability check: {}", e.getMessage());
            log.warn("Application will continue, but database connection may fail.");
        }
    }
}
