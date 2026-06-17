package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.repository.BenutzerRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * NEU (v11): Tests für passwortPruefen() und die überarbeitete
 * BenutzerService-Logik (HttpSession-Injektion, passwortAktualisieren,
 * datenAktualisieren, accountLoeschen).
 *
 * Die alten Methoden aenderePasswort() und getAlleBenutzer() existieren
 * in v11 nicht mehr → betroffene Tests dort entfernen.
 */
@ExtendWith(MockitoExtension.class)
class BenutzerServicePasswortTest {

    @Mock
    private BenutzerRepository repository;

    @Mock
    private HttpSession session;

    private PasswordEncoder realBcrypt = new BCryptPasswordEncoder();

    private BenutzerService service;

    private Benutzer testBenutzer;

    @BeforeEach
    void setUp() {
        service = new BenutzerService(repository, realBcrypt, session);
        testBenutzer = new Benutzer("MaxMuster", "max@example.com", "klartext", "Hamburg");
        testBenutzer.setBenutzerId(1L);
    }

    // -----------------------------------------------------------------------
    // passwortPruefen() – BCrypt-Pfad
    // -----------------------------------------------------------------------

    @Test
    void passwortPruefen_bcryptHash_korrektesPW_gibtTrue() {
        String hash = realBcrypt.encode("geheimesPasswort");
        assertTrue(service.passwortPruefen("geheimesPasswort", hash));
    }

    @Test
    void passwortPruefen_bcryptHash_falschesPW_gibtFalse() {
        String hash = realBcrypt.encode("geheimesPasswort");
        assertFalse(service.passwortPruefen("falsesPasswort", hash));
    }

    // -----------------------------------------------------------------------
    // passwortPruefen() – Klartext-Pfad (Testdaten ohne BCrypt-Präfix)
    // -----------------------------------------------------------------------

    @Test
    void passwortPruefen_klartext_korrektesPW_gibtTrue() {
        assertTrue(service.passwortPruefen("klartext", "klartext"));
    }

    @Test
    void passwortPruefen_klartext_falschesPW_gibtFalse() {
        assertFalse(service.passwortPruefen("falsch", "klartext"));
    }

    @Test
    void passwortPruefen_leeresPW_gegenKlartext_gibtFalse() {
        assertFalse(service.passwortPruefen("", "klartext"));
    }

    @Test
    void passwortPruefen_bcryptPraefix_wirdAlsBcryptBehandelt() {
        // Passwort das zufällig mit "$2a$" anfängt, aber kein echter Hash ist
        // → BCrypt.matches() wirft keine Exception, gibt false zurück
        assertFalse(service.passwortPruefen("test", "$2a$10$ungueltigerHash"));
    }

    // -----------------------------------------------------------------------
    // passwortAktualisieren() – speichert BCrypt-Hash
    // -----------------------------------------------------------------------

    @Test
    void passwortAktualisieren_speichertBcryptHash() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));

        service.passwortAktualisieren(1L, "neuesPasswort123");

        // Gespeichertes Passwort muss BCrypt-Hash sein
        assertTrue(testBenutzer.getPasswort().startsWith("$2a$"));
        verify(repository, times(1)).save(testBenutzer);
    }

    @Test
    void passwortAktualisieren_neuerHashFunktioniert_mitPastwortPruefen() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));

        service.passwortAktualisieren(1L, "sicheresPasswort!");

        // Neuer Hash muss über passwortPruefen validierbar sein
        assertTrue(service.passwortPruefen("sicheresPasswort!", testBenutzer.getPasswort()));
    }

    // -----------------------------------------------------------------------
    // datenAktualisieren()
    // -----------------------------------------------------------------------

    @Test
    void datenAktualisieren_aendertBenutzernameUndEmail() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));

        boolean ergebnis = service.datenAktualisieren(1L, "NeuerName", "neu@example.com");

        assertTrue(ergebnis);
        assertEquals("NeuerName", testBenutzer.getBenutzername());
        assertEquals("neu@example.com", testBenutzer.getEmail());
        verify(repository, times(1)).save(testBenutzer);
    }

    @Test
    void datenAktualisieren_nichtExistierenderBenutzer_gibtFalse() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(service.datenAktualisieren(99L, "X", "x@x.de"));
        verify(repository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // accountLoeschen()
    // -----------------------------------------------------------------------

    @Test
    void accountLoeschen_loeschtBenutzerAusRepository() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));

        service.accountLoeschen(1L);

        verify(repository, times(1)).delete(testBenutzer);
    }

    @Test
    void accountLoeschen_leertKleiderschrankUndOutfits() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));

        service.accountLoeschen(1L);

        assertTrue(testBenutzer.getKleiderschrank().isEmpty());
        assertTrue(testBenutzer.getOutfits().isEmpty());
    }

    @Test
    void accountLoeschen_nichtExistierenderBenutzer_wirftKeineException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.accountLoeschen(99L));
        verify(repository, never()).delete(any());
    }

    // -----------------------------------------------------------------------
    // login() mit Session (v11: Session wird per Injektion verwaltet)
    // -----------------------------------------------------------------------

    @Test
    void login_korrekteDaten_setztSessionAttribut() {
        String hash = realBcrypt.encode("passwort");
        testBenutzer.setPasswort(hash);
        when(repository.findByEmail("max@example.com")).thenReturn(Optional.of(testBenutzer));

        boolean ergebnis = service.login("max@example.com", "passwort");

        assertTrue(ergebnis);
        verify(session, times(1)).setAttribute("benutzerId", testBenutzer.getBenutzerId());
    }

    @Test
    void login_falschePW_setztKeineSession() {
        String hash = realBcrypt.encode("richtig");
        testBenutzer.setPasswort(hash);
        when(repository.findByEmail("max@example.com")).thenReturn(Optional.of(testBenutzer));

        boolean ergebnis = service.login("max@example.com", "falsch");

        assertFalse(ergebnis);
        verify(session, never()).setAttribute(anyString(), any());
    }
}