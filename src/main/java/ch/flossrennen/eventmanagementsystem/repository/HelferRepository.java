package ch.flossrennen.eventmanagementsystem.repository;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HelferRepository extends JpaRepository<Helfer, Long> {
    Optional<Helfer> findByEmail(String email);

    @Query("SELECT h FROM Helfer h LEFT JOIN FETCH h.ressort")
    List<Helfer> findAllWithRessort();
}
