package com.example.sea2_bora.implementation.wetter;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PrecipitationType {
    NONE,
    RAIN,
    DRIZZLE,
    SNOW,
    THUNDERSTORM,
    CLOUDY,
    CLEAR;

    @JsonCreator
    public static PrecipitationType fromString(String value) {
        if (value == null) return NONE;
        try {
            return PrecipitationType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }


}


