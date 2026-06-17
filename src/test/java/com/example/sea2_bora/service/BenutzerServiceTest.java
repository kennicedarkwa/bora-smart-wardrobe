/**
 * Testklasse für BenutzerService.
 *
 * Warum Mockito?
 * BenutzerService hängt vom BenutzerRepository ab, das normalerweise mit einer
 * echten Datenbank kommuniziert. Im Unit-Test wollen wir aber nur die Logik
 * im Service prüfen – nicht die Datenbank. Mockito ersetzt das Repository durch
 * ein "Fake-Objekt", das kontrolliert antwortet, ohne echte DB-Verbindung.
 *
 * GEÄNDERT (v11):
 * - BenutzerService braucht jetzt 3 Konstruktor-Args: repository, passwordEncoder, session
 * - @Mock HttpSession session ergänzt
 * - aenderePasswort() existiert nicht mehr → Tests entfernt
 * - getAlleBenutzer() existiert nicht mehr → Tests entfernt
 * - getAktuellerNutzer() liest jetzt aus der Session → Mocks angepasst
 */
package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.repository.BenutzerRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BenutzerServiceTest {

    @Mock
    private BenutzerRepository repository;

    // NEU (v11): Session wird jetzt in den Service injiziert
    @Mock
    private HttpSession session;

    // Kein @InjectMocks mehr, da wir den Konstruktor manuell aufrufen
    private BenutzerService service;

    // Einfacher Fake-Encoder: speichert Klartext, damit Tests lesbar bleiben
    private PasswordEncoder passwordEncoder = new PasswordEncoder() {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword.toString().equals(encodedPassword);
        }
    };

    private Benutzer testBenutzer;

    @BeforeEach
    void setUp() {
        // NEU (v11): 3-Argument-Konstruktor
        service = new BenutzerService(repository, passwordEncoder, session);

        testBenutzer = new Benutzer("MaxMuster", "max@example.com", "passwort123", "Hamburg");
        testBenutzer.setBenutzerId(1L);
    }

    // -----------------------------------------------------------------------
    // registrieren()
    // -----------------------------------------------------------------------

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

    @Test
    void registrieren_speichertBenutzerMitKorrektemNamen() {
        when(repository.findByEmail("neu@example.com")).thenReturn(Optional.empty());

        service.registrieren("NeuerNutzer", "neu@example.com", "pw");

        ArgumentCaptor<Benutzer> captor = ArgumentCaptor.forClass(Benutzer.class);
        verify(repository).save(captor.capture());
        assertEquals("NeuerNutzer", captor.getValue().getBenutzername());
        assertEquals("neu@example.com", captor.getValue().getEmail());
    }

    // -----------------------------------------------------------------------
    // login()
    // -----------------------------------------------------------------------

    @Test
    void login_benutzerGefunden_gibtTrue() {
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testBenutzer));

        assertTrue(service.login("test@example.com", "passwort123"));
        // Session-Attribut muss gesetzt worden sein
        verify(session).setAttribute("benutzerId", testBenutzer.getBenutzerId());
    }

    @Test
    void login_benutzerNichtGefunden_gibtFalse() {
        when(repository.findByEmail("falsch@example.com")).thenReturn(Optional.empty());

        assertFalse(service.login("falsch@example.com", "irgendwas"));
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void login_falschesPasswort_gibtFalse() {
        when(repository.findByEmail("max@example.com")).thenReturn(Optional.of(testBenutzer));

        assertFalse(service.login("max@example.com", "falsch"));
        verify(session, never()).setAttribute(anyString(), any());
    }

    // -----------------------------------------------------------------------
    // logout()
    // -----------------------------------------------------------------------

    @Test
    void logout_invalidiertSession() {
        service.logout();
        verify(session, times(1)).invalidate();
    }

    @Test
    void logout_ohneVorherinLogin_wirftKeineException() {
        assertDoesNotThrow(() -> service.logout());
    }

    // -----------------------------------------------------------------------
    // getAktuellerNutzer()
    // NEU (v11): liest benutzerId aus Session, dann Datenbankabfrage
    // -----------------------------------------------------------------------

    @Test
    void getAktuellerNutzer_sessionHatId_gibtBenutzerZurueck() {
        when(session.getAttribute("benutzerId")).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(testBenutzer));

        Benutzer result = service.getAktuellerNutzer();

        assertEquals(testBenutzer, result);
    }

    @Test
    void getAktuellerNutzer_sessionLeer_gibtNullZurueck() {
        when(session.getAttribute("benutzerId")).thenReturn(null);

        assertNull(service.getAktuellerNutzer());
    }

    // -----------------------------------------------------------------------
    // emailPruefen()
    // -----------------------------------------------------------------------

    @Test
    void emailPruefen_emailExistiert_gibtTrue() {
        when(repository.findByEmail("max@example.com")).thenReturn(Optional.of(testBenutzer));

        assertTrue(service.emailPruefen("max@example.com"));
    }

    @Test
    void emailPruefen_emailNichtVorhanden_gibtFalse() {
        when(repository.findByEmail("unbekannt@example.com")).thenReturn(Optional.empty());

        assertFalse(service.emailPruefen("unbekannt@example.com"));
    }
}