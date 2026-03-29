package com.weather.api.infrastructure.adapter.inbound.rest;

import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherServicePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forecast")
public class WeatherController {

    private final WeatherServicePort weatherService;

    public WeatherController(WeatherServicePort weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping
    public ResponseEntity<WeatherForecast> create(@RequestBody WeatherForecast forecast) {
        return ResponseEntity.ok(weatherService.create(forecast));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeatherForecast> getById(@PathVariable Long id) {
        return weatherService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<WeatherForecast>> getByCity(@RequestParam(required = false) String city) {
        if (city != null) {
            return ResponseEntity.ok(weatherService.getByCity(city));
        }
        return ResponseEntity.ok(weatherService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        weatherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
