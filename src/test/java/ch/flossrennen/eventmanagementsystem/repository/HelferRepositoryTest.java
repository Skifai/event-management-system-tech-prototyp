package ch.flossrennen.eventmanagementsystem.repository;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HelferRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HelferRepository helferRepository;

    @Test
    void testSaveHelfer() {
        Helfer helfer = new Helfer();
        helfer.setVorname("Max");
        helfer.setNachname("Mustermann");
        helfer.setEmail("max.mustermann@example.com");
        helfer.setTelefon("0123456789");

        Helfer savedHelfer = helferRepository.save(helfer);

        assertThat(savedHelfer).isNotNull();
        assertThat(savedHelfer.getId()).isNotNull();
        assertThat(savedHelfer.getVorname()).isEqualTo("Max");
        assertThat(savedHelfer.getNachname()).isEqualTo("Mustermann");
    }

    @Test
    void testFindByEmail() {
        Helfer helfer = new Helfer();
        helfer.setVorname("Anna");
        helfer.setNachname("Schmidt");
        helfer.setEmail("anna.schmidt@example.com");
        helfer.setTelefon("9876543210");

        entityManager.persist(helfer);
        entityManager.flush();

        Optional<Helfer> found = helferRepository.findByEmail("anna.schmidt@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getVorname()).isEqualTo("Anna");
        assertThat(found.get().getNachname()).isEqualTo("Schmidt");
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<Helfer> found = helferRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        Helfer helfer1 = new Helfer();
        helfer1.setVorname("Max");
        helfer1.setNachname("Mustermann");
        helfer1.setEmail("max@example.com");

        Helfer helfer2 = new Helfer();
        helfer2.setVorname("Anna");
        helfer2.setNachname("Schmidt");
        helfer2.setEmail("anna@example.com");

        entityManager.persist(helfer1);
        entityManager.persist(helfer2);
        entityManager.flush();

        var helfers = helferRepository.findAll();

        assertThat(helfers).hasSize(2);
    }

    @Test
    void testDeleteHelfer() {
        Helfer helfer = new Helfer();
        helfer.setVorname("Max");
        helfer.setNachname("Mustermann");
        helfer.setEmail("max@example.com");

        entityManager.persist(helfer);
        entityManager.flush();

        Long id = helfer.getId();
        helferRepository.deleteById(id);

        Optional<Helfer> deleted = helferRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void testUpdateHelfer() {
        Helfer helfer = new Helfer();
        helfer.setVorname("Max");
        helfer.setNachname("Mustermann");
        helfer.setEmail("max@example.com");
        helfer.setTelefon("111");

        entityManager.persist(helfer);
        entityManager.flush();

        helfer.setTelefon("222");
        Helfer updated = helferRepository.save(helfer);

        assertThat(updated.getTelefon()).isEqualTo("222");
    }
}
