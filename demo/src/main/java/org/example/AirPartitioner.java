package org.example;

import org.example.Environment.Air ;
import java . util . Map ;
import org . apache . kafka . common . Cluster ;
import org . apache . kafka . clients . producer . Partitioner ;
public class AirPartitioner implements Partitioner {
    @Override
    public void configure ( Map < String , ?> configs ) {
    // Do nothing , not necessary right now
    }
    @Override
    public int partition ( String topic , Object key , byte [] keyBytes, Object value , byte [] valueBytes , Cluster cluster ) {
        Air data = ( Air ) value ;
        return data.getStation().equals("SVDT1") ? 0 : 1;
    }
    @Override
    public void close () {
    // Do nothing , not necessary right now
    }
}