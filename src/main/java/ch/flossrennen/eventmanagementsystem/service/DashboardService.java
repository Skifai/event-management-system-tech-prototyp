package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.repository.EinsatzRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service für Dashboard-Statistiken
 * Implementiert KFA.03 - Dashboard
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final EinsatzRepository einsatzRepository;
    private final RessortService ressortService;
    private final SchichtService schichtService;
    private final HelferService helferService;
    
    public DashboardData getDashboardData() {
        DashboardData data = new DashboardData();
        
        List<Einsatz> alleEinsaetze = einsatzRepository.findAll();
        
        // Gesamtstatistiken
        data.setGesamtEinsaetze(alleEinsaetze.size());
        data.setGesamtHelfer(helferService.findAll().size());
        data.setGesamtRessorts(ressortService.findAll().size());
        data.setGesamtSchichten(schichtService.findAll().size());
        
        // Einsatzstatus
        data.setOffeneEinsaetze(alleEinsaetze.stream()
            .filter(e -> e.getStatus() == Einsatz.EinsatzStatus.OFFEN).count());
        data.setInPlanungEinsaetze(alleEinsaetze.stream()
            .filter(e -> e.getStatus() == Einsatz.EinsatzStatus.IN_PLANUNG).count());
        data.setVollstaendigeEinsaetze(alleEinsaetze.stream()
            .filter(e -> e.getStatus() == Einsatz.EinsatzStatus.VOLLSTAENDIG).count());
        
        // Helfer-Statistiken pro Ressort
        Map<String, RessortStats> ressortStats = new HashMap<>();
        for (Ressort ressort : ressortService.findAll()) {
            RessortStats stats = new RessortStats();
            stats.setRessortName(ressort.getName());
            
            List<Einsatz> ressortEinsaetze = einsatzRepository.findByRessort(ressort);
            stats.setAnzahlEinsaetze(ressortEinsaetze.size());
            
            int benoetigteHelfer = ressortEinsaetze.stream()
                .mapToInt(e -> e.getBenoetigteHelfer() != null ? e.getBenoetigteHelfer() : 0)
                .sum();
            int zugewieseneHelfer = ressortEinsaetze.stream()
                .mapToInt(e -> e.getZugewieseneHelfer().size())
                .sum();
            
            stats.setBenoetigteHelfer(benoetigteHelfer);
            stats.setZugewieseneHelfer(zugewieseneHelfer);
            stats.setFehlendeHelfer(benoetigteHelfer - zugewieseneHelfer);
            
            ressortStats.put(ressort.getName(), stats);
        }
        data.setRessortStatistiken(ressortStats);
        
        return data;
    }
    
    @Data
    public static class DashboardData {
        private int gesamtEinsaetze;
        private int gesamtHelfer;
        private int gesamtRessorts;
        private int gesamtSchichten;
        
        private long offeneEinsaetze;
        private long inPlanungEinsaetze;
        private long vollstaendigeEinsaetze;
        
        private Map<String, RessortStats> ressortStatistiken;
    }
    
    @Data
    public static class RessortStats {
        private String ressortName;
        private int anzahlEinsaetze;
        private int benoetigteHelfer;
        private int zugewieseneHelfer;
        private int fehlendeHelfer;
    }
}
