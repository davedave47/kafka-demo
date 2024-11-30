package org.example.KafkaStreams.Serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.example.Deserializer.EnvironmentDeserializer;
import org.example.Environment.Environment;
import org.example.Serializer.EnvironmentSerializer;

import java.util.Map;

public class EnvironmentSerde implements Serde<Environment> {
    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {
        // Do nothing , not necessary right now
    }
    @Override
    public Serializer <Environment> serializer () {
        return new EnvironmentSerializer();
    }
    @Override
    public Deserializer <Environment > deserializer () {
        return new EnvironmentDeserializer();
    }
    @Override
    public void close () {}
}
