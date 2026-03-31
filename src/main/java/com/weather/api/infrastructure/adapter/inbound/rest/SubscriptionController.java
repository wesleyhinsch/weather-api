package com.weather.api.infrastructure.adapter.inbound.rest;

import com.weather.api.domain.model.Subscription;
import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherProviderPort;
import com.weather.api.infrastructure.adapter.outbound.persistence.SubscriptionPersistenceAdapter;
import com.weather.api.infrastructure.adapter.outbound.telegram.TelegramAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscription")
@Tag(name = "Subscription", description = "Cadastro para receber relatório diário via Telegram")
public class SubscriptionController {

    private final SubscriptionPersistenceAdapter adapter;
    private final WeatherProviderPort weatherProvider;
    private final TelegramAdapter telegram;

    public SubscriptionController(SubscriptionPersistenceAdapter adapter, WeatherProviderPort weatherProvider, TelegramAdapter telegram) {
        this.adapter = adapter;
        this.weatherProvider = weatherProvider;
        this.telegram = telegram;
    }

    @PostMapping
    @Operation(summary = "Cadastra e envia o primeiro relatório via Telegram")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Subscription subscription) {
        Subscription saved = adapter.save(subscription);
        WeatherForecast forecast = weatherProvider.getCurrentWeather(saved.getCity(), saved.getUf());
        telegram.sendMessage(saved.getChatId(), forecast.getSummary());
        return ResponseEntity.ok(Map.of("subscription", saved, "forecast", forecast));
    }

    @GetMapping
    @Operation(summary = "Lista todos os cadastros")
    public ResponseEntity<List<Subscription>> getAll() {
        return ResponseEntity.ok(adapter.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um cadastro")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adapter.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
