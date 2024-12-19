package org.example;

import org.example.rmi.IProcessFile;
import org.example.util.BlockFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.rmi.Naming;
import org.example.util.Result;
import java.util.concurrent.*;
import java.util.Random;

public class ComputeToData {
    public static void main(String[] args) throws Exception {
        Result[] results = new Result[4];
        Map<String, Integer> portMap = new HashMap<>();
        portMap.put("hdfs-datanode1", 1099);
        portMap.put("hdfs-datanode2", 1098);
        portMap.put("hdfs-datanode3", 1097);

        BlockFinder finder = new BlockFinder();
        Map<String, String[]> blockMap = finder.findBlocks("/data/input.txt");
        Map<String, String> containerMap = finder.getContainers("hdfs_hdfs-net");

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Result>> futures = new ArrayList<>();
        Random random = new Random();

        for (Map.Entry<String, String[]> entry : blockMap.entrySet()) {
            String ip = entry.getValue()[random.nextInt(entry.getValue().length)];
            String container = containerMap.get(ip);
            String path = finder.getBlockPath(container, entry.getKey());
            Integer port = portMap.get(container);
            futures.add(executor.submit(() -> {
                try {
                    System.out.println("Processing block: " + entry.getKey() + " at "+container+" on port: " + port);
                    System.out.println("Path: "+path+"-");
                    IProcessFile processFile = (IProcessFile) Naming.lookup("rmi://localhost:" + port + "/processFile");
                    return processFile.processFile(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }));
        }
        for (int i = 0; i < futures.size(); i++) {
            Result result = futures.get(i).get();
            if (result != null) {
                results[i] = result;
            } else {
                throw new RuntimeException("Error processing file");
            }
        }
        executor.shutdown();
        Result finalResult = Result.aggregate(results);
        System.out.println(finalResult.toString());
        finalResult.printIntervals("");
    }
}
