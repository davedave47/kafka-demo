package org.example.Deserializer;

import org.example.Environment.Water;
import java.util.Map ;
import java.nio.ByteBuffer ;
import java.text.SimpleDateFormat ;
import org.apache.kafka.common.errors.SerializationException ;
import org.apache.kafka.common.serialization.Deserializer ;

public class WaterDeserializer implements Deserializer<Water> {
    final SimpleDateFormat DF = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");
    final String encoding = "UTF8";

    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {}
    @Override
    public Water deserialize ( String topic , byte [] data ) {
        try {
            if ( data == null ) {
                return null ;
            }
            ByteBuffer buf = ByteBuffer.wrap(data);
            float ph = buf.getFloat();
            float DO = buf.getFloat();
            float temperature = buf.getFloat();
            float salinity = buf.getFloat();
            byte [] time = new byte[buf.getInt()];
            buf.get(time);
            byte [] station = new byte[buf.remaining()];
            buf.get(station);
            return new Water(DF.parse(new String(time, encoding)), new String(station, encoding), ph, DO, temperature, salinity);
        } catch ( Exception e) {
            throw new SerializationException (" Error when deserializing environment data .");
        }
    }
    @Override
    public void close () {}
}
