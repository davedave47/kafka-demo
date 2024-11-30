package org.example.KafkaStreams;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import org.example.Environment.*;
import org.example.KafkaStreams.Serde.*;

public class JoinStream {

    public void start() {
        System.out.println("Starting Join Stream");
        // Configurations for the Kafka Streams application
        Properties conf = new Properties();
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, "join-stream-app");
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29095");
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        conf.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        conf.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        // Create streams for Air, Water, and Earth
        KStream<String, Air> airStream = builder.stream("processed_air", Consumed.with(Serdes.String(), new AirSerde()));
        KStream<String, Water> waterStream = builder.stream("processed_water", Consumed.with(Serdes.String(), new WaterSerde()));
        KStream<String, Earth> earthStream = builder.stream("processed_earth", Consumed.with(Serdes.String(), new EarthSerde()));
        // Perform joins: Air + Water first, then join with Earth
        KStream<String, Environment> joinedStream = airStream
                .join(waterStream,
                        (air, water) -> new Environment(air, water, null), // Initial join: Air + Water
                        JoinWindows.of(Duration.ofMillis(1000)),
                        StreamJoined.with(Serdes.String(), new AirSerde(), new WaterSerde())
                )
                .join(earthStream,
                        (envTuple, earth) -> {
                            envTuple.setEarth(earth); // Add Earth to the tuple
                            return envTuple;
                        },
                        JoinWindows.of(Duration.ofMillis(1000)),
                        StreamJoined.with(Serdes.String(), new EnvironmentSerde(), new EarthSerde())
                );
        // Publish the joined stream to the "environment" topic
        joinedStream.to("environment", Produced.with(Serdes.String(), new EnvironmentSerde()));

        // Build and start the Kafka Streams application
        KafkaStreams streams = new KafkaStreams(builder.build(), conf);
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            streams.close();
        }));
        streams.start();
    }
}
