package com.weather.api.infrastructure.adapter.outbound.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_forecast")
public class WeatherForecastEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    private Double temperature;

    private String description;

    @Column(name = "forecast_date")
    private LocalDateTime forecastDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDateTime forecastDate) { this.forecastDate = forecastDate; }
}
