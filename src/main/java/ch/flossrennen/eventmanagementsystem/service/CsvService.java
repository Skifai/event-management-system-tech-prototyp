package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service für Import/Export von Helferdaten im CSV-Format
 * Implementiert KFA.02 - Import / Export
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CsvService {
    
    private final HelferService helferService;
    private final RessortService ressortService;
    
    private static final String CSV_SEPARATOR = ";";
    private static final String CSV_HEADER = "Vorname;Nachname;Email;Telefon;Ressort";
    
    /**
     * Exportiert alle Helfer als CSV-String
     */
    public String exportHelferToCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append(CSV_HEADER).append("\n");
        
        List<Helfer> helfer = helferService.findAll();
        for (Helfer h : helfer) {
            csv.append(h.getVorname()).append(CSV_SEPARATOR)
               .append(h.getNachname()).append(CSV_SEPARATOR)
               .append(h.getEmail() != null ? h.getEmail() : "").append(CSV_SEPARATOR)
               .append(h.getTelefon() != null ? h.getTelefon() : "").append(CSV_SEPARATOR)
               .append(h.getRessort() != null ? h.getRessort().getName() : "")
               .append("\n");
        }
        
        return csv.toString();
    }
    
    /**
     * Importiert Helfer aus CSV-String
     * @param csvContent CSV-Inhalt
     * @return Anzahl importierter Helfer
     */
    @Transactional
    public int importHelferFromCsv(String csvContent) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(csvContent));
        
        String line = reader.readLine(); // Skip header
        if (line == null || !line.startsWith("Vorname")) {
            throw new IllegalArgumentException("Ungültiges CSV-Format. Erwarteter Header: " + CSV_HEADER);
        }
        
        int count = 0;
        List<String> errors = new ArrayList<>();
        
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            
            try {
                Helfer helfer = parseHelferFromCsvLine(line);
                helferService.save(helfer);
                count++;
            } catch (Exception e) {
                errors.add("Fehler in Zeile " + (count + 2) + ": " + e.getMessage());
                log.warn("Error importing line: {}", line, e);
            }
        }
        
        if (!errors.isEmpty() && count == 0) {
            throw new IOException("Import fehlgeschlagen:\n" + String.join("\n", errors));
        }
        
        return count;
    }
    
    private Helfer parseHelferFromCsvLine(String line) {
        String[] parts = line.split(CSV_SEPARATOR, -1);
        
        if (parts.length < 2) {
            throw new IllegalArgumentException("Ungültige Zeile: mindestens Vorname und Nachname erforderlich");
        }
        
        Helfer helfer = new Helfer();
        helfer.setVorname(parts[0].trim());
        helfer.setNachname(parts[1].trim());
        
        if (parts.length > 2 && !parts[2].trim().isEmpty()) {
            helfer.setEmail(parts[2].trim());
        }
        
        if (parts.length > 3 && !parts[3].trim().isEmpty()) {
            helfer.setTelefon(parts[3].trim());
        }
        
        if (parts.length > 4 && !parts[4].trim().isEmpty()) {
            String ressortName = parts[4].trim();
            ressortService.findByName(ressortName).ifPresent(helfer::setRessort);
        }
        
        return helfer;
    }
}
