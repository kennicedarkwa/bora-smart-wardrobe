package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.wetter.CityWeather;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.implementation.wetter.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse für WeatherService.
 *
 * WeatherService liest wetter.json aus dem Classpath. Diese Tests prüfen,
 * ob die Datei korrekt geladen wird und die Such-/Filter-Methoden funktionieren.
 * Kein Mockito nötig – der Service ist zustandslos und hat keine DB-Abhängigkeit.
 */
class WeatherServiceTest {

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // Konstruktor lädt wetter.json aus dem Classpath (src/main/resources)
        weatherService = new WeatherService();
    }

    // --- Laden der Wetterdaten ---

    @Test
    void getWeather_gibtNichtNullZurueck() {
        Weather result = weatherService.getWeather();
        assertNotNull(result);
    }

    @Test
    void getAllCities_gibtMindestensEineStadtZurueck() {
        List<CityWeather> cities = weatherService.getAllCities();
        assertNotNull(cities);
        assertFalse(cities.isEmpty(), "wetter.json sollte mindestens eine Stadt enthalten");
    }

    @Test
    void getAllCities_jedeStadtHatEinenNamen() {
        weatherService.getAllCities().forEach(city ->
                assertNotNull(city.getCity(), "Stadtname darf nicht null sein")
        );
    }

    @Test
    void getAllCities_jedeStadtHatMindestensEineVorhersage() {
        weatherService.getAllCities().forEach(city ->
                assertFalse(city.getDailyForecast().isEmpty(),
                        "Stadt " + city.getCity() + " hat keine Vorhersagen")
        );
    }

    // --- getCity() ---

    @Test
    void getCity_vorhandeneStadt_gibtOptionalMitStadtZurueck() {
        // Ersten vorhandenen Stadtnamen aus der JSON-Datei holen
        String existingCity = weatherService.getAllCities().get(0).getCity();

        Optional<CityWeather> result = weatherService.getCity(existingCity);

        assertTrue(result.isPresent());
        assertEquals(existingCity.toLowerCase(), result.get().getCity().toLowerCase());
    }

    @Test
    void getCity_grossKleinschreibungIstEgal_gibtStadtZurueck() {
        String existingCity = weatherService.getAllCities().get(0).getCity();

        Optional<CityWeather> upper = weatherService.getCity(existingCity.toUpperCase());
        Optional<CityWeather> lower = weatherService.getCity(existingCity.toLowerCase());

        assertTrue(upper.isPresent());
        assertTrue(lower.isPresent());
    }

    @Test
    void getCity_nichtVorhandeneStadt_gibtLeereOptionalZurueck() {
        Optional<CityWeather> result = weatherService.getCity("XxxNichtVorhandenXxx");
        assertFalse(result.isPresent());
    }

    // --- getForecasts() ---

    @Test
    void getForecasts_vorhandeneStadt_gibtVorhersagenZurueck() {
        String existingCity = weatherService.getAllCities().get(0).getCity();

        List<DailyForecast> forecasts = weatherService.getForecasts(existingCity);

        assertNotNull(forecasts);
        assertFalse(forecasts.isEmpty());
    }

    @Test
    void getForecasts_nichtVorhandeneStadt_gibtLeereListeZurueck() {
        List<DailyForecast> forecasts = weatherService.getForecasts("NichtVorhanden");
        assertNotNull(forecasts);
        assertTrue(forecasts.isEmpty());
    }

    @Test
    void getForecasts_jedeForecastHatDatum() {
        String existingCity = weatherService.getAllCities().get(0).getCity();

        weatherService.getForecasts(existingCity).forEach(f ->
                assertNotNull(f.getDate(), "Datum in DailyForecast darf nicht null sein")
        );
    }

    // --- getForecastByDate() ---

    @Test
    void getForecastByDate_vorhandenesDatum_gibtForecastZurueck() {
        CityWeather firstCity = weatherService.getAllCities().get(0);
        String city = firstCity.getCity();
        String date = firstCity.getDailyForecast().get(0).getDate();

        Optional<DailyForecast> result = weatherService.getForecastByDate(city, date);

        assertTrue(result.isPresent());
        assertEquals(date, result.get().getDate());
    }

    @Test
    void getForecastByDate_nichtVorhandenesDatum_gibtLeereOptionalZurueck() {
        String existingCity = weatherService.getAllCities().get(0).getCity();

        Optional<DailyForecast> result = weatherService.getForecastByDate(existingCity, "9999-99-99");

        assertFalse(result.isPresent());
    }

    @Test
    void getForecastByDate_nichtVorhandeneStadt_gibtLeereOptionalZurueck() {
        Optional<DailyForecast> result = weatherService.getForecastByDate("NichtVorhanden", "2025-07-01");
        assertFalse(result.isPresent());
    }
}
