package com.fidenz.weathercomfort.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CityListWrapper {

    @JsonProperty("List")
    private List<City> list;
}
