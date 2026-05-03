package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KleidungsstueckTest {

    @Test
    void addTag_fuegtTagHinzu() {
        Kleidungsstueck stueck = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        stueck.addTag(Tags.ALLTAG);

        assertTrue(stueck.get_tags().contains(Tags.ALLTAG));
    }

    @Test
    void addTag_doppelterTag_wirdNichtDupliziert() {
        Kleidungsstueck stueck = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        stueck.addTag(Tags.ALLTAG);
        stueck.addTag(Tags.ALLTAG);

        assertEquals(1, stueck.get_tags().size());
    }

    @Test
    void deleteTag_entferntVorhandenenTag() {
        Kleidungsstueck stueck = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        stueck.addTag(Tags.FITNESS);
        stueck.deleteTag(Tags.FITNESS);

        assertFalse(stueck.get_tags().contains(Tags.FITNESS));
    }

    @Test
    void deleteTag_nichtVorhandenerTag_keineAenderung() {
        Kleidungsstueck stueck = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        stueck.addTag(Tags.ALLTAG);
        stueck.deleteTag(Tags.FORMAL);

        assertEquals(1, stueck.get_tags().size());
    }
}