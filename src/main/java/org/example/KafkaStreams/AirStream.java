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

import org.example.Environment.Air;
import org.example.KafkaStreams.Serde.AirSerde;
import org.example.Serializer.AirSerializer;

public class AirStream {
    private Air averageAir = new Air(null, null, Float.NaN, Float.NaN, Integer.MIN_VALUE, Float.NaN, Integer.MIN_VALUE, Integer.MIN_VALUE, Float.NaN, Float.NaN, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    private Air stdAir =new Air(null, null, Float.NaN, Float.NaN, Integer.MIN_VALUE, Float.NaN, Integer.MIN_VALUE, Integer.MIN_VALUE, Float.NaN, Float.NaN, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    private int recordCount = 0;
    private final Random random = new Random();
    private KafkaProducer<String, Air> producer = null;

    private synchronized Air map(Air value) {
        if (value == null) return null;
        boolean isNull = false;

        // Increment the record recordCount
        recordCount++;

        // Use reflection to iterate through fields of Air
        Field[] fields = Air.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // Allow access to private fields

            try {
                Class<?> type = field.getType();

                if (type == float.class) {
                    float currentValue = field.getFloat(value);
                    if (Float.isNaN(currentValue)) {
                        isNull = true;
                        // Impute missing value for float
                        float avgValue = field.getFloat(averageAir);
                        float stdValue = field.getFloat(stdAir);
                        float imputedValue = avgValue + random.nextFloat()*2*stdValue - stdValue;
                        field.setFloat(value, imputedValue);
                        currentValue = imputedValue;
                    }
                        // Update averages and standard deviations incrementally
                        float previousAvg = field.getFloat(averageAir);
                        if (Float.isNaN(previousAvg)) {
                            field.setFloat(averageAir, currentValue);
                        } else {
                            float newAvg = previousAvg + (currentValue - previousAvg) / recordCount;
                            field.setFloat(averageAir, newAvg);
                        }
                        float previousStd = field.getFloat(stdAir);
                        if (Float.isNaN(previousStd)) {
                                field.setFloat(stdAir, 0);
                            } else {
                                float newAvg = field.getFloat(averageAir);
                                float newStd = (float) Math.sqrt(
                                        (previousStd * previousStd * (recordCount - 1)
                                                + (currentValue - previousAvg) * (currentValue - newAvg)) / recordCount
                                );
                                field.setFloat(stdAir, newStd);
                            }

                } else if (type == int.class) {
                    int currentValue = field.getInt(value);
                    float currentFloatValue = (float) currentValue;
                    if (currentValue == Integer.MIN_VALUE) {
                        isNull = true;
                        // Impute missing value for int (convert the average from int bits to float)
                        float avgValue = Float.intBitsToFloat(field.getInt(averageAir));
                        float stdValue = Float.intBitsToFloat(field.getInt(stdAir));
                        float imputedValue = avgValue + random.nextFloat()*2*stdValue - stdValue;
                        field.setInt(value, Math.round(imputedValue));
                        currentFloatValue = imputedValue;
                    }
                        // Update averages for int fields
                        int previousAvgInt = field.getInt(averageAir);
                        float previousAvg = Float.intBitsToFloat(previousAvgInt);
                        if (previousAvgInt == Integer.MIN_VALUE) {
                            field.setInt(averageAir, Float.floatToIntBits(currentFloatValue));
                        } else {
                            float newAvg = previousAvg + (currentFloatValue - previousAvg) / recordCount;
                            field.setInt(averageAir, Float.floatToIntBits(newAvg));
                        }
                        int previousStdInt = field.getInt(stdAir);
                        float previousStd = Float.intBitsToFloat(previousStdInt);
                        if (previousStdInt == Integer.MIN_VALUE) {
                            field.setInt(stdAir, 0);
                        } else {
                            float newAvg = Float.intBitsToFloat(field.getInt(averageAir));
                            float newStd = (float) Math.sqrt(
                                    ((previousStd * previousStd * (recordCount - 1)) + (currentFloatValue - previousAvg) * (currentFloatValue - newAvg)) / recordCount
                            );
                            field.setInt(stdAir, Float.floatToIntBits(newStd));
                        }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
//        if (isNull) {
//            System.out.println("\n=============================================================================\nAverage:"+averageAir.toString() + "\nStd"+stdAir.toString()+"\nImputed missing values for air record: " + value.toString()+"\n==============================================================================================");
//        }
        return value;
    }
    KafkaProducer<String, Air> createProducer() {
        final Properties airConf = new Properties();
        airConf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29095");
        airConf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        airConf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AirSerializer.class.getName());
        return new KafkaProducer<>(airConf);
    }
    public void start() {
        System.out.println("Starting Air Stream");
        Properties conf = new Properties () ;
        conf . put ( StreamsConfig . APPLICATION_ID_CONFIG , "air-stream-app");
        conf . put ( StreamsConfig . BOOTSTRAP_SERVERS_CONFIG , "localhost:29092");
        conf . put ( StreamsConfig . NUM_STREAM_THREADS_CONFIG , 1) ;
        conf . put ( StreamsConfig . DEFAULT_KEY_SERDE_CLASS_CONFIG , Serdes . String () . getClass () . getName () );
        conf . put ( StreamsConfig . DEFAULT_VALUE_SERDE_CLASS_CONFIG , AirSerde. class . getName () ) ;

        producer = createProducer();

        StreamsBuilder builder = new StreamsBuilder () ;
        KStream < String , Air> air_stream = builder .< String , Air > stream ("air");
        air_stream.selectKey((k,v) -> v.getTime().toString())
                .mapValues(this::map)
                .foreach((key, value) -> {
                    producer.send(new ProducerRecord<>("processed_air", key,value), (recordMetadata, e) -> {
                    });
                });

        KafkaStreams streams = new KafkaStreams ( builder . build () , conf );
        Runtime . getRuntime () . addShutdownHook ( new Thread () {
            public void run () {
                System.out.println (" Shutting down ... ") ;
                streams.close () ;
            }
        }) ;
        streams.start();
    }
}
