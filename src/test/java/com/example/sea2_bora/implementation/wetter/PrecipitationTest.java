package com.example.sea2_bora.implementation.wetter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse für Precipitation.
 *
 * Prüft Validierung der Wahrscheinlichkeit (0–100), isLikely() (ab 50%)
 * und isSevere() (THUNDERSTORM oder SNOW).
 */
class PrecipitationTest {

    // --- Konstruktor & Setter ---

    @Test
    void konstruktor_gueltigeWerte_wirdErstellt() {
        Precipitation p = new Precipitation(PrecipitationType.RAIN, 70);
        assertEquals(PrecipitationType.RAIN, p.getType());
        assertEquals(70, p.getProbability_percent());
    }

    @Test
    void konstruktor_wahrscheinlichkeit0_wirdErstellt() {
        Precipitation p = new Precipitation(PrecipitationType.CLEAR, 0);
        assertEquals(0, p.getProbability_percent());
    }

    @Test
    void konstruktor_wahrscheinlichkeit100_wirdErstellt() {
        Precipitation p = new Precipitation(PrecipitationType.SNOW, 100);
        assertEquals(100, p.getProbability_percent());
    }

    @Test
    void konstruktor_wahrscheinlichkeitNegativ_wirftIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Precipitation(PrecipitationType.RAIN, -1));
    }

    @Test
    void konstruktor_wahrscheinlichkeitUeber100_wirftIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Precipitation(PrecipitationType.RAIN, 101));
    }

    @Test
    void setProbability_gueltigerWert_wirdGesetzt() {
        Precipitation p = new Precipitation(PrecipitationType.NONE, 10);
        p.setProbability_percent(50);
        assertEquals(50, p.getProbability_percent());
    }

    @Test
    void setProbability_ungueltigerWert_wirftIllegalArgumentException() {
        Precipitation p = new Precipitation(PrecipitationType.NONE, 10);
        assertThrows(IllegalArgumentException.class, () -> p.setProbability_percent(200));
    }

    // --- isLikely() ---

    @Test
    void isLikely_unter50Prozent_gibtFalse() {
        Precipitation p = new Precipitation(PrecipitationType.RAIN, 49);
        assertFalse(p.isLikely());
    }

    @Test
    void isLikely_genau50Prozent_gibtTrue() {
        Precipitation p = new Precipitation(PrecipitationType.RAIN, 50);
        assertTrue(p.isLikely());
    }

    @Test
    void isLikely_ueber50Prozent_gibtTrue() {
        Precipitation p = new Precipitation(PrecipitationType.RAIN, 80);
        assertTrue(p.isLikely());
    }

    @Test
    void isLikely_0Prozent_gibtFalse() {
        Precipitation p = new Precipitation(PrecipitationType.CLEAR, 0);
        assertFalse(p.isLikely());
    }

    @Test
    void isLikely_100Prozent_gibtTrue() {
        Precipitation p = new Precipitation(PrecipitationType.THUNDERSTORM, 100);
        assertTrue(p.isLikely());
    }

    // --- isSevere() ---

    @Test
    void isSevere_thunderstorm_gibtTrue() {
        Precipitation p = new Precipitation(PrecipitationType.THUNDERSTORM, 60);
        assertTrue(p.isSevere());
    }

    @Test
    void isSevere_snow_gibtTrue() {
        Precipitation p = new Precipitation(PrecipitationType.SNOW, 40);
        assertTrue(p.isSevere());
    }

    @Test
    void isSevere_rain_gibtFalse() {
        Precipitation p = new Precipitation(PrecipitationType.RAIN, 80);
        assertFalse(p.isSevere());
    }

    @Test
    void isSevere_clear_gibtFalse() {
        Precipitation p = new Precipitation(PrecipitationType.CLEAR, 0);
        assertFalse(p.isSevere());
    }

    @Test
    void isSevere_none_gibtFalse() {
        Precipitation p = new Precipitation(PrecipitationType.NONE, 0);
        assertFalse(p.isSevere());
    }

    // --- equals() & hashCode() ---

    @Test
    void equals_gleicheWerte_istGleich() {
        Precipitation p1 = new Precipitation(PrecipitationType.RAIN, 70);
        Precipitation p2 = new Precipitation(PrecipitationType.RAIN, 70);
        assertEquals(p1, p2);
    }

    @Test
    void equals_verschiedeneWahrscheinlichkeit_istNichtGleich() {
        Precipitation p1 = new Precipitation(PrecipitationType.RAIN, 70);
        Precipitation p2 = new Precipitation(PrecipitationType.RAIN, 30);
        assertNotEquals(p1, p2);
    }

    @Test
    void hashCode_gleicheObjekte_gleichHashCode() {
        Precipitation p1 = new Precipitation(PrecipitationType.SNOW, 90);
        Precipitation p2 = new Precipitation(PrecipitationType.SNOW, 90);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
