package Trial_1;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;



public class Process extends UnicastRemoteObject
{
	int sclk=0,ackinc=0,mesid;
	Registry r;
	List<int[]> ackList = new ArrayList<>();
	int procid,totproc;
	int temp[]={0,0};
	public static Process p;
	public static MessageInt m,m1,m2;
	private PriorityQueue <MessageInt> mesQ;

	public Process(int procid,int totproc) throws RemoteException
	{
		procid=procid;
		totproc=totproc;
		mesid=procid;
		mesQ = new PriorityQueue<>(100,(m1, m2)->{
		try
		{
			if(m1.gettimestamp()<m2.gettimestamp())
			{
				return -1;
			}
			else if(m1.gettimestamp()>m2.gettimestamp())
			{
				return 1;
			}
			else if(m1.gettimestamp()==m2.gettimestamp())
			{
				if(m1.getprocid()<m2.getprocid())
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
		}
		catch(RemoteException e)
		{
			System.out.println(e);
		}
		return 1;
	});
	}

	public void receive(MessageInt rmessage)
	{
		try
		{
			m=rmessage;
			switch(m.getmessagetype())
			{
				case 'm':  
						sclk= Math.max(sclk+1,m.gettimestamp());
						m.timestamp(sclk);
						mesQ.add(m);
						System.out.println("Message received from Process "+m.getprocid()+" with message ID: "+m.getmessageid());
						Message ack = new Message(procid,'a',m.getmessageid());
						for(int i=1;i<=totproc;i++)
						{
							try
							{
								p=(Process)r.lookup("Process:"+i);
								p.receive(ack);
							}
							catch(NotBoundException e)
							{
								continue;
							}
						}
						break;
				case 'a':
						ackinc=0;
						System.out.println("Acknoledgement received from Process "+m.getprocid()+" for Message "+m.getmessageid());
						for(int i=0;i<ackList.size();i++)
						{
							if(ackList.get(i)[0]==m.getmessageid())
							{
								temp[0]=ackList.get(i)[0];
								temp[1]=ackList.get(i)[1] +1;
								ackList.set(i,temp);
								ackinc=1;
								break;
							}
						}
						if(ackinc==0)
						{
							ackList.add(new int[]{m.getmessageid(),0});
						}
						break;
				default:
						System.out.println("Message type unknown");
						break;
			}
		}
		catch(RemoteException e){}
	}

	public void checkacklist()
	{
		MessageInt m;
		m=mesQ.peek();
		for(int i=0;i<ackList.size();i++)
		{
			try
			{
				if(ackList.get(i)[0]==m.getmessageid())
				{
					if(ackList.get(i)[1]==totproc)
					{
						System.out.println("Message "+m.getmessageid()+" from Process "+m.getprocid()+" is delivered.");
						mesQ.poll();
					}
				}
			}
			catch(RemoteException e){}
		}
	}

	public void broadcast()
	{
		mesid+=10;
		sclk+=1;
		try
		{
			m = new Message(procid,'m',mesid,sclk);
		}
		catch(RemoteException e){}
		for(int i=0; i<totproc;i++)
		{
			try
			{
				Process p=(Process)r.lookup("Process:"+i);
				p.receive(m);
			}
			catch (RemoteException | NotBoundException e)
			{
				continue;
			}
		}

	}

}