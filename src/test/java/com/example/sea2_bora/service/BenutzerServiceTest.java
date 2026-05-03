/**
 * Testklasse für BenutzerService.
 *
 * Warum Mockito?
 * BenutzerService hängt vom BenutzerRepository ab, das normalerweise mit einer
 * echten Datenbank kommuniziert. Im Unit-Test wollen wir aber nur die Logik
 * im Service prüfen – nicht die Datenbank. Mockito ersetzt das Repository durch
 * ein "Fake-Objekt", das kontrolliert antwortet, ohne echte DB-Verbindung.
 *
 */
package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.repository.BenutzerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BenutzerServiceTest {

    @Mock
    private BenutzerRepository repository;

    @InjectMocks
    private BenutzerService service;

    private Benutzer testBenutzer;

    @BeforeEach
    void setUp() {
        testBenutzer = new Benutzer("MaxMuster", "max@example.com", "passwort123", "Hamburg");
        testBenutzer.setBenutzerId(1L);
    }

    // registrieren()

    @Test
    void registrieren_neueEmail_gibtTrue() {
        when(repository.findByEmail("max@example.com")).thenReturn(Optional.empty());

        boolean ergebnis = service.registrieren("MaxMuster", "max@example.com", "passwort123");

        assertTrue(ergebnis);
        verify(repository, times(1)).save(any(Benutzer.class));
    }

    @Test
    void registrieren_emailBereitsVergeben_gibtFalse() {
        when(repository.findByEmail("max@example.com")).thenReturn(Optional.of(testBenutzer));

        boolean ergebnis = service.registrieren("MaxMuster", "max@example.com", "passwort123");

        assertFalse(ergebnis);
        verify(repository, never()).save(any());
    }

    // login()

    @Test
    void login_benutzerGefunden_gibtTrue() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));

        assertTrue(service.login(1L));
        assertEquals(testBenutzer, service.getAktuellerNutzer());
    }

    @Test
    void login_benutzerNichtGefunden_gibtFalse() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(service.login(99L));
        assertNull(service.getAktuellerNutzer());
    }

    // logout()

    @Test
    void logout_setzt_aktuellerBenutzer_aufNull() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));
        service.login(1L);

        service.logout();

        assertNull(service.getAktuellerNutzer());
    }

    // aenderePasswort()

    @Test
    void aenderePasswort_korrektesDaten_gibtTrue() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));
        service.login(1L);

        boolean ergebnis = service.aenderePasswort("passwort123", "neuesPasswort!");

        assertTrue(ergebnis);
        assertEquals("neuesPasswort!", service.getAktuellerNutzer().getPasswort());
        verify(repository, times(1)).save(testBenutzer);
    }

    @Test
    void aenderePasswort_falschesAltesPasswort_gibtFalse() {
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));
        service.login(1L);

        assertFalse(service.aenderePasswort("falsch", "neuesPasswort!"));
        verify(repository, never()).save(any());
    }

    @Test
    void aenderePasswort_keinBenutzerEingeloggt_gibtFalse() {
        assertFalse(service.aenderePasswort("passwort123", "neuesPasswort!"));
        verify(repository, never()).save(any());
    }
}