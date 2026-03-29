package com.weather.api.domain.port;

import com.weather.api.domain.model.WeatherForecast;

import java.util.List;
import java.util.Optional;

public interface WeatherRepositoryPort {

    WeatherForecast save(WeatherForecast forecast);

    Optional<WeatherForecast> findById(Long id);

    List<WeatherForecast> findByCity(String city);

    List<WeatherForecast> findAll();

    void deleteById(Long id);
}
