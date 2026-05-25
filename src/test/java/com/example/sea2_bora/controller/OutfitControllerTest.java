package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Outfit;
import com.example.sea2_bora.implementation.Tags;
import com.example.sea2_bora.service.OutfitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OutfitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OutfitService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        OutfitController controller = new OutfitController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // --- GET /api/outfits ---

    @Test
    void getAllOutfits_gibtStatus200() throws Exception {
        when(service.getAllOutfits()).thenReturn(List.of());

        mockMvc.perform(get("/api/outfits"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllOutfits_gibtListeZurueck() throws Exception {
        Outfit outfit = new Outfit("Sommeroutfit", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());
        when(service.getAllOutfits()).thenReturn(List.of(outfit));

        mockMvc.perform(get("/api/outfits"))
                .andExpect(status().isOk());

        verify(service).getAllOutfits();
    }

    // --- GET /api/outfits/{id} ---

    @Test
    void getOutfitById_vorhandeneId_gibt200() throws Exception {
        Outfit outfit = new Outfit("Outfit", Tags.ELEGANT, new ArrayList<>(), new ArrayList<>());
        when(service.getOutfitById(1L)).thenReturn(Optional.of(outfit));

        mockMvc.perform(get("/api/outfits/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOutfitById_nichtVorhandeneId_gibt404() throws Exception {
        when(service.getOutfitById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/outfits/99"))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/outfits ---

    @Test
    void createOutfit_gibt200MitOutfit() throws Exception {
        Outfit outfit = new Outfit("Neues Outfit", Tags.ELEGANT, new ArrayList<>(), new ArrayList<>());
        when(service.createOutfit(any(Outfit.class))).thenReturn(outfit);

        mockMvc.perform(post("/api/outfits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(outfit)))
                .andExpect(status().isOk());
    }

    @Test
    void createOutfit_ruftServiceAuf() throws Exception {
        Outfit outfit = new Outfit("Test", Tags.ALLTAG, new ArrayList<>(), new ArrayList<>());
        when(service.createOutfit(any(Outfit.class))).thenReturn(outfit);

        mockMvc.perform(post("/api/outfits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outfit)));

        verify(service, atLeast(1)).createOutfit(any(Outfit.class));
    }

    // --- PUT /api/outfits/{id} ---

    @Test
    void updateOutfit_vorhandeneId_gibt200() throws Exception {
        // Hier hieß die Variable vorher fälschlicherweise "outfit" statt "neueDaten"
        Outfit neueDaten = new Outfit("Updated", Tags.ELEGANT, new ArrayList<>(), new ArrayList<>());
        when(service.updateOutfit(eq(1L), any(Outfit.class))).thenReturn(neueDaten);

        mockMvc.perform(put("/api/outfits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(neueDaten)))
                .andExpect(status().isOk());
    }

    @Test
    void updateOutfit_nichtVorhandeneId_gibt404() throws Exception {
        // Auch hier die Definition für neueDaten korrigiert
        Outfit neueDaten = new Outfit("Updated", Tags.ELEGANT, new ArrayList<>(), new ArrayList<>());
        when(service.updateOutfit(eq(99L), any(Outfit.class))).thenReturn(null);

        mockMvc.perform(put("/api/outfits/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(neueDaten)))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /api/outfits/{id} ---

    @Test
    void deleteOutfit_vorhandeneId_gibt200() throws Exception {
        when(service.deleteOutfit(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/outfits/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOutfit_nichtVorhandeneId_gibt404() throws Exception {
        when(service.deleteOutfit(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/outfits/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOutfit_ruftServiceAuf() throws Exception {
        when(service.deleteOutfit(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/outfits/1"));

        verify(service).deleteOutfit(1L);
    }
}