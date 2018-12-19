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
	static int rint;// to store randomly generated integers for making a thread sleep
	static Random rand = new Random();

	static char type; // Candidate or Ordinary type of Process 
	
	public static void main(String[] args)throws RemoteException, AlreadyBoundException
	{
		// Specify number of Processes to be created using argument
		// Set up number of channels for each process
		numproc = Integer.parseInt(args[0]);
		proc_id = Integer.parseInt(args[1]);
		type = args[2].charAt(0);
		Ordinary oproc  = new Ordinary(proc_id,numproc); 
		Candidate cproc  = new Candidate(proc_id,numproc);
		if (type == 'O')
		{ 
			try 
			{
				java.rmi.Naming.bind("rmi://localhost/process"+proc_id,oproc);
			}
			catch(MalformedURLException e)
			{
				System.out.println("Malformed URL");
			}
		}
		else
		{ 
			try 
			{
				java.rmi.Naming.bind("rmi://localhost/process"+proc_id,cproc);
			}
			catch(MalformedURLException e)
			{
				System.out.println("Malformed URL");
			}	
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
		System.out.println("\n\n----------------------STARTING ALGORITHM----------------------------------\n\n");
		 
        Thread t1 = new Thread(() ->{
		
			if (type == 'C')
			{
				if(proc_id==1)
				{
					try
					{
						Thread.sleep(3000);
					}
					catch(InterruptedException e){}
				}
				while(!cproc.untraversed.isEmpty())
				{
					int link = cproc.untraversed.get(0);
					cproc.SEND_SEMAPHORE = false;
					cproc.send(link,cproc.level,cproc.processID);
					try
					{
						do
						{
							Thread.sleep(1000);
						}
						while(cproc.SEND_SEMAPHORE == false); // FORCE WAITING FOR ACK
					}
					catch(InterruptedException e){}
				}
				
				if (!cproc.KILLED)
				{
					System.out.println("I have been ELECTED!");
					for(int i=1;i<=numproc;i++)
		 			{
		 				
						if(i==proc_id)
						{
							cproc.printlog();		
						}
						else
						{
							try
							{
								RMI_Interface p=(RMI_Interface)java.rmi.Naming.lookup("rmi://localhost/process"+i);
								p.printlog();
							}
							catch(RemoteException |NotBoundException |MalformedURLException e)
							{}
						}
		 			}
				}
			}
		
		});
		t1.start();
	}
}