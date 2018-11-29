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
	static String procidS , clockS;
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
		System.out.println("\n\n------------------------------------------------------------\n\n");
		
		Thread t1 = new Thread(() ->{
        try
    		{
			while(num_of_messages_sent != 2)
			    {			
			        procidS=Integer.toString(proc_id);
			        clockS=Integer.toString(proc.sclk);
			        String message = 'm' + procidS;
			        message=message.concat(clockS);
			        //System.out.println("DEBUG: SENDING RAW "+message);

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
					    rint = rand.nextInt(3000)+1000;
					    Thread.sleep(rint);
					    proc.broadcast(message);
					    num_of_messages_sent+=1;
				    }
			    }
			 }
			catch(InterruptedException e){}
		});
		t1.start();
		//return;
	}
}