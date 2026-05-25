package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Waechekorb;
import com.example.sea2_bora.service.WaechekorbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WaechekorbControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WaechekorbService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        WaechekorbController controller = new WaechekorbController();
        // Inject mock via reflection since controller uses @Autowired field
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "service", service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // --- POST /api/waechekorb ---

    @Test
    void create_ohneNamen_gibt200() throws Exception {
        Waechekorb korb = new Waechekorb(null);
        when(service.create(null)).thenReturn(korb);

        mockMvc.perform(post("/api/waechekorb"))
                .andExpect(status().isOk());
    }

    @Test
    void create_mitNamen_gibt200() throws Exception {
        Waechekorb korb = new Waechekorb("Sommerkorb");
        when(service.create("Sommerkorb")).thenReturn(korb);

        mockMvc.perform(post("/api/waechekorb")
                        .param("name", "Sommerkorb"))
                .andExpect(status().isOk());

        verify(service).create("Sommerkorb");
    }

    // --- GET /api/waechekorb ---

    @Test
    void getAll_gibt200MitListe() throws Exception {
        when(service.getAll()).thenReturn(List.of(new Waechekorb("Korb1")));

        mockMvc.perform(get("/api/waechekorb"))
                .andExpect(status().isOk());

        verify(service).getAll();
    }

    // --- GET /api/waechekorb/{id} ---

    @Test
    void getById_vorhandeneId_gibt200() throws Exception {
        when(service.getById(1L)).thenReturn(Optional.of(new Waechekorb("Testkorb")));

        mockMvc.perform(get("/api/waechekorb/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_nichtVorhandeneId_gibt404() throws Exception {
        when(service.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/waechekorb/99"))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /api/waechekorb/{id} ---

    @Test
    void delete_ruftServiceAufUndGibt204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/waechekorb/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    // --- POST /api/waechekorb/{id}/items/{itemId} ---

    @Test
    void addItem_gibt200() throws Exception {
        Waechekorb korb = new Waechekorb("Korb");
        when(service.addItem(1L, 10L)).thenReturn(korb);

        mockMvc.perform(post("/api/waechekorb/1/items/10"))
                .andExpect(status().isOk());

        verify(service).addItem(1L, 10L);
    }

    // --- POST /api/waechekorb/{id}/items ---

    @Test
    void addItems_gibt200() throws Exception {
        Waechekorb korb = new Waechekorb("Korb");
        IdsRequest ids = new IdsRequest();
        ids.setIds(List.of(1L, 2L));
        when(service.addItems(eq(1L), anyList())).thenReturn(korb);

        mockMvc.perform(post("/api/waechekorb/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());
    }

    // --- DELETE /api/waechekorb/{id}/items/{itemId} ---

    @Test
    void removeItem_gibt200() throws Exception {
        Waechekorb korb = new Waechekorb("Korb");
        when(service.removeItem(1L, 10L)).thenReturn(korb);

        mockMvc.perform(delete("/api/waechekorb/1/items/10"))
                .andExpect(status().isOk());

        verify(service).removeItem(1L, 10L);
    }

    // --- DELETE /api/waechekorb/{id}/clear ---

    @Test
    void clear_gibt200() throws Exception {
        Waechekorb korb = new Waechekorb("Korb");
        when(service.clear(1L)).thenReturn(korb);

        mockMvc.perform(delete("/api/waechekorb/1/clear"))
                .andExpect(status().isOk());

        verify(service).clear(1L);
    }

    // --- POST /api/waechekorb/items ---

    @Test
    void addItemsToDefaultBasket_gibt200() throws Exception {
        Waechekorb korb = new Waechekorb("Default");
        IdsRequest ids = new IdsRequest();
        ids.setIds(List.of(5L));
        when(service.addItemsToDefaultBasket(anyList())).thenReturn(korb);

        mockMvc.perform(post("/api/waechekorb/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());

        verify(service).addItemsToDefaultBasket(anyList());
    }
}