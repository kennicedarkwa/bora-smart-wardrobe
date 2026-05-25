package com.example.sea2_bora.implementation.wetter;

import java.util.Objects;

public class Temperature {
    private int min_celsius;
    private int max_celsius;


    public Temperature() {
    }

    public Temperature(int min_celsius, int max_celsius) {

        if (min_celsius > max_celsius) {

            throw new IllegalArgumentException("Minimum temperature cannot exceed maximum temperature.");

        }

        this.min_celsius = min_celsius;

        this.max_celsius = max_celsius;

    }

    public int getMin_celsius() {

        return min_celsius;

    }

    public void setMin_celsius(int min_celsius) {

        if (min_celsius > this.max_celsius && this.max_celsius != 0) {

            throw new IllegalArgumentException("Minimum temperature cannot exceed maximum temperature.");

        }

        this.min_celsius = min_celsius;

    }

    public int getMax_celsius() {

        return max_celsius;

    }

    public void setMax_celsius(int max_celsius) {

        if (max_celsius < this.min_celsius) {

            throw new IllegalArgumentException("Maximum temperature cannot be below minimum temperature.");

        }

        this.max_celsius = max_celsius;

    }

    public double getAverageCelsius() {

        return (min_celsius + max_celsius) / 2.0;

    }

    /**
     * Converts temperature into an index from 0 to 5.
     * <p>
     * Scale:
     * 0 = <= 0°C
     * 1 = 1–10°C
     * 2 = 11–15°C
     * 3 = 16–22°C
     * 4 = 23–30°C
     * 5 = > 30°C
     */

    public int getTemperatureIndex() {

        double avg = getAverageCelsius();

        if (avg <= 0) {

            return 0;

        } else if (avg <= 10) {

            return 1;

        } else if (avg <= 15) {

            return 2;

        } else if (avg <= 22) {

            return 3;

        } else if (avg <= 30) {

            return 4;

        } else {

            return 5;

        }

    }

    @Override
    public String toString() {

        return "Temperature{" +

                "min_celsius=" + min_celsius +

                ", max_celsius=" + max_celsius +

                ", index=" + getTemperatureIndex() +

                '}';

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Temperature)) return false;

        Temperature that = (Temperature) o;

        return min_celsius == that.min_celsius &&

                max_celsius == that.max_celsius;

    }

    @Override
    public int hashCode() {

        return Objects.hash(min_celsius, max_celsius);

    }
}
