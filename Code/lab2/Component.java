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
	int mes_rec=0;
	/*
	 * EACH PROCESS HAS 2 SETS OF CHANNELS
	 * 
	 */
	List<String> QBuffer = new ArrayList<>();// Array list for each channel to store the value of Channel c
	List<String> myStateS = new ArrayList<>(); // Array list for each Process to store last sent
	List<String> myStateR = new ArrayList<>(); // Array list for each Process to store last received
	List<Integer> channel_state = new ArrayList<>();
	
	public boolean recording_local_state = false; // Check if recording self state
	char marker = '#'; // Send # as marker
	//String message; // Actual message sent over any channel
	
	public Component(int procID, int total_process) throws RemoteException
	{
		this.processID=procID;
		this.total_proc=total_process;
		QBuffer.add(0,"NA");
		myStateS.add(0,"NA");
		myStateR.add(0,"NA");
		channel_state.add(0,1);
		
		for (int i=1; i<=total_process; i++)
		{
		    QBuffer.add(i,"empty");
		    myStateS.add(i,"empty");
		    myStateR.add(i,"empty");
			channel_state.add(i,0);
		}
		
	}

	// Record Local State
	public void record_local_state()
	{
		// DO SOMETHING TO RECORD LOCAL STATE
		// Maybe save value of some variable into a different variable
		
		// STATE OF PROCESS!
		System.out.println("\n\nProcess " + processID + " recorded its local state!");
		System.out.println("Last Sent: "+ myStateS);
		System.out.println("Last Received: "+ myStateR);
		System.out.println("\n\n");
		
		
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
					System.out.println("Marker received from Process "+ senderID +" along Channel: "+senderID+"->"+processID);
					// CHECK IF ALREADY RECORDED STATE
					if(!recording_local_state) 
					{
						// Record state of channel ID as 0 (empty)
						record_local_state();
					}
					else 
					{
		                // STATE OF CHANNEL!
						System.out.println("\n\nThe state of Channel : "+senderID+"->"+processID+" is : "+QBuffer.get(senderID)+"\n\n"); // Contents of Q(c) == channelState(c);
						channel_state.set(senderID,1);
					}
					break;						
			
			case 'm':
					System.out.println("Message: "+message+" received from Process "+ senderID +" along Channel: "+senderID+"->"+processID);	
					mes_rec+=1;						
					if(recording_local_state) 
					{
						// ADD MESSAGE TO QBUFFER (replace the string of messages 
						// present at SenderID location in buffer to include latest message
						String update = QBuffer.get(senderID);
						if(update=="empty")
						{
							QBuffer.set(senderID,message);
						}
						else
						{
							update = update+","+message;
							QBuffer.set(senderID,update);
						}
					}
					else {
						// Replace message string at senderId location 
						// in myState Buffer to reflect latest received message from Process [senderID]
						myStateR.set(senderID,message); 
					}
					break;
			default:
					System.out.println("Message type unknown");
					break;
		}			
	}

	// Method which sends a message/marker along a single channel
	public void send(int receiverID, String sending_message){
		// Send message to Process with processID = receiverID
		try
		{
				System.out.println("Sending message: "+sending_message+" to Process "+ receiverID +" along Channel: "+processID+"->"+receiverID);				
				RMI_Interface p =(RMI_Interface)java.rmi.Naming.lookup("rmi://localhost/process"+receiverID);
			
				myStateS.set(receiverID,sending_message); // ADD LAST SENT MESSAGE TO OWN STATE
			
				p.receive(processID,sending_message);
		}	
		catch (RemoteException | NotBoundException | MalformedURLException e)
		{	
			System.out.println("Error in sending");
		}
	}
}