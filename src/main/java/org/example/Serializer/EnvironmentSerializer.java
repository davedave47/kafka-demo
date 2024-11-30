package org.example.Serializer;

import org.apache.kafka.common.serialization.Serializer;
import org.example.Environment.*;
import java.nio.ByteBuffer;

public class EnvironmentSerializer implements Serializer<Environment> {

    private final AirSerializer airSerializer = new AirSerializer();
    private final WaterSerializer waterSerializer = new WaterSerializer();
    private final EarthSerializer earthSerializer = new EarthSerializer();

    @Override
    public byte[] serialize(String topic, Environment data) {
        if (data == null) {
            return null;
        }

        // Serialize each component of the Environment object: Air, Water, Earth
        byte[] airBytes = airSerializer.serialize(topic, data.getAir());
        byte[] waterBytes = waterSerializer.serialize(topic, data.getWater());
        byte[] earthBytes = earthSerializer.serialize(topic, data.getEarth());

        // Calculate the lengths of each object
        int airLength = airBytes.length;
        int waterLength = waterBytes.length;
        int earthLength = earthBytes.length;

        // Create a ByteBuffer with space for the 3 lengths (int each) and the 3 byte arrays
        ByteBuffer buffer = ByteBuffer.allocate(12 + airLength + waterLength + earthLength); // 12 bytes for 3 lengths (3 * 4 bytes)

        // Write the lengths first (4 bytes each)
        buffer.putInt(airLength);
        buffer.putInt(waterLength);
        buffer.putInt(earthLength);

        // Then write the byte arrays
        buffer.put(airBytes);
        buffer.put(waterBytes);
        buffer.put(earthBytes);

        // Return the complete byte array
        return buffer.array();
    }
}
