package com.example.sea2_bora.implementation.wetter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CityWeatherTest {

    private CityWeather cityWeather;
    private DailyForecast forecast1;
    private DailyForecast forecast2;

    @BeforeEach
    void setUp() {
        cityWeather = new CityWeather();
        cityWeather.setCity("Hamburg");

        forecast1 = new DailyForecast("2025-07-01",
                new Temperature(18, 24), 5,
                new Precipitation(PrecipitationType.CLEAR, 10));

        forecast2 = new DailyForecast("2025-07-02",
                new Temperature(15, 20), 3,
                new Precipitation(PrecipitationType.RAIN, 70));

        cityWeather.setDailyForecast(new ArrayList<>(List.of(forecast1, forecast2)));
    }

    // --- Konstruktor / Getter / Setter ---

    @Test
    void standardKonstruktor_gibtObjektOhneExceptionZurueck() {
        CityWeather cw = new CityWeather();
        assertNotNull(cw);
    }

    @Test
    void setCity_getCity_gibtGesetztenNamenZurueck() {
        cityWeather.setCity("Berlin");
        assertEquals("Berlin", cityWeather.getCity());
    }

    @Test
    void setDailyForecast_getDailyForecast_gibtGesetzteListeZurueck() {
        assertEquals(2, cityWeather.getDailyForecast().size());
    }

    // --- DailyForecast-Liste ---

    @Test
    void getDailyForecast_enthaeltRichtigeEintraege() {
        List<DailyForecast> forecasts = cityWeather.getDailyForecast();
        assertEquals("2025-07-01", forecasts.get(0).getDate());
        assertEquals("2025-07-02", forecasts.get(1).getDate());
    }

    @Test
    void setDailyForecast_leersteListe_gibtLeereListeZurueck() {
        cityWeather.setDailyForecast(new ArrayList<>());
        assertTrue(cityWeather.getDailyForecast().isEmpty());
    }

    @Test
    void setDailyForecast_null_gibtNullZurueck() {
        cityWeather.setDailyForecast(null);
        assertNull(cityWeather.getDailyForecast());
    }

    // --- Datum-Suche (typische Nutzung im Controller) ---

    @Test
    void getDailyForecast_stream_findetForecastNachDatum() {
        String gesucht = "2025-07-02";
        var result = cityWeather.getDailyForecast().stream()
                .filter(f -> f.getDate().equals(gesucht))
                .findFirst();

        assertTrue(result.isPresent());
        assertEquals(gesucht, result.get().getDate());
    }

    @Test
    void getDailyForecast_stream_nichtVorhandenesDatum_gibtEmpty() {
        var result = cityWeather.getDailyForecast().stream()
                .filter(f -> f.getDate().equals("1900-01-01"))
                .findFirst();

        assertFalse(result.isPresent());
    }
}