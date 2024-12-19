package org.example.rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.example.util.Result;
public interface IProcessFile extends  Remote {
    Result processFile(String fileName) throws RemoteException;
    String readData(String fileName) throws RemoteException;
}
