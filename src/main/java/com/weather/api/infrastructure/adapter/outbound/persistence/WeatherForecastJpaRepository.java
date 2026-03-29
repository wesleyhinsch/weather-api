package com.weather.api.infrastructure.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherForecastJpaRepository extends JpaRepository<WeatherForecastEntity, Long> {

    List<WeatherForecastEntity> findByCityIgnoreCase(String city);
}
