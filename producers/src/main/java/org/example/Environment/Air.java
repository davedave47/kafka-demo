package org.example.Environment;

import java.util.Date;

public class Air {
    Date time;
    String station;
    float temperature;
    float moisture;
    int light;
    float total_rainfall;
    int rainfall;
    int wind_direction;
    float pm25;
    float pm10;
    int co;
    int nox;
    int so2;

    public Air(Date time, String station, float temperature, float moisture, int light, float total_rainfall, int rainfall, int wind_direction, float pm25, float pm10, int co, int nox, int so2) {
        this.time = time;
        this.station = station;
        this.temperature = temperature;
        this.moisture = moisture;
        this.light = light;
        this.total_rainfall = total_rainfall;
        this.rainfall = rainfall;
        this.wind_direction = wind_direction;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.co = co;
        this.nox = nox;
        this.so2 = so2;
    }
    @Override
    public String toString() {
        return "Air{" +
                "time=" + time +
                ", station='" + station + '\'' +
                ", temperature=" + temperature +
                ", moisture=" + moisture +
                ", light=" + light +
                ", total_rainfall=" + total_rainfall +
                ", rainfall=" + rainfall +
                ", wind_direction=" + wind_direction +
                ", pm25=" + pm25 +
                ", pm10=" + pm10 +
                ", co=" + co +
                ", nox=" + nox +
                ", so2=" + so2 +
                '}';
    }
    public String getStation() {
        return station;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getMoisture() {
        return moisture;
    }

    public void setMoisture(float moisture) {
        this.moisture = moisture;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public float getTotal_rainfall() {
        return total_rainfall;
    }

    public void setTotal_rainfall(float total_rainfall) {
        this.total_rainfall = total_rainfall;
    }

    public int getRainfall() {
        return rainfall;
    }

    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    public int getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(int wind_direction) {
        this.wind_direction = wind_direction;
    }

    public float getPm25() {
        return pm25;
    }

    public void setPm25(float pm25) {
        this.pm25 = pm25;
    }

    public float getPm10() {
        return pm10;
    }

    public void setPm10(float pm10) {
        this.pm10 = pm10;
    }

    public int getCo() {
        return co;
    }

    public void setCo(int co) {
        this.co = co;
    }

    public int getNox() {
        return nox;
    }

    public void setNox(int nox) {
        this.nox = nox;
    }

    public int getSo2() {
        return so2;
    }

    public void setSo2(int so2) {
        this.so2 = so2;
    }
}
