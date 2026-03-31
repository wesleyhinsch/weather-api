package com.weather.api.infrastructure.adapter.outbound.weather;

import com.weather.api.domain.model.ForecastPeriod;
import com.weather.api.domain.model.WeatherForecast;
import com.weather.api.domain.port.WeatherProviderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class OpenWeatherMapAdapter implements WeatherProviderPort {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast?q={query},BR&appid={apiKey}&units=metric&lang=pt_br";
    private static final String CURRENT_URL = "https://api.openweathermap.org/data/2.5/weather?q={query},BR&appid={apiKey}&units=metric&lang=pt_br";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OpenWeatherMapAdapter(RestTemplate restTemplate, @Value("${openweathermap.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public WeatherForecast getCurrentWeather(String city, String uf) {
        String query = city + "," + uf;

        Map<String, Object> current = restTemplate.getForObject(CURRENT_URL, Map.class, query, apiKey);
        Map<String, Object> currentMain = (Map<String, Object>) current.get("main");
        Map<String, Object> currentWeather = ((List<Map<String, Object>>) current.get("weather")).get(0);
        String currentDesc = (String) currentWeather.get("description");
        String currentMainType = ((String) currentWeather.get("main")).toLowerCase();

        Map<String, Object> forecast = restTemplate.getForObject(FORECAST_URL, Map.class, query, apiKey);
        List<Map<String, Object>> list = (List<Map<String, Object>>) forecast.get("list");

        LocalDate today = LocalDate.now();
        List<ForecastPeriod> allPeriods = list.stream()
                .filter(item -> {
                    LocalDateTime dt = LocalDateTime.parse((String) item.get("dt_txt"), FORMATTER);
                    return dt.toLocalDate().equals(today);
                })
                .map(this::toPeriod)
                .toList();

        List<ForecastPeriod> periods = new ArrayList<>();
        addPeriod(periods, "🌅 Manhã", allPeriods, 6, 11);
        addPeriod(periods, "☀️ Tarde", allPeriods, 12, 17);
        addPeriod(periods, "🌙 Noite", allPeriods, 18, 23);

        double tempMin = ((Number) currentMain.get("temp_min")).doubleValue();
        double tempMax = ((Number) currentMain.get("temp_max")).doubleValue();

        if (!allPeriods.isEmpty()) {
            double forecastMin = allPeriods.stream().mapToDouble(ForecastPeriod::getTemperature).min().orElse(tempMin);
            double forecastMax = allPeriods.stream().mapToDouble(ForecastPeriod::getTemperature).max().orElse(tempMax);
            tempMin = Math.min(tempMin, forecastMin);
            tempMax = Math.max(tempMax, forecastMax);
        }

        boolean willRain = currentMainType.contains("rain") || currentMainType.contains("drizzle")
                || allPeriods.stream().anyMatch(ForecastPeriod::isRain);

        WeatherForecast result = new WeatherForecast();
        result.setCity(city);
        result.setUf(uf);
        result.setTempMin(Math.round(tempMin * 10.0) / 10.0);
        result.setTempMax(Math.round(tempMax * 10.0) / 10.0);
        result.setWillRain(willRain);
        result.setPeriods(periods);
        result.setSummary(buildSummary(city, uf, currentDesc, tempMin, tempMax, willRain, periods));
        return result;
    }

    private void addPeriod(List<ForecastPeriod> result, String label, List<ForecastPeriod> allPeriods, int hourStart, int hourEnd) {
        List<ForecastPeriod> filtered = allPeriods.stream()
                .filter(p -> p.getDateTime().getHour() >= hourStart && p.getDateTime().getHour() <= hourEnd)
                .toList();

        if (filtered.isEmpty()) return;

        double avg = filtered.stream().mapToDouble(ForecastPeriod::getTemperature).average().orElse(0);
        double min = filtered.stream().mapToDouble(ForecastPeriod::getTemperature).min().orElse(0);
        double max = filtered.stream().mapToDouble(ForecastPeriod::getTemperature).max().orElse(0);
        boolean rain = filtered.stream().anyMatch(ForecastPeriod::isRain);
        double rainVolume = filtered.stream().mapToDouble(ForecastPeriod::getRainVolume).sum();
        String desc = filtered.stream().findFirst().map(ForecastPeriod::getDescription).orElse("");

        ForecastPeriod period = new ForecastPeriod();
        period.setLabel(label);
        period.setDateTime(filtered.get(0).getDateTime());
        period.setTemperature(Math.round(avg * 10.0) / 10.0);
        period.setDescription(desc);
        period.setRain(rain);
        period.setRainVolume(rainVolume);
        result.add(period);
    }

    @SuppressWarnings("unchecked")
    private ForecastPeriod toPeriod(Map<String, Object> item) {
        Map<String, Object> main = (Map<String, Object>) item.get("main");
        Map<String, Object> weather = ((List<Map<String, Object>>) item.get("weather")).get(0);
        Map<String, Object> rain = (Map<String, Object>) item.get("rain");

        ForecastPeriod period = new ForecastPeriod();
        period.setDateTime(LocalDateTime.parse((String) item.get("dt_txt"), FORMATTER));
        period.setTemperature(Math.round(((Number) main.get("temp")).doubleValue() * 10.0) / 10.0);
        period.setDescription((String) weather.get("description"));

        String mainWeather = ((String) weather.get("main")).toLowerCase();
        period.setRain(mainWeather.contains("rain") || mainWeather.contains("drizzle"));
        period.setRainVolume(rain != null ? ((Number) rain.get("3h")).doubleValue() : 0.0);

        return period;
    }

    private String buildSummary(String city, String uf, String currentDesc, double tempMin, double tempMax, boolean willRain, List<ForecastPeriod> periods) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🌤 Previsão do tempo - %s/%s\n", city, uf));
        sb.append(String.format("📅 %s\n", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        sb.append("─────────────────────\n");
        sb.append(String.format("🌡 Agora: %s\n", currentDesc));

        for (ForecastPeriod p : periods) {
            sb.append(String.format("\n%s\n", p.getLabel()));
            sb.append(String.format("  🌡 Média: %.0f°C\n", p.getTemperature()));
            sb.append(String.format("  %s %s\n", p.isRain() ? "🌧" : "☁️", p.getDescription()));
            if (p.isRain()) {
                sb.append(String.format("  💧 Volume: %.1fmm\n", p.getRainVolume()));
            }
        }

        sb.append("─────────────────────\n");
        sb.append(String.format("🌡 Mínima: %.0f°C | Máxima: %.0f°C\n", tempMin, tempMax));

        if (willRain) {
            List<String> rainyParts = new ArrayList<>();
            for (ForecastPeriod p : periods) {
                if (p.isRain()) rainyParts.add(p.getLabel().substring(2).trim().toLowerCase());
            }
            if (rainyParts.isEmpty()) {
                sb.append("🌧 Chovendo agora!\n");
            } else {
                sb.append("🌧 Chuva prevista: ").append(String.join(", ", rainyParts)).append("\n");
            }
            sb.append("☂️ Leve um guarda-chuva!");
        } else {
            sb.append("☀️ Sem previsão de chuva para hoje!");
        }

        return sb.toString();
    }
}
