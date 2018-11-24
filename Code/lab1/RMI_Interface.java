

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_Interface extends Remote
{
	public void receive(String m) throws RemoteException;
} 