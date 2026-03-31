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
    private static final String URL = "https://api.openweathermap.org/data/2.5/forecast?q={query},BR&appid={apiKey}&units=metric&lang=pt_br";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<Integer, String> PERIOD_LABELS = Map.of(
            6, "Manhã",
            12, "Tarde",
            18, "Noite",
            21, "Noite"
    );

    private static final List<Integer> TARGET_HOURS = List.of(6, 12, 18, 21);

    public OpenWeatherMapAdapter(RestTemplate restTemplate, @Value("${openweathermap.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public WeatherForecast getCurrentWeather(String city, String uf) {
        String query = city + "," + uf;
        Map<String, Object> response = restTemplate.getForObject(URL, Map.class, query, apiKey);
        List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");

        LocalDate today = LocalDate.now();

        List<ForecastPeriod> allPeriods = list.stream()
                .filter(item -> {
                    LocalDateTime dt = LocalDateTime.parse((String) item.get("dt_txt"), FORMATTER);
                    return dt.toLocalDate().equals(today);
                })
                .map(this::toPeriod)
                .toList();

        List<ForecastPeriod> periods = new ArrayList<>();
        for (int targetHour : TARGET_HOURS) {
            findClosestPeriod(allPeriods, targetHour).ifPresent(p -> {
                p.setLabel(PERIOD_LABELS.get(targetHour));
                periods.add(p);
            });
        }

        boolean willRain = allPeriods.stream().anyMatch(ForecastPeriod::isRain);
        double tempMin = allPeriods.stream().mapToDouble(ForecastPeriod::getTemperature).min().orElse(0);
        double tempMax = allPeriods.stream().mapToDouble(ForecastPeriod::getTemperature).max().orElse(0);

        WeatherForecast forecast = new WeatherForecast();
        forecast.setCity(city);
        forecast.setUf(uf);
        forecast.setTempMin(Math.round(tempMin * 10.0) / 10.0);
        forecast.setTempMax(Math.round(tempMax * 10.0) / 10.0);
        forecast.setWillRain(willRain);
        forecast.setPeriods(periods);
        forecast.setSummary(buildSummary(city, uf, allPeriods));
        return forecast;
    }

    private Optional<ForecastPeriod> findClosestPeriod(List<ForecastPeriod> periods, int targetHour) {
        return periods.stream()
                .min(Comparator.comparingInt(p -> Math.abs(p.getDateTime().getHour() - targetHour)));
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

    private String buildSummary(String city, String uf, List<ForecastPeriod> periods) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🌤 Previsão do tempo - %s/%s\n", city, uf));
        sb.append(String.format("📅 %s\n", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        sb.append("─────────────────────\n");

        buildPeriodSummary(sb, "🌅 Manhã (6h-12h)", periods, 6, 11);
        buildPeriodSummary(sb, "☀️ Tarde (12h-18h)", periods, 12, 17);
        buildPeriodSummary(sb, "🌙 Noite (18h-23h)", periods, 18, 23);

        sb.append("─────────────────────\n");

        double tempMin = periods.stream().mapToDouble(ForecastPeriod::getTemperature).min().orElse(0);
        double tempMax = periods.stream().mapToDouble(ForecastPeriod::getTemperature).max().orElse(0);
        sb.append(String.format("🌡 Geral: %.0f°C ~ %.0f°C\n", tempMin, tempMax));

        boolean rainManha = hasRainInRange(periods, 6, 11);
        boolean rainTarde = hasRainInRange(periods, 12, 17);
        boolean rainNoite = hasRainInRange(periods, 18, 23);

        if (rainManha || rainTarde || rainNoite) {
            sb.append("🌧 Chuva prevista: ");
            List<String> rainyParts = new ArrayList<>();
            if (rainManha) rainyParts.add("manhã");
            if (rainTarde) rainyParts.add("tarde");
            if (rainNoite) rainyParts.add("noite");
            sb.append(String.join(", ", rainyParts)).append("\n");
            sb.append("☂️ Leve um guarda-chuva!");
        } else {
            sb.append("☀️ Sem previsão de chuva para hoje!");
        }

        return sb.toString();
    }

    private void buildPeriodSummary(StringBuilder sb, String label, List<ForecastPeriod> periods, int hourStart, int hourEnd) {
        List<ForecastPeriod> filtered = periods.stream()
                .filter(p -> p.getDateTime().getHour() >= hourStart && p.getDateTime().getHour() <= hourEnd)
                .toList();

        if (filtered.isEmpty()) {
            sb.append(String.format("\n%s\n  Sem dados disponíveis\n", label));
            return;
        }

        double avg = filtered.stream().mapToDouble(ForecastPeriod::getTemperature).average().orElse(0);
        double min = filtered.stream().mapToDouble(ForecastPeriod::getTemperature).min().orElse(0);
        double max = filtered.stream().mapToDouble(ForecastPeriod::getTemperature).max().orElse(0);
        boolean rain = filtered.stream().anyMatch(ForecastPeriod::isRain);
        String desc = filtered.stream().findFirst().map(ForecastPeriod::getDescription).orElse("");

        sb.append(String.format("\n%s\n", label));
        sb.append(String.format("  🌡 Média: %.0f°C (%.0f°C ~ %.0f°C)\n", avg, min, max));
        sb.append(String.format("  %s %s\n", rain ? "🌧" : "☁️", desc));
        if (rain) {
            double volume = filtered.stream().mapToDouble(ForecastPeriod::getRainVolume).sum();
            sb.append(String.format("  💧 Volume: %.1fmm\n", volume));
        }
    }

    private boolean hasRainInRange(List<ForecastPeriod> periods, int hourStart, int hourEnd) {
        return periods.stream()
                .filter(p -> p.getDateTime().getHour() >= hourStart && p.getDateTime().getHour() <= hourEnd)
                .anyMatch(ForecastPeriod::isRain);
    }
}
