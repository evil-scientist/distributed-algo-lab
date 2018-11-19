
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatInterface extends Remote {
    boolean checkClientCredintials(ChatInterface ci,String name, String pass) throws RemoteException;
    void broadcastMessage(String name,String message) throws RemoteException;
    void sendMessageToClient(String message) throws RemoteException;
}