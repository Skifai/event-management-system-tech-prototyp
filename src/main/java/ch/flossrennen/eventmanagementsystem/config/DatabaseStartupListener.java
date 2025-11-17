package ch.flossrennen.eventmanagementsystem.config;

import ch.flossrennen.eventmanagementsystem.service.DockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Application listener that ensures PostgreSQL Docker container is running on startup
 * when running in development mode (not in production).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final DockerService dockerService;
    private final Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Only auto-start PostgreSQL in development mode, not in production
        boolean isProduction = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        
        if (isProduction) {
            log.info("Running in production mode - skipping Docker PostgreSQL auto-start");
            return;
        }

        log.info("Checking PostgreSQL availability...");
        
        try {
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
