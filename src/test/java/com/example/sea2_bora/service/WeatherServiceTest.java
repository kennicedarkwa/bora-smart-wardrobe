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

    // -----------------------------------------------------------------------
    // Hilfsmethode: baut einen Mock-Response
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private HttpResponse<String> mockResponse(int status, String body) {
        HttpResponse<String> r = Mockito.mock(HttpResponse.class);
        when(r.statusCode()).thenReturn(status);
        when(r.body()).thenReturn(body);
        return r;
    }

    // -----------------------------------------------------------------------
    // testGetWeatherForCityAndDate_Success
    //
    // GEÄNDERT (v11): aggregateHoursToSingleDay berechnet jetzt:
    //   - precipitation_probability: Ø der Tageslicht-Stunden (solar > 0)
    //     → beide Stunden haben solar > 0 → (10 + 80) / 2 = 45
    //   - uv_index: peakSolar = 500, convertedW_m2 = 500 (>15 → kein *1000),
    //     computedUv = round(500 / 230.0) = round(2.17) = 2
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_Success() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";

        // Zwei Stunden: beide mit Tageslicht (solar > 0)
        // precipProb: 10 und 80 → Tageslicht-Durchschnitt = (10+80)/2 = 45
        // peakSolar  = 500 → UV = round(500/230) = 2
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

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, geocodeJson), mockResponse(200, weatherJson));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Berlin", "2026-06-08");

        assertTrue(result.isPresent());
        DailyForecast forecast = result.get();

        assertEquals("2026-06-08", forecast.getDate());
        // Temperatur: floor(15.5)=15 und ceil(22.1)=23
        assertEquals(15, forecast.getTemperature().getMin_celsius());
        assertEquals(23, forecast.getTemperature().getMax_celsius());
        // Regen-Typ: "rain" dominiert
        assertEquals(PrecipitationType.RAIN, forecast.getPrecipitation().getType());
        // Wahrscheinlichkeit: Tageslicht-Durchschnitt (10+80)/2 = 45
        assertEquals(45, forecast.getPrecipitation().getProbability_percent());
        // UV: peak=500, 500/230 ≈ 2.17 → round → 2
        assertEquals(2, forecast.getUv_index());
    }

    // -----------------------------------------------------------------------
    // Nur eine Nacht-Stunde (solar=0) → kein Tageslicht-Zähler,
    // absoluteMaxPrecipProbability greift → probability = 70
    // UV bleibt 0, weil peakSolarInsideDaylight = 0
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_NurNachtstunden_KeineTageslichtDurchschnitt() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";
        String weatherJson = """
                {
                    "weather": [
                        {
                            "temperature": 10.0,
                            "precipitation_probability": 70,
                            "condition": "rain",
                            "solar": 0.0
                        }
                    ]
                }
                """;

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, geocodeJson), mockResponse(200, weatherJson));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Hamburg", "2026-01-10");

        assertTrue(result.isPresent());
        DailyForecast forecast = result.get();
        // absoluteMaxPrecipProbability = 70, daylightHoursCount = 0 → Fallback
        assertEquals(70, forecast.getPrecipitation().getProbability_percent());
        assertEquals(0, forecast.getUv_index());
    }

    // -----------------------------------------------------------------------
    // Vergangenheitsdaten: precipitation_probability fehlt (=0), aber
    // echte Regenmenge > 0 → finalPrecipProbability bleibt 0 (kein Forecast)
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_Vergangenheit_EchteRegenmenge_ProbabilityBleibt0() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":53.5500,\"lon\":10.0000}]";
        String weatherJson = """
                {
                    "weather": [
                        {
                            "temperature": 12.0,
                            "precipitation": 3.5,
                            "condition": "rain",
                            "solar": 100.0
                        }
                    ]
                }
                """;

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, geocodeJson), mockResponse(200, weatherJson));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Hamburg", "2025-11-01");

        assertTrue(result.isPresent());
        // Keine Vorhersage-Wahrscheinlichkeit → bleibt 0
        assertEquals(0, result.get().getPrecipitation().getProbability_percent());
        assertEquals(PrecipitationType.RAIN, result.get().getPrecipitation().getType());
    }

    // -----------------------------------------------------------------------
    // Geocoding schlägt fehl → Optional.empty()
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_GeocodingFails() throws IOException, InterruptedException {
        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, "[]"));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("UnknownCity", "2026-06-08");

        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // BrightSky antwortet mit leerer Liste → Optional.empty()
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_BrightSkyEmpty() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, geocodeJson), mockResponse(200, "{ \"weather\": [] }"));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Berlin", "2026-06-08");

        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // BrightSky antwortet mit HTTP 500 → Optional.empty()
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_BrightSkyHttpError() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, geocodeJson), mockResponse(500, ""));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Berlin", "2026-06-08");

        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // Sehr hoher Solar-Wert (kleine Einheit < 15 → wird *1000) → UV capped bei 11
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_UvIndex_CappedBei11() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";
        // solar=10.0 < 15 → convertedW_m2 = 10*1000 = 10000 → computedUv = round(10000/230) = 43 → capped 11
        String weatherJson = """
                {
                    "weather": [
                        {
                            "temperature": 30.0,
                            "condition": "dry",
                            "solar": 10.0
                        }
                    ]
                }
                """;

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, geocodeJson), mockResponse(200, weatherJson));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Wüste", "2026-07-15");

        assertTrue(result.isPresent());
        assertEquals(11, result.get().getUv_index());
    }

    // -----------------------------------------------------------------------
    // Schneebedingung → PrecipitationType.SNOW
    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Test
    void testGetWeatherForCityAndDate_SnowCondition() throws IOException, InterruptedException {
        String geocodeJson = "[{\"lat\":52.5200,\"lon\":13.4050}]";
        String weatherJson = """
                {
                    "weather": [
                        {
                            "temperature": -2.0,
                            "condition": "snow",
                            "solar": 50.0
                        }
                    ]
                }
                """;

        when(httpClient.send(
                ArgumentMatchers.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse(200, geocodeJson), mockResponse(200, weatherJson));

        Optional<DailyForecast> result = weatherService.getWeatherForCityAndDate("Berlin", "2026-01-20");

        assertTrue(result.isPresent());
        assertEquals(PrecipitationType.SNOW, result.get().getPrecipitation().getType());
    }
}