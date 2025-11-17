package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Benutzer;
import ch.flossrennen.eventmanagementsystem.repository.BenutzerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BenutzerService {
    
    private final BenutzerRepository benutzerRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<Benutzer> findAll() {
        return benutzerRepository.findAll();
    }
    
    public Optional<Benutzer> findById(Long id) {
        return benutzerRepository.findById(id);
    }
    
    public Optional<Benutzer> findByBenutzername(String benutzername) {
        return benutzerRepository.findByBenutzername(benutzername);
    }
    
    @Transactional
    public Benutzer save(Benutzer benutzer) {
        // Encode password if it's a new user or password has been changed
        if (benutzer.getId() == null || !benutzer.getPasswort().startsWith("$2a$")) {
            benutzer.setPasswort(passwordEncoder.encode(benutzer.getPasswort()));
        }
        return benutzerRepository.save(benutzer);
    }
    
    @Transactional
    public void delete(Long id) {
        benutzerRepository.deleteById(id);
    }
    
    public boolean existsByBenutzername(String benutzername) {
        return benutzerRepository.existsByBenutzername(benutzername);
    }
}
