package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.implementation.Waechekorb;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import com.example.sea2_bora.repository.WaechekorbRepository;
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
 * Testklasse für WaechekorbService.
 *
 * Mockito ersetzt WaechekorbRepository und KleidungsstueckRepository,
 * damit die Service-Logik isoliert und ohne Datenbankverbindung getestet wird.
 */
@ExtendWith(MockitoExtension.class)
class WaechekorbServiceTest {

    @Mock
    private WaechekorbRepository waechekorbRepository;

    @Mock
    private KleidungsstueckRepository kleidungsstueckRepository;

    @InjectMocks
    private WaechekorbService waechekorbService;

    private Waechekorb korb;
    private Kleidungsstueck shirt;

    @BeforeEach
    void setUp() {
        korb = new Waechekorb("Testkorb");
        shirt = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
    }

    // --- create() ---

    @Test
    void create_speichertNeuenKorbUndGibtIhnZurueck() {
        when(waechekorbRepository.save(any(Waechekorb.class))).thenReturn(korb);

        Waechekorb result = waechekorbService.create("Testkorb");

        assertNotNull(result);
        assertEquals("Testkorb", result.getName());
        verify(waechekorbRepository).save(any(Waechekorb.class));
    }

    @Test
    void create_ohneNamen_speichertMitNull() {
        Waechekorb ohneNamen = new Waechekorb(null);
        when(waechekorbRepository.save(any(Waechekorb.class))).thenReturn(ohneNamen);

        Waechekorb result = waechekorbService.create(null);

        assertNotNull(result);
        assertNull(result.getName());
    }

    // --- getAll() ---

    @Test
    void getAll_gibtAlleKoerbeZurueck() {
        when(waechekorbRepository.findAll()).thenReturn(List.of(korb));

        List<Waechekorb> result = waechekorbService.getAll();

        assertEquals(1, result.size());
    }

    // --- getById() ---

    @Test
    void getById_vorhandeneId_gibtOptionalMitKorbZurueck() {
        when(waechekorbRepository.findById(1L)).thenReturn(Optional.of(korb));

        Optional<Waechekorb> result = waechekorbService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("Testkorb", result.get().getName());
    }

    @Test
    void getById_nichtVorhandeneId_gibtLeereOptionalZurueck() {
        when(waechekorbRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Waechekorb> result = waechekorbService.getById(99L);

        assertFalse(result.isPresent());
    }

    // --- delete() ---

    @Test
    void delete_ruftDeleteByIdAuf() {
        waechekorbService.delete(1L);

        verify(waechekorbRepository).deleteById(1L);
    }

    // --- addItem() ---

    @Test
    void addItem_fuegtKleidungsstueckZumKorbHinzu() {
        when(waechekorbRepository.findById(1L)).thenReturn(Optional.of(korb));
        when(kleidungsstueckRepository.findById(10L)).thenReturn(Optional.of(shirt));
        when(waechekorbRepository.save(korb)).thenReturn(korb);

        Waechekorb result = waechekorbService.addItem(1L, 10L);

        assertTrue(result.getItems().contains(shirt));
        verify(waechekorbRepository).save(korb);
    }

    @Test
    void addItem_korbNichtGefunden_wirftException() {
        when(waechekorbRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> waechekorbService.addItem(99L, 1L));
    }

    @Test
    void addItem_itemNichtGefunden_wirftException() {
        when(waechekorbRepository.findById(1L)).thenReturn(Optional.of(korb));
        when(kleidungsstueckRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> waechekorbService.addItem(1L, 99L));
    }

    // --- addItems() ---

    @Test
    void addItems_fuegtMehrereItemsHinzu() {
        Kleidungsstueck hose = new Kleidungsstueck("Hose", 3, false, MainCategory.UNTERTEIL, "");
        when(waechekorbRepository.findById(1L)).thenReturn(Optional.of(korb));
        when(kleidungsstueckRepository.findById(10L)).thenReturn(Optional.of(shirt));
        when(kleidungsstueckRepository.findById(20L)).thenReturn(Optional.of(hose));
        when(waechekorbRepository.save(korb)).thenReturn(korb);

        Waechekorb result = waechekorbService.addItems(1L, List.of(10L, 20L));

        assertEquals(2, result.getItems().size());
    }

    // --- removeItem() ---

    @Test
    void removeItem_entferntItemAusKorb() {
        korb.addItem(shirt);
        when(waechekorbRepository.findById(1L)).thenReturn(Optional.of(korb));
        when(kleidungsstueckRepository.findById(10L)).thenReturn(Optional.of(shirt));
        when(waechekorbRepository.save(korb)).thenReturn(korb);

        Waechekorb result = waechekorbService.removeItem(1L, 10L);

        assertFalse(result.getItems().contains(shirt));
    }

    // --- clear() ---

    @Test
    void clear_leertDenKorb() {
        korb.addItem(shirt);
        when(waechekorbRepository.findById(1L)).thenReturn(Optional.of(korb));
        when(waechekorbRepository.save(korb)).thenReturn(korb);

        Waechekorb result = waechekorbService.clear(1L);

        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void clear_korbNichtGefunden_wirftException() {
        when(waechekorbRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> waechekorbService.clear(99L));
    }

    // --- addItemsToDefaultBasket() ---

    @Test
    void addItemsToDefaultBasket_verwendetVorhandenenKorb() {
        when(waechekorbRepository.findAll()).thenReturn(List.of(korb));
        when(kleidungsstueckRepository.findById(10L)).thenReturn(Optional.of(shirt));
        when(waechekorbRepository.save(korb)).thenReturn(korb);

        Waechekorb result = waechekorbService.addItemsToDefaultBasket(List.of(10L));

        assertTrue(result.getItems().contains(shirt));
    }

    @Test
    void addItemsToDefaultBasket_keinKorbVorhanden_erstelltDefault() {
        when(waechekorbRepository.findAll()).thenReturn(List.of());
        Waechekorb defaultKorb = new Waechekorb("Default");
        when(waechekorbRepository.save(any(Waechekorb.class))).thenReturn(defaultKorb);
        when(kleidungsstueckRepository.findById(10L)).thenReturn(Optional.of(shirt));

        Waechekorb result = waechekorbService.addItemsToDefaultBasket(List.of(10L));

        assertNotNull(result);
    }
}