package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.service.BenutzerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class BenutzerControllerTest {


    private MockMvc mockMvc;

    @Mock
    private BenutzerService benutzerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Hier wird der Controller manuell mit dem Mock-Service erstellt
        BenutzerController controller = new BenutzerController(benutzerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }



    // POST /api/benutzer/registrieren

    @Test
    void registrieren_erfolgreich_gibt200() throws Exception {
        Benutzer body = new Benutzer("Max", "max@example.com", "pw123", "Hamburg");
        when(benutzerService.registrieren(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/api/benutzer/registrieren")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void registrieren_emailVergeben_gibt400() throws Exception {
        Benutzer body = new Benutzer("Max", "max@example.com", "pw123", "Hamburg");
        when(benutzerService.registrieren(any(), any(), any())).thenReturn(false);

        mockMvc.perform(post("/api/benutzer/registrieren")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email bereits vergeben"));
    }


    // POST /api/benutzer/login/{id}

    @Test
    void login_benutzerGefunden_gibt200() throws Exception {
        when(benutzerService.login(1L)).thenReturn(true);

        mockMvc.perform(post("/api/benutzer/login/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login erfolgreich"));
    }

    @Test
    void login_benutzerNichtGefunden_gibt400() throws Exception {
        when(benutzerService.login(99L)).thenReturn(false);

        mockMvc.perform(post("/api/benutzer/login/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Benutzer nicht gefunden"));
    }


    // POST /api/benutzer/logout

    @Test
    void logout_gibt200() throws Exception {
        mockMvc.perform(post("/api/benutzer/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout erfolgreich"));

        verify(benutzerService, times(1)).logout();
    }


    // GET /api/benutzer/aktueller

    @Test
    void aktuellerBenutzer_eingeloggt_gibt200MitBenutzer() throws Exception {
        Benutzer benutzer = new Benutzer("Max", "max@example.com", "pw", "Hamburg");
        when(benutzerService.getAktuellerNutzer()).thenReturn(benutzer);

        mockMvc.perform(get("/api/benutzer/aktueller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.benutzername").value("Max"))
                .andExpect(jsonPath("$.email").value("max@example.com"));
    }

    @Test
    void aktuellerBenutzer_nichtEingeloggt_gibt204() throws Exception {
        when(benutzerService.getAktuellerNutzer()).thenReturn(null);

        mockMvc.perform(get("/api/benutzer/aktueller"))
                .andExpect(status().isNoContent());
    }


    // POST /api/benutzer/passwort-aendern

    @Test
    void passwortAendern_erfolgreich_gibt200() throws Exception {
        when(benutzerService.aenderePasswort("alt", "neu")).thenReturn(true);

        mockMvc.perform(post("/api/benutzer/passwort-aendern")
                        .param("altesPasswort", "alt")
                        .param("neuesPasswort", "neu"))
                .andExpect(status().isOk())
                .andExpect(content().string("Passwort erfolgreich geändert"));
    }

    @Test
    void passwortAendern_fehlgeschlagen_gibt400() throws Exception {
        when(benutzerService.aenderePasswort("falsch", "neu")).thenReturn(false);

        mockMvc.perform(post("/api/benutzer/passwort-aendern")
                        .param("altesPasswort", "falsch")
                        .param("neuesPasswort", "neu"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Passwort konnte nicht geändert werden"));
    }


    // GET /api/benutzer/email-pruefen

    @Test
    void emailPruefen_emailExistiert_gibtTrue() throws Exception {
        when(benutzerService.emailPruefen("max@example.com")).thenReturn(true);

        mockMvc.perform(get("/api/benutzer/email-pruefen")
                        .param("email", "max@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void emailPruefen_emailNichtVorhanden_gibtFalse() throws Exception {
        when(benutzerService.emailPruefen("neu@example.com")).thenReturn(false);

        mockMvc.perform(get("/api/benutzer/email-pruefen")
                        .param("email", "neu@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}