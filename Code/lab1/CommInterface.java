package lab1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CommInterface extends Remote {
	boolean checkMessageBuffer(CommInterface ci,String name, String pass) throws RemoteException;
    void broadcastMessage(String name,String message) throws RemoteException;
}