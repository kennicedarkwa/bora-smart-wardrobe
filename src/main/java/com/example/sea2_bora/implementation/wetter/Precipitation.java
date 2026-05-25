package com.example.sea2_bora.implementation.wetter;

import java.util.Objects;

public class Precipitation {
        private PrecipitationType type;
        private int probability_percent;


    public Precipitation() {
    }

    public Precipitation(PrecipitationType type, int probability_percent) {

        this.type = type;

        setProbability_percent(probability_percent);

    }

    public PrecipitationType getType() {

        return type;

    }

    public void setType(PrecipitationType type) {

        this.type = type;

    }

    public int getProbability_percent() {

        return probability_percent;

    }

    public void setProbability_percent(int probability_percent) {

        if (probability_percent < 0 || probability_percent > 100) {

            throw new IllegalArgumentException("Probability must be between 0 and 100.");

        }

        this.probability_percent = probability_percent;

    }

    public boolean isLikely() {

        return probability_percent >= 50;

    }

    public boolean isSevere() {

        return type == PrecipitationType.THUNDERSTORM || type == PrecipitationType.SNOW;

    }

    @Override

    public String toString() {

        return "Precipitation{" +

                "type=" + type +

                ", probability_percent=" + probability_percent +

                '}';

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Precipitation)) return false;

        Precipitation that = (Precipitation) o;

        return probability_percent == that.probability_percent &&

                type == that.type;

    }

    @Override
    public int hashCode() {

        return Objects.hash(type, probability_percent);

    }
    }

