package com.weather.api.infrastructure.adapter.inbound.rest;

import com.weather.api.domain.model.Subscription;
import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherProviderPort;
import com.weather.api.infrastructure.adapter.outbound.persistence.SubscriptionPersistenceAdapter;
import com.weather.api.infrastructure.adapter.outbound.telegram.TelegramAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final SubscriptionPersistenceAdapter subscriptionAdapter;
    private final WeatherProviderPort weatherProvider;
    private final TelegramAdapter telegram;

    public TelegramWebhookController(SubscriptionPersistenceAdapter subscriptionAdapter, WeatherProviderPort weatherProvider, TelegramAdapter telegram) {
        this.subscriptionAdapter = subscriptionAdapter;
        this.weatherProvider = weatherProvider;
        this.telegram = telegram;
    }

    @PostMapping("/webhook")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> handleUpdate(@RequestBody Map<String, Object> update) {
        Map<String, Object> message = (Map<String, Object>) update.get("message");
        if (message == null) return ResponseEntity.ok().build();

        Map<String, Object> chat = (Map<String, Object>) message.get("chat");
        String chatId = String.valueOf(((Number) chat.get("id")).longValue());
        String text = (String) message.get("text");

        if (text == null) return ResponseEntity.ok().build();

        if (text.equals("/start")) {
            telegram.sendMessage(chatId, "☀️ Bem-vindo ao Weather Report!\n\nEnvie a cidade e UF para receber o relatório diário.\n\nExemplo: Morro Reuter - RS");
        } else if (text.equals("/minhas")) {
            var subs = subscriptionAdapter.findByChatId(chatId);
            if (subs.isEmpty()) {
                telegram.sendMessage(chatId, "Você não tem cidades cadastradas.\n\nEnvie uma cidade no formato: Morro Reuter - RS");
            } else {
                StringBuilder sb = new StringBuilder("📋 Suas cidades:\n\n");
                subs.forEach(s -> sb.append("• ").append(s.getCity()).append(" - ").append(s.getUf()).append(" (ID: ").append(s.getId()).append(")\n"));
                sb.append("\nPara remover, envie: /remover ID");
                telegram.sendMessage(chatId, sb.toString());
            }
        } else if (text.startsWith("/remover ")) {
            try {
                Long id = Long.parseLong(text.replace("/remover ", "").trim());
                subscriptionAdapter.deleteById(id);
                telegram.sendMessage(chatId, "✅ Cidade removida!");
            } catch (Exception e) {
                telegram.sendMessage(chatId, "❌ ID inválido. Use /minhas para ver seus cadastros.");
            }
        } else if (text.contains(" - ")) {
            String[] parts = text.split(" - ");
            if (parts.length == 2) {
                String city = parts[0].trim();
                String uf = parts[1].trim().toUpperCase();

                try {
                    Subscription sub = new Subscription();
                    sub.setChatId(chatId);
                    sub.setCity(city);
                    sub.setUf(uf);
                    subscriptionAdapter.save(sub);

                    WeatherForecast forecast = weatherProvider.getCurrentWeather(city, uf);
                    telegram.sendMessage(chatId, "✅ Cidade cadastrada! Você receberá o relatório todo dia às 8h.\n\n" + forecast.getSummary());
                } catch (Exception e) {
                    telegram.sendMessage(chatId, "❌ Não encontrei essa cidade. Verifique o nome e tente novamente.");
                }
            } else {
                telegram.sendMessage(chatId, "Formato inválido. Envie assim: Morro Reuter - RS");
            }
        } else {
            telegram.sendMessage(chatId, "Envie a cidade no formato: Morro Reuter - RS\n\nComandos:\n/minhas - ver cidades cadastradas\n/remover ID - remover uma cidade");
        }

        return ResponseEntity.ok().build();
    }
}
