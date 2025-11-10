package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.repository.RessortRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RessortServiceTest {

    @Mock
    private RessortRepository ressortRepository;

    @InjectMocks
    private RessortService ressortService;

    private Ressort ressort;

    @BeforeEach
    void setUp() {
        ressort = new Ressort();
        ressort.setId(1L);
        ressort.setName("Küche");
        ressort.setBeschreibung("Verantwortlich für Catering");
    }

    @Test
    void testFindAll() {
        // Arrange
        when(ressortRepository.findAll()).thenReturn(List.of(ressort));

        // Act
        List<Ressort> result = ressortService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Küche", result.get(0).getName());
    }

    @Test
    void testFindById() {
        // Arrange
        when(ressortRepository.findById(1L)).thenReturn(Optional.of(ressort));

        // Act
        Optional<Ressort> result = ressortService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Küche", result.get().getName());
    }

    @Test
    void testSave() {
        // Arrange
        when(ressortRepository.save(any(Ressort.class))).thenReturn(ressort);

        // Act
        Ressort result = ressortService.save(ressort);

        // Assert
        assertNotNull(result);
        assertEquals("Küche", result.getName());
        verify(ressortRepository).save(ressort);
    }

    @Test
    void testDelete() {
        // Act
        ressortService.delete(1L);

        // Assert
        verify(ressortRepository).deleteById(1L);
    }

    @Test
    void testExistsByName() {
        // Arrange
        when(ressortRepository.existsByName("Küche")).thenReturn(true);

        // Act
        boolean exists = ressortService.existsByName("Küche");

        // Assert
        assertTrue(exists);
    }
}
