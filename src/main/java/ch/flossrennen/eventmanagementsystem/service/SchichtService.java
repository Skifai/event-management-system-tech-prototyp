package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.repository.SchichtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchichtService {
    
    private final SchichtRepository schichtRepository;
    
    public List<Schicht> findAll() {
        return schichtRepository.findAll();
    }
    
    public Optional<Schicht> findById(Long id) {
        return schichtRepository.findById(id);
    }
    
    @Transactional
    public Schicht save(Schicht schicht) {
        if (schicht.getStartzeit().isAfter(schicht.getEndzeit())) {
            throw new IllegalArgumentException("Startzeit muss vor Endzeit liegen");
        }
        return schichtRepository.save(schicht);
    }
    
    @Transactional
    public void delete(Long id) {
        schichtRepository.deleteById(id);
    }
    
    public List<Schicht> findOverlappingSchichten(LocalDateTime startzeit, LocalDateTime endzeit) {
        return schichtRepository.findOverlappingSchichten(startzeit, endzeit);
    }
}
