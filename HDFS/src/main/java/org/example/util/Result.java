package org.example.util;
import  java.io.Serializable ;
import java.text.SimpleDateFormat;
import java.util.*;

public class Result implements Serializable {
    private static class Interval implements Serializable {
        private static final long serialVersionUID = 1L;
        Date start;
        Date end;
        public Interval(Date start, Date end) {
            this.start = start;
            this.end = end;
        }
        public float getMinutes() {
            long diff = end.getTime() - start.getTime();
            return ((float) diff)/(60*1000);
        }
        private static Interval combine(Interval interval1, Interval interval2) {
            if (interval1.start.getTime() - interval2.end.getTime() == 60*1000) {
                return new Interval(interval2.start, interval1.end);
            }
            if (interval2.start.getTime() - interval1.end.getTime() == 60*1000) {
                return new Interval(interval1.start, interval2.end);
            }
            return null;
        }
        public static List<Interval> aggregate(List<Interval> intervals1, List<Interval> intervals2) {
            List<Interval> result = new LinkedList<>();
            List<Interval> combinedIntervals = new LinkedList<>();
            while (!intervals1.isEmpty() && !intervals2.isEmpty()) {
                Interval interval1 = intervals1.get(0);
                Interval interval2 = intervals2.get(0);
                if (interval1.start.getTime() < interval2.start.getTime()) {
                    combinedIntervals.add(interval1);
                    intervals1.remove(0);
                } else {
                    combinedIntervals.add(interval2);
                    intervals2.remove(0);
                }
            }
            combinedIntervals.addAll(intervals1);
            combinedIntervals.addAll(intervals2);

            while (!combinedIntervals.isEmpty()) {
                Interval current = combinedIntervals.remove(0);
                Iterator<Interval> iterator = combinedIntervals.iterator();

                while (iterator.hasNext()) {
                    Interval interval = iterator.next();
                    Interval combined = combine(current, interval);
                    if (combined != null) {
                        current = combined;
                        iterator.remove();
                    }
                }
                result.add(current);
            }
            return result;
        }
        public void print() {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss zzz");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println(format.format(start) + " - " + format.format(end));
        }
    }
    private static final long serialVersionUID = 1L;
    private List<Interval> times;
    private float maxAirTemperature;
    private float minAirTemperature;
    private float maxAirMoisture;
    private float minAirMoisture;
    private float maxEarthPH;
    private float minEarthPH;
    private Result(List<Interval> times, float maxAirTemperature, float minAirTemperature, float maxAirMoisture, float minAirMoisture, float maxEarthPH, float minEarthPH) {
        this.times = times;
        this.maxAirTemperature = maxAirTemperature;
        this.minAirTemperature = minAirTemperature;
        this.maxAirMoisture = maxAirMoisture;
        this.minAirMoisture = minAirMoisture;
        this.maxEarthPH = maxEarthPH;
        this.minEarthPH = minEarthPH;
    }
    public Result(float maxAirTemperature, float minAirTemperature, float maxAirMoisture, float minAirMoisture, float maxEarthPH, float minEarthPH) {
        this.times = new LinkedList<>();
        this.maxAirTemperature = maxAirTemperature;
        this.minAirTemperature = minAirTemperature;
        this.maxAirMoisture = maxAirMoisture;
        this.minAirMoisture = minAirMoisture;
        this.maxEarthPH = maxEarthPH;
        this.minEarthPH = minEarthPH;
    }
    public void setMaxMin(float airTemperature, float airMoisture, float earthPH) {
        if (airTemperature > maxAirTemperature) {
            maxAirTemperature = airTemperature;
        }
        if (airTemperature < minAirTemperature) {
            minAirTemperature = airTemperature;
        }
        if (airMoisture > maxAirMoisture) {
            maxAirMoisture = airMoisture;
        }
        if (airMoisture < minAirMoisture) {
            minAirMoisture = airMoisture;
        }
        if (earthPH > maxEarthPH) {
            maxEarthPH = earthPH;
        }
        if (earthPH < minEarthPH) {
            minEarthPH = earthPH;
        }
    }

    public float getTotalOutOfIdealTime() {
        float sum = 0;
        for (Interval interval: times) {
            sum += interval.getMinutes() +1;
        }
        return sum;
    }

    public float getMaxAirTemperature() {
        return maxAirTemperature;
    }

    public float getMinAirTemperature() {
        return minAirTemperature;
    }

    public float getMaxAirMoisture() {
        return maxAirMoisture;
    }

    public float getMinAirMoisture() {
        return minAirMoisture;
    }

    public float getMaxEarthPH() {
        return maxEarthPH;
    }

    public float getMinEarthPH() {
        return minEarthPH;
    }
    public void addMinute(Date minute) {
        Interval newInterval = new Interval(minute, minute);
        Iterator<Interval> iterator = times.iterator();
        while (iterator.hasNext()) {
            Interval interval = iterator.next();
            Interval combined = Interval.combine(interval, newInterval);
            if (combined != null) {
                iterator.remove();
                newInterval = combined;
            }
        }

        ListIterator<Interval> listIterator = times.listIterator();
        while (listIterator.hasNext()) {
            Interval interval = listIterator.next();
            if (interval.start.getTime() > newInterval.start.getTime()) {
                listIterator.previous();
                listIterator.add(newInterval);
                return;
            }
        }
        times.add(newInterval);
    }
    public static Result aggregate(Result[] results) {
        List<Interval> times = new LinkedList<>();

        float maxAirTemperature = 0;
        float minAirTemperature = Float.MAX_VALUE;
        float maxAirMoisture = 0;
        float minAirMoisture = Float.MAX_VALUE;
        float maxEarthPH = 0;
        float minEarthPH = Float.MAX_VALUE;
        for (Result result : results) {
            times = Interval.aggregate(times, result.times);
            if (result.getMaxAirTemperature() > maxAirTemperature) {
                maxAirTemperature = result.getMaxAirTemperature();
            }
            if (result.getMinAirTemperature() < minAirTemperature) {
                minAirTemperature = result.getMinAirTemperature();
            }
            if (result.getMaxAirMoisture() > maxAirMoisture) {
                maxAirMoisture = result.getMaxAirMoisture();
            }
            if (result.getMinAirMoisture() < minAirMoisture) {
                minAirMoisture = result.getMinAirMoisture();
            }
            if (result.getMaxEarthPH() > maxEarthPH) {
                maxEarthPH = result.getMaxEarthPH();
            }
            if (result.getMinEarthPH() < minEarthPH) {
                minEarthPH = result.getMinEarthPH();
            }
        }
        return new Result(times, maxAirTemperature, minAirTemperature, maxAirMoisture, minAirMoisture, maxEarthPH, minEarthPH);
    }
    @Override
    public String toString() {
        return "Result{" +
                "number of out of ideal intervals=" + times.size() +
                ", total out of ideal time=" + this.getTotalOutOfIdealTime() + " minutes" +
                ", average out of ideal interval=" + this.getTotalOutOfIdealTime()/times.size() + " minutes" +
                ", maxAirTemperature=" + maxAirTemperature +
                ", minAirTemperature=" + minAirTemperature +
                ", maxAirMoisture=" + maxAirMoisture +
                ", minAirMoisture=" + minAirMoisture +
                ", maxEarthPH=" + maxEarthPH +
                ", minEarthPH=" + minEarthPH +
                '}';
    }
    public void printIntervals(String prefix) {
        prefix = prefix.isEmpty() ? prefix : prefix + ":";
        for (Interval interval: times) {
            System.out.print(prefix);
            interval.print();
        }
    }
}