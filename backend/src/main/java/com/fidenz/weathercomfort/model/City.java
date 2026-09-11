package com.fidenz.weathercomfort.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class City {

    @JsonProperty("CityCode")
    private String cityCode;

    @JsonProperty("CityName")
    private String cityName;

    @JsonProperty("Temp")
    private String temp;

    @JsonProperty("Status")
    private String status;
}
