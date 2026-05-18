package com.example.sea2_bora.implementation.wetter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Root weather model containing all cities.
 */
public class Weather {

    private List<CityWeather> cities;

    // Default constructor for JSON frameworks (Jackson, etc.)
    public Weather() {
    }

    public Weather(List<CityWeather> cities) {
        this.cities = cities;
    }

    public List<CityWeather> getCities() {
        return cities;
    }

    public void setCities(List<CityWeather> cities) {
        this.cities = cities;
    }

    /**
     * Returns all available city names.
     */
    public List<String> getCityNames() {
        return cities.stream()
                .map(CityWeather::getCity)
                .toList();
    }

    /**
     * Finds a city by name (case-insensitive).
     */
    public Optional<CityWeather> findCity(String cityName) {
        if (cities == null) return Optional.empty();

        return cities.stream()
                .filter(c -> c.getCity().equalsIgnoreCase(cityName))
                .findFirst();
    }

    /**
     * Checks if dataset contains a city.
     */
    public boolean containsCity(String cityName) {
        return findCity(cityName).isPresent();
    }

    /**
     * Returns number of cities in dataset.
     */
    public int size() {
        return cities == null ? 0 : cities.size();
    }

    @Override
    public String toString() {
        return "Weather{" +
                "cities=" + (cities != null ? cities.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weather)) return false;
        Weather weather = (Weather) o;
        return Objects.equals(cities, weather.cities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cities);
    }
}