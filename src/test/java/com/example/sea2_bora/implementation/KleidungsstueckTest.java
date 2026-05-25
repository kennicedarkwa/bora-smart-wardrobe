package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KleidungsstueckTest {

    // addTag()
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
    void addTag_mehrereVerschiedeneTagsWerdenAlleHinzugefuegt() {
        Kleidungsstueck stueck = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        stueck.addTag(Tags.ALLTAG);
        stueck.addTag(Tags.FITNESS);
        stueck.addTag(Tags.ELEGANT);
        assertEquals(3, stueck.get_tags().size());
    }


    // deleteTag()

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
        stueck.deleteTag(Tags.ELEGANT);

        assertEquals(1, stueck.get_tags().size());
    }


    // Konstruktor & Getter

    @Test
    void konstruktor_setztAlleFelder_korrekt() {
        Kleidungsstueck stueck = new Kleidungsstueck("Jeans", 2, true, MainCategory.UNTERTEIL, "http://bild.de/jeans.jpg");

        assertEquals("Jeans",                      stueck.get_name());
        assertEquals(2,                            stueck.get_temperaturIndex());
        assertTrue(stueck.is_istFuerRegenGeeignet());
        assertEquals(MainCategory.UNTERTEIL,       stueck.get_hauptkategorie());
        assertEquals("http://bild.de/jeans.jpg",  stueck.get_bildURL());
    }

    @Test
    void konstruktor_tags_istAnfangsLeer() {
        Kleidungsstueck stueck = new Kleidungsstueck("Hut", 1, false, MainCategory.KOPFBEDECKUNG, "");
        assertNotNull(stueck.get_tags());
        assertTrue(stueck.get_tags().isEmpty());
    }


    @Test
    void konstruktor_regentauglichFalse_wirdKorrektGesetzt() {
        Kleidungsstueck stueck = new Kleidungsstueck("Sandalen", 5, false, MainCategory.SCHUHE, "");
        assertFalse(stueck.is_istFuerRegenGeeignet());
    }

    @Test
    void konstruktor_alleHauptkategorienMoeglich() {
        for (MainCategory kat : MainCategory.values()) {
            Kleidungsstueck stueck = new Kleidungsstueck("Item", 3, false, kat, "");
            assertEquals(kat, stueck.get_hauptkategorie());
        }
    }
}