package com.example.sea2_bora.service;

import com.example.sea2_bora.DTO.Coordinates;
import com.example.sea2_bora.implementation.wetter.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class WeatherService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Optional<Coordinates> fetchCoordinatesForCity(String cityName) {
        try {
            String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
            String geocodeUrl = String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1", encodedCity);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(geocodeUrl))
                    // CHANGE THIS LINE BELOW: Make it unique and add your real email
                    .header("User-Agent", "Sea2BoraWeatherApp/1.0 (dadireddyjnanadha@gmail.com)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Logging
            System.out.println("[DIAGNOSTIC] 2. Geocode HTTP Status: " + response.statusCode());
            System.out.println("[DIAGNOSTIC] 3. Geocode Response Body: " + response.body());

            if (response.statusCode() == 200) {
                JsonNode arrayNode = objectMapper.readTree(response.body());
                if (arrayNode.isArray() && arrayNode.size() > 0) {
                    double lat = arrayNode.get(0).get("lat").asDouble();
                    double lon = arrayNode.get(0).get("lon").asDouble();
                    return Optional.of(new Coordinates(lat, lon));
                }
            }
        } catch (Exception e) {
            System.err.println("[DIAGNOSTIC ERROR] Geocoding exception: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<DailyForecast> getWeatherForCityAndDate(String cityName, String date) {
        Optional<Coordinates> liveCoords = fetchCoordinatesForCity(cityName);
        if (liveCoords.isEmpty()) return Optional.empty();

        Coordinates coords = liveCoords.get();

        try {
            // Using /weather with the date parameter to return the hourly array for that specific date
            String url = String.format(
                    "https://api.brightsky.dev/weather?lat=%s&lon=%s&date=%s",
                    coords.lat(), coords.lon(), date
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Optional.empty();

            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode weatherArray = rootNode.get("weather");

            if (weatherArray == null || !weatherArray.isArray() || weatherArray.size() == 0) {
                return Optional.empty();
            }

            // Process the 24 hours of that day into one single daily record
            return Optional.of(aggregateHoursToSingleDay(date, weatherArray));

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

//    private DailyForecast aggregateHoursToSingleDay(String date, JsonNode weatherArray) {
//        double minTemp = Double.MAX_VALUE;
//        double maxTemp = -Double.MAX_VALUE;
//        int maxPrecipProbability = 0;
//        int maxUvIndex = 0;
//        String dominantCondition = "dry";
//
//        for (JsonNode hourRecord : weatherArray) {
//            if (hourRecord.has("temperature") && !hourRecord.get("temperature").isNull()) {
//                double temp = hourRecord.get("temperature").asDouble();
//                minTemp = Math.min(minTemp, temp);
//                maxTemp = Math.max(maxTemp, temp);
//            }
//            if (hourRecord.has("precipitation_probability") && !hourRecord.get("precipitation_probability").isNull()) {
//                maxPrecipProbability = Math.max(maxPrecipProbability, hourRecord.get("precipitation_probability").asInt());
//            }
//            if (hourRecord.has("condition") && !hourRecord.get("condition").isNull()) {
//                String condition = hourRecord.get("condition").asText();
//                if (condition.equals("rain") || condition.equals("thunderstorm") || condition.equals("snow")) {
//                    dominantCondition = condition;
//                }
//            }
//            if (hourRecord.has("solar") && !hourRecord.get("solar").isNull()) {
//                int approxUv = (int) Math.min(Math.max(hourRecord.get("solar").asDouble() / 100.0, 0), 11);
//                maxUvIndex = Math.max(maxUvIndex, approxUv);
//            }
//        }
//
//        Temperature tempEntity = new Temperature((int) Math.floor(minTemp), (int) Math.ceil(maxTemp));
//        PrecipitationType precipType = switch (dominantCondition.toLowerCase()) {
//            case "rain" -> PrecipitationType.RAIN;
//            case "snow" -> PrecipitationType.SNOW;
//            case "thunderstorm" -> PrecipitationType.THUNDERSTORM;
//            default -> PrecipitationType.NONE;
//        };
//
//        return new DailyForecast(date, tempEntity, maxUvIndex, new Precipitation(precipType, maxPrecipProbability));
//    }


    private DailyForecast aggregateHoursToSingleDay(String date, JsonNode weatherArray) {
        double minTemp = Double.MAX_VALUE;
        double maxTemp = -Double.MAX_VALUE;
        String dominantCondition = "dry";

        double peakSolarInsideDaylight = 0.0;
        int totalPrecipProbabilitySum = 0;
        int daylightHoursCount = 0;
        int absoluteMaxPrecipProbability = 0;

        // Hier messen wir die exakte Regenmenge des Tages
        double totalRealPrecipitationMm = 0.0;

        for (JsonNode hourRecord : weatherArray) {
            if (hourRecord.has("temperature") && !hourRecord.get("temperature").isNull()) {
                double temp = hourRecord.get("temperature").asDouble();
                minTemp = Math.min(minTemp, temp);
                maxTemp = Math.max(maxTemp, temp);
            }

            if (hourRecord.has("condition") && !hourRecord.get("condition").isNull()) {
                String condition = hourRecord.get("condition").asText();
                if (condition.equals("rain") || condition.equals("thunderstorm") || condition.equals("snow")) {
                    dominantCondition = condition;
                }
            }

            boolean isDaylight = false;
            if (hourRecord.has("solar") && !hourRecord.get("solar").isNull()) {
                double currentSolar = hourRecord.get("solar").asDouble();
                if (currentSolar > 0.0) {
                    isDaylight = true;
                    if (currentSolar > peakSolarInsideDaylight) {
                        peakSolarInsideDaylight = currentSolar;
                    }
                }
            }

            // Wahrscheinlichkeit für die Zukunft
            if (hourRecord.has("precipitation_probability") && !hourRecord.get("precipitation_probability").isNull()) {
                int hourlyProbability = hourRecord.get("precipitation_probability").asInt();
                absoluteMaxPrecipProbability = Math.max(absoluteMaxPrecipProbability, hourlyProbability);
                if (isDaylight) {
                    totalPrecipProbabilitySum += hourlyProbability;
                    daylightHoursCount++;
                }
            }

            // Reale Regenmenge für die Vergangenheit aufsummieren
            if (hourRecord.has("precipitation") && !hourRecord.get("precipitation").isNull()) {
                totalRealPrecipitationMm += hourRecord.get("precipitation").asDouble();
            }
        }

        // --- REGEN-LOGIK ---
        int finalPrecipProbability = 0;
        double finalPrecipAmountMm = totalRealPrecipitationMm; // Wir speichern die echten mm

        // Wenn wir in der Vergangenheit sind (keine Vorhersage-Wahrscheinlichkeiten vorhanden)
        if (absoluteMaxPrecipProbability == 0 && totalRealPrecipitationMm > 0.0) {
            finalPrecipProbability = 0; // Bleibt sauber bei 0%, da es keine Vorhersage mehr ist
        } else {
            // Standard-Zukunftsberechnung (Prozent)
            if (daylightHoursCount > 0 && totalPrecipProbabilitySum > 0) {
                finalPrecipProbability = totalPrecipProbabilitySum / daylightHoursCount;
            } else {
                finalPrecipProbability = absoluteMaxPrecipProbability;
            }
        }

        // --- UV-BERECHNUNG ---
        int finalSafeUvIndex = 0;
        if (peakSolarInsideDaylight > 0) {
            double convertedW_m2 = peakSolarInsideDaylight < 15.0 ? peakSolarInsideDaylight * 1000.0 : peakSolarInsideDaylight;
            int computedUv = (int) Math.round(convertedW_m2 / 230.0);
            finalSafeUvIndex = Math.max(0, Math.min(computedUv, 11));
        }

        Temperature tempEntity = new Temperature((int) Math.floor(minTemp), (int) Math.ceil(maxTemp));
        PrecipitationType precipType = switch (dominantCondition.toLowerCase()) {
            case "rain" -> PrecipitationType.RAIN;
            case "snow" -> PrecipitationType.SNOW;
            case "thunderstorm" -> PrecipitationType.THUNDERSTORM;
            default -> PrecipitationType.NONE;
        };

        return new DailyForecast(date, tempEntity, finalSafeUvIndex, new Precipitation(precipType, finalPrecipProbability));
    }
}