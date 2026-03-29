package com.weather.api.infrastructure.config;

import com.weather.api.domain.port.WeatherRepositoryPort;
import com.weather.api.domain.port.WeatherServicePort;
import com.weather.api.domain.service.WeatherDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public WeatherServicePort weatherServicePort(WeatherRepositoryPort repositoryPort) {
        return new WeatherDomainService(repositoryPort);
    }
}
