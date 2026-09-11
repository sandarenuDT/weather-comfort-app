
package com.fidenz.weathercomfort.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ForecastService {

    private static final String FORECAST_URL =
            "https://api.openweathermap.org/data/2.5/forecast";

    private static final DateTimeFormatter LABEL_FORMAT =
            DateTimeFormatter.ofPattern("EEE HH:mm", Locale.ENGLISH)
                    .withZone(ZoneOffset.UTC);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openweather.api-key}")
    private String apiKey;

    public List<ForecastPoint> getForecast(String cityCode) {

        String url = UriComponentsBuilder
                .fromUriString(FORECAST_URL)
                .queryParam("id", cityCode)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        try {
            // Get OpenWeather response as plain text
            String response = restTemplate.getForObject(url, String.class);

            // Parse JSON manually
            JsonNode root = objectMapper.readTree(response);

            List<ForecastPoint> points = new ArrayList<>();

            if (root != null && root.has("list")) {

                for (JsonNode entry : root.get("list")) {

                    long dt = entry.get("dt").asLong();

                    double temp = entry
                            .get("main")
                            .get("temp")
                            .asDouble();

                    String label = LABEL_FORMAT.format(
                            Instant.ofEpochSecond(dt)
                    );

                    points.add(
                            new ForecastPoint(
                                    dt,
                                    Math.round(temp * 10) / 10.0,
                                    label
                            )
                    );
                }
            }

            return points;

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to fetch weather forecast: " + ex.getMessage(),
                    ex
            );
        }
    }

    public static class ForecastPoint {

        private long time;
        private double temp;
        private String label;

        public ForecastPoint(long time, double temp, String label) {
            this.time = time;
            this.temp = temp;
            this.label = label;
        }

        public long getTime() {
            return time;
        }

        public double getTemp() {
            return temp;
        }

        public String getLabel() {
            return label;
        }
    }
}