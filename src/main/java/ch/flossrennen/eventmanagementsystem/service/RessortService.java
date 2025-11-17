package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.repository.RessortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RessortService {
    
    private final RessortRepository ressortRepository;
    
    public List<Ressort> findAll() {
        return ressortRepository.findAll();
    }
    
    public Optional<Ressort> findById(Long id) {
        return ressortRepository.findById(id);
    }
    
    public Optional<Ressort> findByName(String name) {
        return ressortRepository.findByName(name);
    }
    
    @Transactional
    public Ressort save(Ressort ressort) {
        return ressortRepository.save(ressort);
    }
    
    @Transactional
    public void delete(Long id) {
        ressortRepository.deleteById(id);
    }
    
    public boolean existsByName(String name) {
        return ressortRepository.existsByName(name);
    }
}
