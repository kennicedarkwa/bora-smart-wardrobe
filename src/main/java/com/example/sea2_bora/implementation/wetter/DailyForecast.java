package com.example.sea2_bora.implementation.wetter;

import java.util.Objects;

/**
 * Represents the weather forecast for a single day.
 */
public class DailyForecast {

    private String date;
    private Temperature temperature;
    private int uv_index;
    private Precipitation precipitation;

    // Default constructor for JSON frameworks
    public DailyForecast() {
    }

    public DailyForecast(String date, Temperature temperature, int uv_index, Precipitation precipitation) {
        this.date = date;
        this.temperature = temperature;
        setUv_index(uv_index);
        this.precipitation = precipitation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public int getUv_index() {
        return uv_index;
    }

    public void setUv_index(int uv_index) {
        if (uv_index < 0 || uv_index > 11) {
            throw new IllegalArgumentException("UV index must be between 0 and 11.");
        }
        this.uv_index = uv_index;
    }

    public Precipitation getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Precipitation precipitation) {
        this.precipitation = precipitation;
    }

    /**
     * Returns a simplified UV category.
     * <p>
     * 0–2   = Low
     * 3–5   = Moderate
     * 6–7   = High
     * 8–10  = Very High
     * 11+   = Extreme
     */
    public String getUvCategory() {
        if (uv_index <= 2) {
            return "Low";
        } else if (uv_index <= 5) {
            return "Moderate";
        } else if (uv_index <= 7) {
            return "High";
        } else if (uv_index <= 10) {
            return "Very High";
        } else {
            return "Extreme";
        }
    }


    @Override
    public String toString() {
        return "DailyForecast{" +
                "date='" + date + '\'' +
                ", temperature=" + temperature +
                ", uv_index=" + uv_index +
                ", uvCategory='" + getUvCategory() + '\'' +
                ", precipitation=" + precipitation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyForecast)) return false;
        DailyForecast that = (DailyForecast) o;
        return uv_index == that.uv_index &&
                Objects.equals(date, that.date) &&
                Objects.equals(temperature, that.temperature) &&
                Objects.equals(precipitation, that.precipitation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, temperature, uv_index, precipitation);
    }
}