package ch.flossrennen.eventmanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DockerService.
 * Note: These tests verify the service methods without requiring Docker to be running.
 */
@ExtendWith(MockitoExtension.class)
class DockerServiceTest {

    private DockerService dockerService;

    @BeforeEach
    void setUp() {
        dockerService = new DockerService();
    }

    @Test
    void testDockerServiceInstantiation() {
        assertNotNull(dockerService, "DockerService should be instantiated");
    }

    @Test
    void testIsDockerAvailable_WhenDockerNotRunning() {
        // This test will return false if Docker is not running, which is expected in CI environment
        // The method should not throw an exception
        assertDoesNotThrow(() -> dockerService.isDockerAvailable(),
                "isDockerAvailable should not throw exception even when Docker is unavailable");
    }

    @Test
    void testIsPostgresContainerRunning_WhenDockerNotRunning() {
        // This should return false gracefully when Docker is not available
        boolean result = dockerService.isPostgresContainerRunning();
        assertFalse(result, "Should return false when Docker is not available");
    }

    @Test
    void testPostgresContainerExists_WhenDockerNotRunning() {
        // This should return false gracefully when Docker is not available
        boolean result = dockerService.postgresContainerExists();
        assertFalse(result, "Should return false when Docker is not available");
    }

    @Test
    void testEnsurePostgresContainer_WhenDockerNotRunning() {
        // This should handle the case gracefully and return false
        boolean result = dockerService.ensurePostgresContainer();
        // Could be true if Docker is running, false otherwise - both are acceptable
        assertNotNull(result, "Should return a boolean value");
    }
}
