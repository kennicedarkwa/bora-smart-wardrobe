//package com.example.sea2_bora.service;
//
//import com.example.sea2_bora.implementation.wetter.CityWeather;
//import com.example.sea2_bora.implementation.wetter.DailyForecast;
//import com.example.sea2_bora.implementation.wetter.Weather;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Integrations-ähnlicher Test für WeatherService mit der echten wetter.json.
// *
// * Prüft, dass die geladenen Wetterdaten Hamburg enthalten und sinnvolle Werte haben.
// */
//public class MocktestWetter {
//
//    @Test
//    void testGetWeather_gibtNichtNullZurueck() {
//        WeatherService weatherService = new WeatherService();
//
//        Weather result = weatherService.getWeather();
//
//        assertNotNull(result, "Weather-Objekt darf nicht null sein");
//    }
//
//    @Test
//    void testGetWeather_enthaltMindestensEineStadt() {
//        WeatherService weatherService = new WeatherService();
//
//        Weather result = weatherService.getWeather();
//
//        assertNotNull(result.getCities());
//        assertFalse(result.getCities().isEmpty(), "Städteliste darf nicht leer sein");
//    }
//
//    @Test
//    void testGetCity_Hamburg_wirdGefunden() {
//        WeatherService weatherService = new WeatherService();
//
//        Optional<CityWeather> hamburg = weatherService.getCity("Hamburg");
//
//        assertTrue(hamburg.isPresent(), "Hamburg sollte in wetter.json enthalten sein");
//    }
//
//    @Test
//    void testGetCity_Hamburg_hatVorhersagen() {
//        WeatherService weatherService = new WeatherService();
//
//        CityWeather hamburg = weatherService.getCity("Hamburg").orElseThrow();
//
//        assertFalse(hamburg.getDailyForecast().isEmpty(),
//                "Hamburg sollte mindestens eine Tagesvorhersage haben");
//    }
//
//    @Test
//    void testGetForecasts_Hamburg_jedeForecastHatTemperatur() {
//        WeatherService weatherService = new WeatherService();
//
//        weatherService.getForecasts("Hamburg").forEach(forecast -> {
//            assertNotNull(forecast.getDate(), "Datum darf nicht null sein");
//            assertNotNull(forecast.getTemperature(), "Temperatur darf nicht null sein");
//            assertNotNull(forecast.getPrecipitation(), "Niederschlag darf nicht null sein");
//        });
//    }
//
//    @Test
//    void testGetForecastByDate_ersterEintrag_wirdGefunden() {
//        WeatherService weatherService = new WeatherService();
//
//        // Ersten vorhandenen Eintrag aus Hamburg holen
//        DailyForecast erster = weatherService.getForecasts("Hamburg").get(0);
//        String datum = erster.getDate();
//
//        Optional<DailyForecast> result = weatherService.getForecastByDate("Hamburg", datum);
//
//        assertTrue(result.isPresent(), "Erster Hamburg-Eintrag sollte per Datum abrufbar sein");
//        assertEquals(datum, result.get().getDate());
//    }
//}
