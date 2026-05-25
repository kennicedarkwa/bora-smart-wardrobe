package com.example.sea2_bora.implementation.wetter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse für Temperature.
 *
 * Prüft Konstruktor-Validierung, Setter-Validierung, Durchschnittsberechnung
 * sowie die Umrechnung in einen Temperatur-Index (0–5).
 */
class TemperatureTest {

    // --- Konstruktor ---

    @Test
    void konstruktor_gueltigeWerte_wirdErstellt() {
        Temperature t = new Temperature(5, 15);
        assertEquals(5, t.getMin_celsius());
        assertEquals(15, t.getMax_celsius());
    }

    @Test
    void konstruktor_gleicheWerte_wirdErstellt() {
        Temperature t = new Temperature(10, 10);
        assertEquals(10, t.getMin_celsius());
        assertEquals(10, t.getMax_celsius());
    }

    @Test
    void konstruktor_minGroesserAlsMax_wirftIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Temperature(20, 10));
    }

    @Test
    void konstruktor_negativerBereich_wirdErstellt() {
        Temperature t = new Temperature(-10, -5);
        assertEquals(-10, t.getMin_celsius());
        assertEquals(-5, t.getMax_celsius());
    }

    // --- setMin_celsius() ---

    @Test
    void setMin_minKleinerAlsMax_wirdGesetzt() {
        Temperature t = new Temperature(5, 20);
        t.setMin_celsius(10);
        assertEquals(10, t.getMin_celsius());
    }

    @Test
    void setMin_minGroesserAlsMax_wirftIllegalArgumentException() {
        Temperature t = new Temperature(5, 15);
        assertThrows(IllegalArgumentException.class, () -> t.setMin_celsius(20));
    }

    // --- setMax_celsius() ---

    @Test
    void setMax_maxGroesserAlsMin_wirdGesetzt() {
        Temperature t = new Temperature(5, 10);
        t.setMax_celsius(25);
        assertEquals(25, t.getMax_celsius());
    }

    @Test
    void setMax_maxKleinerAlsMin_wirftIllegalArgumentException() {
        Temperature t = new Temperature(10, 20);
        assertThrows(IllegalArgumentException.class, () -> t.setMax_celsius(5));
    }

    // --- getAverageCelsius() ---

    @Test
    void getAverageCelsius_gibtMittelwertZurueck() {
        Temperature t = new Temperature(10, 20);
        assertEquals(15.0, t.getAverageCelsius());
    }

    @Test
    void getAverageCelsius_gleicheWerte_gibtSelbigenWertZurueck() {
        Temperature t = new Temperature(8, 8);
        assertEquals(8.0, t.getAverageCelsius());
    }

    @Test
    void getAverageCelsius_negativerBereich_korrekt() {
        Temperature t = new Temperature(-10, 0);
        assertEquals(-5.0, t.getAverageCelsius());
    }

    // --- getTemperatureIndex() ---

    @Test
    void getTemperatureIndex_unterOderGleich0_gibtIndex0() {
        // avg = (-5 + -1) / 2 = -3 → Index 0
        Temperature t = new Temperature(-5, -1);
        assertEquals(0, t.getTemperatureIndex());
    }

    @Test
    void getTemperatureIndex_genau0_gibtIndex0() {
        Temperature t = new Temperature(0, 0);
        assertEquals(0, t.getTemperatureIndex());
    }

    @Test
    void getTemperatureIndex_1bis10_gibtIndex1() {
        // avg = (2 + 10) / 2 = 6 → Index 1
        Temperature t = new Temperature(2, 10);
        assertEquals(1, t.getTemperatureIndex());
    }

    @Test
    void getTemperatureIndex_11bis15_gibtIndex2() {
        // avg = (11 + 15) / 2 = 13 → Index 2
        Temperature t = new Temperature(11, 15);
        assertEquals(2, t.getTemperatureIndex());
    }

    @Test
    void getTemperatureIndex_16bis22_gibtIndex3() {
        // avg = (16 + 22) / 2 = 19 → Index 3
        Temperature t = new Temperature(16, 22);
        assertEquals(3, t.getTemperatureIndex());
    }

    @Test
    void getTemperatureIndex_23bis30_gibtIndex4() {
        // avg = (24 + 28) / 2 = 26 → Index 4
        Temperature t = new Temperature(24, 28);
        assertEquals(4, t.getTemperatureIndex());
    }

    @Test
    void getTemperatureIndex_ueber30_gibtIndex5() {
        // avg = (32 + 40) / 2 = 36 → Index 5
        Temperature t = new Temperature(32, 40);
        assertEquals(5, t.getTemperatureIndex());
    }

    // --- equals() & hashCode() ---

    @Test
    void equals_gleicheWerte_istGleich() {
        Temperature t1 = new Temperature(10, 20);
        Temperature t2 = new Temperature(10, 20);
        assertEquals(t1, t2);
    }

    @Test
    void equals_verschiedeneWerte_istNichtGleich() {
        Temperature t1 = new Temperature(10, 20);
        Temperature t2 = new Temperature(10, 25);
        assertNotEquals(t1, t2);
    }

    @Test
    void hashCode_gleicheObjekte_gleichHashCode() {
        Temperature t1 = new Temperature(5, 15);
        Temperature t2 = new Temperature(5, 15);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
