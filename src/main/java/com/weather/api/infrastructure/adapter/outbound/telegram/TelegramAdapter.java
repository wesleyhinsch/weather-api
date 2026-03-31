package com.weather.api.infrastructure.adapter.outbound.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class TelegramAdapter {

    private final RestTemplate restTemplate;
    private final String botToken;
    private static final String URL = "https://api.telegram.org/bot{token}/sendMessage";

    public TelegramAdapter(RestTemplate restTemplate, @Value("${telegram.bot-token}") String botToken) {
        this.restTemplate = restTemplate;
        this.botToken = botToken;
    }

    public void sendMessage(String chatId, String text) {
        restTemplate.postForObject(URL, Map.of("chat_id", chatId, "text", text), String.class, botToken);
    }
}
