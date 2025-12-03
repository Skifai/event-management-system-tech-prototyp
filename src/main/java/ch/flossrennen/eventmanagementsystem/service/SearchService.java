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

/**
 * Service für die Suche und Filterung von Einsätzen.
 * Implementiert MFA.09 - Suchfunktion
 *
 * Dieser Service ermöglicht die kombinierte Suche nach mehreren Kriterien.
 * Die Filter werden nacheinander angewendet (AND-Verknüpfung).
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private final EinsatzRepository einsatzRepository;

    /**
     * Sucht Einsätze nach verschiedenen optionalen Kriterien.
     *
     * Die Methode lädt zunächst alle Einsätze und wendet dann die angegebenen Filter
     * schrittweise an. Alle Filter sind optional (null = Filter nicht aktiv).
     *
     * Filter-Logik (AND-Verknüpfung):
     * 1. Ressort-Filter: Nur Einsätze des angegebenen Ressorts
     * 2. Zeitraum-Filter: Nur Einsätze, die im Zeitraum starten
     * 3. Helfer-Filter: Nur Einsätze, denen der Helfer zugewiesen ist
     * 4. Status-Filter: Nur Einsätze mit dem angegebenen Status
     *
     * @param ressort Filter nach Ressort (optional, null = keine Filterung)
     * @param startDatum Start des Zeitraums (optional, benötigt endDatum)
     * @param endDatum Ende des Zeitraums (optional, benötigt startDatum)
     * @param helfer Filter nach zugewiesenem Helfer (optional, null = keine Filterung)
     * @param status Filter nach Einsatzstatus (optional, null = keine Filterung)
     * @return Liste der gefilterten Einsätze (leer wenn keine Treffer)
     */
    public List<Einsatz> searchEinsaetze(
            Ressort ressort,
            LocalDateTime startDatum,
            LocalDateTime endDatum,
            Helfer helfer,
            Einsatz.EinsatzStatus status) {

        // Starte mit allen Einsätzen
        List<Einsatz> results = einsatzRepository.findAll();

        // Filter 1: Nach Ressort filtern
        if (ressort != null) {
            results = results.stream()
                .filter(e -> e.getRessort().equals(ressort))
                .collect(Collectors.toList());
        }

        // Filter 2: Nach Zeitraum filtern (beide Grenzen müssen angegeben sein)
        if (startDatum != null && endDatum != null) {
            results = results.stream()
                .filter(e -> e.getStartzeit() != null &&
                            e.getStartzeit().isAfter(startDatum) &&
                            e.getStartzeit().isBefore(endDatum))
                .collect(Collectors.toList());
        }

        // Filter 3: Nach zugewiesenem Helfer filtern
        if (helfer != null) {
            results = results.stream()
                .filter(e -> e.getZugewieseneHelfer().contains(helfer))
                .collect(Collectors.toList());
        }

        // Filter 4: Nach Status filtern
        if (status != null) {
            results = results.stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
        }

        return results;
    }
}
