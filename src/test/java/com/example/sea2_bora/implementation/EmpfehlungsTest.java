package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmpfehlungTest {

    // --- Konstruktoren ---

    @Test
    void standardKonstruktor_gibtObjektOhneExceptionZurueck() {
        Empfehlung e = new Empfehlung();
        assertNotNull(e);
    }

    @Test
    void standardKonstruktor_kleidungsstueckeListeIstLeer() {
        Empfehlung e = new Empfehlung();
        assertNotNull(e.getKleidungsstuecke());
        assertTrue(e.getKleidungsstuecke().isEmpty());
    }

    @Test
    void konstruktorMitDatum_setzt_datum() {
        LocalDate heute = LocalDate.now();
        Empfehlung e = new Empfehlung(heute);
        assertEquals(heute, e.getDatum());
    }

    @Test
    void konstruktorMitDatum_kleidungsstueckeListeIstLeer() {
        Empfehlung e = new Empfehlung(LocalDate.now());
        assertNotNull(e.getKleidungsstuecke());
        assertTrue(e.getKleidungsstuecke().isEmpty());
    }

    // --- Datum ---

    @Test
    void setDatum_aktualisiertDatum() {
        Empfehlung e = new Empfehlung();
        LocalDate morgen = LocalDate.now().plusDays(1);
        e.setDatum(morgen);
        assertEquals(morgen, e.getDatum());
    }

    @Test
    void setDatum_null_wirdAkzeptiert() {
        Empfehlung e = new Empfehlung(LocalDate.now());
        e.setDatum(null);
        assertNull(e.getDatum());
    }

    // --- Kleidungsstücke ---

    @Test
    void getKleidungsstuecke_nachHinzufuegen_enthaeltItems() {
        Empfehlung e = new Empfehlung(LocalDate.now());
        Kleidungsstueck shirt = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");

        e.getKleidungsstuecke().add(shirt);

        assertEquals(1, e.getKleidungsstuecke().size());
        assertTrue(e.getKleidungsstuecke().contains(shirt));
    }

    @Test
    void getKleidungsstuecke_addAll_fuegtMehrereHinzu() {
        Empfehlung e = new Empfehlung(LocalDate.now());
        Kleidungsstueck shirt = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        Kleidungsstueck hose = new Kleidungsstueck("Hose", 3, false, MainCategory.UNTERTEIL, "");

        e.getKleidungsstuecke().addAll(List.of(shirt, hose));

        assertEquals(2, e.getKleidungsstuecke().size());
    }

    // --- ID (Lombok @Data generiert Setter) ---

    @Test
    void setId_setzt_id() {
        Empfehlung e = new Empfehlung();
        e.setId(42L);
        assertEquals(42L, e.getId());
    }

    @Test
    void getId_anfangsNull() {
        Empfehlung e = new Empfehlung();
        assertNull(e.getId());
    }
}