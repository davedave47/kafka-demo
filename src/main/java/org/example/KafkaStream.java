package org.example;
import org.example.KafkaStreams.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaStream {
    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        AirStream airStream = new AirStream();
        WaterStream waterStream = new WaterStream();
        EarthStream earthStream = new EarthStream();
        JoinStream joinStream = new JoinStream();

        executorService.execute(airStream::start);
        executorService.execute(waterStream::start);
        executorService.execute(earthStream::start);
        executorService.execute(joinStream::start);

        executorService.shutdown();

    }
}
