/**
 * DA Class that implements Remote Interface ChandyLamport_RMI
 * Describes the procedures for a single process P
 * Used to create objects which are single process in system
 */
package chandyLamport;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
//import java.net.MalformedURLException;
//import java.rmi.RemoteException;

public class Component extends UnicastRemoteObject implements RMI
{
	private int processID; // Unique ID for each Process Object
	private int mystate = 0; // State of Process P
	int channelID; // Identify channel c
	int channel_number; // Total number of channels (must be)= number of processes
	// Array list for each channel which stores the value of Channel c
	
	private boolean recording_local_state = false; // Check if recording self state
	char marker = '#'; // Send # as marker
	char message; // Actual message sent over any channel (convert to int and add to mystate)
	
	private static final long serialVersionUID = 1L; // for RMI
	
	public Component(int procID, int state) throws RemoteException
	{
		this.processID=procID;
		this.mystate=state;
		try {
		    java.rmi.Naming.bind("rmi://localhost:1099/process"+processID, this);
		    System.err.println("Process ready");
			} 
		catch (Exception e) {
		    System.err.println("RMI Binding exception: " + e.toString());
		    e.printStackTrace();
			}
	}

	// Record Local State
	public void record_local_state()
	{
		// DO SOMETHING TO RECORD LOCAL STATE
		// Maybe save value of some variable into a different variable
		System.out.println("Process " + processID + " recorded its local state: " + mystate);
		
		recording_local_state = true;
		
		for (int i =0; i < channel_number; i ++) {
			send(channel_number,"#"); // Send marker along every channel
		}
		
		for (int i =0; i < channel_number; i ++) {
			// Create message Buffer Q(c) for each channel 
		}
	}
	
	public void receive(String message){
		// Receive message along channel
		switch(message.charAt(0)){
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
						int value = Integer.parseInt(message); 
						mystate = mystate + value;
						break;
			}			
	}

	// Method which sends a message along a single channel (can send marker)
	public void send(int procID, String message){
		// Send message along Channel ID
		try {	
				RMI proc = (RMI) java.rmi.Naming.lookup("rmi://localhost:1099/process"+procID);
				proc.receive(message);
		}
		catch (Exception e) {
		    System.err.println("Lookup exception: " + e.toString());
		    e.printStackTrace();
		}
	}
}