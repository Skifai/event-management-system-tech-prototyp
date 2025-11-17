package ch.flossrennen.eventmanagementsystem.service;

import ch.flossrennen.eventmanagementsystem.repository.EinsatzRepository;
import ch.flossrennen.eventmanagementsystem.repository.HelferRepository;
import ch.flossrennen.eventmanagementsystem.repository.RessortRepository;
import ch.flossrennen.eventmanagementsystem.repository.SchichtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestDataLoaderTest {

    @Mock
    private RessortRepository ressortRepository;

    @Mock
    private HelferRepository helferRepository;

    @Mock
    private SchichtRepository schichtRepository;

    @Mock
    private EinsatzRepository einsatzRepository;

    private TestDataLoader testDataLoader;

    @BeforeEach
    void setUp() {
        testDataLoader = new TestDataLoader(
            ressortRepository,
            helferRepository,
            schichtRepository,
            einsatzRepository
        );
    }

    @Test
    void shouldLoadTestDataWhenDatabaseIsEmpty() throws Exception {
        // Given
        when(ressortRepository.count()).thenReturn(0L);

        // When
        testDataLoader.run();

        // Then
        verify(ressortRepository).count();
        verify(ressortRepository).saveAll(anyList());
        verify(helferRepository).saveAll(anyList());
        verify(schichtRepository).saveAll(anyList());
        verify(einsatzRepository).saveAll(anyList());
    }

    @Test
    void shouldSkipLoadingWhenDataAlreadyExists() throws Exception {
        // Given
        when(ressortRepository.count()).thenReturn(5L);

        // When
        testDataLoader.run();

        // Then
        verify(ressortRepository).count();
        verify(ressortRepository, never()).saveAll(anyList());
        verify(helferRepository, never()).saveAll(anyList());
        verify(schichtRepository, never()).saveAll(anyList());
        verify(einsatzRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldHandleExceptionsGracefully() throws Exception {
        // Given
        when(ressortRepository.count()).thenReturn(0L);
        when(ressortRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));

        // When
        testDataLoader.run();

        // Then - should not throw exception, just log the error
        verify(ressortRepository).count();
        verify(ressortRepository).saveAll(anyList());
    }
}
