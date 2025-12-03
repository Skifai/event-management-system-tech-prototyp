package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Entity für Benutzer (System-Anwender).
 *
 * Verwendet @Getter/@Setter statt @Data um equals/hashCode-Probleme
 * mit bidirektionalen JPA-Beziehungen zu vermeiden.
 */
@Entity
@Table(name = "benutzer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Benutzername ist erforderlich")
    @Size(min = 3, max = 50, message = "Benutzername muss zwischen 3 und 50 Zeichen lang sein")
    @Column(nullable = false, unique = true, length = 50)
    private String benutzername;

    @NotBlank(message = "Passwort ist erforderlich")
    @Column(nullable = false)
    private String passwort;

    @NotBlank(message = "Name ist erforderlich")
    @Size(max = 100, message = "Name darf maximal 100 Zeichen lang sein")
    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rolle rolle = Rolle.RESSORTLEITER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ressort_id")
    private Ressort ressort;

    @Column(nullable = false)
    private Boolean aktiv = true;

    public enum Rolle {
        ADMINISTRATOR,
        RESSORTLEITER
    }

    /**
     * Equals/HashCode basierend auf Business-Key (Benutzername ist unique).
     * Verhindert Probleme mit Lazy Loading und bidirektionalen Beziehungen.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Benutzer benutzer = (Benutzer) o;
        return benutzername != null && Objects.equals(benutzername, benutzer.benutzername);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(benutzername);
    }
}
