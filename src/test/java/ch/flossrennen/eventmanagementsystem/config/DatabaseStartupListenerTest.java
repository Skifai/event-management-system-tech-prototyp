package ch.flossrennen.eventmanagementsystem.config;

import ch.flossrennen.eventmanagementsystem.service.DockerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseStartupListener.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseStartupListenerTest {

    @Mock
    private DockerService dockerService;

    @Mock
    private Environment environment;

    @Mock
    private ApplicationReadyEvent event;

    private DatabaseStartupListener listener;

    @BeforeEach
    void setUp() {
        listener = new DatabaseStartupListener(dockerService, environment);
    }

    @Test
    void testListenerInstantiation() {
        assertNotNull(listener, "DatabaseStartupListener should be instantiated");
    }

    @Test
    void testOnApplicationEvent_InProductionMode_ShouldSkipDockerCheck() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});

        // Act
        listener.onApplicationEvent(event);

        // Assert
        verify(dockerService, never()).isDockerAvailable();
        verify(dockerService, never()).ensurePostgresContainer();
    }

    @Test
    void testOnApplicationEvent_InDevelopmentMode_WithDockerAvailable() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        when(dockerService.isDockerAvailable()).thenReturn(true);
        when(dockerService.ensurePostgresContainer()).thenReturn(true);

        // Act
        listener.onApplicationEvent(event);

        // Assert
        verify(dockerService).isDockerAvailable();
        verify(dockerService).ensurePostgresContainer();
    }

    @Test
    void testOnApplicationEvent_InDevelopmentMode_WithDockerUnavailable() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        when(dockerService.isDockerAvailable()).thenReturn(false);

        // Act
        listener.onApplicationEvent(event);

        // Assert
        verify(dockerService).isDockerAvailable();
        verify(dockerService, never()).ensurePostgresContainer();
    }

    @Test
    void testOnApplicationEvent_WithNoProfile_ShouldCheckDocker() {
        // Arrange - no active profiles means development mode
        when(environment.getActiveProfiles()).thenReturn(new String[]{});
        when(dockerService.isDockerAvailable()).thenReturn(true);
        when(dockerService.ensurePostgresContainer()).thenReturn(true);

        // Act
        listener.onApplicationEvent(event);

        // Assert
        verify(dockerService).isDockerAvailable();
        verify(dockerService).ensurePostgresContainer();
    }

    @Test
    void testOnApplicationEvent_HandlesExceptionGracefully() {
        // Arrange
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        when(dockerService.isDockerAvailable()).thenThrow(new RuntimeException("Docker error"));

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> listener.onApplicationEvent(event),
                "Listener should handle exceptions gracefully");
    }
}
