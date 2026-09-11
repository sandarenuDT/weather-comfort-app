package com.fidenz.weathercomfort.service;

import com.fidenz.weathercomfort.config.CacheConfig;
import com.fidenz.weathercomfort.exception.WeatherApiException;
import com.fidenz.weathercomfort.model.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenWeatherClient {

    private final RestClient restClient;

    @Value("${openweather.api-key}")
    private String apiKey;

    @Value("${openweather.base-url}")
    private String baseUrl;

    public OpenWeatherClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Cacheable(value = CacheConfig.WEATHER_CACHE, key = "#cityCode")
    public WeatherResponse fetchWeather(String cityCode) {
        try {
            return restClient.get()
                    .uri(baseUrl + "?id={id}&appid={key}&units=metric", cityCode, apiKey)
                    .retrieve()
                    .body(WeatherResponse.class);
        } catch (RestClientException ex) {
            throw new WeatherApiException("Failed to fetch weather for city " + cityCode, ex);
        }
    }
}
