package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WaechekorbTest {

    private Waechekorb korb;
    private Kleidungsstueck shirt;
    private Kleidungsstueck hose;

    @BeforeEach
    void setUp() {
        korb = new Waechekorb("Testkorb");
        shirt = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        hose = new Kleidungsstueck("Hose", 3, false, MainCategory.UNTERTEIL, "");
    }

    // --- Konstruktor ---

    @Test
    void konstruktor_setzt_name() {
        assertEquals("Testkorb", korb.getName());
    }

    @Test
    void standardKonstruktor_itemsListeIstLeer() {
        Waechekorb leererKorb = new Waechekorb();
        assertNotNull(leererKorb.getItems());
        assertTrue(leererKorb.getItems().isEmpty());
    }

    // --- addItem() ---

    @Test
    void addItem_fuegtKleidungsstueckHinzu() {
        korb.addItem(shirt);

        assertEquals(1, korb.getItems().size());
        assertTrue(korb.getItems().contains(shirt));
    }

    @Test
    void addItem_doppeltesItem_wirdNichtDupliziert() {
        korb.addItem(shirt);
        korb.addItem(shirt);

        assertEquals(1, korb.getItems().size());
    }

    @Test
    void addItem_nullWirdIgnoriert() {
        korb.addItem(null);

        assertTrue(korb.getItems().isEmpty());
    }

    @Test
    void addItem_mehrereVerschiedeneItems() {
        korb.addItem(shirt);
        korb.addItem(hose);

        assertEquals(2, korb.getItems().size());
    }

    // --- addItems() ---

    @Test
    void addItems_fuegt_allHinzu() {
        korb.addItems(List.of(shirt, hose));

        assertEquals(2, korb.getItems().size());
    }

    @Test
    void addItems_duplikateWerdenNichtHinzugefuegt() {
        korb.addItem(shirt);
        korb.addItems(List.of(shirt, hose));

        assertEquals(2, korb.getItems().size());
    }

    // --- removeItem() ---

    @Test
    void removeItem_entferntItem() {
        korb.addItem(shirt);
        korb.addItem(hose);

        korb.removeItem(shirt);

        assertFalse(korb.getItems().contains(shirt));
        assertEquals(1, korb.getItems().size());
    }

    @Test
    void removeItem_nichtVorhandenes_aendertNichts() {
        korb.addItem(shirt);

        korb.removeItem(hose); // hose war nie drin

        assertEquals(1, korb.getItems().size());
    }

    // --- clear() ---

    @Test
    void clear_entferntAlleItems() {
        korb.addItem(shirt);
        korb.addItem(hose);

        korb.clear();

        assertTrue(korb.getItems().isEmpty());
    }

    @Test
    void clear_aufLeeremKorb_bleibtLeer() {
        korb.clear();

        assertTrue(korb.getItems().isEmpty());
    }
}