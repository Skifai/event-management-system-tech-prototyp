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

@Service
@RequiredArgsConstructor
public class EinsatzService {
    
    private final EinsatzRepository einsatzRepository;
    
    public List<Einsatz> findAll() {
        return einsatzRepository.findAll();
    }
    
    public Optional<Einsatz> findById(Long id) {
        return einsatzRepository.findById(id);
    }
    
    public List<Einsatz> findByRessort(Ressort ressort) {
        return einsatzRepository.findByRessort(ressort);
    }
    
    public List<Einsatz> findByStatus(Einsatz.EinsatzStatus status) {
        return einsatzRepository.findByStatus(status);
    }
    
    public List<Einsatz> findByZeitraum(LocalDateTime startzeit, LocalDateTime endzeit) {
        return einsatzRepository.findByZeitraum(startzeit, endzeit);
    }
    
    public List<Einsatz> findByHelfer(Helfer helfer) {
        return einsatzRepository.findByHelfer(helfer);
    }
    
    @Transactional
    public Einsatz save(Einsatz einsatz) {
        if (einsatz.getStartzeit().isAfter(einsatz.getEndzeit())) {
            throw new IllegalArgumentException("Startzeit muss vor Endzeit liegen");
        }
        return einsatzRepository.save(einsatz);
    }
    
    @Transactional
    public void delete(Long id) {
        einsatzRepository.deleteById(id);
    }
    
    /**
     * Weist einen Helfer einem Einsatz zu und prüft auf Zeitkonflikte
     * Implementiert MFA.03 - Validierung gegen Doppelzuweisung
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
    
    @Transactional
    public Einsatz removeHelfer(Long einsatzId, Helfer helfer) {
        Einsatz einsatz = einsatzRepository.findById(einsatzId)
            .orElseThrow(() -> new IllegalArgumentException("Einsatz nicht gefunden"));
        
        einsatz.getZugewieseneHelfer().remove(helfer);
        updateEinsatzStatus(einsatz);
        
        return einsatzRepository.save(einsatz);
    }
    
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
     * Prüft ob ein Helfer im angegebenen Zeitraum verfügbar ist
     */
    public boolean isHelferAvailable(Helfer helfer, LocalDateTime startzeit, LocalDateTime endzeit) {
        List<Einsatz> conflicts = einsatzRepository.findOverlappingEinsaetzeForHelfer(helfer, startzeit, endzeit);
        return conflicts.isEmpty();
    }
}
