package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity für Schichten (Zeitabschnitte).
 * <p>
 * Verwendet @Getter/@Setter statt @Data um equals/hashCode-Probleme
 * mit bidirektionalen JPA-Beziehungen zu vermeiden.
 */
@Entity
@Table(name = "schicht")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schicht {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name ist erforderlich")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Startzeit ist erforderlich")
    @Column(nullable = false)
    private LocalDateTime startzeit;

    @NotNull(message = "Endzeit ist erforderlich")
    @Column(nullable = false)
    private LocalDateTime endzeit;

    @Column(length = 300)
    private String beschreibung;

    @OneToMany(mappedBy = "schicht", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Einsatz> einsaetze = new HashSet<>();

    /**
     * Equals/HashCode basierend auf ID.
     * Für Entities ist ID-basierte Gleichheit meist am sichersten.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schicht schicht = (Schicht) o;
        return id != null && Objects.equals(id, schicht.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
