package com.fidenz.weathercomfort.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityComfortDto {

    private String cityCode;
    private String cityName;
    private String description;
    private double temperature;
    private int humidity;
    private double windSpeed;
    private int comfortScore;
    private int rank;
}
