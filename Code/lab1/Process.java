

import java.rmi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.server.UnicastRemoteObject;


public class Process extends UnicastRemoteObject implements RMI_Interface
{
	private static final long serialVersionUID = 1L;
	int sclk=0,ack_counter=0;
	public int proc_id,total_proc;
	List<String> ackBuffer = new ArrayList<>();
	List<String> messageBuffer = new ArrayList<>();
	
	// CONSTRUCTOR FOR SINGLE PROCESS
	public Process(int procid,int totproc) throws RemoteException
	{
		this.proc_id=procid;
		this.total_proc=totproc;
	}

	public void receive(String message)
	{
		/*
		 * MESSAGE HAVE THE FOLLOWING FORMAT:
		 * <type><sender process><timestamp>
		 * ex: m15 [M from 1 at time 5]
		 * ACK HAVE THE FOLLOWING FORMAT:
		 * <type><message for which ack><process_sending_ack>
		 * ex: a12 [ACK FOR m1 from process 2]
		 */
			switch(message.charAt(0))
			{
				case 'm':  
						System.out.println("Message received from Process "+message.charAt(1));	
						
						// GET CORRECT TIMESTAMP
						sclk= Math.max(sclk+1,Integer.parseInt(message.substring(2)));
						message = message.substring(0, 1)+Integer.toString(sclk); 
	
						// STORE IN MESSAGE BUFFER
						messageBuffer.add(message); 
						sort();
	
						// SEND ACK FOR RECEIVED MESSAGE
						// INCREMENT TIMESTAMP
						sclk += 1;
						String ack = "a"+message.charAt(1)+Integer.toString(proc_id); // Look into sending timestamp for ACK | really needed?
						for(int i=1;i<=total_proc;i++)
						{
							broadcast(ack);
						}
						break;
				case 'a':
						System.out.println("Acknowledgement received from Process "+message.charAt(2));	
						char messageID = message.charAt(1); 
						try
						{
							if(messageID == messageBuffer.get(0).charAt(1)) // CHECK IF ACK IS FOR HEAD OF MESSAGE BUFFER
							{
								ack_counter  += 1;
								// CHECK IF ALL ACK RECEIVED FOR HEAD
								if (ack_counter == total_proc) 
								{
									ack_counter = 0; // RESET COUNTER
									deliver(); // METHOD TO CLEAR BOTH BUFFERS
								}
							}
							else 
							{
								ackBuffer.add(message);
							}
						}
						catch(IndexOutOfBoundsException e)
						{}
						break;
				default:
						System.out.println("Message type unknown");
						break;
			}
	}

	public void sort() 
	{
		String[] buffer = new String[messageBuffer.size()];
	    buffer = messageBuffer.toArray(buffer);
		Arrays.sort(buffer, new Comparator<String>() 
		{
		    public int compare(String str1, String str2) 
		    {
		        String substr1 = str1.substring(2);
		        String substr2 = str2.substring(2);
		        return Integer.valueOf(substr1).compareTo(Integer.valueOf(substr2));
		    }
		});
		
		messageBuffer = Arrays.asList(buffer);
	}
	public void deliver() 
	{
		System.out.println("Message "+messageBuffer.get(0).charAt(1)+"delivered");
		messageBuffer.remove(0); // DELETE MESSAGE AT HEAD (deliver)
		for (int i = 0; i< ackBuffer.size(); i++) // ITERATE OVER ACK BUFFER TO INCREMENT COUNTER FOR NEW HEAD
		{
			if (messageBuffer.get(0).charAt(1) == ackBuffer.get(i).charAt(1)) 
			{
				ack_counter += 1;
				ackBuffer.remove(i); // ALREADY COUNTED, hence removed
			}
		}
	}
	
	public void broadcast(String message)
	{
		for(int i=1; i<=total_proc;i++)
		{
			try
			{
				if(i==proc_id)
				{
					receive(message);
					continue;
				}
				RMI_Interface p =(RMI_Interface)java.rmi.Naming.lookup("rmi://localhost/process"+i);
				p.receive(message);
				
			}
			catch (RemoteException | NotBoundException | MalformedURLException e)
			{
				continue;
			}
		}

	}

}