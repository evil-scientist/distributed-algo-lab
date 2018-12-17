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

public class Ordinary extends UnicastRemoteObject implements RMI_Interface
{
	private static final long serialVersionUID = 1L; // For RMI
	int processID; // Unique ID for each Process in system
	int total_process; // Each process should know all other processes
	
	int level; // Level stored by process
	int owner_ID; // Owner of the process, also its own ID for all comparisons
	
	int father; // Father of this process
	int potential_father; // Potential Father of this process 

	public static int sent_capture=0; // number of capture messages sent out
	public static int sent_ack=0; // number of acknowledgements sent out
	public static int receive_capture=0; // number of capture messages received
	public static int receive_ack=0; // number of acknowledgements received
	public static int num_capture=0; // number of times a process has been captured
	
	public Ordinary(int procID, int total_proc) throws RemoteException
	{
		this.processID=procID;
		this.total_process=total_proc;
		
		this.owner_ID = 0; // No owner at start
		this.level=0; // No level at start
		
		this.father = 0; // No father at start, equivalent to Father = nil
		this.potential_father = 0; // No Potential Father at start 

		this.sent_capture=0; 
		this.sent_ack=0;
		this.receive_capture=0;
		this.receive_ack=0;
		this.num_capture=0;
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
			if (owner_ID== sID)
			{
				return 0; // Same Level, Same Process ID
			}
			else if(owner_ID< sID)
			{
				return 1; // Same Level, Smaller Process
			}
			else  //(owner_ID>sID)
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
			case -1:  // Message is smaller, IGNORE
					System.out.println("Discarding received message: ["+ senderLevel +","+senderID+"], my owner has: ["+level+","+owner_ID+"]");
					receive_capture+=1;
					break;						
			
			case 1: // Message is larger, process is potential father 
					potential_father = senderID;						
					if(father == 0) // No owner  
					{
						father = potential_father;
						level = senderLevel; // Will now store owner's level
						owner_ID = father; // Will now store owner's ID	
					}
					send(father, senderLevel, senderID); // ACK or KILL to father	
					receive_capture+=1;
					sent_ack+=1;
					break;
			case 0: // Message from Previous Father, accepting kill
					father = potential_father;
					System.out.println("Captured by: "+father);	
					level = senderLevel; // Will now store owner's level
					owner_ID = father; // Will now store owner's ID	
					send(father,senderLevel, senderID); // ACK to new father	
					sent_ack+=1;
					receive_ack+=1;
					num_capture+=1;
					break;		
			default:
					System.out.println("How did this happen?");
					break;
		}			
	}

	public void printlog()
	{
		System.out.println("\n\n----------------------LOG----------------------------------\n\n");
		System.out.println("PROCESS :"+processID);	
		System.out.println("Maximum Level :"+level);
		System.out.println("Number of times process has been captured :"+num_capture);
		System.out.println("Number of capture messages sent :"+sent_capture);
		System.out.println("Number of capture messages received :"+receive_capture);
		System.out.println("Number of acknowledgements sent :"+sent_ack);
		System.out.println("Number of acknowledgements received :"+receive_ack);	
	}
}