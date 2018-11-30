/**
 * Main Class that creates all processes that run on a (single) host.
 * Used to create objects of Component class which are processes in system.
 */

import java.net.MalformedURLException;
import java.rmi.*;
import java.io.IOException;
import java.util.Random;

public class Main {

	static int numproc; //number of processes
	static int proc_id; //Process identifier
	static int connected_processes=0;
	static int temp;
	static int 	num_messages;
	static int rint;// to store randomly generated integers for making a thread sleep
	static Random rand = new Random();
	
	public static void main(String[] args)throws RemoteException, AlreadyBoundException
	{
		// Specify number of Processes to be created using argument
		// Set up number of channels for each process
		numproc = Integer.parseInt(args[0]);
		proc_id = Integer.parseInt(args[1]);
		num_messages = Integer.parseInt(args[2]);

		Component proc  = new Component(proc_id,numproc);  
		try 
		{
			java.rmi.Naming.bind("rmi://localhost/process"+proc_id,proc);
		}
		catch(MalformedURLException e)
		{
			System.out.println("Malformed URL");
		}

		while(connected_processes!=numproc)
		{
			connected_processes=0;
			for(int i=1;i<=numproc;i++)
			{
				try 
				{
					RMI_Interface p=(RMI_Interface)java.rmi.Naming.lookup("rmi://localhost/process"+i);
					connected_processes+=1;
				}
				catch(NotBoundException |MalformedURLException e)
				{}
			}
			if(temp!=(numproc-connected_processes))
			{
				System.out.println("Number of Processes yet to connect:"+(numproc-connected_processes));
				temp=numproc-connected_processes;
			}
		}

		System.out.println("All Processes have been connected");
		System.out.println("\n\n----------------------STARTING COMMUNICATION----------------------------------\n\n");
		 
        Thread t1 = new Thread(() ->{
		try
		{	
			for (int i =1; i<= num_messages; i++) // ITERATE OVER NUMBER OF MESSAGES TO BE SENT
			{
				rint = rand.nextInt(3000)+1000;
				String message = 'm'+Integer.toString(i);	
				for (int j = 1; j <= numproc; j ++) 
				{
					if (j == proc_id) 
					{
					    continue;
					}
				
					Thread.sleep(rint); // SLEEPY KITTY
				    proc.send(j,message); // Send message along channel
				
				    if(proc_id==1 && i == 2 && !proc.recording_local_state) // DECIDE WHO AND WHEN STARTS ALGO
				    {
					    System.out.println("\nI AM STARTING THE ALGORITHM\n");
					    proc.record_local_state();
				    }
				}
			}
		}
		catch(InterruptedException e){}
		});
		t1.start();
	}
}

/*
int mr=0;
			while(mr != (num_messages*(numproc-1))){mr=proc.mes_rec;}
			for(int j = 1; j<proc.channel_state.size();j++)
			{
				if(proc.channel_state.get(j)==0 && j!=proc_id)
				{
					System.out.println("\n\nThe state of Channel : "+j+"->"+proc_id+" is : "+proc.QBuffer.get(j)+"\n\n"); // Contents of Q(c) == channelState(c);
				}
			}
*/