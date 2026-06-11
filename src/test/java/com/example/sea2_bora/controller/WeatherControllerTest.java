package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.implementation.wetter.Precipitation;
import com.example.sea2_bora.implementation.wetter.PrecipitationType;
import com.example.sea2_bora.implementation.wetter.Temperature;
import com.example.sea2_bora.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testklasse für WeatherController.
 *
 * Prüft GET /api/weather/{city}/{date}:
 * - 200 OK mit DailyForecast wenn Service etwas zurückgibt
 * - 404 wenn Service Optional.empty() zurückgibt (Stadt unbekannt oder kein Wetter)
 */
@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    private DailyForecast sampleForecast;

    @BeforeEach
    void setUp() {
        WeatherController controller = new WeatherController(weatherService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        sampleForecast = new DailyForecast(
                "2026-06-09",
                new Temperature(15, 24),
                5,
                new Precipitation(PrecipitationType.NONE, 10)
        );
    }

    // GET /api/weather/{city}/{date} — Erfolgsfall

    @Test
    void getWeather_vorhandeneStadtUndDatum_gibt200() throws Exception {
        when(weatherService.getWeatherForCityAndDate("Hamburg", "2026-06-09"))
                .thenReturn(Optional.of(sampleForecast));

        mockMvc.perform(get("/api/weather/Hamburg/2026-06-09"))
                .andExpect(status().isOk());

        verify(weatherService).getWeatherForCityAndDate("Hamburg", "2026-06-09");
    }

    @Test
    void getWeather_antwortEnthaeltDatum() throws Exception {
        when(weatherService.getWeatherForCityAndDate("Hamburg", "2026-06-09"))
                .thenReturn(Optional.of(sampleForecast));

        mockMvc.perform(get("/api/weather/Hamburg/2026-06-09"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.date").value("2026-06-09"));
    }

    @Test
    void getWeather_antwortEnthaeltTemperatur() throws Exception {
        when(weatherService.getWeatherForCityAndDate("Hamburg", "2026-06-09"))
                .thenReturn(Optional.of(sampleForecast));

        mockMvc.perform(get("/api/weather/Hamburg/2026-06-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperature.min_celsius").value(15))
                .andExpect(jsonPath("$.temperature.max_celsius").value(24));
    }

    @Test
    void getWeather_antwortEnthaeltUvIndex() throws Exception {
        when(weatherService.getWeatherForCityAndDate("Hamburg", "2026-06-09"))
                .thenReturn(Optional.of(sampleForecast));

        mockMvc.perform(get("/api/weather/Hamburg/2026-06-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uv_index").value(5));
    }

    // GET /api/weather/{city}/{date} — Fehlerfall

    @Test
    void getWeather_unbekannteStadt_gibt404() throws Exception {
        when(weatherService.getWeatherForCityAndDate("NichtVorhanden", "2026-06-09"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/weather/NichtVorhanden/2026-06-09"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWeather_geocodingSchlagtFehl_gibt404() throws Exception {
        when(weatherService.getWeatherForCityAndDate("XYZ", "2026-06-09"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/weather/XYZ/2026-06-09"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWeather_regenWetter_gibt200MitKorrektemPrecipitationType() throws Exception {
        DailyForecast regenForecast = new DailyForecast(
                "2026-06-09",
                new Temperature(12, 18),
                2,
                new Precipitation(PrecipitationType.RAIN, 80)
        );
        when(weatherService.getWeatherForCityAndDate("Hamburg", "2026-06-09"))
                .thenReturn(Optional.of(regenForecast));

        mockMvc.perform(get("/api/weather/Hamburg/2026-06-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precipitation.probability_percent").value(80));
    }
}