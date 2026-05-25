package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutfitTest {

    // --- outfitErstellen() ---

    @Test
    void outfitErstellen_setzt_alleFelder() {
        Kleidungsstueck shirt = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        List<Kleidungsstueck> teile = List.of(shirt);
        List<Tags> tags = List.of(Tags.ALLTAG);

        Outfit outfit = Outfit.outfitErstellen("Sommeroutfit", Tags.ALLTAG, teile, tags);

        assertEquals("Sommeroutfit", outfit.getName());
        assertEquals(Tags.ALLTAG, outfit.getAnlass());
        assertEquals(1, outfit.getBestandteile().size());
        assertEquals(1, outfit.getTags().size());
    }

    @Test
    void outfitErstellen_ohneTeile_liefertLeereListeAusBestandteilen() {
        Outfit outfit = Outfit.outfitErstellen("Leeres Outfit", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());

        assertNotNull(outfit.getBestandteile());
        assertTrue(outfit.getBestandteile().isEmpty());
    }

    // --- outfitBearbeiten() ---

    @Test
    void outfitBearbeiten_neuerName_wirdAktualisiert() {
        Outfit outfit = new Outfit("Alt", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());

        outfit.outfitBearbeiten("Neu", null, null, null);

        assertEquals("Neu", outfit.getName());
    }

    @Test
    void outfitBearbeiten_nullName_behaeltAltenNamen() {
        Outfit outfit = new Outfit("Original", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());

        outfit.outfitBearbeiten(null, null, null, null);

        assertEquals("Original", outfit.getName());
    }

    @Test
    void outfitBearbeiten_neuerAnlass_wirdAktualisiert() {
        Outfit outfit = new Outfit("Outfit", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());

        outfit.outfitBearbeiten(null, Tags.ELEGANT, null, null);

        assertEquals(Tags.ELEGANT, outfit.getAnlass());
    }

    @Test
    void outfitBearbeiten_neueBestandteile_werdenErsetzt() {
        Kleidungsstueck hose = new Kleidungsstueck("Hose", 3, false, MainCategory.UNTERTEIL, "");
        Outfit outfit = new Outfit("Outfit", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());

        outfit.outfitBearbeiten(null, null, List.of(hose), null);

        assertEquals(1, outfit.getBestandteile().size());
        assertEquals(hose, outfit.getBestandteile().get(0));
    }

    @Test
    void outfitBearbeiten_neueTags_werdenErsetzt() {
        Outfit outfit = new Outfit("Outfit", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());

        outfit.outfitBearbeiten(null, null, null, List.of(Tags.ELEGANT, Tags.ALLTAG));

        assertEquals(2, outfit.getTags().size());
        assertTrue(outfit.getTags().contains(Tags.ELEGANT));
    }

    // --- outfitLoeschen() ---

    @Test
    void outfitLoeschen_gibtImmerTrue() {
        Outfit outfit = new Outfit("Test", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());

        assertTrue(outfit.outfitLoeschen());
    }

    // --- Konstruktoren ---

    @Test
    void standardKonstruktor_gibtLeereListenZurueck() {
        Outfit outfit = new Outfit();

        assertNotNull(outfit.getBestandteile());
        assertNotNull(outfit.getTags());
    }
}