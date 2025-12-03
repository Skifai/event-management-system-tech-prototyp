package ch.flossrennen.eventmanagementsystem.repository;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository für Einsatz-Entitäten.
 *
 * Bietet Datenbankzugriff für Einsätze mit verschiedenen Suchmethoden.
 * Nutzt Spring Data JPA für automatische Implementierung der Basismethoden
 * und benutzerdefinierte Queries für komplexere Abfragen.
 */
@Repository
public interface EinsatzRepository extends JpaRepository<Einsatz, Long> {

    /**
     * Findet alle Einsätze eines bestimmten Ressorts.
     * Spring Data JPA generiert die Query automatisch basierend auf dem Methodennamen.
     *
     * @param ressort Das Ressort, nach dem gesucht wird
     * @return Liste aller Einsätze des Ressorts (leer wenn keine gefunden)
     */
    List<Einsatz> findByRessort(Ressort ressort);

    /**
     * Findet alle Einsätze mit einem bestimmten Status.
     * Spring Data JPA generiert die Query automatisch.
     *
     * @param status Der gewünschte Einsatzstatus (OFFEN, IN_PLANUNG, VOLLSTAENDIG, ABGESCHLOSSEN)
     * @return Liste aller Einsätze mit diesem Status (leer wenn keine gefunden)
     */
    List<Einsatz> findByStatus(Einsatz.EinsatzStatus status);

    /**
     * Findet alle Einsätze, die komplett innerhalb eines Zeitraums liegen.
     * Bedingung: Einsatz startet nach/an startzeit UND endet vor/an endzeit.
     *
     * @param startzeit Start des Suchzeitraums (inklusive)
     * @param endzeit Ende des Suchzeitraums (inklusive)
     * @return Liste aller Einsätze im Zeitraum (leer wenn keine gefunden)
     */
    @Query("SELECT e FROM Einsatz e WHERE e.startzeit >= :startzeit AND e.endzeit <= :endzeit")
    List<Einsatz> findByZeitraum(@Param("startzeit") LocalDateTime startzeit,
                                  @Param("endzeit") LocalDateTime endzeit);

    /**
     * Findet alle Einsätze, denen ein bestimmter Helfer zugewiesen ist.
     * Nutzt JOIN über die ManyToMany-Beziehung.
     *
     * @param helfer Der Helfer, nach dem gesucht wird
     * @return Liste aller Einsätze des Helfers (leer wenn keine Zuweisung)
     */
    @Query("SELECT e FROM Einsatz e JOIN e.zugewieseneHelfer h WHERE h = :helfer")
    List<Einsatz> findByHelfer(@Param("helfer") Helfer helfer);

    /**
     * Findet alle Einsätze, die sich zeitlich mit dem angegebenen Zeitraum überschneiden
     * UND denen der angegebene Helfer bereits zugewiesen ist.
     *
     * Diese Methode wird für die Doppelzuweisungs-Prüfung verwendet (MFA.03).
     * Zwei Zeiträume überschneiden sich, wenn:
     * - Einsatz.startzeit < parameter.endzeit UND
     * - Einsatz.endzeit > parameter.startzeit
     *
     * Beispiel:
     * - Einsatz: 10:00-12:00
     * - Geprüfter Zeitraum: 11:00-13:00
     * - Ergebnis: Überschneidung erkannt (11:00-12:00)
     *
     * @param helfer Der Helfer, für den Konflikte geprüft werden
     * @param startzeit Start des zu prüfenden Zeitraums
     * @param endzeit Ende des zu prüfenden Zeitraums
     * @return Liste aller überschneidenden Einsätze des Helfers (leer wenn keine Konflikte)
     */
    @Query("SELECT e FROM Einsatz e WHERE :helfer MEMBER OF e.zugewieseneHelfer " +
           "AND e.startzeit < :endzeit AND e.endzeit > :startzeit")
    List<Einsatz> findOverlappingEinsaetzeForHelfer(
        @Param("helfer") Helfer helfer,
        @Param("startzeit") LocalDateTime startzeit,
        @Param("endzeit") LocalDateTime endzeit
    );
}
