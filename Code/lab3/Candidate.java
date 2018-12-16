/**
 * DA Class that implements Remote RMI_Interface
 * Describes the procedures for a single process Ordinary P
 * Used to create objects which are single process in system
 * This PROCESS does NOT want to be elected 
 */
import java.rmi.*;
import java.util.ArrayList;
import java.util.List;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;

public class Candidate extends UnicastRemoteObject implements RMI_Interface
{
	private static final long serialVersionUID = 1L; // For RMI
	int processID; // Unique ID for each Process in system
	int total_process; // Each process should know all other processes
	
	int level; // Level of candidate
	List<Integer> untraversed = new ArrayList<>(); // List of links to send message

	
	boolean KILLED; // Used to show if killed or alive
	boolean SEND_SEMAPHORE; // Used to halt send loop to wait for ACK


	public Candidate(int procID, int total_proc) throws RemoteException
	{
		this.processID=procID;
		this.total_process=total_proc;		
		
		this.level=1; // No level at start
		
		for (int i=1; i<=total_process; i++) // Initialize list of untraversed links 
		{
			if (i == procID) // Not included process itself in list of links
			{
				continue;
			}
			this.untraversed.add(i);  
		}

		this.KILLED = false; // Not dead
		this.SEND_SEMAPHORE = false; // didnt receive ACK 
		
	}

/*	Function to compare messages lexicographically
	returns -1 if message smaller 
	returns 0 if equal
	returns 1 if message larger
*/	public int compareMessage(int sLevel, int sID)
	{
		if (level > sLevel) // level is smaller, no need to check ID
		{
			return -1; // Sending Process has Smaller Level 
		}
		else if (level == sLevel) // level is same, compare IDs 
		{
			if (processID == sID)
			{
				return 0; // Same Level, Same Process ID
			}
			else if(processID<sID)
			{
				return 1; // Same Level, Smaller Process
			}
			else //if(processID>sID)
			{
				return -1;
			}
		}
		else // level is larger, compare IDs
		{
			return 1;	
		}
	}
	// Method which sends a message
	public void send(int receiverID, int senderLevel, int senderID)
	{
		// Send message to Process with processID = receiverID (link number is same as Process ID of receiver)
		try
		{
				System.out.println("Sending message:"+" ["+senderLevel+","+senderID+"] "+"to Process "+ receiverID);				
				RMI_Interface p =(RMI_Interface)java.rmi.Naming.lookup("rmi://localhost/process"+receiverID);			
				// Maybe add delay?
				p.receive(processID,senderLevel,senderID);
		}	
		catch (RemoteException | NotBoundException | MalformedURLException e)
		{	
			
			System.out.println(e);
			System.out.println("Error in sending");

		}
	}
	public void receive(int link, int senderLevel, int senderID)
	{
		/*
		 * MESSAGE HAVE THE FOLLOWING FORMAT:
		 * <sender's Level><sender's ID>
		 * ex: 1,10 [level 1, Process ID 1] [can come from any link though]
		 */

		System.out.println("Received message: ["+ senderLevel +","+senderID+"]");		
		int message_state = compareMessage(senderLevel, senderID);
		switch(message_state)
		{
			case -1:// Message is smaller, IGNORE
					System.out.println("Discarding received message: ["+ senderLevel +","+senderID+"]");
					break;									
			case 1: // Message is larger, get killed 
					KILLED = true;
					send(senderID, senderLevel, senderID); // send ACK to the one that killed
					break;
			case 0: // Receiving ACK for message sent
					if (KILLED == false)
					{
						System.out.println("Captured another!");	
						level = level + 1;
						for(int i=0; i<=untraversed.size(); i++)
						{
							if(untraversed.get(i)==link) 
							{
								untraversed.remove(i);
								break;
							}
						}
						//untraversed.remove(link); // Remove captured link from untraversed
						SEND_SEMAPHORE = true;
						break;			
					}					
			default:
					System.out.println("How did this happen?");
					break;
		}			
	}
}