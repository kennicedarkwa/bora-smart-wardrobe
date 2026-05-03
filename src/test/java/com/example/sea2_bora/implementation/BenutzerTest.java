package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BenutzerTest {

    @Test
    void validiereEinstellungen_gueltigeEmail_undPasswort_gibtTrue() {
        Benutzer b = new Benutzer("Max", "max@example.com", "passwort123", "Hamburg");

        assertTrue(b.validiereEinstellungen());
    }

    @Test
    void validiereEinstellungen_emailNull_gibtFalse() {
        Benutzer b = new Benutzer("Max", null, "passwort123", "Hamburg");

        assertFalse(b.validiereEinstellungen());
    }

    @Test
    void validiereEinstellungen_emailLeer_gibtFalse() {
        Benutzer b = new Benutzer("Max", "   ", "passwort123", "Hamburg");

        assertFalse(b.validiereEinstellungen());
    }

    @Test
    void validiereEinstellungen_passwortNull_gibtFalse() {
        Benutzer b = new Benutzer("Max", "max@example.com", null, "Hamburg");

        assertFalse(b.validiereEinstellungen());
    }

    @Test
    void validiereEinstellungen_passwortLeer_gibtFalse() {
        Benutzer b = new Benutzer("Max", "max@example.com", "", "Hamburg");

        assertFalse(b.validiereEinstellungen());
    }
}