package org.example.util;

import java.util.Date;

public class ParserResult {
    private final Date timestamp;
    private final float airTemperature;
    private final float airMoisture;
    private final float earthPH;

    public boolean isValid() {
        boolean goodTemperature = airTemperature >= 23 && airTemperature <= 28;
        boolean goodMoisture = airMoisture >= 60 && airMoisture <= 80;
        boolean goodPH = earthPH >= 5.5 && earthPH <= 7;
        return goodTemperature && goodMoisture && goodPH;
    }

    public ParserResult(Date timestamp, float airTemperature, float airMoisture, float earthPH) {
        this.timestamp = timestamp;
        this.airTemperature = airTemperature;
        this.airMoisture = airMoisture;
        this.earthPH = earthPH;
    }

    public Date getTime() {
        return timestamp;
    }

    public float getTemp() {
        return airTemperature;
    }

    public float getMoisture() {
        return airMoisture;
    }

    public float getPH() {
        return earthPH;
    }

    @Override
    public String toString() {
        return "ParserResult{" +
                "timestamp=" + timestamp +
                ", airTemperature=" + airTemperature +
                ", airMoisture=" + airMoisture +
                ", earthPH=" + earthPH +
                '}';
    }
}
