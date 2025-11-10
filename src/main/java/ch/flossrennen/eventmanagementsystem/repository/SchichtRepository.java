package ch.flossrennen.eventmanagementsystem.repository;

import ch.flossrennen.eventmanagementsystem.model.Schicht;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SchichtRepository extends JpaRepository<Schicht, Long> {
    @Query("SELECT s FROM Schicht s WHERE s.startzeit <= :endzeit AND s.endzeit >= :startzeit")
    List<Schicht> findOverlappingSchichten(@Param("startzeit") LocalDateTime startzeit, 
                                           @Param("endzeit") LocalDateTime endzeit);
}
