/**
 * Main Class that creates all processes that run on a (single) host.
 * Used to create objects of Component class which are processes in system.
 */

import java.net.MalformedURLException;
import java.rmi.*;
import java.io.IOException;
public class Main {

	static int numproc; //number of processes
	static int proc_id; //Process identifier
	static int connected_processes=0;
	
	public static void main(String[] args)throws RemoteException, AlreadyBoundException
	{
		// Specify number of Processes to be created using argument
		// Set up number of channels for each process
		numproc = Integer.parseInt(args[0]);
		proc_id = Integer.parseInt(args[1]);
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
					java.rmi.Naming.lookup("rmi://localhost/process"+i);
					connected_processes+=1;
				}
				catch(NotBoundException |MalformedURLException e)
				{}
			}
			System.out.println("Number of Processes yet to connect:"+(numproc-connected_processes));
		}

		System.out.println("All Processes have been connected");
        
		Thread t1 = new Thread(() ->{
			for (int i =1; i<= 5; i++) 
			{
				String message = 'm'+Integer.toString(i);	
				for (int j = 1; j <= numproc; j ++) 
				{
					if (j == proc_id) 
					{
					continue;
					}
				proc.send(j,message); // Send marker along every channel
				
				}
				if(proc_id==1)
				{
					proc.record_local_state();
				}
			}
		});
		t1.start();
	}
}
