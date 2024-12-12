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

import org.example.Environment.Water;
import org.example.KafkaStreams.Serde.WaterSerde;
import org.example.Serializer.WaterSerializer;

public class WaterStream {
    private Water averageWater = new Water(null, null, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    private Water stdWater = new Water(null, null, Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    private int recordCount = 0;
    private final Random random = new Random();
    private KafkaProducer<String, Water> producer = null;

    private synchronized Water map(Water value) {
        if (value == null) return null;

        // Increment the record recordCount
        recordCount++;

        // Use reflection to iterate through fields of Water
        Field[] fields = Water.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // Allow access to private fields

            try {
                Class<?> type = field.getType();

                if (type == float.class) {
                    float currentValue = field.getFloat(value);
                    if (Float.isNaN(currentValue)) {
                        // Impute missing value for float
                        float avgValue = field.getFloat(averageWater);
                        float stdValue = field.getFloat(stdWater);
                        float imputedValue = avgValue + random.nextFloat()*2*stdValue - stdValue;
                        field.setFloat(value, imputedValue);
                        currentValue = imputedValue;
                    }
                    // Update averages and standard deviations incrementally
                    float previousAvg = field.getFloat(averageWater);
                    if (Float.isNaN(previousAvg)) {
                        field.setFloat(averageWater, currentValue);
                    } else {
                        float newAvg = previousAvg + (currentValue - previousAvg) / recordCount;
                        field.setFloat(averageWater, newAvg);
                    }
                    float previousStd = field.getFloat(stdWater);
                    if (Float.isNaN(previousStd)) {
                        field.setFloat(stdWater, 0);
                    } else {
                        float newAvg = field.getFloat(averageWater);
                        float newStd = (float) Math.sqrt(
                                (previousStd * previousStd * (recordCount - 1)
                                        + (currentValue - previousAvg) * (currentValue - newAvg)) / recordCount
                        );
                        field.setFloat(stdWater, newStd);
                    }

                } else if (type == int.class) {
                    int currentValue = field.getInt(value);
                    float currentFloatValue = (float) currentValue;
                    if (currentValue == Integer.MIN_VALUE) {
                        // Impute missing value for int (convert the average from int bits to float)
                        float avgValue = Float.intBitsToFloat(field.getInt(averageWater));
                        float stdValue = Float.intBitsToFloat(field.getInt(stdWater));
                        float imputedValue = avgValue + random.nextFloat()*2*stdValue - stdValue;
                        field.setInt(value, Math.round(imputedValue));
                        currentFloatValue = imputedValue;
                    }
                    // Update averages for int fields
                    int previousAvgInt = field.getInt(averageWater);
                    float previousAvg = Float.intBitsToFloat(previousAvgInt);
                    if (previousAvgInt == Integer.MIN_VALUE) {
                        field.setInt(averageWater, Float.floatToIntBits(currentFloatValue));
                    } else {
                        float newAvg = previousAvg + (currentFloatValue - previousAvg) / recordCount;
                        field.setInt(averageWater, Float.floatToIntBits(newAvg));
                    }
                    int previousStdInt = field.getInt(stdWater);
                    float previousStd = Float.intBitsToFloat(previousStdInt);
                    if (previousStdInt == Integer.MIN_VALUE) {
                        field.setInt(stdWater, 0);
                    } else {
                        float newAvg = Float.intBitsToFloat(field.getInt(averageWater));
                        float newStd = (float) Math.sqrt(
                                ((previousStd * previousStd * (recordCount - 1)) + (currentFloatValue - previousAvg) * (currentFloatValue - newAvg)) / recordCount
                        );
                        field.setInt(stdWater, Float.floatToIntBits(newStd));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
    KafkaProducer<String, Water> createProducer() {
        final Properties waterConf = new Properties();
        waterConf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29095");
        waterConf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        waterConf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, WaterSerializer.class.getName());
        return new KafkaProducer<>(waterConf);
    }
    public void start() {
        System.out.println("Starting Water Stream");
        Properties conf = new Properties () ;
        conf . put ( StreamsConfig . APPLICATION_ID_CONFIG , "water-stream-app");
        conf . put ( StreamsConfig . BOOTSTRAP_SERVERS_CONFIG , "localhost:29093");
        conf . put ( StreamsConfig . NUM_STREAM_THREADS_CONFIG , 1) ;
        conf . put ( StreamsConfig . DEFAULT_KEY_SERDE_CLASS_CONFIG , Serdes . String () . getClass () . getName () );
        conf . put ( StreamsConfig . DEFAULT_VALUE_SERDE_CLASS_CONFIG , WaterSerde. class . getName () ) ;

        producer = createProducer();

        StreamsBuilder builder = new StreamsBuilder () ;
        KStream < String , Water> water_stream = builder .< String , Water > stream ("water");
        water_stream.selectKey((k,v) -> v.getTime().toString())
                .mapValues(this::map)
                .foreach((key, value) -> {
                    producer.send(new ProducerRecord<>("processed_water", key,value), (recordMetadata, e) -> {
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
