//package com.example.sea2_bora.controller;
//
//import com.example.sea2_bora.implementation.wetter.CityWeather;
//import com.example.sea2_bora.implementation.wetter.DailyForecast;
//import com.example.sea2_bora.implementation.wetter.Precipitation;
//import com.example.sea2_bora.implementation.wetter.PrecipitationType;
//import com.example.sea2_bora.implementation.wetter.Temperature;
//import com.example.sea2_bora.service.WeatherService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Testklasse für WeatherController.
// *
// * Prüft den einzigen Endpunkt GET /api/weather/{city}/{date}:
// * - 200 OK wenn Stadt und Datum gefunden
// * - 404 wenn Stadt unbekannt
// * - 404 wenn Datum nicht vorhanden
// */
//@ExtendWith(MockitoExtension.class)
//class WeatherControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private WeatherService weatherService;
//
//    private DailyForecast sampleForecast;
//    private CityWeather sampleCity;
//
//    @BeforeEach
//    void setUp() {
//        WeatherController controller = new WeatherController(weatherService);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//
//        sampleForecast = new DailyForecast(
//                "2025-07-01",
//                new Temperature(18, 24),
//                6,
//                new Precipitation(PrecipitationType.CLEAR, 10)
//        );
//
//        sampleCity = new CityWeather();
//        sampleCity.setCity("Hamburg");
//        sampleCity.setDailyForecast(List.of(sampleForecast));
//    }
//
//    // --- GET /api/weather/{city}/{date} ---
//
//    @Test
//    void getWeatherByCityAndDate_vorhandeneStadtUndDatum_gibt200() throws Exception {
//        when(weatherService.getCity("Hamburg")).thenReturn(Optional.of(sampleCity));
//
//        mockMvc.perform(get("/api/weather/Hamburg/2025-07-01"))
//                .andExpect(status().isOk());
//
//        verify(weatherService).getCity("Hamburg");
//    }
//
//    @Test
//    void getWeatherByCityAndDate_nichtVorhandeneStadt_gibt404() throws Exception {
//        when(weatherService.getCity("NichtVorhanden")).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/weather/NichtVorhanden/2025-07-01"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getWeatherByCityAndDate_stadtVorhandenAberDatumNicht_gibt404() throws Exception {
//        when(weatherService.getCity("Hamburg")).thenReturn(Optional.of(sampleCity));
//
//        mockMvc.perform(get("/api/weather/Hamburg/9999-12-31"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getWeatherByCityAndDate_antwortEnthaeltDatum() throws Exception {
//        when(weatherService.getCity("Hamburg")).thenReturn(Optional.of(sampleCity));
//
//        mockMvc.perform(get("/api/weather/Hamburg/2025-07-01"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.date").value("2025-07-01"));
//    }
//
//    @Test
//    void getWeatherByCityAndDate_stadtMitMehrerenDaten_gibtKorrektesZurueck() throws Exception {
//        DailyForecast forecast2 = new DailyForecast(
//                "2025-07-02",
//                new Temperature(20, 28),
//                8,
//                new Precipitation(PrecipitationType.CLEAR, 5)
//        );
//        sampleCity.setDailyForecast(List.of(sampleForecast, forecast2));
//        when(weatherService.getCity("Hamburg")).thenReturn(Optional.of(sampleCity));
//
//        mockMvc.perform(get("/api/weather/Hamburg/2025-07-02"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.date").value("2025-07-02"));
//    }
//}
