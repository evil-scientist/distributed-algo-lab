/**
 * Remote Algorithm Class that implements Remote Interface ChandyLamport_RMI
 * Describes the procedure of a single process P
 */
package chandyLamport;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * @author evilscientist
 *
 */
public class Component {

	/**
	 * @param args
	 */
/*
	int sclk=0,ackinc=0,mesid;
	Registry r;
	List<int[]> ackList = new ArrayList<>();
	int procid,totproc;
	int temp[]={0,0};
	public static Process p;
	public static MessageInt m,m1,m2;
	private final PriorityQueue <MessageInt> mesQ;
*/
	private int processID; // Unique ID for each Process Object
	private int mystate = 0; // State of Process P
	int channelID; // Identify channel c
	int channel_number; // Total number of channels (must be)= number of processes
	// Array list for each channel which stores the value of Channel c
	
	private boolean recording_local_state = false; // Check if recording self state
	char marker = '#'; // Send # as marker
	char message; // Actual message sent over any channel (convert to int and add to mystate)
	
	// Record Local State
	public void record_local_state()
	{
		// DO SOMETHING TO RECORD LOCAL STATE
		// Maybe save value of some variable into a different variable
		System.out.println("Process " + processID + " recorded its local state: " + mystate);
		
		recording_local_state = true;
		
		for (int i =0; i < channel_number; i ++) {
			send(channel_number,'#'); // Send marker along every channel
		}
		
		for (int i =0; i < channel_number; i ++) {
			// Create message Buffer Q(c) for each channel 
		}
	}
	
	public void receive(char message)
	{
			switch(message)
			{
				// Received a marker
				case '#':   
						System.out.println("Marker received from Process "+ processID +" along Channel: "+channelID);
						if(!recording_local_state) {
							// Record state of channel ID as 0 (empty)
							record_local_state();
						}
						else {
							// Contents of Q(c) == channelState;
						}
						break;
				default:
						System.out.println("Message: "+message+"received from Process "+ processID +" along Channel: "+channelID);
						int value = Character.getNumericValue(message); 
						mystate = mystate + value;
						
						break;
			}
	}

	// Method which sends a message along a single channel
	// Can send marker
	public void send(int channelID, char message)
	{
		// Send message along Channel ID
	}
	
}
