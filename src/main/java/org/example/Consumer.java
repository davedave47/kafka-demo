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
    KafkaConsumer<String, Environment> createEnvironmentConsumer() {
        final Properties conf = new Properties();
        conf.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        conf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29095");
        conf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        conf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EnvironmentDeserializer.class.getName());
        return new KafkaConsumer<>(conf);
    }
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Consumer consumer = new Consumer();
        KafkaConsumer<String, Environment> enviConsumer = consumer.createEnvironmentConsumer();
        enviConsumer.subscribe(List.of("environment"));

        executor.execute(() -> {
            try (BufferedWriter enviWriter = new BufferedWriter(new FileWriter("src/main/resources/output/output.txt"))) {
                while (true) {
                    ConsumerRecords<String, Environment> records = enviConsumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, Environment> record : records) {
                        System.out.println("Received" + record.value());
                        enviWriter.write(record.value().toString() + "\n");
                        enviWriter.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error during file writing", e);
            }
        });
    }
}