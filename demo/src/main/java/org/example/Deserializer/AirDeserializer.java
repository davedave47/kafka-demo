package org.example.Deserializer;

import org.example.Environment.Air;
import java.util.Map ;
import java.nio.ByteBuffer ;
import java.text.SimpleDateFormat ;
import org.apache.kafka.common.errors.SerializationException ;
import org.apache.kafka.common.serialization.Deserializer ;

public class AirDeserializer implements Deserializer<Air> {
    final SimpleDateFormat DF = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss") ;
    final String encoding = "UTF8";
    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {}
    @Override
    public Air deserialize ( String topic , byte [] data ) {
        try {
            if ( data == null ) {
                return null ;
            }
            ByteBuffer buf = ByteBuffer.wrap(data);
            float temperature = buf.getFloat();
            float moisture = buf.getFloat();
            int light = buf.getInt();
            float total_rainfall = buf.getFloat();
            int rainfall = buf.getInt();
            int wind_direction = buf.getInt();
            float pm25 = buf.getFloat();
            float pm10 = buf.getFloat();
            int co = buf.getInt();
            int nox = buf.getInt();
            int so2 = buf.getInt();
            byte [] time = new byte [buf.getInt()];
            buf.get(time);
            byte [] station = new byte [buf.remaining()];
            buf.get(station);
            return new Air(DF.parse(new String(time, encoding)), new String(station, encoding), temperature, moisture, light, total_rainfall, rainfall, wind_direction, pm25, pm10, co, nox, so2);
        } catch ( Exception e) {
            throw new SerializationException (" Error when deserializing environment data .");
        }
    }
    @Override
    public void close () {}
}
