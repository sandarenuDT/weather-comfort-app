package com.fidenz.weathercomfort.service;

import com.fidenz.weathercomfort.model.City;
import com.fidenz.weathercomfort.model.dto.CityComfortDto;
import com.fidenz.weathercomfort.model.dto.WeatherResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final CityLoader cityLoader;
    private final OpenWeatherClient openWeatherClient;
    private final ComfortIndexCalculator comfortIndexCalculator;

    public WeatherService(CityLoader cityLoader,
                           OpenWeatherClient openWeatherClient,
                           ComfortIndexCalculator comfortIndexCalculator) {
        this.cityLoader = cityLoader;
        this.openWeatherClient = openWeatherClient;
        this.comfortIndexCalculator = comfortIndexCalculator;
    }

    public List<CityComfortDto> getRankedComfortList() {
        List<City> cities = cityLoader.getCities();

        List<CityComfortDto> results = cities.stream()
                .map(this::toComfortDto)
                .sorted(Comparator.comparingInt(CityComfortDto::getComfortScore).reversed())
                .collect(Collectors.toList());

        // assign rank after sorting (1 = most comfortable)
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setRank(i + 1);
        }
        return results;
    }

    private CityComfortDto toComfortDto(City city) {
        WeatherResponse weather = openWeatherClient.fetchWeather(city.getCityCode());

        double temp = weather.getMain().getTemp();
        int humidity = weather.getMain().getHumidity();
        double windSpeed = weather.getWind().getSpeed();
        String description = weather.getWeather().isEmpty()
                ? "Unknown"
                : weather.getWeather().get(0).getDescription();

        int score = comfortIndexCalculator.calculate(temp, humidity, windSpeed);

        return new CityComfortDto(
                city.getCityCode(),
                city.getCityName(),
                description,
                temp,
                humidity,
                windSpeed,
                score,
                0
        );
    }
}
