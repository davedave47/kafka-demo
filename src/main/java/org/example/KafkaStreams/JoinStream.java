package org.example.KafkaStreams;

import java.time.Duration;
import java.util.Properties;

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
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29095");
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        conf.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        conf.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        // Create streams for Air, Water, and Earth
        KStream<String, Air> airStream = builder.stream("processed_air", Consumed.with(Serdes.String(), new AirSerde()));
        KStream<String, Water> waterStream = builder.stream("processed_water", Consumed.with(Serdes.String(), new WaterSerde()));
        KStream<String, Earth> earthStream = builder.stream("processed_earth", Consumed.with(Serdes.String(), new EarthSerde()));
        // Perform joins: Air + Water first, then join with Earth
        
        KStream<String, String> joinedStream = airStream
                .join(waterStream, (air, water) -> String.format("air=%s, water=%s", air.toString(), water.toString()),
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMillis(10000)), StreamJoined.with(Serdes.String(), new AirSerde(), new WaterSerde()))
                        .join(earthStream, (v, earth) -> String.format("%s, earth=%s", v, earth.toString()),
                                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMillis(10000)), StreamJoined.with(Serdes.String(), Serdes.String(), new EarthSerde()));

        // Publish the joined stream to the "environment" topic
        joinedStream.to("environment", Produced.with(Serdes.String(), Serdes.String()));
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
