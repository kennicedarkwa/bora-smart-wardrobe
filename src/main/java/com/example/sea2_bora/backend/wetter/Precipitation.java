package com.example.sea2_bora.backend.wetter;

public class Precipitation {
        private PrecipitationType type;
        private int probability_percent;

        private  Precipitation(PrecipitationType type, int probability_percent) {
            this.type = type;
            this.probability_percent = probability_percent;
        }
    }

