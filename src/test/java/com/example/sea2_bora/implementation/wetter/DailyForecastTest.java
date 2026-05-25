package com.example.sea2_bora.implementation.wetter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse für DailyForecast.
 *
 * Prüft UV-Index-Validierung, UV-Kategorie-Einteilung (Low/Moderate/High/Very High/Extreme)
 * sowie Konstruktor und equals/hashCode.
 */
class DailyForecastTest {

    private Temperature defaultTemp = new Temperature(15, 20);
    private Precipitation defaultPrec = new Precipitation(PrecipitationType.CLEAR, 10);

    // --- Konstruktor / UV-Validierung ---

    @Test
    void konstruktor_gueltigeWerte_wirdErstellt() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        assertEquals("2025-07-01", f.getDate());
        assertEquals(5, f.getUv_index());
    }

    @Test
    void konstruktor_uvIndex0_wirdErstellt() {
        DailyForecast f = new DailyForecast("2025-01-01", defaultTemp, 0, defaultPrec);
        assertEquals(0, f.getUv_index());
    }

    @Test
    void konstruktor_uvIndex11_wirdErstellt() {
        DailyForecast f = new DailyForecast("2025-07-15", defaultTemp, 11, defaultPrec);
        assertEquals(11, f.getUv_index());
    }

    @Test
    void konstruktor_uvIndexNegativ_wirftIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new DailyForecast("2025-07-01", defaultTemp, -1, defaultPrec));
    }

    @Test
    void konstruktor_uvIndexUeber11_wirftIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new DailyForecast("2025-07-01", defaultTemp, 12, defaultPrec));
    }

    @Test
    void setUvIndex_ungueltigerWert_wirftIllegalArgumentException() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        assertThrows(IllegalArgumentException.class, () -> f.setUv_index(15));
    }

    // --- getUvCategory() ---

    @Test
    void getUvCategory_index0_gibtLow() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 0, defaultPrec);
        assertEquals("Low", f.getUvCategory());
    }

    @Test
    void getUvCategory_index2_gibtLow() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 2, defaultPrec);
        assertEquals("Low", f.getUvCategory());
    }

    @Test
    void getUvCategory_index3_gibtModerate() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 3, defaultPrec);
        assertEquals("Moderate", f.getUvCategory());
    }

    @Test
    void getUvCategory_index5_gibtModerate() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        assertEquals("Moderate", f.getUvCategory());
    }

    @Test
    void getUvCategory_index6_gibtHigh() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 6, defaultPrec);
        assertEquals("High", f.getUvCategory());
    }

    @Test
    void getUvCategory_index7_gibtHigh() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 7, defaultPrec);
        assertEquals("High", f.getUvCategory());
    }

    @Test
    void getUvCategory_index8_gibtVeryHigh() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 8, defaultPrec);
        assertEquals("Very High", f.getUvCategory());
    }

    @Test
    void getUvCategory_index10_gibtVeryHigh() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 10, defaultPrec);
        assertEquals("Very High", f.getUvCategory());
    }

    @Test
    void getUvCategory_index11_gibtExtreme() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 11, defaultPrec);
        assertEquals("Extreme", f.getUvCategory());
    }

    // --- Getter / Setter ---

    @Test
    void getDate_gibtGesetztesDatumZurueck() {
        DailyForecast f = new DailyForecast("2025-12-24", defaultTemp, 1, defaultPrec);
        assertEquals("2025-12-24", f.getDate());
    }

    @Test
    void setDate_aktualisiertDatum() {
        DailyForecast f = new DailyForecast("2025-01-01", defaultTemp, 1, defaultPrec);
        f.setDate("2025-06-15");
        assertEquals("2025-06-15", f.getDate());
    }

    @Test
    void getTemperature_gibtTemperaturObjektZurueck() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 4, defaultPrec);
        assertEquals(defaultTemp, f.getTemperature());
    }

    @Test
    void getPrecipitation_gibtNiederschlagsobjektZurueck() {
        DailyForecast f = new DailyForecast("2025-07-01", defaultTemp, 4, defaultPrec);
        assertEquals(defaultPrec, f.getPrecipitation());
    }

    // --- equals() & hashCode() ---

    @Test
    void equals_gleicheFelder_istGleich() {
        DailyForecast f1 = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        DailyForecast f2 = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        assertEquals(f1, f2);
    }

    @Test
    void equals_verschiedenesDatum_istNichtGleich() {
        DailyForecast f1 = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        DailyForecast f2 = new DailyForecast("2025-08-01", defaultTemp, 5, defaultPrec);
        assertNotEquals(f1, f2);
    }

    @Test
    void hashCode_gleicheObjekte_gleichHashCode() {
        DailyForecast f1 = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        DailyForecast f2 = new DailyForecast("2025-07-01", defaultTemp, 5, defaultPrec);
        assertEquals(f1.hashCode(), f2.hashCode());
    }
}
