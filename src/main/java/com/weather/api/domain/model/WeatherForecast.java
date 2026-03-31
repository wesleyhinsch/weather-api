package com.weather.api.domain.model;

import java.util.List;

public class WeatherForecast {

    private String city;
    private String uf;
    private String summary;
    private Double tempMin;
    private Double tempMax;
    private boolean willRain;
    private List<ForecastPeriod> periods;

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Double getTempMin() { return tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }
    public Double getTempMax() { return tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }
    public boolean isWillRain() { return willRain; }
    public void setWillRain(boolean willRain) { this.willRain = willRain; }
    public List<ForecastPeriod> getPeriods() { return periods; }
    public void setPeriods(List<ForecastPeriod> periods) { this.periods = periods; }
}
