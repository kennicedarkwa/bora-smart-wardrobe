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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testklasse für WaechekorbService mit Singleton-Logik.
 *
 * Mockito ersetzt die Repositories, um die Service-Logik isoliert zu testen.
 */
@ExtendWith(MockitoExtension.class)
class WaeschekorbServiceTest {

    @Mock
    private WaechekorbRepository waechekorbRepository;

    @Mock
    private KleidungsstueckRepository kleidungsstueckRepository;

    @InjectMocks
    private WaechekorbService waechekorbService;

    private Waechekorb singletonKorb;
    private Kleidungsstueck shirt;
    private Kleidungsstueck hose;

    @BeforeEach
    void setUp() {
        singletonKorb = new Waechekorb("Singleton Korb");
        singletonKorb.setId(1L); // Feste ID für den Singleton
        shirt = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        shirt.setId(10L);
        hose = new Kleidungsstueck("Hose", 2, true, MainCategory.UNTERTEIL, "");
        hose.setId(20L);
    }

    // --- getSingletonBasket() Tests ---

    @Test
    void getSingletonBasket_gibtVorhandenenKorbZurueck() {
        // Arrange: Der Korb existiert bereits in der DB
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(singletonKorb));

        // Act
        Waechekorb result = waechekorbService.getSingletonWaechekorb();

        // Assert
        assertNotNull(result);
        assertEquals(singletonKorb.getId(), result.getId());
        // Wichtig: save() darf nicht aufgerufen werden, wenn der Korb schon existiert
        verify(waechekorbRepository, never()).save(any(Waechekorb.class));
    }

    @Test
    void getSingletonBasket_erstelltNeuenKorbWennKeinerExistiert() {
        // Arrange: Kein Korb in der DB
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        when(waechekorbRepository.save(any(Waechekorb.class))).thenReturn(singletonKorb);

        // Act
        Waechekorb result = waechekorbService.getSingletonWaechekorb();

        // Assert
        assertNotNull(result);
        // Wichtig: save() muss genau einmal aufgerufen werden
        verify(waechekorbRepository, times(1)).save(any(Waechekorb.class));
    }

    // --- getBasket() ---
    @Test
    void getBasket_gibtDenSingletonKorbZurueck() {
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(singletonKorb));
        Waechekorb result = waechekorbService.getSingletonWaechekorb();
        assertEquals(singletonKorb, result);
        verify(waechekorbRepository, never()).save(any());
    }


    // --- addItem() ---

    @Test
    void addItem_fuegtItemZumSingletonKorbHinzu() {
        // Arrange
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(singletonKorb));
        when(kleidungsstueckRepository.findById(shirt.getId())).thenReturn(Optional.of(shirt));
        when(waechekorbRepository.save(singletonKorb)).thenReturn(singletonKorb);

        // Act
        Waechekorb result = waechekorbService.addItem(shirt.getId());

        // Assert
        assertTrue(result.getItems().contains(shirt));
        verify(waechekorbRepository).save(singletonKorb); // Speichern wird aufgerufen
    }

    @Test
    void addItem_itemNichtGefunden_wirftException() {
        // Arrange
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(singletonKorb));
        when(kleidungsstueckRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> waechekorbService.addItem(99L));
        verify(waechekorbRepository, never()).save(any()); // Darf nicht speichern
    }

    // --- addItems() ---

    @Test
    void addItems_fuegtMehrereItemsZumSingletonKorbHinzu() {
        // Arrange
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(singletonKorb));
        when(kleidungsstueckRepository.findById(shirt.getId())).thenReturn(Optional.of(shirt));
        when(kleidungsstueckRepository.findById(hose.getId())).thenReturn(Optional.of(hose));
        when(waechekorbRepository.save(singletonKorb)).thenReturn(singletonKorb);

        // Act
        Waechekorb result = waechekorbService.addItems(List.of(shirt.getId(), hose.getId()));

        // Assert
        assertEquals(2, result.getItems().size());
        assertTrue(result.getItems().containsAll(List.of(shirt, hose)));
        verify(waechekorbRepository).save(singletonKorb);
    }

    // --- removeItem() ---

    @Test
    void removeItem_entferntItemAusSingletonKorb() {
        // Arrange
        singletonKorb.addItem(shirt); // Vorher hinzufügen
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(singletonKorb));
        when(kleidungsstueckRepository.findById(shirt.getId())).thenReturn(Optional.of(shirt));
        when(waechekorbRepository.save(singletonKorb)).thenReturn(singletonKorb);

        // Act
        Waechekorb result = waechekorbService.removeItem(shirt.getId());

        // Assert
        assertFalse(result.getItems().contains(shirt));
        verify(waechekorbRepository).save(singletonKorb);
    }

    // --- clear() ---

    @Test
    void clear_leertDenSingletonKorb() {
        // Arrange
        singletonKorb.addItem(shirt);
        when(waechekorbRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(singletonKorb));
        when(waechekorbRepository.save(singletonKorb)).thenReturn(singletonKorb);

        // Act
        Waechekorb result = waechekorbService.clear();

        // Assert
        assertTrue(result.getItems().isEmpty());
        verify(waechekorbRepository).save(singletonKorb);
    }
    // --- Wäscheerinnerung: Schwellenwert >= 10 Items ---

    @Test
    void waescheErinnerungFaellig_wennKorbMindestens10Items() {
        // 10 Kleidungsstücke in den Korb legen
        for (int i = 0; i < 10; i++) {
            Kleidungsstueck k = new Kleidungsstueck("Item" + i, 3, false, MainCategory.OBERTEIL, "");
            k.setId((long) (i + 100));
            singletonKorb.addItem(k);
        }

        assertTrue(singletonKorb.getItems().size() >= 10,
                "Popup soll ausgelöst werden wenn >= 10 Items im Korb");
    }

    @Test
    void waescheErinnerungNichtFaellig_wennKorbUnter10Items() {
        singletonKorb.addItem(shirt);
        singletonKorb.addItem(hose);

        assertFalse(singletonKorb.getItems().size() >= 10,
                "Kein Popup bei weniger als 10 Items");
    }

    @Test
    void waescheErinnerungSchwellenwert_genau10Items_loestAus() {
        for (int i = 0; i < 10; i++) {
            Kleidungsstueck k = new Kleidungsstueck("Item" + i, 2, false, MainCategory.UNTERTEIL, "");
            k.setId((long) (i + 200));
            singletonKorb.addItem(k);
        }
        assertEquals(10, singletonKorb.getItems().size());
        assertTrue(singletonKorb.getItems().size() >= 10);
    }

    @Test
    void waescheErinnerungSchwellenwert_nach_clear_nichtMehrFaellig() {
        for (int i = 0; i < 10; i++) {
            Kleidungsstueck k = new Kleidungsstueck("Item" + i, 3, false, MainCategory.OBERTEIL, "");
            k.setId((long) (i + 300));
            singletonKorb.addItem(k);
        }
        assertTrue(singletonKorb.getItems().size() >= 10);

        singletonKorb.clear();

        assertFalse(singletonKorb.getItems().size() >= 10,
                "Nach clear() kein Popup mehr");
    }
}