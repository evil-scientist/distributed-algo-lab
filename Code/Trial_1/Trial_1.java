package Trial_1;

import java.util.Scanner;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NotBoundException;
import java.rmi.*;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public class Trial_1
{
	static int numproc; //number of processes
	static int proc_assign=0; //to check if program has been assigned a process
	static int proc_id; //Process identifier
	public static Process proc;
	//static Message stub;
	public static Registry reg;
	public Trial_1() {}

	public static void main(String args[])
	{
		try
		{
			//Trial_1 obj = new Trial_1();
			//MessageInt stub = (MessageInt) UnicastRemoteObject.exportObject(obj,1100);
			reg = LocateRegistry.createRegistry(1099);
			//stub =new Message();
			//reg.bind("Message",(Registry)stub);
			//reg=LocateRegistry.getRegistry("localhost",1099);
			//System.out.println("Registry already running.");
			System.out.println("Registry created");
		}
		catch(RemoteException ex)
		{
			try
			{
				//reg = LocateRegistry.createRegistry(1099);
				//System.out.println("Registry created");
				reg=LocateRegistry.getRegistry("localhost",1099);
				System.out.println("Registry already running.");
			}
			catch(RemoteException e) {}
		}
		
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter the number of processes:");
		numproc = sc.nextInt();
		while(proc_assign==0)
		{
			System.out.println("Enter process ID:");
			proc_id = sc.nextInt();
			if(proc_id>numproc)
			{
				System.out.println("ID should be within [1,"+numproc+"]");
				continue;
			}
			try
			{
				reg.lookup("Process:"+proc_id);
				System.out.println("Process with that ID already exists,try again");
			}
			catch(RemoteException | NotBoundException e)
			{
				try
				{
				proc_assign=1;
				proc = new Process(proc_id,numproc);
				reg.bind(("Process:"+proc_id),(Registry)proc);
				}
				catch(RemoteException | AlreadyBoundException ex) {}
			}
		}

		Thread t1 = new Thread(() ->{
				if(proc_id==1)
				{
					proc.broadcast();
				}
		});
		t1.start();

		Thread t2 = new Thread(()->{
				proc.checkacklist();
		});
		t2.start();
	}
}