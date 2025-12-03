package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity für Ressorts (Organisationsbereiche).
 * <p>
 * Verwendet @Getter/@Setter statt @Data um equals/hashCode-Probleme
 * mit bidirektionalen JPA-Beziehungen zu vermeiden.
 */
@Entity
@Table(name = "ressort")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ressort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name ist erforderlich")
    @Size(max = 100, message = "Name darf maximal 100 Zeichen lang sein")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 500, message = "Beschreibung darf maximal 500 Zeichen lang sein")
    @Column(length = 500)
    private String beschreibung;

    @Size(max = 300, message = "Zuständigkeiten dürfen maximal 300 Zeichen lang sein")
    @Column(length = 300)
    private String zustaendigkeiten;

    @Size(max = 100, message = "Kontaktperson darf maximal 100 Zeichen lang sein")
    @Column(length = 100)
    private String kontaktperson;

    @OneToMany(mappedBy = "ressort", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Helfer> helfer = new HashSet<>();

    @OneToMany(mappedBy = "ressort", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Einsatz> einsaetze = new HashSet<>();

    /**
     * Equals/HashCode basierend auf Business-Key (Name ist unique).
     * Verhindert Probleme mit Lazy Loading und bidirektionalen Beziehungen.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ressort ressort = (Ressort) o;
        return name != null && Objects.equals(name, ressort.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
