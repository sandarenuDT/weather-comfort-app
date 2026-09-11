package com.fidenz.weathercomfort.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidenz.weathercomfort.model.City;
import com.fidenz.weathercomfort.model.CityListWrapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CityLoader {

    private final ObjectMapper objectMapper;
    private List<City> cities;

    public CityLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void load() {
        try {
            ClassPathResource resource = new ClassPathResource("cities.json");
            CityListWrapper wrapper = objectMapper.readValue(resource.getInputStream(), CityListWrapper.class);
            this.cities = wrapper.getList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load cities.json", e);
        }
    }

    public List<City> getCities() {
        return cities;
    }
}
