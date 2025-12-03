package ch.flossrennen.eventmanagementsystem.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class HelferTest {

    @Test
    void testHelferCreation() {
        Helfer helfer = new Helfer();
        helfer.setVorname("Max");
        helfer.setNachname("Mustermann");
        helfer.setEmail("max.mustermann@example.com");
        helfer.setTelefon("0123456789");

        assertThat(helfer.getVorname()).isEqualTo("Max");
        assertThat(helfer.getNachname()).isEqualTo("Mustermann");
        assertThat(helfer.getEmail()).isEqualTo("max.mustermann@example.com");
        assertThat(helfer.getTelefon()).isEqualTo("0123456789");
    }

    @Test
    void testHelferAllArgsConstructor() {
        Ressort ressort = new Ressort();
        ressort.setName("Küche");
        
        Helfer helfer = new Helfer(1L, "Anna", "Schmidt", "anna.schmidt@example.com", "9876543210", ressort, new HashSet<>());

        assertThat(helfer.getId()).isEqualTo(1L);
        assertThat(helfer.getVorname()).isEqualTo("Anna");
        assertThat(helfer.getNachname()).isEqualTo("Schmidt");
        assertThat(helfer.getEmail()).isEqualTo("anna.schmidt@example.com");
        assertThat(helfer.getTelefon()).isEqualTo("9876543210");
        assertThat(helfer.getRessort()).isEqualTo(ressort);
    }

    @Test
    void testHelferNoArgsConstructor() {
        Helfer helfer = new Helfer();
        
        assertThat(helfer).isNotNull();
        assertThat(helfer.getVorname()).isNull();
        assertThat(helfer.getNachname()).isNull();
        assertThat(helfer.getEmail()).isNull();
        assertThat(helfer.getTelefon()).isNull();
        assertThat(helfer.getRessort()).isNull();
    }

    @Test
    void testHelferEquality() {
        Ressort ressort = new Ressort();
        ressort.setName("Küche");
        
        Helfer helfer1 = new Helfer(1L, "Max", "Mustermann", "max@example.com", "123", ressort, new HashSet<>());
        Helfer helfer2 = new Helfer(1L, "Max", "Mustermann", "max@example.com", "123", ressort, new HashSet<>());

        assertThat(helfer1).isEqualTo(helfer2);
        assertThat(helfer1.hashCode()).isEqualTo(helfer2.hashCode());
    }

    @Test
    void testHelferEqualsBasedOnEmail() {
        // Equals basiert nun auf Email (Business-Key)
        Helfer helfer1 = new Helfer();
        helfer1.setEmail("max@example.com");
        helfer1.setVorname("Max");

        Helfer helfer2 = new Helfer();
        helfer2.setEmail("max@example.com");
        helfer2.setVorname("Different Name"); // Verschiedener Name, aber gleiche Email

        // Beide sollten gleich sein, weil Email gleich ist
        assertThat(helfer1).isEqualTo(helfer2);
        assertThat(helfer1.hashCode()).isEqualTo(helfer2.hashCode());

        // Verschiedene Emails -> nicht gleich
        Helfer helfer3 = new Helfer();
        helfer3.setEmail("different@example.com");
        assertThat(helfer1).isNotEqualTo(helfer3);
    }
}
