package org.example;

import org.example.Environment.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.Deserializer.*;

public class Consumer {
    KafkaConsumer<String, Air> createAirConsumer() {
        final Properties conf = new Properties();
        conf.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        conf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29092");
        conf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        conf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AirDeserializer.class.getName());
        return new KafkaConsumer<>(conf);
    }

    KafkaConsumer<String, Water> createWaterConsumer() {
        final Properties conf = new Properties();
        conf.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        conf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29092");
        conf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        conf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, WaterDeserializer.class.getName());
        return new KafkaConsumer<>(conf);
    }

    KafkaConsumer<String, Earth> createEarthConsumer() {
        final Properties conf = new Properties();
        conf.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        conf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29092");
        conf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        conf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EarthDeserializer.class.getName());
        return new KafkaConsumer<>(conf);
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Consumer consumer = new Consumer();
        KafkaConsumer<String, Air> airConsumer1 = consumer.createAirConsumer();
        KafkaConsumer<String, Air> airConsumer2 = consumer.createAirConsumer();
        KafkaConsumer<String, Water> waterConsumer = consumer.createWaterConsumer();
        KafkaConsumer<String, Earth> earthConsumer = consumer.createEarthConsumer();

        airConsumer1.subscribe(List.of("air"));
        airConsumer2.subscribe(List.of("air"));
        waterConsumer.subscribe(List.of("water"));
        earthConsumer.subscribe(List.of("earth"));
        executor.execute(() -> {
            try (BufferedWriter airWriter1 = new BufferedWriter(new FileWriter("src/main/resources/output/air-partition-1.txt"))) {
                while (true) {
                    ConsumerRecords<String, Air> records = airConsumer1.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, Air> record : records) {
                        airWriter1.write(record.value().toString() + "\n");
                        airWriter1.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error during file writing", e);
            }
        });


        executor.execute(() -> {
            try (BufferedWriter airWriter2 = new BufferedWriter(new FileWriter("src/main/resources/output/air-partition-2.txt"))) {
                while (true) {
                    ConsumerRecords<String, Air> records = airConsumer2.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, Air> record : records) {
                        airWriter2.write(record.value().toString() + "\n");
                        airWriter2.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error during file writing", e);
            }
        });

        executor.execute(() -> {
            try (BufferedWriter waterWriter = new BufferedWriter(new FileWriter("src/main/resources/output/water.txt"))) {
                while (true) {
                    ConsumerRecords<String, Water> records = waterConsumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, Water> record : records) {
                        waterWriter.write(record.value().toString() + "\n");
                        waterWriter.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error during file writing", e);
            }
        });
        executor.execute(() -> {
            try (BufferedWriter earthWriter = new BufferedWriter(new FileWriter("src/main/resources/output/earth.txt"))) {
                while (true) {
                    ConsumerRecords<String, Earth> records = earthConsumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, Earth> record : records) {
                        earthWriter.write(record.value().toString() + "\n");
                        earthWriter.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error during file writing", e);
            }
        });
    }
}