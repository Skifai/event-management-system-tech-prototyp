package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ressort")
@Data
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
}
