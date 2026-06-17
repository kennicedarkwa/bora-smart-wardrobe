package com.example.sea2_bora.controller;

import com.example.sea2_bora.DTO.KleidungsstueckDTO;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.implementation.Tags;
import com.example.sea2_bora.service.KleidungsstueckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * NEU (v11): Tests für den Multipart-Endpunkt POST /api/kleidung/anlegen.
 */
@ExtendWith(MockitoExtension.class)
class KleidungsstueckControllerAnlegenTest {

    private MockMvc mockMvc;

    @Mock
    private KleidungsstueckService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        KleidungsstueckController controller = new KleidungsstueckController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // -----------------------------------------------------------------------
    // POST /api/kleidung/anlegen – ohne Bild
    // -----------------------------------------------------------------------

    @Test
    void anlegen_ohneBild_gibt200() throws Exception {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Jeans", 2, false, MainCategory.UNTERTEIL, List.of(Tags.ALLTAG)
        );
        Kleidungsstueck gespeichert = new Kleidungsstueck("Jeans", 2, false, MainCategory.UNTERTEIL, null);
        when(service.anlegen(any(KleidungsstueckDTO.class), isNull())).thenReturn(gespeichert);

        MockMultipartFile datenPart = new MockMultipartFile(
                "daten", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );

        mockMvc.perform(multipart("/api/kleidung/anlegen").file(datenPart))
                .andExpect(status().isOk());
    }

    @Test
    void anlegen_mitBild_gibt200() throws Exception {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Poloshirt", 3, false, MainCategory.OBERTEIL, List.of(Tags.ALLTAG)
        );
        Kleidungsstueck gespeichert = new Kleidungsstueck("Poloshirt", 3, false, MainCategory.OBERTEIL, "/uploads/bild.jpg");
        when(service.anlegen(any(KleidungsstueckDTO.class), any())).thenReturn(gespeichert);

        MockMultipartFile datenPart = new MockMultipartFile(
                "daten", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );
        MockMultipartFile bildPart = new MockMultipartFile(
                "bild", "bild.jpg", MediaType.IMAGE_JPEG_VALUE,
                "fake-image-bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/kleidung/anlegen")
                        .file(datenPart)
                        .file(bildPart))
                .andExpect(status().isOk());
    }

    @Test
    void anlegen_ruftServiceAuf() throws Exception {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Hut", 1, true, MainCategory.KOPFBEDECKUNG, List.of()
        );
        Kleidungsstueck gespeichert = new Kleidungsstueck("Hut", 1, true, MainCategory.KOPFBEDECKUNG, null);
        when(service.anlegen(any(), any())).thenReturn(gespeichert);

        MockMultipartFile datenPart = new MockMultipartFile(
                "daten", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );

        mockMvc.perform(multipart("/api/kleidung/anlegen").file(datenPart));

        verify(service, times(1)).anlegen(any(KleidungsstueckDTO.class), any());
    }

    @Test
    void anlegen_antwortEnthaeltName() throws Exception {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Sneaker", 3, false, MainCategory.SCHUHE, List.of(Tags.ALLTAG)
        );
        Kleidungsstueck gespeichert = new Kleidungsstueck("Sneaker", 3, false, MainCategory.SCHUHE, null);
        when(service.anlegen(any(), any())).thenReturn(gespeichert);

        MockMultipartFile datenPart = new MockMultipartFile(
                "daten", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(dto)
        );

        mockMvc.perform(multipart("/api/kleidung/anlegen").file(datenPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sneaker"));
    }

    @Test
    void anlegen_ohneDatenPart_gibt4xx() throws Exception {
        // Kein "daten"-Part → Spring gibt 400 zurück
        mockMvc.perform(multipart("/api/kleidung/anlegen"))
                .andExpect(status().is4xxClientError());
    }
}