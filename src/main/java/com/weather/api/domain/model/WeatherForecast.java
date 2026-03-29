package com.weather.api.domain.model;

import java.time.LocalDateTime;

public class WeatherForecast {

    private Long id;
    private String city;
    private Double temperature;
    private String description;
    private LocalDateTime forecastDate;

    public WeatherForecast() {}

    public WeatherForecast(String city, Double temperature, String description, LocalDateTime forecastDate) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.forecastDate = forecastDate;
    }

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
