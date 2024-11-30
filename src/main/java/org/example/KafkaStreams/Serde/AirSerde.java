package org.example.KafkaStreams.Serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.example.Deserializer.AirDeserializer;
import org.example.Environment.Air;
import org.example.Serializer.AirSerializer;

import java.util.Map;

public class AirSerde implements Serde<Air> {
    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {
        // Do nothing , not necessary right now
    }
    @Override
    public Serializer <Air> serializer () {
        return new AirSerializer();
    }
    @Override
    public Deserializer <Air > deserializer () {
        return new AirDeserializer();
    }
    @Override
    public void close () {}
}
