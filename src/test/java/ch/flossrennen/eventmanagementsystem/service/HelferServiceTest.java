package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.repository.HelferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelferServiceTest {

    @Mock
    private HelferRepository helferRepository;

    @InjectMocks
    private HelferService helferService;

    private Helfer testHelfer;

    @BeforeEach
    void setUp() {
        testHelfer = new Helfer();
        testHelfer.setId(1L);
        testHelfer.setVorname("Max");
        testHelfer.setNachname("Mustermann");
        testHelfer.setEmail("max@example.com");
        testHelfer.setTelefon("123456");
    }

    @Test
    void testFindAll() {
        List<Helfer> helfers = Arrays.asList(testHelfer);
        when(helferRepository.findAll()).thenReturn(helfers);

        List<Helfer> result = helferService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVorname()).isEqualTo("Max");
        verify(helferRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(helferRepository.findById(1L)).thenReturn(Optional.of(testHelfer));

        Optional<Helfer> result = helferService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getVorname()).isEqualTo("Max");
        verify(helferRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByEmail() {
        when(helferRepository.findByEmail("max@example.com")).thenReturn(Optional.of(testHelfer));

        Optional<Helfer> result = helferService.findByEmail("max@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("max@example.com");
        verify(helferRepository, times(1)).findByEmail("max@example.com");
    }

    @Test
    void testSave() {
        when(helferRepository.save(any(Helfer.class))).thenReturn(testHelfer);

        Helfer result = helferService.save(testHelfer);

        assertThat(result).isNotNull();
        assertThat(result.getVorname()).isEqualTo("Max");
        verify(helferRepository, times(1)).save(testHelfer);
    }

    @Test
    void testDelete() {
        doNothing().when(helferRepository).deleteById(1L);

        helferService.delete(1L);

        verify(helferRepository, times(1)).deleteById(1L);
    }
}
