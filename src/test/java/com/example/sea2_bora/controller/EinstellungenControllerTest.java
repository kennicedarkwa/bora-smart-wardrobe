package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Einstellungen;
import com.example.sea2_bora.service.EinstellungenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EinstellungenControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EinstellungenService service;

    @InjectMocks
    private EinstellungenController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Einstellungen einstellungen;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        einstellungen = new Einstellungen("Hamburg");
        einstellungen.setEinstellungsID(1L);
    }

    // GET /api/einstellungen/{id}

    @Test
    void lade_vorhandeneId_gibt200MitEinstellungen() throws Exception {
        when(service.ladeEinstellungen(1L)).thenReturn(einstellungen);

        mockMvc.perform(get("/api/einstellungen/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.standardStandort").value("Hamburg"));

        verify(service).ladeEinstellungen(1L);
    }

    @Test
    void lade_unbekannteId_gibt200MitNull() throws Exception {
        when(service.ladeEinstellungen(99L)).thenReturn(null);

        mockMvc.perform(get("/api/einstellungen/99"))
                .andExpect(status().isOk());
    }

    // PUT /api/einstellungen/{id}

    @Test
    void speichern_gueltigeEinstellungen_gibt200() throws Exception {
        when(service.speichereEinstellungen(any(Einstellungen.class))).thenReturn(einstellungen);

        mockMvc.perform(put("/api/einstellungen/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(einstellungen)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.standardStandort").value("Hamburg"));

        verify(service).speichereEinstellungen(any(Einstellungen.class));
    }

    // POST /api/einstellungen/{id}/standardwerte

    @Test
    void standardwerte_vorhandeneId_gibt200() throws Exception {
        when(service.setzeStandardwerte(1L)).thenReturn(einstellungen);

        mockMvc.perform(post("/api/einstellungen/1/standardwerte"))
                .andExpect(status().isOk());

        verify(service).setzeStandardwerte(1L);
    }

    @Test
    void standardwerte_unbekannteId_gibt200MitNull() throws Exception {
        when(service.setzeStandardwerte(99L)).thenReturn(null);

        mockMvc.perform(post("/api/einstellungen/99/standardwerte"))
                .andExpect(status().isOk());
    }

    // POST /api/einstellungen/{id}/erinnerung/aktivieren

    @Test
    void aktiviere_ruftServiceAuf_und_gibt200() throws Exception {
        doNothing().when(service).aktiviereErinnerung(1L);

        mockMvc.perform(post("/api/einstellungen/1/erinnerung/aktivieren"))
                .andExpect(status().isOk());

        verify(service).aktiviereErinnerung(1L);
    }

    // POST /api/einstellungen/{id}/erinnerung/deaktivieren

    @Test
    void deaktiviere_ruftServiceAuf_und_gibt200() throws Exception {
        doNothing().when(service).deaktiviereErinnerung(1L);

        mockMvc.perform(post("/api/einstellungen/1/erinnerung/deaktivieren"))
                .andExpect(status().isOk());

        verify(service).deaktiviereErinnerung(1L);
    }
}