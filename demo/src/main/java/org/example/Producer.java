package org.example;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;

import org.example.Environment.*;
import org.example.Serializer.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;


public class Producer {
    final String AIR_CSV_PATH = "src/main/resources/Dataset/AIR2308.csv";
    final String WATER_CSV_PATH = "src/main/resources/Dataset/WATER2308.csv";
    final String EARTH_CSV_PATH = "src/main/resources/Dataset/EARTH2308.csv";
    private static final ThreadLocal<SimpleDateFormat> DF = ThreadLocal.withInitial(() -> new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));

    KafkaProducer<String, Air> createAirProducer() {
        // Producer mandatory config
        final Properties airConf = new Properties();
        airConf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29092");
        airConf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        airConf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AirSerializer.class.getName());
        airConf.setProperty(ProducerConfig.PARTITIONER_CLASS_CONFIG, AirPartitioner.class.getName());
        return new KafkaProducer<>(airConf);
    }

    KafkaProducer<String, Water> createWaterProducer() {
        // Producer mandatory config
        final Properties waterConf = new Properties();
        waterConf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29092");
        waterConf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        waterConf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, WaterSerializer.class.getName());
        return new KafkaProducer<>(waterConf);
    }

    KafkaProducer<String, Earth> createEarthProducer() {
        // Producer mandatory config
        final Properties earthConf = new Properties();
        earthConf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29092");
        earthConf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        earthConf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EarthSerializer.class.getName());
        return new KafkaProducer<>(earthConf);
    }
    boolean isValid(String value) {
        return !value.equals("---");
    }

    Air parseAir(String[] values) {
        try {
            Date time = isValid(values[0]) ? DF.get().parse(values[0]) : null;
            String station = values[1];
            float temperature = isValid(values[2]) ? Float.parseFloat(values[2]) : Float.NaN;
            float moisture = isValid(values[3]) ? Float.parseFloat(values[3]) : Float.NaN;
            int light = isValid(values[4]) ? Integer.parseInt(values[4]) : -1;
            float total_rainfall = isValid(values[5]) ? Float.parseFloat(values[5]) : Float.NaN;
            int rainfall = isValid(values[6]) ? Integer.parseInt(values[6]) : -1;
            int wind_direction = isValid(values[7]) ? Integer.parseInt(values[7]) : -1;
            float pm25 = isValid(values[8]) ? Float.parseFloat(values[8]) : Float.NaN;
            float pm10 = isValid(values[9]) ? Float.parseFloat(values[9]) : Float.NaN;
            int co = isValid(values[10]) ? Integer.parseInt(values[10]) : -1;
            int nox = isValid(values[11]) ? Integer.parseInt(values[11]) : -1;
            int so2 = isValid(values[12]) ? Integer.parseInt(values[12]) : -1;
            return new Air(time, station, temperature, moisture, light, total_rainfall, rainfall, wind_direction, pm25, pm10, co, nox, so2);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("Error parsing record: " + Arrays.toString(values));
            e.printStackTrace();
            return null;
        }
    }

    Water parseWater(String[] values) {
        try {
            Date time = isValid(values[0]) ? DF.get().parse(values[0]) : null;
            String station = values[1];
            float ph = isValid(values[2]) ? Float.parseFloat(values[2]) : Float.NaN;
            float DO = isValid(values[3]) ? Float.parseFloat(values[3]) : Float.NaN;
            float temperature = isValid(values[4]) ? Float.parseFloat(values[4]) : Float.NaN;
            float salinity = isValid(values[5]) ? Float.parseFloat(values[5]) : Float.NaN;
            return new Water(time, station, ph, DO, temperature, salinity);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("Error parsing record: " + Arrays.toString(values));
            e.printStackTrace();
            return null;
        }
    }

    Earth parseEarth(String[] values) {
        try {
            Date time = isValid(values[0]) ? DF.get().parse(values[0]) : null;
            String station = values[1];
            float temperature = isValid(values[2]) ? Float.parseFloat(values[2]) : Float.NaN;
            float moisture = isValid(values[3]) ? Float.parseFloat(values[3]) : Float.NaN;
            float pH = isValid(values[4]) ? Float.parseFloat(values[4]) : Float.NaN;
            float salinity = isValid(values[5]) ? Float.parseFloat(values[5]) : Float.NaN;
            int water_root = isValid(values[6]) ? Integer.parseInt(values[6]) : -1;
            int water_leaf = isValid(values[7]) ? Integer.parseInt(values[7]) : -1;
            int water_level = isValid(values[8]) ? Integer.parseInt(values[8]) : -1;
            float voltage = isValid(values[9]) ? Float.parseFloat(values[9]) : Float.NaN;
            return new Earth(time, station, temperature, moisture, pH, salinity, water_root, water_leaf, water_level, voltage);
        } catch (ParseException | NumberFormatException e) {
            System.out.println("Error parsing record: " + Arrays.toString(values));
            e.printStackTrace();
            return null;
        }
    }

    public <T> void produceRecordsFromCSV(KafkaProducer<String, T> producer, String csvFilePath, String topic) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            int lineCount = 1;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                    T recordObject = switch (topic.toLowerCase()) {
                        case "air" -> (T) parseAir(values);
                        case "water" -> (T) parseWater(values);
                        case "earth" -> (T) parseEarth(values);
                        default -> throw new IllegalArgumentException("Invalid topic: " + topic);
                    };
                    ProducerRecord<String, T> record = new ProducerRecord<>(topic, recordObject);
                    producer.send(record, (recordMetadata, e) -> {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                        System.out.println(Arrays.toString(values));
                          System.out.println("Record sent to " + topic + " partition " + recordMetadata.partition() + " with offset " + recordMetadata.offset());
                        }
                    });
                }
            System.out.println("Finished reading records for " + csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            KafkaProducer<String, Air> airProducer = producer.createAirProducer();
            KafkaProducer<String, Water> waterProducer = producer.createWaterProducer();
            KafkaProducer<String, Earth> earthProducer = producer.createEarthProducer();

            executorService.execute(() -> {producer.produceRecordsFromCSV(airProducer, producer.AIR_CSV_PATH, "air");});
            executorService.execute(() -> {producer.produceRecordsFromCSV(waterProducer, producer.WATER_CSV_PATH, "water");});
            executorService.execute(() -> {producer.produceRecordsFromCSV(earthProducer, producer.EARTH_CSV_PATH, "earth");});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
