package org.example.Deserializer;

import org.example.Environment.Earth;
import java.util.Map ;
import java.nio.ByteBuffer ;
import java.text.SimpleDateFormat ;
import org.apache.kafka.common.errors.SerializationException ;
import org.apache.kafka.common.serialization.Deserializer ;

public class EarthDeserializer implements Deserializer<Earth> {
    final SimpleDateFormat DF = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss") ;
    final String encoding = "UTF8";
    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {}
    @Override
    public Earth deserialize ( String topic , byte [] data ) {
        try {
            if ( data == null ) {
                return null ;
            }
            ByteBuffer buf = ByteBuffer.wrap(data);
            float temperature = buf.getFloat();
            float moisture = buf.getFloat();
            float ph = buf.getFloat();
            float salinity = buf.getFloat();
            int water_root = buf.getInt();
            int water_leaf = buf.getInt();
            int water_level = buf.getInt();
            float voltage = buf.getFloat();
            byte [] time = new byte [buf.getInt()];
            buf.get(time);
            byte [] station = new byte [buf.remaining()];
            buf.get(station);
            return new Earth(DF.parse(new String(time, encoding)), new String(station, encoding), temperature, moisture, ph, salinity, water_root, water_leaf, water_level, voltage);
        } catch ( Exception e) {
            throw new SerializationException (" Error when deserializing environment data .");
        }
    }
    @Override
    public void close () {}
}
