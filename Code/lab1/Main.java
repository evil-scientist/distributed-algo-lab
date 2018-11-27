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
		System.out.println("Press \"ENTER\" to continue...");
        try {
        System.in.read();
        } 
        catch (IOException e) 
        {
        e.printStackTrace();
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