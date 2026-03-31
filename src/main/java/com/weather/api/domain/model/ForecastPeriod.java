package com.weather.api.domain.model;

import java.time.LocalDateTime;

public class ForecastPeriod {

    private String label;
    private LocalDateTime dateTime;
    private Double temperature;
    private String description;
    private boolean rain;
    private Double rainVolume;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isRain() { return rain; }
    public void setRain(boolean rain) { this.rain = rain; }
    public Double getRainVolume() { return rainVolume; }
    public void setRainVolume(Double rainVolume) { this.rainVolume = rainVolume; }
}
