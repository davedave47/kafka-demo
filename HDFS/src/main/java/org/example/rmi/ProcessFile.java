package org.example.rmi;
import java . io .*;
import java . rmi . RemoteException ;
import java . rmi . server . UnicastRemoteObject ;
import java.text.ParseException;

import org.example.util.Result;
import org.example.util.DataParser;
import org.example.util.ParserResult;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;public class ProcessFile extends UnicastRemoteObject implements IProcessFile {
    public ProcessFile() throws RemoteException {
    }

    public Result processFile(String fileName) {
        LinkedList<ParserResult> data = new LinkedList<>();
        Result result = new Result(Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    ParserResult parsed = DataParser.parseData(line);
                    data.add(parsed);
                } catch (ParseException e) {
                    System.out.println("Invalid data: " + line);
                }
            }
            reader.close();
            Collections.sort(data, new Comparator<ParserResult>() {
                @Override
                public int compare(ParserResult o1, ParserResult o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
            for (ParserResult parsed : data) {
                result.setMaxMin(parsed.getTemp(), parsed.getMoisture(), parsed.getPH());
                if (!parsed.isValid()) {
                    System.out.println(parsed.toString());
                    result.addMinute(parsed.getTime());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Printing intervals of block: " + fileName);
        result.printIntervals("");
        return result;
    }
    public String readData(String fileName) {
        StringBuilder data = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                if (data.length() > 0) {
                    data.append("\n");
                }
                data.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
