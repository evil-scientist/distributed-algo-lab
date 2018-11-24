package chandyLamport;

import java.rmi.*;
//import java.rmi.Remote;
//import java.rmi.RemoteException;

public interface RMI extends Remote
{
	public void receive(String message) throws RemoteException;
} 