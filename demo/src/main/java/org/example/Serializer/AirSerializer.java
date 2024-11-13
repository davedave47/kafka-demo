package org.example.Serializer;

import org.example.Environment.Air;

import java.util.Map ;
import java.nio.ByteBuffer ;
import java.text.SimpleDateFormat ;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer ;

public class AirSerializer implements Serializer<Air> {
    final String encoding = "UTF8";
    final SimpleDateFormat DF = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss") ;
    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {
        // Nothing to configure
    }
    @Override
    public byte [] serialize ( String topic , Air data ) {
        try {
            if ( data == null )
                return null ;
            else {
                byte [] station = data.getStation().getBytes( encoding ) ;
                byte [] time = DF . format ( data.getTime() ).getBytes (encoding ) ;
                ByteBuffer buffer = ByteBuffer.allocate(4*12 + time.length + station.length); // station length and actual bytes);
//    float temperature;
//    float moisture;
//    int light;
//    float total_rainfall;
//    int rainfall;
//    int wind_direction;
//    float pm25;
//    float pm10;
//    int co;
//    int nox;
//    int so2;
//    Date time;
//    String station;

                buffer.putFloat(data.getTemperature());
                buffer.putFloat(data.getMoisture());
                buffer.putInt(data.getLight());
                buffer.putFloat(data.getTotal_rainfall());
                buffer.putInt(data.getRainfall());
                buffer.putInt(data.getWind_direction());
                buffer.putFloat(data.getPm25());
                buffer.putFloat(data.getPm10());
                buffer.putInt(data.getCo());
                buffer.putInt(data.getNox());
                buffer.putInt(data.getSo2());
                buffer.putInt(time.length);
                buffer.put(time);
                buffer.put(station);
                return buffer.array();
            }
        } catch ( Exception e ) {
            throw new SerializationException("Error when serializing Air to byte []" + e);
        }
    }
    @Override
    public void close () {
        // Nothing to close
    }
}
