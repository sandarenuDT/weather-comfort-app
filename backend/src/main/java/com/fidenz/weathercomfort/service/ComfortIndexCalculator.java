package com.fidenz.weathercomfort.service;

import org.springframework.stereotype.Component;

@Component
public class ComfortIndexCalculator {

    private static final double TEMP_WEIGHT = 0.45;
    private static final double HUMIDITY_WEIGHT = 0.35;
    private static final double WIND_WEIGHT = 0.20;

    public int calculate(double tempCelsius, int humidityPercent, double windSpeedMs) {
        double tempScore = temperatureScore(tempCelsius);
        double humidityScore = humidityScore(humidityPercent);
        double windScore = windScore(windSpeedMs);

        double total = (tempScore * TEMP_WEIGHT)
                + (humidityScore * HUMIDITY_WEIGHT)
                + (windScore * WIND_WEIGHT);

        return (int) Math.round(clamp(total, 0, 100));
    }

    private double temperatureScore(double temp) {
        double idealLow = 18, idealHigh = 24;
        if (temp >= idealLow && temp <= idealHigh) return 100;
        double distance = temp < idealLow ? idealLow - temp : temp - idealHigh;
        // lose 4 points per degree outside the ideal band
        return clamp(100 - (distance * 4), 0, 100);
    }

    private double humidityScore(int humidity) {
        double idealLow = 30, idealHigh = 60;
        if (humidity >= idealLow && humidity <= idealHigh) return 100;
        double distance = humidity < idealLow ? idealLow - humidity : humidity - idealHigh;
        // lose 2 points per % outside the ideal band
        return clamp(100 - (distance * 2), 0, 100);
    }

    private double windScore(double windSpeed) {
        double idealMax = 5.0;
        if (windSpeed <= idealMax) return 100;
        double distance = windSpeed - idealMax;
        // lose 8 points per m/s above the calm threshold
        return clamp(100 - (distance * 8), 0, 100);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
