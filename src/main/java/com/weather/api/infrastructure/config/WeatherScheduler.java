package com.weather.api.infrastructure.config;

import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherProviderPort;
import com.weather.api.infrastructure.adapter.outbound.persistence.SubscriptionPersistenceAdapter;
import com.weather.api.infrastructure.adapter.outbound.telegram.TelegramAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherScheduler {

    private final SubscriptionPersistenceAdapter subscriptionAdapter;
    private final WeatherProviderPort weatherProvider;
    private final TelegramAdapter telegram;

    public WeatherScheduler(SubscriptionPersistenceAdapter subscriptionAdapter, WeatherProviderPort weatherProvider, TelegramAdapter telegram) {
        this.subscriptionAdapter = subscriptionAdapter;
        this.weatherProvider = weatherProvider;
        this.telegram = telegram;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyReport() {
        subscriptionAdapter.findAll().forEach(sub -> {
            WeatherForecast forecast = weatherProvider.getCurrentWeather(sub.getCity(), sub.getUf());
            telegram.sendMessage(sub.getChatId(), forecast.getSummary());
        });
    }
}
