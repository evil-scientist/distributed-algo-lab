import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;
import java.util.Random;


public class Main
{
	static int numproc; //number of processes
	static int proc_assign=0; //to check if program has been assigned a process
	static int proc_id; //Process identifier
	static int connected_processes=0;
	static int temp;
	static int rint;// to store randomly generated integers for making a thread sleep
	static int checkmessageBuffer; // check if the message sent by the process is still in the buffer
	static int num_of_messages_sent=0;
	static String procid , clock;
	static Random rand = new Random();

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

			System.out.println("Clock: "+proc.sclk);
			procid=Integer.toString(proc_id);
			clock=Integer.toString(proc.sclk);
			String message = 'm'+procid;
			message=message.concat(clock);
			try
			{
				checkmessageBuffer=0;
				for(int i=0;i<proc.messageBuffer.size();i++)
				{
					if(Integer.valueOf(proc.messageBuffer.get(i).charAt(1))==proc_id)
					{
						checkmessageBuffer=1;
					}
				}
				if(checkmessageBuffer==0)
				{
					rint = rand.nextInt(100)+1;
					Thread.sleep(rint);
					proc.broadcast(message);
					num_of_messages_sent+=1;
				}
				if(num_of_messages_sent==3)
				{
					return;
				}
			}
			catch(InterruptedException e){}
		});
		t1.start();
	}
}