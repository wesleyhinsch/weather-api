package com.weather.api.infrastructure.adapter.outbound.persistence;

import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WeatherPersistenceAdapter implements WeatherRepositoryPort {

    private final WeatherForecastJpaRepository jpaRepository;

    public WeatherPersistenceAdapter(WeatherForecastJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WeatherForecast save(WeatherForecast forecast) {
        WeatherForecastEntity entity = toEntity(forecast);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<WeatherForecast> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WeatherForecast> findByCity(String city) {
        return jpaRepository.findByCityIgnoreCase(city).stream().map(this::toDomain).toList();
    }

    @Override
    public List<WeatherForecast> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private WeatherForecastEntity toEntity(WeatherForecast domain) {
        WeatherForecastEntity entity = new WeatherForecastEntity();
        entity.setId(domain.getId());
        entity.setCity(domain.getCity());
        entity.setTemperature(domain.getTemperature());
        entity.setDescription(domain.getDescription());
        entity.setForecastDate(domain.getForecastDate());
        return entity;
    }

    private WeatherForecast toDomain(WeatherForecastEntity entity) {
        WeatherForecast domain = new WeatherForecast();
        domain.setId(entity.getId());
        domain.setCity(entity.getCity());
        domain.setTemperature(entity.getTemperature());
        domain.setDescription(entity.getDescription());
        domain.setForecastDate(entity.getForecastDate());
        return domain;
    }
}
