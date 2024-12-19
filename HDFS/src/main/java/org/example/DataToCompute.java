package org.example;

import org.example.rmi.IProcessFile;
import org.example.util.BlockFinder;
import org.example.util.DataParser;
import org.example.util.ParserResult;
import org.example.util.Result;

import java.io.IOException;
import java.rmi.Naming;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DataToCompute {
    public static void main(String[] args) {
        Date start = new Date();
        Map<String, Integer> portMap = new HashMap<>();
        portMap.put("hdfs-datanode1", 1099);
        portMap.put("hdfs-datanode2", 1098);
        portMap.put("hdfs-datanode3", 1097);

        BlockFinder finder = new BlockFinder();
        Map<String, String[]> blockMap = finder.findBlocks("/data/input.txt");
        Map<String, String> containerMap = finder.getContainers("hdfs_hdfs-net");

        StringBuilder filedata = new StringBuilder();

        for (Map.Entry<String, String[]> entry : blockMap.entrySet()) {
            String blockId = entry.getKey();
            String ip = entry.getValue()[0];
            String container = containerMap.get(ip);
            String path = finder.getBlockPath(container, blockId);
            Integer port = portMap.get(container);
            System.out.println("Processing block: " + blockId + " at " + container + " on port: " + port);

            if (ip != null && port != null && path != null) {
                try {
                    IProcessFile processFile = (IProcessFile) Naming.lookup("rmi://localhost:" + port + "/processFile");
                    String blockData = processFile.readData(path);
                    filedata.append(blockData);
                } catch (Exception e) {
                    System.err.println("Failed to fetch block " + blockId + " from DataNode at " + ip + ":" + port);
                    e.printStackTrace();
                }
            } else {
                System.err.println("Invalid block or DataNode information for block ID: " + blockId);
            }
        }

        LinkedList<ParserResult> parsedData = new LinkedList<>();
        Result result = new Result(Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
        try (BufferedReader reader = new BufferedReader(new StringReader(filedata.toString()))) {
            String line;
                while ((line = reader.readLine()) != null) {
                ParserResult parsed = DataParser.parseData(line);
                parsedData.add(parsed);
            }
            Collections.sort(parsedData, new Comparator<ParserResult>() {
                @Override
                public int compare(ParserResult p1, ParserResult p2) {
                    return p1.getTime().compareTo(p2.getTime());
                }
            });
            for (ParserResult parsed : parsedData) {
                result.setMaxMin(parsed.getTemp(), parsed.getMoisture(), parsed.getPH());
                if (!parsed.isValid()) {
                    result.addMinute(parsed.getTime());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Time taken: " + (new Date().getTime() - start.getTime()) + "ms");
        System.out.println(result.toString());
        result.printIntervals("");
    }
}
