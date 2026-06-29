package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.implementation.Outfit;
import com.example.sea2_bora.implementation.Tags;
import com.example.sea2_bora.repository.BenutzerRepository;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
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

@ExtendWith(MockitoExtension.class)
class OutfitServiceTest {

    @Mock
    private OutfitRepository repository;

    @Mock
    private BenutzerRepository benutzerRepository; // 🌟 NEU: Wird für den Service-Konstruktor benötigt

    @Mock
    private KleidungsstueckRepository kleidungsstueckRepository; // 🌟 NEU: Wird für den Service-Konstruktor benötigt

    @InjectMocks
    private OutfitService outfitService;

    private Outfit sampleOutfit;
    private Benutzer sampleBenutzer;

    @BeforeEach
    void setUp() {
        sampleOutfit = new Outfit("Sommeroutfit", Tags.ALLTAG, new ArrayList<>(), List.of(Tags.ALLTAG));
        // Einen Test-Benutzer für die Mocks anlegen
        sampleBenutzer = new Benutzer("TestUser", "test@sea.de", "password123", "Hamburg");
    }

    // --- createOutfit() ---

    @Test
    void createOutfit_speichertUndGibtOutfitZurueck() {
        // 🌟 ANGEPASST: Der Service sucht zuerst nach dem Benutzer in der DB
        when(benutzerRepository.findById(1L)).thenReturn(Optional.of(sampleBenutzer));
        when(repository.save(sampleOutfit)).thenReturn(sampleOutfit);

        // 🌟 ANGEPASST: ID '1L' wird jetzt als zweiter Parameter mitgegeben
        Outfit result = outfitService.createOutfit(sampleOutfit, 1L);

        assertNotNull(result);
        assertEquals("Sommeroutfit", result.getName());
        assertEquals(sampleBenutzer, result.getBenutzer()); // Prüft, ob der User verknüpft wurde
        verify(repository, times(1)).save(sampleOutfit);
    }

    // --- getAllOutfits() -> jetzt getAllOutfitsForUser() ---

    @Test
    void getAllOutfits_gibtAlleZurueck() {
        Outfit zweites = new Outfit("Winteroutfit", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());
        // 🌟 ANGEPASST: Nutzt jetzt die neue DB-Abfragemethode nach Benutzer-ID
        when(repository.findByBenutzer_BenutzerId(1L)).thenReturn(List.of(sampleOutfit, zweites));

        // 🌟 ANGEPASST: Methodenname geändert und ID mitgegeben
        List<Outfit> result = outfitService.getAllOutfitsForUser(1L);

        assertEquals(2, result.size());
        verify(repository, times(1)).findByBenutzer_BenutzerId(1L);
    }

    @Test
    void getAllOutfits_leereListe_gibtLeereListeZurueck() {
        // 🌟 ANGEPASST: Nutzt findByBenutzer_BenutzerId
        when(repository.findByBenutzer_BenutzerId(1L)).thenReturn(List.of());

        // 🌟 ANGEPASST: Methodenname geändert und ID mitgegeben
        List<Outfit> result = outfitService.getAllOutfitsForUser(1L);

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