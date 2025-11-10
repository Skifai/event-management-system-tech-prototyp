package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "benutzer")
@Data
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
}
