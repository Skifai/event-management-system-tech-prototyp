package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "einsatz")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Einsatz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Beschreibung ist erforderlich")
    @Size(max = 300, message = "Beschreibung darf maximal 300 Zeichen lang sein")
    @Column(nullable = false, length = 300)
    private String beschreibung;

    @NotNull(message = "Startzeit ist erforderlich")
    @Column(nullable = false)
    private LocalDateTime startzeit;

    @NotNull(message = "Endzeit ist erforderlich")
    @Column(nullable = false)
    private LocalDateTime endzeit;

    @Size(max = 150, message = "Ort darf maximal 150 Zeichen lang sein")
    @Column(length = 150)
    private String ort;

    @Size(max = 200, message = "Mittel dürfen maximal 200 Zeichen lang sein")
    @Column(length = 200)
    private String mittel;

    @Min(value = 0, message = "Benötigte Anzahl Helfer muss mindestens 0 sein")
    @Column(name = "benoetigte_helfer")
    private Integer benoetigteHelfer = 0;

    @NotNull(message = "Ressort ist erforderlich")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressort_id", nullable = false)
    private Ressort ressort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schicht_id")
    private Schicht schicht;

    @ManyToMany
    @JoinTable(
        name = "einsatz_helfer",
        joinColumns = @JoinColumn(name = "einsatz_id"),
        inverseJoinColumns = @JoinColumn(name = "helfer_id")
    )
    private Set<Helfer> zugewieseneHelfer = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EinsatzStatus status = EinsatzStatus.OFFEN;

    public enum EinsatzStatus {
        OFFEN,
        IN_PLANUNG,
        VOLLSTAENDIG,
        ABGESCHLOSSEN
    }
}
