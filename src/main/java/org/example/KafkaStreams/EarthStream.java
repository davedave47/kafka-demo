package org.example.KafkaStreams;
import java . util .*;
import java.lang.reflect.Field;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org . apache . kafka . streams .*;
import org . apache . kafka . streams . kstream .*;
import org . apache . kafka . common . serialization .*;

import org.example.Environment.Earth;
import org.example.KafkaStreams.Serde.EarthSerde;
import org.example.Serializer.EarthSerializer;

public class EarthStream {
    private Earth averageEarth = new Earth(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
    private Earth stdEarth = new Earth(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
    private int recordCount = 0;
    private final Random random = new Random();
    private KafkaProducer<String, Earth> producer = null;

    private synchronized Earth map(Earth value) {
        if (value == null) return null;

        // Increment the record recordCount
        recordCount++;

        // Use reflection to iterate through fields of Earth
        Field[] fields = Earth.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // Allow access to private fields

            try {
                Class<?> type = field.getType();

                if (type == float.class) {
                    float currentValue = field.getFloat(value);
                    if (Float.isNaN(currentValue)) {
                        // Impute missing value for float
                        float avgValue = field.getFloat(averageEarth);
                        float stdValue = field.getFloat(stdEarth);
                        float imputedValue = avgValue + random.nextFloat()*2*stdValue - stdValue;
                        field.setFloat(value, imputedValue);
                        currentValue = imputedValue;
                    }
                    // Update averages and standard deviations incrementally
                    float previousAvg = field.getFloat(averageEarth);
                    float newAvg = previousAvg + (currentValue - previousAvg) / recordCount;
                    float previousStd = field.getFloat(stdEarth);
                    float newStd = (float) Math.sqrt(
                            (previousStd * previousStd * (recordCount - 1)
                                    + (currentValue - previousAvg) * (currentValue - newAvg)) / recordCount
                    );

                    field.setFloat(averageEarth, newAvg);
                    field.setFloat(stdEarth, newStd);

                } else if (type == int.class) {
                    int currentValue = field.getInt(value);
                    float currentFloatValue = (float) currentValue;
                    if (currentValue == Integer.MIN_VALUE) {
                        // Impute missing value for int (convert the average from int bits to float)
                        float avgValue = Float.intBitsToFloat(field.getInt(averageEarth));
                        float stdValue = Float.intBitsToFloat(field.getInt(stdEarth));
                        float imputedValue = avgValue + random.nextFloat()*2*stdValue - stdValue;
                        field.setInt(value, (int) Math.round(imputedValue));
                        currentFloatValue = imputedValue;
                    }
                    // Update averages for int fields
                    float previousAvg = Float.intBitsToFloat(field.getInt(averageEarth));
                    float newAvg = previousAvg + (currentFloatValue - previousAvg) / recordCount;
                    float previousStd = Float.intBitsToFloat(field.getInt(stdEarth));

                    float newStd = (float) Math.sqrt(
                            ((previousStd * previousStd * (recordCount - 1)) + (currentFloatValue - previousAvg) * (currentFloatValue - newAvg))/recordCount
                    );
                    // Convert the new average and standard deviation back to int bits
                    field.setInt(averageEarth, Float.floatToIntBits(newAvg));
                    field.setInt(stdEarth, Float.floatToIntBits(newStd));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
    KafkaProducer<String, Earth> createProducer() {
        final Properties earthConf = new Properties();
        earthConf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29095");
        earthConf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        earthConf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EarthSerializer.class.getName());
        return new KafkaProducer<>(earthConf);
    }
    public void start() {
        System.out.println("Starting Earth Stream");
        Properties conf = new Properties () ;
        conf . put ( StreamsConfig . APPLICATION_ID_CONFIG , "earth-stream-app");
        conf . put ( StreamsConfig . BOOTSTRAP_SERVERS_CONFIG , "localhost:29094");
        conf . put ( StreamsConfig . NUM_STREAM_THREADS_CONFIG , 1) ;
        conf . put ( StreamsConfig . DEFAULT_KEY_SERDE_CLASS_CONFIG , Serdes . String () . getClass () . getName () );
        conf . put ( StreamsConfig . DEFAULT_VALUE_SERDE_CLASS_CONFIG , EarthSerde. class . getName () ) ;

        producer = createProducer();

        StreamsBuilder builder = new StreamsBuilder () ;
        KStream < String , Earth> earth_stream = builder .< String , Earth > stream ("earth");
        earth_stream.selectKey((k,v) -> v.getTime().toString())
                .mapValues(this::map)
                .foreach((key, value) -> {
                    producer.send(new ProducerRecord<>("processed_earth", key,value), (recordMetadata, e) -> {
                    });
                });

        KafkaStreams streams = new KafkaStreams ( builder . build () , conf );
        Runtime . getRuntime () . addShutdownHook ( new Thread () {
            public void run () {
                System.out.println (" Shutting down ... ") ;
                streams.close () ;
            }
        }) ;
        streams . start () ;
    }
}
