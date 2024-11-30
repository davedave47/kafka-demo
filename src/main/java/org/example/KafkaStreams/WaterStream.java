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
    private final ThreadLocal<Water> averageWater = ThreadLocal.withInitial(() ->
            new Water(null, null, 0, 0, 0, 0));
    private final ThreadLocal<Water> stdWater = ThreadLocal.withInitial(() ->
            new Water(null, null, 0, 0, 0, 0));
    private final ThreadLocal<Integer> recordCount = ThreadLocal.withInitial(() -> 0);
    private final ThreadLocal<Random> random = ThreadLocal.withInitial(() -> new Random());
    private KafkaProducer<String, Water> producer = null;

    private Water map(Water value) {
        if (value == null) return null;

        // Increment the record count
        int count = recordCount.get() + 1;
        recordCount.set(count);

        // Retrieve current averages and standard deviations
        Water avg = averageWater.get();
        Water std = stdWater.get();

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
                        float avgValue = field.getFloat(avg);
                        float stdValue = field.getFloat(std);
                        float imputedValue = avgValue + random.get().nextFloat()*2*stdValue - stdValue;
                        field.setFloat(value, imputedValue);
                        currentValue = imputedValue;
                    }
                    // Update averages and standard deviations incrementally
                    float previousAvg = field.getFloat(avg);
                    float newAvg = previousAvg + (currentValue - previousAvg) / count;
                    float previousStd = field.getFloat(std);
                    float newStd = (float) Math.sqrt(
                            (previousStd * previousStd * (count - 1)
                                    + (currentValue - previousAvg) * (currentValue - newAvg)) / count
                    );

                    field.setFloat(avg, newAvg);
                    field.setFloat(std, newStd);

                } else if (type == int.class) {
                    int currentValue = field.getInt(value);
                    float currentFloatValue = (float) currentValue;
                    if (currentValue == Integer.MIN_VALUE) {
                        // Impute missing value for int (using average)
                        float avgValue = Float.intBitsToFloat(field.getInt(avg));
                        float stdValue = Float.intBitsToFloat(field.getInt(std));
                        float imputedValue = avgValue + random.get().nextFloat()*2*stdValue - stdValue;
                        field.setInt(value, (int) Math.round(imputedValue));
                        currentFloatValue = imputedValue;
                    }
                    // Update averages for int fields
                    float previousAvg = Float.intBitsToFloat(field.getInt(avg));
                    float newAvg = previousAvg + (currentFloatValue - previousAvg) / count;

                    float previousStd = Float.intBitsToFloat(field.getInt(std));
                    float newStd = (float) Math.sqrt(
                            ((previousStd * previousStd * (count - 1)) + (currentFloatValue - previousAvg) * (currentFloatValue - newAvg))/count
                    );

                    field.setInt(avg, Float.floatToIntBits(newAvg));
                    field.setInt(std, Float.floatToIntBits(newStd));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Update thread-local variables
        averageWater.set(avg);
        stdWater.set(std);


        return value;
    }
    KafkaProducer<String, Water> createProducer() {
        final Properties waterConf = new Properties();
        waterConf.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.222:29095");
        waterConf.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        waterConf.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, WaterSerializer.class.getName());
        return new KafkaProducer<>(waterConf);
    }
    public void start() {
        System.out.println("Starting Water Stream");
        Properties conf = new Properties () ;
        conf . put ( StreamsConfig . APPLICATION_ID_CONFIG , "water-stream-app");
        conf . put ( StreamsConfig . BOOTSTRAP_SERVERS_CONFIG , "192.168.1.222:29093");
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
