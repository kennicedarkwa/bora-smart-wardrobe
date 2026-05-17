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
import com.example.sea2_bora.implementation.Tags;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Test
    void createKleidungsstueck_speichertGenauDiesesObjekt() {
        service.createKleidungsstueck(testStueck);

        ArgumentCaptor<Kleidungsstueck> captor = ArgumentCaptor.forClass(Kleidungsstueck.class);
        verify(repository).save(captor.capture());
        assertEquals("Weißes T-Shirt", captor.getValue().get_name());
        assertEquals(MainCategory.OBERTEIL, captor.getValue().get_hauptkategorie());
    }

    @Test
    void createKleidungsstueck_mitTags_wirdKorrektGespeichert() {
        testStueck.addTag(Tags.ALLTAG);
        testStueck.addTag(Tags.FITNESS);

        service.createKleidungsstueck(testStueck);

        ArgumentCaptor<Kleidungsstueck> captor = ArgumentCaptor.forClass(Kleidungsstueck.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().get_tags().contains(Tags.ALLTAG));
        assertTrue(captor.getValue().get_tags().contains(Tags.FITNESS));
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

    @Test
    void getAll_gibtExaktDieListeAusRepositoryZurueck() {
        when(repository.findAll()).thenReturn(List.of(testStueck));

        List<Kleidungsstueck> ergebnis = service.getAll();

        assertTrue(ergebnis.contains(testStueck));
    }


    // delete()

    @Test
    void delete_ruftDeleteByIdAuf() {
        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void delete_mitVerschiedenenIds_ruftJeweiligeIdAuf() {
        service.delete(42L);
        verify(repository, times(1)).deleteById(42L);
        verify(repository, never()).deleteById(1L);
    }

    @Test
    void delete_nichtExistierendeId_wirftKeineException() {
        doNothing().when(repository).deleteById(999L);
        assertDoesNotThrow(() -> service.delete(999L));
    }
}