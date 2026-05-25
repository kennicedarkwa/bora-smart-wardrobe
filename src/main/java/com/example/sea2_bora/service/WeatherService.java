package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.wetter.CityWeather;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.implementation.wetter.Weather;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {

    private Weather weather;

    public WeatherService() {
        loadWeatherFromFile();
    }

    /**
     * Loads weather.json from resources folder
     */
    private void loadWeatherFromFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("wetter.json");

            if (is == null) {
                throw new RuntimeException("wetter.json NOT found on classpath");
            }

            this.weather = mapper.readValue(is, Weather.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load wetter.json", e);
        }
    }


    public Weather getWeather() {
        return weather;
    }

    public List<CityWeather> getAllCities() {
        return weather.getCities();
    }

    public Optional<CityWeather> getCity(String cityName) {
        return weather.getCities()
                .stream()
                .filter(c -> c.getCity().equalsIgnoreCase(cityName))
                .findFirst();
    }

    public List<DailyForecast> getForecasts(String cityName) {
        return getCity(cityName)
                .map(CityWeather::getDailyForecast)
                .orElse(List.of());
    }

    public Optional<DailyForecast> getForecastByDate(String cityName, String date) {
        return getCity(cityName)
                .flatMap(city -> city.getDailyForecast()
                        .stream()
                        .filter(f -> f.getDate().equals(date))
                        .findFirst());
    }




}