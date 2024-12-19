package org.example.util;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DataParser {
    private static final ThreadLocal<SimpleDateFormat> DF = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    });
        public static ParserResult parseData(String data) throws ParseException {
        if (!data.contains("air") || !data.contains("earth")) {
            throw new ParseException("Invalid data", 0);
        }
        int timeIndex = data.indexOf("time=");
        int tempIndex = data.indexOf("temperature=");
        int moistIndex = data.indexOf("moisture=");
        int phIndex = data.indexOf("ph=",data.indexOf("ph=")+"ph=".length());

        if (timeIndex == -1 || tempIndex == -1 || moistIndex == -1 || phIndex == -1) {
            throw new ParseException("Invalid data", 0);
        }

        timeIndex += "time=".length();
        tempIndex += "temperature=".length();
        moistIndex += "moisture=".length();
        phIndex += "ph=".length();

        int endIndex = data.indexOf(", ", timeIndex);
        Date time = DF.get().parse(data.substring(timeIndex, endIndex));

        endIndex = data.indexOf(", ", tempIndex);
        float temp = Float.parseFloat(data.substring(tempIndex, endIndex));

        endIndex = data.indexOf(", ", moistIndex);
        float moist = Float.parseFloat(data.substring(moistIndex, endIndex));

        endIndex = data.indexOf(", ", phIndex);
        float ph = Float.parseFloat(data.substring(phIndex, endIndex));

        return new ParserResult(time, temp, moist, ph);
    }
}
