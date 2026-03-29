package com.weather.api.domain.service;

import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherRepositoryPort;
import com.weather.api.domain.port.WeatherServicePort;

import java.util.List;
import java.util.Optional;

public class WeatherDomainService implements WeatherServicePort {

    private final WeatherRepositoryPort repository;

    public WeatherDomainService(WeatherRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public WeatherForecast create(WeatherForecast forecast) {
        return repository.save(forecast);
    }

    @Override
    public Optional<WeatherForecast> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<WeatherForecast> getByCity(String city) {
        return repository.findByCity(city);
    }

    @Override
    public List<WeatherForecast> getAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
