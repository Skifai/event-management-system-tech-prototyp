package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "schicht")
@Data
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
}
