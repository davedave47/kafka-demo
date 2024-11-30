package org.example.KafkaStreams.Serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.example.Deserializer.WaterDeserializer;
import org.example.Environment.Water;
import org.example.Serializer.WaterSerializer;

import java.util.Map;

public class WaterSerde implements Serde<Water> {
    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {
        // Do nothing , not necessary right now
    }
    @Override
    public Serializer <Water> serializer () {
        return new WaterSerializer();
    }
    @Override
    public Deserializer <Water > deserializer () {
        return new WaterDeserializer();
    }
    @Override
    public void close () {}
}
