package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.wetter.Weather;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MocktestWetter {



        @Test
        void testGetWeather_containsWeatherInfo() {
            com.example.sea2_bora.backend.wetter.WeatherService weatherService = new com.example.sea2_bora.backend.wetter.WeatherService();

            Weather result = weatherService.getWeather();

            assertNotNull(result);
            assertTrue(
                    result.toLowerCase().contains("temp") ||
                            result.toLowerCase().contains("weather") ||
                            result.toLowerCase().contains("humidity")
            );
        }

}
