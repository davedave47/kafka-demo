package org.example.Environment;

import java.util.Date;

public class Earth {
    Date time;
    String station;
    float temperature;
    float moisture;
    float ph;
    float salinity;
    int water_root;
    int water_leaf;
    int water_level;
    float voltage;

    public Earth(Date time, String station, float temperature, float moisture, float ph, float salinity, int water_root, int water_leaf, int water_level, float voltage) {
        this.time = time;
        this.station = station;
        this.temperature = temperature;
        this.moisture = moisture;
        this.ph = ph;
        this.salinity = salinity;
        this.water_root = water_root;
        this.water_leaf = water_leaf;
        this.water_level = water_level;
        this.voltage = voltage;
    }

    @Override
    public String toString() {
        return "Earth{" +
                "time=" + time +
                ", station='" + station + '\'' +
                ", temperature=" + temperature +
                ", moisture=" + moisture +
                ", ph=" + ph +
                ", salinity=" + salinity +
                ", water_root=" + water_root +
                ", water_leaf=" + water_leaf +
                ", water_level=" + water_level +
                ", voltage=" + voltage +
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

    public float getPh() {
        return ph;
    }

    public void setPh(float ph) {
        this.ph = ph;
    }

    public float getSalinity() {
        return salinity;
    }

    public void setSalinity(float salinity) {
        this.salinity = salinity;
    }

    public int getWater_root() {
        return water_root;
    }

    public void setWater_root(int water_root) {
        this.water_root = water_root;
    }

    public int getWater_leaf() {
        return water_leaf;
    }

    public void setWater_leaf(int water_leaf) {
        this.water_leaf = water_leaf;
    }

    public int getWater_level() {
        return water_level;
    }

    public void setWater_level(int water_level) {
        this.water_level = water_level;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }
}
