package ch.flossrennen.eventmanagementsystem.repository;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EinsatzRepository extends JpaRepository<Einsatz, Long> {
    
    List<Einsatz> findByRessort(Ressort ressort);
    
    List<Einsatz> findByStatus(Einsatz.EinsatzStatus status);
    
    @Query("SELECT e FROM Einsatz e WHERE e.startzeit >= :startzeit AND e.endzeit <= :endzeit")
    List<Einsatz> findByZeitraum(@Param("startzeit") LocalDateTime startzeit, 
                                  @Param("endzeit") LocalDateTime endzeit);
    
    @Query("SELECT e FROM Einsatz e JOIN e.zugewieseneHelfer h WHERE h = :helfer")
    List<Einsatz> findByHelfer(@Param("helfer") Helfer helfer);
    
    @Query("SELECT e FROM Einsatz e WHERE :helfer MEMBER OF e.zugewieseneHelfer " +
           "AND e.startzeit < :endzeit AND e.endzeit > :startzeit")
    List<Einsatz> findOverlappingEinsaetzeForHelfer(
        @Param("helfer") Helfer helfer,
        @Param("startzeit") LocalDateTime startzeit,
        @Param("endzeit") LocalDateTime endzeit
    );
}
