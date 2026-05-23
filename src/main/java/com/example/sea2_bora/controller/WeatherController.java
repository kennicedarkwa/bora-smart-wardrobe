package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.wetter.CityWeather;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.implementation.wetter.Weather;
import com.example.sea2_bora.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {


    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {

        this.weatherService = weatherService;

    }



    @GetMapping("/{city}/{date}")
    public ResponseEntity<DailyForecast> getWeatherByCityAndDate(

            @PathVariable String city,

            @PathVariable String date) {

        Optional<CityWeather> cityWeather = weatherService.getCity(city);

        if (cityWeather.isEmpty()) {

            return ResponseEntity.notFound().build();

        }

        Optional<DailyForecast> forecast = cityWeather.get()

                .getDailyForecast()

                .stream()

                .filter(f -> f.getDate().equals(date))

                .findFirst();

        return forecast

                .map(ResponseEntity::ok)

                .orElse(ResponseEntity.notFound().build());

    }
}
