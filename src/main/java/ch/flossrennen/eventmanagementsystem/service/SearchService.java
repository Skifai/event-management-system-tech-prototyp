package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.repository.EinsatzRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final EinsatzRepository einsatzRepository;
    
    /**
     * Sucht Einsätze nach verschiedenen Kriterien
     * Implementiert MFA.09 - Suchfunktion
     */
    public List<Einsatz> searchEinsaetze(
            Ressort ressort,
            LocalDateTime startDatum,
            LocalDateTime endDatum,
            Helfer helfer,
            Einsatz.EinsatzStatus status) {
        
        List<Einsatz> results = einsatzRepository.findAll();
        
        if (ressort != null) {
            results = results.stream()
                .filter(e -> e.getRessort().equals(ressort))
                .collect(Collectors.toList());
        }
        
        if (startDatum != null && endDatum != null) {
            results = results.stream()
                .filter(e -> e.getStartzeit() != null && 
                            e.getStartzeit().isAfter(startDatum) && 
                            e.getStartzeit().isBefore(endDatum))
                .collect(Collectors.toList());
        }
        
        if (helfer != null) {
            results = results.stream()
                .filter(e -> e.getZugewieseneHelfer().contains(helfer))
                .collect(Collectors.toList());
        }
        
        if (status != null) {
            results = results.stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
        }
        
        return results;
    }
}
