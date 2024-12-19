package org.example.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
public class ProcessFileServer {
    public static void main(String[] args) throws Exception {
        try {
            IProcessFile processFile = new ProcessFile();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://0.0.0.0:1099/processFile", processFile);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
