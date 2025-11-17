package ch.flossrennen.eventmanagementsystem.repository;

import ch.flossrennen.eventmanagementsystem.model.Ressort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RessortRepository extends JpaRepository<Ressort, Long> {
    Optional<Ressort> findByName(String name);
    boolean existsByName(String name);
}
