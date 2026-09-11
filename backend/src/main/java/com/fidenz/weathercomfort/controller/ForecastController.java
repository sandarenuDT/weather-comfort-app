package com.fidenz.weathercomfort.controller;

import com.fidenz.weathercomfort.service.ForecastService;
import com.fidenz.weathercomfort.service.ForecastService.ForecastPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/weather")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping("/forecast/{cityCode}")
    public ResponseEntity<List<ForecastPoint>> getForecast(@PathVariable String cityCode) {
        return ResponseEntity.ok(forecastService.getForecast(cityCode));
    }
}