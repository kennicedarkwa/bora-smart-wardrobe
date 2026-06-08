package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*") // Allows your frontend file to read data directly
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}/{date}")
    public ResponseEntity<DailyForecast> getWeather(@PathVariable String city, @PathVariable String date) {
        return weatherService.getWeatherForCityAndDate(city, date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}