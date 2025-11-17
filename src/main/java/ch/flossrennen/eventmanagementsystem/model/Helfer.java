package ch.flossrennen.eventmanagementsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "helfer")
@Data
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
}
