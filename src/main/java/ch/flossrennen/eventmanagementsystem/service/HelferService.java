package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.repository.HelferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HelferService {
    
    private final HelferRepository helferRepository;
    
    public List<Helfer> findAll() {
        return helferRepository.findAll();
    }
    
    public Optional<Helfer> findById(Long id) {
        return helferRepository.findById(id);
    }
    
    public Optional<Helfer> findByEmail(String email) {
        return helferRepository.findByEmail(email);
    }
    
    @Transactional
    public Helfer save(Helfer helfer) {
        return helferRepository.save(helfer);
    }
    
    @Transactional
    public void delete(Long id) {
        helferRepository.deleteById(id);
    }
}
