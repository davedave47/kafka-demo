package org.example.KafkaStreams.Serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.example.Deserializer.EarthDeserializer;
import org.example.Environment.Earth;
import org.example.Serializer.EarthSerializer;

import java.util.Map;

public class EarthSerde implements Serde<Earth> {
    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {
        // Do nothing , not necessary right now
    }
    @Override
    public Serializer <Earth> serializer () {
        return new EarthSerializer();
    }
    @Override
    public Deserializer <Earth > deserializer () {
        return new EarthDeserializer();
    }
    @Override
    public void close () {}
}
