package com.example.sea2_bora.implementation.wetter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WeatherTest {

    private CityWeather hamburg;
    private CityWeather berlin;
    private Weather weather;

    @BeforeEach
    void setUp() {
        hamburg = new CityWeather();
        hamburg.setCity("Hamburg");
        hamburg.setDailyForecast(List.of());

        berlin = new CityWeather();
        berlin.setCity("Berlin");
        berlin.setDailyForecast(List.of());

        weather = new Weather(new ArrayList<>(List.of(hamburg, berlin)));
    }

    // --- Konstruktoren ---

    @Test
    void standardKonstruktor_gibtObjektOhneExceptionZurueck() {
        Weather w = new Weather();
        assertNotNull(w);
    }

    @Test
    void konstruktorMitListe_setztCities() {
        assertEquals(2, weather.getCities().size());
    }

    // --- getCityNames() ---

    @Test
    void getCityNames_gibtAlleNamenZurueck() {
        List<String> namen = weather.getCityNames();
        assertTrue(namen.contains("Hamburg"));
        assertTrue(namen.contains("Berlin"));
    }

    @Test
    void getCityNames_reihenfolgeKorrekt() {
        List<String> namen = weather.getCityNames();
        assertEquals("Hamburg", namen.get(0));
        assertEquals("Berlin", namen.get(1));
    }

    // --- findCity() ---

    @Test
    void findCity_vorhandeneStadt_gibtOptionalMitStadtZurueck() {
        Optional<CityWeather> result = weather.findCity("Hamburg");
        assertTrue(result.isPresent());
        assertEquals("Hamburg", result.get().getCity());
    }

    @Test
    void findCity_grossKleinschreibungEgal_findetStadt() {
        assertTrue(weather.findCity("hamburg").isPresent());
        assertTrue(weather.findCity("HAMBURG").isPresent());
        assertTrue(weather.findCity("HaMbUrG").isPresent());
    }

    @Test
    void findCity_nichtVorhandeneStadt_gibtLeereOptionalZurueck() {
        assertFalse(weather.findCity("München").isPresent());
    }

    @Test
    void findCity_citiesNull_gibtLeereOptionalZurueck() {
        Weather w = new Weather();
        // cities ist null (kein Setter aufgerufen)
        assertFalse(w.findCity("Hamburg").isPresent());
    }

    // --- containsCity() ---

    @Test
    void containsCity_vorhandeneStadt_gibtTrue() {
        assertTrue(weather.containsCity("Berlin"));
    }

    @Test
    void containsCity_nichtVorhandeneStadt_gibtFalse() {
        assertFalse(weather.containsCity("Paris"));
    }

    @Test
    void containsCity_grossKleinschreibungEgal() {
        assertTrue(weather.containsCity("berlin"));
        assertTrue(weather.containsCity("BERLIN"));
    }

    // --- size() ---

    @Test
    void size_gibtAnzahlStaedteZurueck() {
        assertEquals(2, weather.size());
    }

    @Test
    void size_leereListeGibt0() {
        Weather w = new Weather(new ArrayList<>());
        assertEquals(0, w.size());
    }

    @Test
    void size_citiesNull_gibt0() {
        Weather w = new Weather();
        assertEquals(0, w.size());
    }

    // --- equals() / hashCode() ---

    @Test
    void equals_gleicheListe_istGleich() {
        Weather w1 = new Weather(List.of(hamburg));
        Weather w2 = new Weather(List.of(hamburg));
        assertEquals(w1, w2);
    }

    @Test
    void equals_verschiedeneListen_istNichtGleich() {
        Weather w1 = new Weather(List.of(hamburg));
        Weather w2 = new Weather(List.of(berlin));
        assertNotEquals(w1, w2);
    }

    @Test
    void equals_sichSelbst_istGleich() {
        assertEquals(weather, weather);
    }

    @Test
    void hashCode_gleicheObjekte_gleichHashCode() {
        Weather w1 = new Weather(List.of(hamburg));
        Weather w2 = new Weather(List.of(hamburg));
        assertEquals(w1.hashCode(), w2.hashCode());
    }

    // --- toString() ---

    @Test
    void toString_enthaeltAnzahlStaedte() {
        String str = weather.toString();
        assertTrue(str.contains("2"));
    }
}