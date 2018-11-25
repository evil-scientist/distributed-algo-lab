
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_Interface extends Remote
{
	public void receive(int channelID, String m) throws RemoteException;
} 