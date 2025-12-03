package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity-Klasse für einen Einsatz.
 *
 * Ein Einsatz repräsentiert eine konkrete Aufgabe während einer Schicht,
 * die von einem oder mehreren Helfern eines Ressorts durchgeführt wird.
 *
 * Beziehungen:
 * - ManyToOne zu Ressort (Pflicht): Jeder Einsatz gehört zu einem Ressort
 * - ManyToOne zu Schicht (optional): Einsätze können einer Schicht zugeordnet werden
 * - ManyToMany zu Helfer: Mehrere Helfer können einem Einsatz zugewiesen werden
 *
 * Datenbank-Tabelle: einsatz
 * Join-Tabelle für Helfer: einsatz_helfer
 *
 * Verwendet @Getter/@Setter statt @Data um equals/hashCode-Probleme
 * mit bidirektionalen JPA-Beziehungen zu vermeiden.
 */
@Entity
@Table(name = "einsatz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Einsatz {

    /** Eindeutige ID des Einsatzes (Auto-generiert) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Beschreibung des Einsatzes (max. 300 Zeichen, Pflichtfeld) */
    @NotBlank(message = "Beschreibung ist erforderlich")
    @Size(max = 300, message = "Beschreibung darf maximal 300 Zeichen lang sein")
    @Column(nullable = false, length = 300)
    private String beschreibung;

    /** Startzeit des Einsatzes (Pflichtfeld) */
    @NotNull(message = "Startzeit ist erforderlich")
    @Column(nullable = false)
    private LocalDateTime startzeit;

    /** Endzeit des Einsatzes (Pflichtfeld, muss nach startzeit liegen) */
    @NotNull(message = "Endzeit ist erforderlich")
    @Column(nullable = false)
    private LocalDateTime endzeit;

    /** Ort des Einsatzes, z.B. "Hauptbühne" oder "Catering-Zelt" (max. 150 Zeichen, optional) */
    @Size(max = 150, message = "Ort darf maximal 150 Zeichen lang sein")
    @Column(length = 150)
    private String ort;

    /** Benötigte Mittel/Ressourcen für den Einsatz, z.B. "Mikrofone, Kabel" (max. 200 Zeichen, optional) */
    @Size(max = 200, message = "Mittel dürfen maximal 200 Zeichen lang sein")
    @Column(length = 200)
    private String mittel;

    /** Anzahl der benötigten Helfer für diesen Einsatz (min. 0, Default: 0) */
    @Min(value = 0, message = "Benötigte Anzahl Helfer muss mindestens 0 sein")
    @Column(name = "benoetigte_helfer")
    private Integer benoetigteHelfer = 0;

    /** Das Ressort, das für diesen Einsatz verantwortlich ist (Pflichtfeld, LAZY loading) */
    @NotNull(message = "Ressort ist erforderlich")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressort_id", nullable = false)
    private Ressort ressort;

    /** Die Schicht, in der dieser Einsatz stattfindet (optional, LAZY loading) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schicht_id")
    private Schicht schicht;

    /**
     * Menge der diesem Einsatz zugewiesenen Helfer.
     * ManyToMany-Beziehung über Join-Tabelle "einsatz_helfer".
     * Ein Helfer kann mehreren Einsätzen zugewiesen sein (Doppelzuweisung wird in EinsatzService validiert).
     */
    @ManyToMany
    @JoinTable(
        name = "einsatz_helfer",
        joinColumns = @JoinColumn(name = "einsatz_id"),
        inverseJoinColumns = @JoinColumn(name = "helfer_id")
    )
    private Set<Helfer> zugewieseneHelfer = new HashSet<>();

    /**
     * Status des Einsatzes (als String in DB gespeichert, Default: OFFEN).
     * Wird automatisch von EinsatzService aktualisiert basierend auf Helferanzahl.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EinsatzStatus status = EinsatzStatus.OFFEN;

    /**
     * Status-Enum für Einsätze.
     * - OFFEN: Keine Helfer zugewiesen
     * - IN_PLANUNG: Weniger Helfer als benötigt zugewiesen
     * - VOLLSTAENDIG: Ausreichend Helfer zugewiesen
     * - ABGESCHLOSSEN: Einsatz wurde durchgeführt und ist abgeschlossen
     */
    public enum EinsatzStatus {
        OFFEN,
        IN_PLANUNG,
        VOLLSTAENDIG,
        ABGESCHLOSSEN
    }

    /**
     * Equals/HashCode basierend auf ID.
     * Für Entities ist ID-basierte Gleichheit meist am sichersten.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Einsatz einsatz = (Einsatz) o;
        return id != null && Objects.equals(id, einsatz.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
