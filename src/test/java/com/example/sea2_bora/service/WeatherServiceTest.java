package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.implementation.wetter.PrecipitationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "httpClient", httpClient);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_Success() throws IOException, InterruptedException {
        // 1. Setup Nominatim Geocoding Mock Response
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";
        HttpResponse<String> geocodeResponse = Mockito.mock(HttpResponse.class);
        when(geocodeResponse.statusCode()).thenReturn(200);
        when(geocodeResponse.body()).thenReturn(geocodeJson);

        // 2. Setup BrightSky Weather Mock Response
        String weatherJson = """
                {
                    "weather": [
                        {
                            "temperature": 15.5,
                            "precipitation_probability": 10,
                            "condition": "dry",
                            "solar": 200.0
                        },
                        {
                            "temperature": 22.1,
                            "precipitation_probability": 80,
                            "condition": "rain",
                            "solar": 500.0
                        }
                    ]
                }
                """;
        HttpResponse<String> brightSkyResponse = Mockito.mock(HttpResponse.class);
        when(brightSkyResponse.statusCode()).thenReturn(200);
        when(brightSkyResponse.body()).thenReturn(weatherJson);

        // 3. Stub sequential calls
        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(geocodeResponse, brightSkyResponse);

        // Act
        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Berlin", "2026-06-08");

        // Assert
        assertTrue(result.isPresent(), "Forecast should be present");
        DailyForecast forecast = result.get();

        assertEquals("2026-06-08", forecast.getDate());
        assertNotNull(forecast.getTemperature());
        assertEquals(15, forecast.getTemperature().getMin_celsius());
        assertEquals(23, forecast.getTemperature().getMax_celsius());
        assertNotNull(forecast.getPrecipitation());
        assertEquals(80, forecast.getPrecipitation().getProbability_percent());
        assertEquals(PrecipitationType.RAIN, forecast.getPrecipitation().getType());
        assertEquals(5, forecast.getUv_index());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_GeocodingFails() throws IOException, InterruptedException {
        HttpResponse<String> geocodeResponse = Mockito.mock(HttpResponse.class);
        when(geocodeResponse.statusCode()).thenReturn(200);
        when(geocodeResponse.body()).thenReturn("[]");

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(geocodeResponse);

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("UnknownCity", "2026-06-08");

        assertTrue(result.isEmpty(), "Result should be empty when geocoding fails");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_BrightSkyEmpty() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";
        HttpResponse<String> geocodeResponse = Mockito.mock(HttpResponse.class);
        when(geocodeResponse.statusCode()).thenReturn(200);
        when(geocodeResponse.body()).thenReturn(geocodeJson);

        String weatherJson = "{ \"weather\": [] }";
        HttpResponse<String> brightSkyResponse = Mockito.mock(HttpResponse.class);
        when(brightSkyResponse.statusCode()).thenReturn(200);
        when(brightSkyResponse.body()).thenReturn(weatherJson);

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(geocodeResponse, brightSkyResponse);

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Berlin", "2026-06-08");

        assertTrue(result.isEmpty(), "Result should be empty when BrightSky returns no data");
    }
}