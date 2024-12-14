package org.example.Environment;

import java.util.Date;

public class Water {
    Date time;
    String station;
    float ph;
    float DO;
    float temperature;
    float salinity;
    public Water(Date time, String station, float ph, float DO,  float temperature, float salinity) {
        this.time = time;
        this.station = station;
        this.DO = DO;
        this.ph = ph;
        this.temperature = temperature;
        this.salinity = salinity;
    }

    @Override
    public String toString() {
        return "Water{" +
                "time=" + time +
                ", station='" + station + '\'' +
                ", ph=" + ph +
                ", DO=" + DO +
                ", temperature=" + temperature +
                ", salinity=" + salinity +
                '}';
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public float getPh() {
        return ph;
    }

    public void setPh(float ph) {
        this.ph = ph;
    }

    public float getDO() {
        return DO;
    }

    public void setDO(float DO) {
        this.DO = DO;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getSalinity() {
        return salinity;
    }

    public void setSalinity(float salinity) {
        this.salinity = salinity;
    }
};

;