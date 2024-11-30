package org.example.Deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.example.Environment.*;
import java.nio.ByteBuffer;

public class EnvironmentDeserializer implements Deserializer<Environment> {

    private final AirDeserializer airDeserializer = new AirDeserializer();
    private final WaterDeserializer waterDeserializer = new WaterDeserializer();
    private final EarthDeserializer earthDeserializer = new EarthDeserializer();

    @Override
    public Environment deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);

        // Read the lengths of each object (4 bytes for each)
        int airLength = buffer.getInt();
        int waterLength = buffer.getInt();
        int earthLength = buffer.getInt();

        // Read the serialized byte arrays for Air, Water, Earth
        byte[] airBytes = new byte[airLength];
        buffer.get(airBytes);

        byte[] waterBytes = new byte[waterLength];
        buffer.get(waterBytes);

        byte[] earthBytes = new byte[earthLength];
        buffer.get(earthBytes);

        // Deserialize each object using the respective deserializer
        Air air = airDeserializer.deserialize(topic, airBytes);
        Water water = waterDeserializer.deserialize(topic, waterBytes);
        Earth earth = earthDeserializer.deserialize(topic, earthBytes);

        // Create and return the Environment object
        return new Environment(air, water, earth);
    }
}
