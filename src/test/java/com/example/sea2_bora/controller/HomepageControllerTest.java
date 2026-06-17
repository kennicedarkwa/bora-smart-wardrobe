package com.example.sea2_bora.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NEU (v11): HomepageController leitet GET / auf die statische Homepage weiter.
 */
@ExtendWith(MockitoExtension.class)
class HomepageControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HomepageController())
                .build();
    }

    @Test
    void get_root_gibt200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void get_root_gibtRichtigenViewNamen() throws Exception {
        // Spring MVC gibt den View-Namen "homepage/homepage.html" zurück
        mockMvc.perform(get("/"))
                .andExpect(view().name("homepage/homepage.html"));
    }
}