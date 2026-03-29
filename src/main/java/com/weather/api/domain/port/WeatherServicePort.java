package com.weather.api.domain.port;

import com.weather.api.domain.model.WeatherForecast;

import java.util.List;
import java.util.Optional;

public interface WeatherServicePort {

    WeatherForecast create(WeatherForecast forecast);

    Optional<WeatherForecast> getById(Long id);

    List<WeatherForecast> getByCity(String city);

    List<WeatherForecast> getAll();

    void delete(Long id);
}
