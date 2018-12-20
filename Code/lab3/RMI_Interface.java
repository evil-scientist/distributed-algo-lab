import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_Interface extends Remote
{
	public void printlog() throws RemoteException;
	public void receive(int link, int senderLevel, int senderID) throws RemoteException;
}