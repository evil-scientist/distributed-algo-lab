import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.Remote;
import java.rmi.RemoteException;



public class Main
{
	static int numproc; //number of processes
	static int proc_assign=0; //to check if program has been assigned a process
	static int proc_id; //Process identifier
	static Process proc;

	public static void main(String[] args) throws RemoteException, AlreadyBoundException
	{
        numproc = Integer.parseInt(args[0]);
		proc_id = Integer.parseInt(args[1]);
		Process proc  = new Process(proc_id,numproc);  
		try 
		{
		java.rmi.Naming.bind("rmi://localhost/process"+proc_id,(Remote) proc);
		}
		catch(MalformedURLException e)
		{
			System.out.println("Malformed URL");
		}
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