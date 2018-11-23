package Trial_1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageInt extends Remote
{
	public int gettimestamp() throws RemoteException;
	
	public int getmessageid() throws RemoteException;
	
	public char getmessagetype() throws RemoteException;

	public int getprocid() throws RemoteException;
	
	public void timestamp(int sclk) throws RemoteException;
} 