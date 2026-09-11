package com.fidenz.weathercomfort.controller;

import com.fidenz.weathercomfort.model.dto.CityComfortDto;
import com.fidenz.weathercomfort.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public List<CityComfortDto> getRankedWeather() {
        return weatherService.getRankedComfortList();
    }
}
