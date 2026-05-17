package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BenutzerTest {


    // validiereEinstellungen()

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


    // Konstruktor & Getter

    @Test
    void konstruktor_setztAlleFelder_korrekt() {
        Benutzer b = new Benutzer("Anna", "anna@example.com", "geheim42", "Berlin");

        assertEquals("Anna",             b.getBenutzername());
        assertEquals("anna@example.com", b.getEmail());
        assertEquals("geheim42",         b.getPasswort());
        assertEquals("Berlin",           b.getStandort());
    }

    @Test
    void konstruktor_bevorzugterStyle_istAnfangsLeer() {
        Benutzer b = new Benutzer("Tom", "tom@example.com", "pw", "München");
        assertNotNull(b.getBevorzugterStyle());
        assertTrue(b.getBevorzugterStyle().isEmpty());
    }

    @Test
    void konstruktor_benutzerIdIstAnfangsNull() {
        Benutzer b = new Benutzer("Lena", "lena@example.com", "pw", "Hamburg");
        assertNull(b.getBenutzerId());
    }


    // Setter

    @Test
    void setPasswort_aendertPasswort() {
        Benutzer b = new Benutzer("Max", "max@example.com", "alt", "Hamburg");
        b.setPasswort("neu");
        assertEquals("neu", b.getPasswort());
    }

    @Test
    void setStandort_aendertStandort() {
        Benutzer b = new Benutzer("Max", "max@example.com", "pw", "Hamburg");
        b.setStandort("Frankfurt");
        assertEquals("Frankfurt", b.getStandort());
    }

    @Test
    void setBenutzerId_setzt_id() {
        Benutzer b = new Benutzer("Max", "max@example.com", "pw", "Hamburg");
        b.setBenutzerId(42L);
        assertEquals(42L, b.getBenutzerId());
    }


    // Stub-Methoden (smoke tests – dürfen keine Exception werfen)

    @Test
    void outfitEmpfehlungAnfordern_gibtNullZurueck_ohneException() {
        Benutzer b = new Benutzer("Max", "max@example.com", "pw", "Hamburg");
        assertNull(b.outfitEmpfehlungAnfordern("Business", new java.util.Date()));
    }

    @Test
    void waescheErinnerungErhalten_wirftKeineException() {
        Benutzer b = new Benutzer("Max", "max@example.com", "pw", "Hamburg");
        assertDoesNotThrow(b::waescheErinnerungErhalten);
    }

    @Test
    void kleidungsstueckHinzufuegen_wirftKeineException() {
        Benutzer b = new Benutzer("Max", "max@example.com", "pw", "Hamburg");
        assertDoesNotThrow(() -> b.kleidungsstueckHinzufuegen(new Object()));
    }

    @Test
    void kleidungsstueckEntfernen_wirftKeineException() {
        Benutzer b = new Benutzer("Max", "max@example.com", "pw", "Hamburg");
        assertDoesNotThrow(() -> b.kleidungsstueckEntfernen(new Object()));
    }

}