package com.weather.api.infrastructure.adapter.inbound.rest;

import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherProviderPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forecast")
public class WeatherController {

    private final WeatherProviderPort weatherProvider;

    public WeatherController(WeatherProviderPort weatherProvider) {
        this.weatherProvider = weatherProvider;
    }

    @GetMapping("/current")
    public ResponseEntity<WeatherForecast> getCurrentWeather(@RequestParam String city, @RequestParam String uf) {
        return ResponseEntity.ok(weatherProvider.getCurrentWeather(city, uf));
    }
}
