package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.repository.SchichtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchichtServiceTest {

    @Mock
    private SchichtRepository schichtRepository;

    @InjectMocks
    private SchichtService schichtService;

    private Schicht schicht;

    @BeforeEach
    void setUp() {
        schicht = new Schicht();
        schicht.setId(1L);
        schicht.setName("Frühschicht");
        schicht.setStartzeit(LocalDateTime.of(2024, 6, 1, 6, 0));
        schicht.setEndzeit(LocalDateTime.of(2024, 6, 1, 14, 0));
    }

    @Test
    void testSave_ValidTimeRange() {
        // Arrange
        when(schichtRepository.save(any(Schicht.class))).thenReturn(schicht);

        // Act
        Schicht result = schichtService.save(schicht);

        // Assert
        assertNotNull(result);
        verify(schichtRepository).save(schicht);
    }

    @Test
    void testSave_InvalidTimeRange_ThrowsException() {
        // Arrange
        schicht.setStartzeit(LocalDateTime.of(2024, 6, 1, 14, 0));
        schicht.setEndzeit(LocalDateTime.of(2024, 6, 1, 6, 0));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            schichtService.save(schicht);
        });

        assertTrue(exception.getMessage().contains("Startzeit muss vor Endzeit liegen"));
        verify(schichtRepository, never()).save(any());
    }
}
