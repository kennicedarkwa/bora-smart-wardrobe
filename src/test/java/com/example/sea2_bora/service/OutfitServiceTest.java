package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Outfit;
import com.example.sea2_bora.implementation.Tags;
import com.example.sea2_bora.repository.OutfitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testklasse für OutfitService.
 *
 * Mockito ersetzt das OutfitRepository durch ein Fake-Objekt,
 * damit wir die Service-Logik isoliert und ohne Datenbankverbindung prüfen können.
 */
@ExtendWith(MockitoExtension.class)
class OutfitServiceTest {

    @Mock
    private OutfitRepository repository;

    @InjectMocks
    private OutfitService outfitService;

    private Outfit sampleOutfit;

    @BeforeEach
    void setUp() {
        sampleOutfit = new Outfit("Sommeroutfit", Tags.ALLTAG, new ArrayList<>(), List.of(Tags.ALLTAG));
    }

    // --- createOutfit() ---

    @Test
    void createOutfit_speichertUndGibtOutfitZurueck() {
        when(repository.save(sampleOutfit)).thenReturn(sampleOutfit);

        Outfit result = outfitService.createOutfit(sampleOutfit);

        assertNotNull(result);
        assertEquals("Sommeroutfit", result.getName());
        verify(repository, times(1)).save(sampleOutfit);
    }

    // --- getAllOutfits() ---

    @Test
    void getAllOutfits_gibtAlleZurueck() {
        Outfit zweites = new Outfit("Winteroutfit", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());
        when(repository.findAll()).thenReturn(List.of(sampleOutfit, zweites));

        List<Outfit> result = outfitService.getAllOutfits();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllOutfits_leereListe_gibtLeereListeZurueck() {
        when(repository.findAll()).thenReturn(List.of());

        List<Outfit> result = outfitService.getAllOutfits();

        assertTrue(result.isEmpty());
    }

    // --- getOutfitById() ---

    @Test
    void getOutfitById_vorhandeneId_gibtOptionalMitOutfitZurueck() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleOutfit));

        Optional<Outfit> result = outfitService.getOutfitById(1L);

        assertTrue(result.isPresent());
        assertEquals("Sommeroutfit", result.get().getName());
    }

    @Test
    void getOutfitById_nichtVorhandeneId_gibtLeereOptionalZurueck() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Outfit> result = outfitService.getOutfitById(99L);

        assertFalse(result.isPresent());
    }

    // --- deleteOutfit() ---

    @Test
    void deleteOutfit_vorhandeneId_gibtTrueZurueckUndLoescht() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean result = outfitService.deleteOutfit(1L);

        assertTrue(result);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOutfit_nichtVorhandeneId_gibtFalseZurueck() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean result = outfitService.deleteOutfit(99L);

        assertFalse(result);
        verify(repository, never()).deleteById(any());
    }

    // --- updateOutfit() ---

    @Test
    void updateOutfit_vorhandeneId_aktualisiertUndSpeichert() {
        Outfit neueDaten = new Outfit("Updatedoutfit", Tags.ELEGANT, new ArrayList<>(), List.of(Tags.ELEGANT));
        when(repository.findById(1L)).thenReturn(Optional.of(sampleOutfit));
        when(repository.save(sampleOutfit)).thenReturn(sampleOutfit);

        Outfit result = outfitService.updateOutfit(1L, neueDaten);

        assertNotNull(result);
        assertEquals("Updatedoutfit", result.getName());
        assertEquals(Tags.ELEGANT, result.getAnlass());
        verify(repository).save(sampleOutfit);
    }

    @Test
    void updateOutfit_nichtVorhandeneId_gibtNullZurueck() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Outfit result = outfitService.updateOutfit(99L, sampleOutfit);

        assertNull(result);
        verify(repository, never()).save(any());
    }
}