package com.example.sea2_bora.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EinstellungenTest {

    private Einstellungen einstellungen;

    @BeforeEach
    void setUp() {
        einstellungen = new Einstellungen("Hamburg");
    }

    // Konstruktor

    @Test
    void konstruktor_setztStandardStandort() {
        assertEquals("Hamburg", einstellungen.getStandardStandort());
    }

    @Test
    void konstruktor_ruftSetzeStandardwerteAuf() {
        // Nach Konstruktor: Standardwerte gesetzt
        assertFalse(einstellungen.isEinfacheAnsichtAktiv());
        assertTrue(einstellungen.isPushBenachrichtigungAktiv());
        assertFalse(einstellungen.isWaescheErinnerungAktiv());
        assertTrue(einstellungen.isEmpfehlungAktiv());
        assertNotNull(einstellungen.getErinnerungsZeitpunkt());
    }

    // setzeStandardwerte()

    @Test
    void setzeStandardwerte_setzt_einfacheAnsicht_aufFalse() {
        einstellungen.setEinfacheAnsichtAktiv(true);
        einstellungen.setzeStandardwerte();
        assertFalse(einstellungen.isEinfacheAnsichtAktiv());
    }

    @Test
    void setzeStandardwerte_setzt_pushBenachrichtigung_aufTrue() {
        einstellungen.setPushBenachrichtigungAktiv(false);
        einstellungen.setzeStandardwerte();
        assertTrue(einstellungen.isPushBenachrichtigungAktiv());
    }

    @Test
    void setzeStandardwerte_setzt_waescheErinnerung_aufFalse() {
        einstellungen.setWaescheErinnerungAktiv(true);
        einstellungen.setzeStandardwerte();
        assertFalse(einstellungen.isWaescheErinnerungAktiv());
    }

    @Test
    void setzeStandardwerte_setzt_empfehlung_aufTrue() {
        einstellungen.setEmpfehlungAktiv(false);
        einstellungen.setzeStandardwerte();
        assertTrue(einstellungen.isEmpfehlungAktiv());
    }

    @Test
    void setzeStandardwerte_setzt_erinnerungsZeitpunkt_nichtNull() {
        einstellungen.setzeStandardwerte();
        assertNotNull(einstellungen.getErinnerungsZeitpunkt());
    }

    // aktiviereErinnerung()

    @Test
    void aktiviereErinnerung_setzt_waescheErinnerung_aufTrue() {
        assertFalse(einstellungen.isWaescheErinnerungAktiv());
        einstellungen.aktiviereErinnerung();
        assertTrue(einstellungen.isWaescheErinnerungAktiv());
    }

    @Test
    void aktiviereErinnerung_zweiMalAufrufen_bleibtTrue() {
        einstellungen.aktiviereErinnerung();
        einstellungen.aktiviereErinnerung();
        assertTrue(einstellungen.isWaescheErinnerungAktiv());
    }

    // deaktiviereErinnerung()

    @Test
    void deaktiviereErinnerung_setzt_waescheErinnerung_aufFalse() {
        einstellungen.aktiviereErinnerung();
        assertTrue(einstellungen.isWaescheErinnerungAktiv());
        einstellungen.deaktiviereErinnerung();
        assertFalse(einstellungen.isWaescheErinnerungAktiv());
    }

    @Test
    void deaktiviereErinnerung_ohnVorherigAktivieren_bleibtFalse() {
        einstellungen.deaktiviereErinnerung();
        assertFalse(einstellungen.isWaescheErinnerungAktiv());
    }

    // ladeEinstellungen()

    @Test
    void ladeEinstellungen_gibtSichSelbstZurueck() {
        assertSame(einstellungen, einstellungen.ladeEinstellungen());
    }

    // Setter / Getter

    @Test
    void setStandardStandort_aendertStandort() {
        einstellungen.setStandardStandort("Berlin");
        assertEquals("Berlin", einstellungen.getStandardStandort());
    }

    @Test
    void setEinstellungsID_setzt_id() {
        einstellungen.setEinstellungsID(42L);
        assertEquals(42L, einstellungen.getEinstellungsID());
    }
}