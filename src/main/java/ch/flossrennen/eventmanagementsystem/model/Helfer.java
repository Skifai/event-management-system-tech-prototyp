package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity für Helfer/Freiwillige.
 * <p>
 * Verwendet @Getter/@Setter statt @Data um equals/hashCode-Probleme
 * mit bidirektionalen JPA-Beziehungen zu vermeiden.
 */
@Entity
@Table(name = "helfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Helfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String vorname;

    @Column(nullable = false, length = 100)
    private String nachname;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stammressort_id")
    private Ressort ressort;

    @ManyToMany(mappedBy = "zugewieseneHelfer")
    private Set<Einsatz> einsaetze = new HashSet<>();

    /**
     * Equals/HashCode nur basierend auf Business-Key (Email).
     * Verhindert Probleme mit Lazy Loading und bidirektionalen Beziehungen.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Helfer helfer = (Helfer) o;
        return email != null && Objects.equals(email, helfer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
