import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;



public class Main
{
	static int numproc; //number of processes
	static int proc_assign=0; //to check if program has been assigned a process
	static int proc_id; //Process identifier
	static int connected_processes=0;
	static int temp;

	public static void main(String[] args) throws RemoteException, AlreadyBoundException
	{
        numproc = Integer.parseInt(args[0]);
		proc_id = Integer.parseInt(args[1]);
		Process proc  = new Process(proc_id,numproc);  
		try 
		{
		java.rmi.Naming.bind("rmi://localhost/process"+proc_id, proc);
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
		Thread t1 = new Thread(() ->{
			String message = 'm'+Integer.toString(proc_id)+Integer.toString(proc.sclk);	
			if(proc_id==1)
			{
				proc.broadcast(message);
			}
		});
		t1.start();
	}
}