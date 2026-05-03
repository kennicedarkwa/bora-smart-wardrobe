/**
 * Testklasse für KleidungsstueckService.
 *
 * Warum Mockito?
 * KleidungsstueckService hängt vom KleidungsstueckRepository ab, das mit einer
 * echten Datenbank kommuniziert. Mockito ersetzt das Repository durch ein
 * Fake-Objekt, damit wir die Service-Methoden isoliert testen können.
 *
 */
package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KleidungsstueckServiceTest {

    @Mock
    private KleidungsstueckRepository repository;

    @InjectMocks
    private KleidungsstueckService service;

    private Kleidungsstueck testStueck;

    @BeforeEach
    void setUp() {
        testStueck = new Kleidungsstueck("Weißes T-Shirt", 3, false, MainCategory.OBERTEIL, "");
    }

    // createKleidungsstueck()

    @Test
    void createKleidungsstueck_ruftSaveAuf() {
        service.createKleidungsstueck(testStueck);

        verify(repository, times(1)).save(testStueck);
    }

    // getAll()

    @Test
    void getAll_gibtAlleZurueck() {
        Kleidungsstueck zweites = new Kleidungsstueck("Jeans", 2, false, MainCategory.UNTERTEIL, "");
        when(repository.findAll()).thenReturn(List.of(testStueck, zweites));

        List<Kleidungsstueck> ergebnis = service.getAll();

        assertEquals(2, ergebnis.size());
    }

    @Test
    void getAll_leereDatenbank_gibtLeereListeZurueck() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.getAll().isEmpty());
    }

    // delete()

    @Test
    void delete_ruftDeleteByIdAuf() {
        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}