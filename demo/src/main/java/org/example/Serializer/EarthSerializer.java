package org.example.Serializer;

import org.example.Environment.Earth;

import java.util.Map ;
import java.nio.ByteBuffer ;
import java.text.SimpleDateFormat ;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer ;

public class EarthSerializer implements Serializer<Earth> {
    final String encoding = "UTF8";
    final SimpleDateFormat DF = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss") ;

    @Override
    public void configure ( Map < String , ?> configs , boolean isKey ) {
    // Nothing to configure
    }
    @Override
    public byte [] serialize ( String topic , Earth data ) {
        try {
            if ( data == null )
                return null ;
            else {
                byte [] station = data.getStation().getBytes( encoding ) ;
                byte [] time = DF . format ( data.getTime() ).getBytes (encoding ) ;
                ByteBuffer buffer = ByteBuffer.allocate( 4*9 + time.length + station.length); // station length and actual bytes);
//                float temperature;
//                float moisture;
//                float ph;
//                float salinity;
//                int water_root;
//                int water_leaf;
//                int water_level;
                // float voltage;
                // Date time;
                //String station;
                buffer.putFloat(data.getTemperature());
                buffer.putFloat(data.getMoisture());
                buffer.putFloat(data.getPh());
                buffer.putFloat(data.getSalinity());
                buffer.putInt(data.getWater_root());
                buffer.putInt(data.getWater_leaf());
                buffer.putInt(data.getWater_level());
                buffer.putFloat(data.getVoltage());
                buffer.putInt(time.length);
                buffer.put(time);
                buffer.put(station);
                return buffer.array();
            }
        } catch ( Exception e ) {
            throw new SerializationException("Error when serializing Earth to byte []" + e);
        }
    }
    @Override
    public void close () {
    // Nothing to close
    }
}
