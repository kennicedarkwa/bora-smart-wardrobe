package com.example.sea2_bora.implementation.wetter;

import java.util.List;
/*** Represents weather of a city */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CityWeather {
    private String city;

    @JsonProperty("daily_forecast")
    private List<DailyForecast> daily_forecast;

    public String getCity() {

        return city;

    }

    public void setCity(String city) {

        this.city = city;

    }

    public List<DailyForecast> getDailyForecast() {

        return daily_forecast;

    }

    public void setDailyForecast(List<DailyForecast> dailyWeathers) {

        this.daily_forecast = dailyWeathers;

    }


}
