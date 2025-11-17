package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.repository.EinsatzRepository;
import ch.flossrennen.eventmanagementsystem.repository.HelferRepository;
import ch.flossrennen.eventmanagementsystem.repository.RessortRepository;
import ch.flossrennen.eventmanagementsystem.repository.SchichtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service that loads test data into the database on application startup.
 * Can be enabled/disabled via configuration property 'app.testdata.enabled'.
 * Automatically enabled in development mode, disabled in production.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "app.testdata.enabled",
    havingValue = "true"
)
public class TestDataLoader implements CommandLineRunner {

    private final RessortRepository ressortRepository;
    private final HelferRepository helferRepository;
    private final SchichtRepository schichtRepository;
    private final EinsatzRepository einsatzRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Loading test data into database...");

        // Check if data already exists
        if (ressortRepository.count() > 0) {
            log.info("Database already contains data. Skipping test data loading.");
            return;
        }

        try {
            loadTestData();
            log.info("Test data loaded successfully!");
        } catch (Exception e) {
            log.error("Error loading test data: {}", e.getMessage(), e);
        }
    }

    private void loadTestData() {
        // Create Ressorts
        Ressort kueche = createRessort("Küche", "Verantwortlich für Essensversorgung", 
            "Kochen, Ausgabe, Reinigung", "Maria Schmidt");
        Ressort bar = createRessort("Bar", "Getränkeversorgung und -ausgabe", 
            "Getränkezubereitung, Verkauf, Bestandsführung", "Thomas Weber");
        Ressort sicherheit = createRessort("Sicherheit", "Sicherheit und Ordnung", 
            "Einlasskontrolle, Überwachung, Erste Hilfe", "Andreas Müller");
        Ressort technik = createRessort("Technik", "Technische Infrastruktur", 
            "Aufbau, Wartung, Abbau von Technik", "Sarah Fischer");
        Ressort dekoration = createRessort("Dekoration", "Gestaltung und Atmosphäre", 
            "Aufbau, Pflege und Abbau der Dekoration", "Laura Klein");

        List<Ressort> ressorts = List.of(kueche, bar, sicherheit, technik, dekoration);
        ressortRepository.saveAll(ressorts);
        log.info("Created {} ressorts", ressorts.size());

        // Create Helfer
        List<Helfer> helfer = List.of(
            createHelfer("Max", "Mustermann", "max.mustermann@example.com", "0123-456789", kueche),
            createHelfer("Anna", "Schmidt", "anna.schmidt@example.com", "0123-456790", kueche),
            createHelfer("Peter", "Meyer", "peter.meyer@example.com", "0123-456791", bar),
            createHelfer("Julia", "Wagner", "julia.wagner@example.com", "0123-456792", bar),
            createHelfer("Michael", "Becker", "michael.becker@example.com", "0123-456793", sicherheit),
            createHelfer("Sabine", "Hoffmann", "sabine.hoffmann@example.com", "0123-456794", sicherheit),
            createHelfer("Christian", "Koch", "christian.koch@example.com", "0123-456795", technik),
            createHelfer("Nina", "Wolf", "nina.wolf@example.com", "0123-456796", technik),
            createHelfer("Stefan", "Schröder", "stefan.schroeder@example.com", "0123-456797", dekoration),
            createHelfer("Lisa", "Neumann", "lisa.neumann@example.com", "0123-456798", dekoration),
            createHelfer("Frank", "Zimmermann", "frank.zimmermann@example.com", "0123-456799", kueche),
            createHelfer("Petra", "Braun", "petra.braun@example.com", "0123-456800", bar),
            createHelfer("Markus", "Lange", "markus.lange@example.com", "0123-456801", sicherheit),
            createHelfer("Sandra", "Krause", "sandra.krause@example.com", "0123-456802", technik),
            createHelfer("Daniel", "Richter", "daniel.richter@example.com", "0123-456803", dekoration)
        );
        helferRepository.saveAll(helfer);
        log.info("Created {} helfer", helfer.size());

        // Create Schichten for the event day
        LocalDateTime eventDate = LocalDateTime.now().plusDays(30).withHour(8).withMinute(0);
        
        Schicht morgenSchicht = createSchicht("Morgen-Schicht", 
            eventDate, 
            eventDate.plusHours(6),
            "Aufbau und Vorbereitung");
        
        Schicht mittagsSchicht = createSchicht("Mittags-Schicht",
            eventDate.plusHours(6),
            eventDate.plusHours(12),
            "Hauptveranstaltung");
        
        Schicht abendSchicht = createSchicht("Abend-Schicht",
            eventDate.plusHours(12),
            eventDate.plusHours(18),
            "Nachveranstaltung und Abbau");

        List<Schicht> schichten = List.of(morgenSchicht, mittagsSchicht, abendSchicht);
        schichtRepository.saveAll(schichten);
        log.info("Created {} schichten", schichten.size());

        // Create Einsätze
        List<Einsatz> einsaetze = List.of(
            // Morgen-Schicht
            createEinsatz("Küche vorbereiten", morgenSchicht, kueche, "Hauptküche", 
                "Kochutensilien, Zutaten", 3, helfer.subList(0, 2)),
            createEinsatz("Bar aufbauen", morgenSchicht, bar, "Hauptbar", 
                "Kühlung, Gläser, Getränke", 2, helfer.subList(2, 4)),
            createEinsatz("Technik-Setup", morgenSchicht, technik, "Hauptbühne", 
                "Mikrofone, Lautsprecher, Beleuchtung", 2, helfer.subList(6, 8)),
            createEinsatz("Dekoration aufbauen", morgenSchicht, dekoration, "Gesamtgelände", 
                "Banner, Ballons, Tische", 2, helfer.subList(8, 10)),
            
            // Mittags-Schicht
            createEinsatz("Essensausgabe", mittagsSchicht, kueche, "Hauptküche", 
                "Besteck, Teller, Servietten", 4, helfer.subList(0, 3)),
            createEinsatz("Getränkeausschank", mittagsSchicht, bar, "Hauptbar", 
                "Zapfanlage, Eiswürfel", 3, helfer.subList(2, 5)),
            createEinsatz("Einlasskontrolle", mittagsSchicht, sicherheit, "Haupteingang", 
                "Scanner, Armbänder", 2, helfer.subList(4, 6)),
            createEinsatz("Technikbetreuung", mittagsSchicht, technik, "Hauptbühne", 
                "Mischpult, Kabel", 2, helfer.subList(6, 8)),
            
            // Abend-Schicht
            createEinsatz("Küche aufräumen", abendSchicht, kueche, "Hauptküche", 
                "Reinigungsmittel", 3, helfer.subList(10, 12)),
            createEinsatz("Bar abbauen", abendSchicht, bar, "Hauptbar", 
                "Transportboxen", 2, helfer.subList(11, 13)),
            createEinsatz("Gelände sichern", abendSchicht, sicherheit, "Gesamtgelände", 
                "Taschenlampen, Funkgeräte", 3, helfer.subList(12, 14)),
            createEinsatz("Technik abbauen", abendSchicht, technik, "Hauptbühne", 
                "Transportwagen, Kabel", 2, helfer.subList(13, 15)),
            createEinsatz("Dekoration abbauen", abendSchicht, dekoration, "Gesamtgelände", 
                "Müllsäcke, Transportboxen", 2, helfer.subList(8, 10))
        );
        einsatzRepository.saveAll(einsaetze);
        log.info("Created {} einsaetze", einsaetze.size());
    }

    private Ressort createRessort(String name, String beschreibung, 
                                  String zustaendigkeiten, String kontaktperson) {
        Ressort ressort = new Ressort();
        ressort.setName(name);
        ressort.setBeschreibung(beschreibung);
        ressort.setZustaendigkeiten(zustaendigkeiten);
        ressort.setKontaktperson(kontaktperson);
        return ressort;
    }

    private Helfer createHelfer(String vorname, String nachname, String email, 
                                String telefon, Ressort ressort) {
        Helfer helfer = new Helfer();
        helfer.setVorname(vorname);
        helfer.setNachname(nachname);
        helfer.setEmail(email);
        helfer.setTelefon(telefon);
        helfer.setRessort(ressort);
        return helfer;
    }

    private Schicht createSchicht(String name, LocalDateTime startzeit, 
                                  LocalDateTime endzeit, String beschreibung) {
        Schicht schicht = new Schicht();
        schicht.setName(name);
        schicht.setStartzeit(startzeit);
        schicht.setEndzeit(endzeit);
        schicht.setBeschreibung(beschreibung);
        return schicht;
    }

    private Einsatz createEinsatz(String beschreibung, Schicht schicht, Ressort ressort,
                                  String ort, String mittel, int benoetigteHelfer,
                                  List<Helfer> zugewieseneHelfer) {
        Einsatz einsatz = new Einsatz();
        einsatz.setBeschreibung(beschreibung);
        einsatz.setStartzeit(schicht.getStartzeit());
        einsatz.setEndzeit(schicht.getEndzeit());
        einsatz.setOrt(ort);
        einsatz.setMittel(mittel);
        einsatz.setBenoetigteHelfer(benoetigteHelfer);
        einsatz.setRessort(ressort);
        einsatz.setSchicht(schicht);
        
        // Set initial status based on assigned helpers
        if (zugewieseneHelfer != null && !zugewieseneHelfer.isEmpty()) {
            einsatz.setZugewieseneHelfer(new HashSet<>(zugewieseneHelfer));
            if (zugewieseneHelfer.size() >= benoetigteHelfer) {
                einsatz.setStatus(Einsatz.EinsatzStatus.VOLLSTAENDIG);
            } else {
                einsatz.setStatus(Einsatz.EinsatzStatus.IN_PLANUNG);
            }
        } else {
            einsatz.setStatus(Einsatz.EinsatzStatus.OFFEN);
        }
        
        return einsatz;
    }
}
