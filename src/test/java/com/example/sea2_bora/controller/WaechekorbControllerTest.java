package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Waechekorb;
import com.example.sea2_bora.service.WaechekorbService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WaechekorbControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WaechekorbService service;

    @InjectMocks
    private WaechekorbController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Waechekorb singletonBasket;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        singletonBasket = new Waechekorb("Singleton Wäschekorb");
        singletonBasket.setId(1L); // Feste ID für den Test
    }

    // --- GET /api/waechekorb ---
    @Test
    void getSingletonWaechekorb_gibtImmerDenEinenKorbZurueck() throws Exception {
        when(service.getSingletonWaechekorb()).thenReturn(singletonBasket);

        mockMvc.perform(get("/api/waechekorb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Singleton Wäschekorb"));

        verify(service).getSingletonWaechekorb();
    }

    // --- POST /api/waechekorb ---
    @Test
    void create_sollteImmerDenSingletonKorbZurueckgeben() throws Exception {
        when(service.getSingletonWaechekorb()).thenReturn(singletonBasket);

        mockMvc.perform(post("/api/waechekorb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        // Stellt sicher, dass keine neue Erstellungsmethode aufgerufen wird
        verify(service, never()).create(any());
        verify(service).getSingletonWaechekorb();
    }

    @Test
    void delete_sollteNichtErlaubtSein_gibt405() throws Exception {
        mockMvc.perform(delete("/api/waechekorb"))
                .andExpect(status().isMethodNotAllowed());

        // Löschen darf niemals aufgerufen werden
        verify(service, never()).delete();
    }

    // --- GET /api/waechekorb/{id} (veraltet) ---
    @Test
    void getById_sollteNichtMehrExistieren_gibt404() throws Exception {
        // Dieser Endpunkt sollte im neuen Controller nicht mehr existieren
        mockMvc.perform(get("/api/waechekorb/1"))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/waechekorb/items ---
    @Test
    void addItems_fuegtItemsZumSingletonKorbHinzu() throws Exception {
        IdsRequest ids = new IdsRequest();
        ids.setIds(List.of(10L, 20L));
        when(service.addItems(ids.getIds())).thenReturn(singletonBasket);

        mockMvc.perform(post("/api/waechekorb/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());

        verify(service).addItems(ids.getIds());
    }

    // --- POST /api/waechekorb/items/{itemId} ---
    @Test
    void addItem_fuegtEinItemZumSingletonKorbHinzu() throws Exception {
        when(service.addItem(10L)).thenReturn(singletonBasket);

        mockMvc.perform(post("/api/waechekorb/items/10"))
                .andExpect(status().isOk());

        verify(service).addItem(10L);
    }

    // --- DELETE /api/waechekorb/items/{itemId} ---
    @Test
    void removeItem_entferntEinItemAusSingletonKorb() throws Exception {
        when(service.removeItem(10L)).thenReturn(singletonBasket);

        mockMvc.perform(delete("/api/waechekorb/items/10"))
                .andExpect(status().isOk());

        verify(service).removeItem(10L);
    }

    // --- DELETE /api/waechekorb/clear ---
    @Test
    void clear_leertDenSingletonKorb() throws Exception {
        when(service.clear()).thenReturn(singletonBasket);

        mockMvc.perform(delete("/api/waechekorb/clear"))
                .andExpect(status().isOk());

        verify(service).clear();
    }
}