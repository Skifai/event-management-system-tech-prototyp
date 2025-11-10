package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.repository.EinsatzRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service für die Verwaltung von Einsätzen.
 * Implementiert die Geschäftslogik für Einsatzplanung und -verwaltung.
 * 
 * Erfüllt Anforderungen:
 * - MFA.02: Einsatzplanung
 * - MFA.03: Validierung gegen Doppelzuweisung
 */
@Service
@RequiredArgsConstructor
public class EinsatzService {
    
    private final EinsatzRepository einsatzRepository;
    
    /**
     * Gibt alle Einsätze zurück.
     * @return Liste aller Einsätze
     */
    public List<Einsatz> findAll() {
        return einsatzRepository.findAll();
    }
    
    /**
     * Sucht einen Einsatz anhand der ID.
     * @param id Die Einsatz-ID
     * @return Optional mit dem Einsatz, falls gefunden
     */
    public Optional<Einsatz> findById(Long id) {
        return einsatzRepository.findById(id);
    }
    
    /**
     * Sucht alle Einsätze eines bestimmten Ressorts.
     * @param ressort Das Ressort
     * @return Liste der Einsätze des Ressorts
     */
    public List<Einsatz> findByRessort(Ressort ressort) {
        return einsatzRepository.findByRessort(ressort);
    }
    
    /**
     * Sucht alle Einsätze mit einem bestimmten Status.
     * @param status Der gewünschte Status
     * @return Liste der Einsätze mit diesem Status
     */
    public List<Einsatz> findByStatus(Einsatz.EinsatzStatus status) {
        return einsatzRepository.findByStatus(status);
    }
    
    /**
     * Sucht alle Einsätze in einem bestimmten Zeitraum.
     * @param startzeit Start des Zeitraums
     * @param endzeit Ende des Zeitraums
     * @return Liste der Einsätze im Zeitraum
     */
    public List<Einsatz> findByZeitraum(LocalDateTime startzeit, LocalDateTime endzeit) {
        return einsatzRepository.findByZeitraum(startzeit, endzeit);
    }
    
    /**
     * Sucht alle Einsätze, denen ein bestimmter Helfer zugewiesen ist.
     * @param helfer Der Helfer
     * @return Liste der Einsätze des Helfers
     */
    public List<Einsatz> findByHelfer(Helfer helfer) {
        return einsatzRepository.findByHelfer(helfer);
    }
    
    /**
     * Speichert einen Einsatz.
     * Validiert, dass Startzeit vor Endzeit liegt.
     * 
     * @param einsatz Der zu speichernde Einsatz
     * @return Der gespeicherte Einsatz
     * @throws IllegalArgumentException wenn Startzeit nach Endzeit liegt
     */
    @Transactional
    public Einsatz save(Einsatz einsatz) {
        if (einsatz.getStartzeit().isAfter(einsatz.getEndzeit())) {
            throw new IllegalArgumentException("Startzeit muss vor Endzeit liegen");
        }
        return einsatzRepository.save(einsatz);
    }
    
    /**
     * Löscht einen Einsatz anhand der ID.
     * @param id Die ID des zu löschenden Einsatzes
     */
    @Transactional
    public void delete(Long id) {
        einsatzRepository.deleteById(id);
    }
    
    /**
     * Weist einen Helfer einem Einsatz zu und prüft auf Zeitkonflikte.
     * 
     * Implementiert MFA.03 - Validierung gegen Doppelzuweisung:
     * - Prüft ob der Helfer bereits einem überschneidenden Einsatz zugewiesen ist
     * - Wirft eine Exception, falls ein Konflikt erkannt wird
     * - Aktualisiert den Einsatzstatus basierend auf der Helferanzahl
     * 
     * @param einsatzId Die ID des Einsatzes
     * @param helfer Der zuzuweisende Helfer
     * @return Der aktualisierte Einsatz
     * @throws IllegalArgumentException wenn der Einsatz nicht gefunden wird
     * @throws IllegalStateException wenn der Helfer bereits einem überschneidenden Einsatz zugewiesen ist
     */
    @Transactional
    public Einsatz assignHelfer(Long einsatzId, Helfer helfer) {
        Einsatz einsatz = einsatzRepository.findById(einsatzId)
            .orElseThrow(() -> new IllegalArgumentException("Einsatz nicht gefunden"));
        
        // Prüfe auf Zeitkonflikte mit anderen Einsätzen des Helfers
        List<Einsatz> conflictingEinsaetze = einsatzRepository.findOverlappingEinsaetzeForHelfer(
            helfer, einsatz.getStartzeit(), einsatz.getEndzeit()
        );
        
        if (!conflictingEinsaetze.isEmpty()) {
            throw new IllegalStateException(
                "Helfer ist bereits für einen überschneidenden Einsatz eingeteilt: " + 
                conflictingEinsaetze.get(0).getBeschreibung()
            );
        }
        
        einsatz.getZugewieseneHelfer().add(helfer);
        
        // Aktualisiere Status basierend auf Helferanzahl
        updateEinsatzStatus(einsatz);
        
        return einsatzRepository.save(einsatz);
    }
    
    /**
     * Entfernt einen Helfer von einem Einsatz.
     * Aktualisiert den Einsatzstatus entsprechend.
     * 
     * @param einsatzId Die ID des Einsatzes
     * @param helfer Der zu entfernende Helfer
     * @return Der aktualisierte Einsatz
     * @throws IllegalArgumentException wenn der Einsatz nicht gefunden wird
     */
    @Transactional
    public Einsatz removeHelfer(Long einsatzId, Helfer helfer) {
        Einsatz einsatz = einsatzRepository.findById(einsatzId)
            .orElseThrow(() -> new IllegalArgumentException("Einsatz nicht gefunden"));
        
        einsatz.getZugewieseneHelfer().remove(helfer);
        updateEinsatzStatus(einsatz);
        
        return einsatzRepository.save(einsatz);
    }
    
    /**
     * Aktualisiert den Status eines Einsatzes basierend auf der Anzahl zugewiesener Helfer.
     * 
     * Status-Logik:
     * - OFFEN: Keine Helfer zugewiesen
     * - IN_PLANUNG: Weniger Helfer als benötigt
     * - VOLLSTAENDIG: Ausreichend Helfer zugewiesen
     * 
     * @param einsatz Der zu aktualisierende Einsatz
     */
    private void updateEinsatzStatus(Einsatz einsatz) {
        int assignedCount = einsatz.getZugewieseneHelfer().size();
        int required = einsatz.getBenoetigteHelfer();
        
        if (assignedCount == 0) {
            einsatz.setStatus(Einsatz.EinsatzStatus.OFFEN);
        } else if (assignedCount < required) {
            einsatz.setStatus(Einsatz.EinsatzStatus.IN_PLANUNG);
        } else {
            einsatz.setStatus(Einsatz.EinsatzStatus.VOLLSTAENDIG);
        }
    }
    
    /**
     * Prüft ob ein Helfer im angegebenen Zeitraum verfügbar ist.
     * Ein Helfer ist verfügbar, wenn er keinem überschneidenden Einsatz zugewiesen ist.
     * 
     * @param helfer Der zu prüfende Helfer
     * @param startzeit Start des Zeitraums
     * @param endzeit Ende des Zeitraums
     * @return true wenn der Helfer verfügbar ist, sonst false
     */
    public boolean isHelferAvailable(Helfer helfer, LocalDateTime startzeit, LocalDateTime endzeit) {
        List<Einsatz> conflicts = einsatzRepository.findOverlappingEinsaetzeForHelfer(helfer, startzeit, endzeit);
        return conflicts.isEmpty();
    }
}
