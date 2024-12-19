package org.example.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BlockFinder {

    private String execCommand(String[] command) {
        ProcessBuilder builder = new ProcessBuilder(command);
        try {
            Process process = builder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (output.length() > 0) {
                    output.append("\n");
                }
                output.append(line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getContainers(String network) {
        String[] command = {"sh", "-c", "docker network inspect " + network + " | jq -r '.[0].Containers[] | \"\\(.Name) \\(.IPv4Address)\"'"};
        String output = execCommand(command);
        Map<String, String> containerMap = new HashMap<>();
        if (output != null) {
            String[] lines = output.split("\n");
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    containerMap.put(parts[1].split("/")[0], parts[0]);
                }
            }
        }
        return containerMap;
    }

    public Map<String, String[]> findBlocks(String filename) {
        String[] command = {"sh", "-c", "docker exec -i hdfs-namenode hdfs fsck " + filename + " -locations -blocks -files"};
        String output = execCommand(command);
        Map<String, String[]> blockMap = new HashMap<>();
        if (output != null) {
            String[] lines = output.split("\n");
            for (String line : lines) {
                if (line.contains("BP-") && line.contains("blk_")) {
                    String[] blockName = line.split(":")[1].split(" ")[0].split("_");
                    String[] parts = line.split("DatanodeInfoWithStorage\\[");
                    String[] ipAddresses = new String[parts.length-1];
                    for (int i = 1; i < parts.length; i++) {
                        String ip = parts[i].split(":")[0];
                        ipAddresses[i-1] = parts[i].split(":")[0];
                    }
                    blockMap.put(blockName[0]+"_"+blockName[1], ipAddresses);
                }
            }
        }
        return blockMap;
    }
    public String getBlockPath(String nodename, String filename) {
        String[] command = {"sh", "-c", "docker exec -i "+nodename+" find -name "+filename};
        String output = execCommand(command);
        return output.substring(1);
    }

    public static void main(String[] args) {
        BlockFinder finder = new BlockFinder();
        Map<String, String> containers = finder.getContainers("hdfs_hdfs-net");
        Map<String, String[]> blocks = finder.findBlocks("input.txt");

        for (Map.Entry<String, String[]> entry : blocks.entrySet()) {
            System.out.println("Block: " + entry.getKey());
            System.out.print("Containers: ");
            String ip = entry.getValue()[0];
            String container = containers.get(ip);
            System.out.println(container);
            String path = finder.getBlockPath(container, entry.getKey());
            System.out.print("Path: "+path);
        }
    }

}