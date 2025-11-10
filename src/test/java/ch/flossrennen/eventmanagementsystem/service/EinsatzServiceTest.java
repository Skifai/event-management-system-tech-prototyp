package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.repository.EinsatzRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests für EinsatzService - insbesondere die Validierung gegen Doppelzuweisung (MFA.03)
 */
@ExtendWith(MockitoExtension.class)
class EinsatzServiceTest {

    @Mock
    private EinsatzRepository einsatzRepository;

    @InjectMocks
    private EinsatzService einsatzService;

    private Einsatz einsatz;
    private Helfer helfer;
    private Ressort ressort;

    @BeforeEach
    void setUp() {
        ressort = new Ressort();
        ressort.setId(1L);
        ressort.setName("Test Ressort");

        helfer = new Helfer();
        helfer.setId(1L);
        helfer.setVorname("Max");
        helfer.setNachname("Mustermann");

        einsatz = new Einsatz();
        einsatz.setId(1L);
        einsatz.setBeschreibung("Test Einsatz");
        einsatz.setStartzeit(LocalDateTime.of(2024, 6, 1, 10, 0));
        einsatz.setEndzeit(LocalDateTime.of(2024, 6, 1, 12, 0));
        einsatz.setRessort(ressort);
        einsatz.setBenoetigteHelfer(2);
        einsatz.setZugewieseneHelfer(new HashSet<>());
    }

    @Test
    void testAssignHelfer_Success() {
        // Arrange
        when(einsatzRepository.findById(1L)).thenReturn(Optional.of(einsatz));
        when(einsatzRepository.findOverlappingEinsaetzeForHelfer(any(), any(), any()))
            .thenReturn(List.of());
        when(einsatzRepository.save(any(Einsatz.class))).thenReturn(einsatz);

        // Act
        Einsatz result = einsatzService.assignHelfer(1L, helfer);

        // Assert
        assertNotNull(result);
        assertTrue(result.getZugewieseneHelfer().contains(helfer));
        assertEquals(Einsatz.EinsatzStatus.IN_PLANUNG, result.getStatus());
        verify(einsatzRepository).save(einsatz);
    }

    @Test
    void testAssignHelfer_WithConflict_ThrowsException() {
        // Arrange
        Einsatz conflictingEinsatz = new Einsatz();
        conflictingEinsatz.setBeschreibung("Konflikt Einsatz");
        conflictingEinsatz.setStartzeit(LocalDateTime.of(2024, 6, 1, 11, 0));
        conflictingEinsatz.setEndzeit(LocalDateTime.of(2024, 6, 1, 13, 0));

        when(einsatzRepository.findById(1L)).thenReturn(Optional.of(einsatz));
        when(einsatzRepository.findOverlappingEinsaetzeForHelfer(any(), any(), any()))
            .thenReturn(List.of(conflictingEinsatz));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            einsatzService.assignHelfer(1L, helfer);
        });

        assertTrue(exception.getMessage().contains("überschneidenden Einsatz"));
        verify(einsatzRepository, never()).save(any());
    }

    @Test
    void testAssignHelfer_StatusUpdateToVollstaendig() {
        // Arrange
        einsatz.setBenoetigteHelfer(1);
        when(einsatzRepository.findById(1L)).thenReturn(Optional.of(einsatz));
        when(einsatzRepository.findOverlappingEinsaetzeForHelfer(any(), any(), any()))
            .thenReturn(List.of());
        when(einsatzRepository.save(any(Einsatz.class))).thenReturn(einsatz);

        // Act
        Einsatz result = einsatzService.assignHelfer(1L, helfer);

        // Assert
        assertEquals(Einsatz.EinsatzStatus.VOLLSTAENDIG, result.getStatus());
    }

    @Test
    void testRemoveHelfer_Success() {
        // Arrange
        einsatz.getZugewieseneHelfer().add(helfer);
        einsatz.setStatus(Einsatz.EinsatzStatus.VOLLSTAENDIG);
        when(einsatzRepository.findById(1L)).thenReturn(Optional.of(einsatz));
        when(einsatzRepository.save(any(Einsatz.class))).thenReturn(einsatz);

        // Act
        Einsatz result = einsatzService.removeHelfer(1L, helfer);

        // Assert
        assertFalse(result.getZugewieseneHelfer().contains(helfer));
        assertEquals(Einsatz.EinsatzStatus.OFFEN, result.getStatus());
    }

    @Test
    void testIsHelferAvailable_NoConflicts() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 1, 12, 0);
        when(einsatzRepository.findOverlappingEinsaetzeForHelfer(helfer, start, end))
            .thenReturn(List.of());

        // Act
        boolean available = einsatzService.isHelferAvailable(helfer, start, end);

        // Assert
        assertTrue(available);
    }

    @Test
    void testIsHelferAvailable_WithConflicts() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 1, 12, 0);
        when(einsatzRepository.findOverlappingEinsaetzeForHelfer(helfer, start, end))
            .thenReturn(List.of(einsatz));

        // Act
        boolean available = einsatzService.isHelferAvailable(helfer, start, end);

        // Assert
        assertFalse(available);
    }

    @Test
    void testSave_InvalidTimeRange_ThrowsException() {
        // Arrange
        einsatz.setStartzeit(LocalDateTime.of(2024, 6, 1, 12, 0));
        einsatz.setEndzeit(LocalDateTime.of(2024, 6, 1, 10, 0));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            einsatzService.save(einsatz);
        });
    }
}
