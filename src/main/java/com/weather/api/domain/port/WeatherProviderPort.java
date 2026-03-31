package com.weather.api.domain.port;

import com.weather.api.domain.model.WeatherForecast;

public interface WeatherProviderPort {
    WeatherForecast getCurrentWeather(String city, String uf);
}
