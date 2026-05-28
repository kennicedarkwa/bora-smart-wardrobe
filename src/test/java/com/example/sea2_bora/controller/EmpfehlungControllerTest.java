package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Empfehlung;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.implementation.wetter.Precipitation;
import com.example.sea2_bora.implementation.wetter.PrecipitationType;
import com.example.sea2_bora.implementation.wetter.Temperature;
import com.example.sea2_bora.service.EmpfehlungService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmpfehlungControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmpfehlungService empfehlungService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        EmpfehlungController controller = new EmpfehlungController(empfehlungService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // --- POST /api/empfehlung ---

    @Test
    void getEmpfehlung_gibt200() throws Exception {
        DailyForecast forecast = new DailyForecast(
                "2025-07-01",
                new Temperature(18, 22),
                4,
                new Precipitation(PrecipitationType.CLEAR, 5)
        );
        Empfehlung empfehlung = new Empfehlung(LocalDate.now());
        when(empfehlungService.generiereEmpfehlung(any(DailyForecast.class))).thenReturn(empfehlung);

        mockMvc.perform(post("/api/empfehlung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forecast)))
                .andExpect(status().isOk());
    }

    @Test
    void getEmpfehlung_ruftServiceAuf() throws Exception {
        DailyForecast forecast = new DailyForecast(
                "2025-07-01",
                new Temperature(5, 10),
                1,
                new Precipitation(PrecipitationType.RAIN, 80)
        );
        Empfehlung empfehlung = new Empfehlung(LocalDate.now());
        when(empfehlungService.generiereEmpfehlung(any(DailyForecast.class))).thenReturn(empfehlung);

        mockMvc.perform(post("/api/empfehlung")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forecast)));

        verify(empfehlungService, times(1)).generiereEmpfehlung(any(DailyForecast.class));
    }

    @Test
    void getEmpfehlung_mitRegenForecast_gibt200() throws Exception {
        DailyForecast forecastRegen = new DailyForecast(
                "2025-11-15",
                new Temperature(2, 8),
                0,
                new Precipitation(PrecipitationType.RAIN, 90)
        );
        Empfehlung empfehlung = new Empfehlung(LocalDate.now());
        when(empfehlungService.generiereEmpfehlung(any(DailyForecast.class))).thenReturn(empfehlung);

        mockMvc.perform(post("/api/empfehlung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forecastRegen)))
                .andExpect(status().isOk());
    }

    @Test
    void getEmpfehlung_ohneBody_gibt4xx() throws Exception {
        mockMvc.perform(post("/api/empfehlung")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getEmpfehlung_antwortEnthaeltBegruendung() throws Exception {
        DailyForecast forecast = new DailyForecast(
                "2025-07-01",
                new Temperature(18, 22),
                4,
                new Precipitation(PrecipitationType.CLEAR, 5)
        );
        Empfehlung empfehlung = new Empfehlung(LocalDate.now());
        empfehlung.setBegruendung("Heute 20°C – passende Kleidung für heute.");
        when(empfehlungService.generiereEmpfehlung(any(DailyForecast.class))).thenReturn(empfehlung);

        mockMvc.perform(post("/api/empfehlung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forecast)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.begruendung").isNotEmpty());
    }
}