/**
 * DA Class that implements Remote Interface ChandyLamport_RMI
 * Describes the procedures for a single process P
 * Used to create objects which are single process in system
 */
import java.rmi.*;
import java.util.ArrayList;
import java.util.List;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;

public class Component extends UnicastRemoteObject implements RMI_Interface
{
	private static final long serialVersionUID = 1L; // For RMI
	int processID; // Unique ID for each Process Object
	int total_proc; // Total number of channels = number of processes
	/*
	 * EACH PROCESS HAS 2 SETS OF CHANNELS
	 * 
	 */
	List<String> QBuffer = new ArrayList<>();// Array list for each channel to store the value of Channel c
	List<String> myState = new ArrayList<>(); // Array list for each Process to store its state
	private boolean recording_local_state = false; // Check if recording self state
	char marker = '#'; // Send # as marker
	String message; // Actual message sent over any channel
	
	public Component(int procID, int total_process) throws RemoteException
	{
		this.processID=procID;
		this.total_proc=total_process;
		QBuffer.add(0,"NA");
		myState.add(0,"NA");
		for (int i=1; i<=total_process; i++)
		{
		    QBuffer.add(i,"");
		    myState.add(i,"");
		}
		
	}

	// Record Local State
	public void record_local_state()
	{
		// DO SOMETHING TO RECORD LOCAL STATE
		// Maybe save value of some variable into a different variable
		System.out.println("Process " + processID + " recorded its local state: " + myState);
		
		recording_local_state = true;
		
		for (int i =1; i <= total_proc; i ++) {
			if (i == processID) {
				continue;
			}
			send(i,"#"); // Send marker along every channel
		}
		/*
		 *for (int i =0; i < total_proc; i ++) {
		 *	// Create message Buffer Q(c) for each channel 
		 *}
		 */
	}
	
	public void receive(int senderID,String message){
		/*
		 * MESSAGE HAVE THE FOLLOWING FORMAT:
		 * <type><sender process>
		 * ex: m1 [M from 1]
		 */
		// Received something along channel from Process with processID = senderID 
		//CHECK IF MARKER OR MESSAGE
		switch(message.charAt(0))
		{
			case '#':  
					System.out.println("Marker received from Process "+ senderID +" along Channel: "+senderID);
					// CHECK IF ALREADY RECORDED STATE
					if(!recording_local_state) 
					{
						// Record state of channel ID as 0 (empty)
						record_local_state();
					}
					else 
					{
						 System.out.println("The state of Channel "+Integer.toString(senderID)+" is : "+QBuffer.get(senderID)); // Contents of Q(c) == channelState(c);
					}
					break;						
			
			case 'm':
					System.out.println("Message: "+message+" received from Process "+ senderID +" along Channel: "+senderID);							
					if(recording_local_state) 
					{
						// ADD MESSAGE TO QBUFFER (replace the string of messages 
						// present at SenderID location in buffer to include latest message
						String update = QBuffer.get(senderID);
						update = update+","+message;
						QBuffer.add(senderID,update);
					}
					else {
						// Replace message string at senderId location 
						// in myState Buffer to reflect latest received message from Process [senderID]
						myState.add(senderID,message); 
					}
					break;
			default:
					System.out.println("Message type unknown");
					break;
		}			
	}

	// Method which sends a message/marker along a single channel
	public void send(int receiverID, String message){
		// Send message to Process with processID = receiverID
		try
		{
			RMI_Interface p =(RMI_Interface)java.rmi.Naming.lookup("rmi://localhost/process"+receiverID);
			myState.add(processID,message); // ADD LAST SENT MESSAGE TO OWN STATE
			p.receive(processID,message);
		}	
		catch (RemoteException | NotBoundException | MalformedURLException e)
		{	
			System.out.println("Error in sending");
		}
	}
}