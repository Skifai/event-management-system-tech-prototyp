package ch.flossrennen.eventmanagementsystem.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseStartupListener.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseStartupListenerTest {

    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private SpringApplication application;

    private DatabaseStartupListener listener;

    @BeforeEach
    void setUp() {
        listener = new DatabaseStartupListener();
    }

    @Test
    void testListenerInstantiation() {
        assertNotNull(listener, "DatabaseStartupListener should be instantiated");
    }

    @Test
    void testPostProcessEnvironment_InProductionMode_ShouldSkip() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> listener.postProcessEnvironment(environment, application),
                "Should handle production mode gracefully");
    }

    @Test
    void testPostProcessEnvironment_InDevelopmentMode_ShouldNotThrow() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // Act & Assert - should not throw exception even if Docker is not available
        assertDoesNotThrow(() -> listener.postProcessEnvironment(environment, application),
                "Should handle development mode gracefully even without Docker");
    }

    @Test
    void testPostProcessEnvironment_WithNoProfile_ShouldNotThrow() {
        // Arrange - no active profiles means development mode
        when(environment.getActiveProfiles()).thenReturn(new String[]{});

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> listener.postProcessEnvironment(environment, application),
                "Should handle default profile gracefully");
    }

    @Test
    void testPostProcessEnvironment_HandlesExceptionGracefully() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // Act & Assert - should not throw exception even if something goes wrong
        assertDoesNotThrow(() -> listener.postProcessEnvironment(environment, application),
                "Listener should handle exceptions gracefully");
    }
}
