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
    KafkaConsumer<String, String> createEnvironmentConsumer() {
        final Properties conf = new Properties();
        conf.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        conf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29095");
        conf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        conf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        conf.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(conf);
    }
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Consumer consumer = new Consumer();
        KafkaConsumer<String,String> enviConsumer = consumer.createEnvironmentConsumer();
        enviConsumer.subscribe(List.of("environment"));

        executor.execute(() -> {
            try (BufferedWriter enviWriter = new BufferedWriter(new FileWriter("src/main/resources/output/output.txt"))) {
                while (true) {
                    ConsumerRecords<String, String> records = enviConsumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        // System.out.println("Received" + record.value());
                        enviWriter.write(record.value() + "\n");
                        enviWriter.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error during file writing", e);
            }
        });
    }
}