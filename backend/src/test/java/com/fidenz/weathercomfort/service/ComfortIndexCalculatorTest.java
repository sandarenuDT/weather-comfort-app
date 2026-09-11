package com.fidenz.weathercomfort.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComfortIndexCalculatorTest {

    private final ComfortIndexCalculator calculator = new ComfortIndexCalculator();

    @Test
    void idealConditions_shouldScorePerfectOrNear() {
        int score = calculator.calculate(21.0, 45, 2.0);
        assertEquals(100, score);
    }

    @Test
    void extremeHeat_shouldScoreLow() {
        int idealScore = calculator.calculate(21.0, 45, 2.0);
        int hotScore = calculator.calculate(40.0, 45, 2.0);
        assertTrue(hotScore < idealScore, "Expected extreme heat to score lower than ideal, got " + hotScore);
    }

    @Test
    void highHumidity_shouldReduceScore() {
        int dryScore = calculator.calculate(21.0, 45, 2.0);
        int humidScore = calculator.calculate(21.0, 90, 2.0);
        assertTrue(humidScore < dryScore);
    }

    @Test
    void strongWind_shouldReduceScore() {
        int calmScore = calculator.calculate(21.0, 45, 2.0);
        int windyScore = calculator.calculate(21.0, 45, 15.0);
        assertTrue(windyScore < calmScore);
    }

    @Test
    void scoreNeverExceedsBounds() {
        int score = calculator.calculate(-20.0, 100, 30.0);
        assertTrue(score >= 0 && score <= 100);
    }
}
