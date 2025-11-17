package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests für CsvService (KFA.02)
 */
@ExtendWith(MockitoExtension.class)
class CsvServiceTest {

    @Mock
    private HelferService helferService;

    @Mock
    private RessortService ressortService;

    @InjectMocks
    private CsvService csvService;

    @Test
    void testExportHelferToCsv() {
        // Arrange
        Ressort ressort = new Ressort();
        ressort.setName("Küche");

        Helfer helfer1 = new Helfer();
        helfer1.setVorname("Max");
        helfer1.setNachname("Mustermann");
        helfer1.setEmail("max@example.com");
        helfer1.setTelefon("1234567890");
        helfer1.setRessort(ressort);

        Helfer helfer2 = new Helfer();
        helfer2.setVorname("Anna");
        helfer2.setNachname("Schmidt");

        when(helferService.findAll()).thenReturn(java.util.List.of(helfer1, helfer2));

        // Act
        String csv = csvService.exportHelferToCsv();

        // Assert
        assertNotNull(csv);
        assertTrue(csv.contains("Vorname;Nachname;Email;Telefon;Ressort"));
        assertTrue(csv.contains("Max;Mustermann;max@example.com;1234567890;Küche"));
        assertTrue(csv.contains("Anna;Schmidt"));
    }

    @Test
    void testImportHelferFromCsv_Success() throws IOException {
        // Arrange
        String csv = """
            Vorname;Nachname;Email;Telefon;Ressort
            Max;Mustermann;max@example.com;1234567890;Küche
            Anna;Schmidt;anna@example.com;;Bar
            """;

        Ressort kueche = new Ressort();
        kueche.setName("Küche");
        Ressort bar = new Ressort();
        bar.setName("Bar");

        when(ressortService.findByName("Küche")).thenReturn(Optional.of(kueche));
        when(ressortService.findByName("Bar")).thenReturn(Optional.of(bar));
        when(helferService.save(any(Helfer.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        int count = csvService.importHelferFromCsv(csv);

        // Assert
        assertEquals(2, count);
        verify(helferService, times(2)).save(any(Helfer.class));
    }

    @Test
    void testImportHelferFromCsv_InvalidHeader() {
        // Arrange
        String csv = "Invalid;Header\nMax;Mustermann";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            csvService.importHelferFromCsv(csv);
        });
    }

    @Test
    void testImportHelferFromCsv_EmptyLines() throws IOException {
        // Arrange
        String csv = """
            Vorname;Nachname;Email;Telefon;Ressort
            Max;Mustermann;max@example.com;;
            
            Anna;Schmidt;anna@example.com;;
            """;

        when(helferService.save(any(Helfer.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        int count = csvService.importHelferFromCsv(csv);

        // Assert
        assertEquals(2, count);
    }
}
