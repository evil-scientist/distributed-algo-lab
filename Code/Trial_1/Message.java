package Trial_1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Message extends UnicastRemoteObject implements MessageInt
{
	char mestype;
	int timestamp;
	int mes_id;
	int orig_proc;

	public Message() throws RemoteException {}

	public Message(int orig_proc, char mestype, int mes_id, int timestamp) throws RemoteException
	{
		this.orig_proc=orig_proc;
		this.mestype=mestype;
		this.mes_id=mes_id;
		this.timestamp=timestamp;
	}

	public Message(int orig_proc, char mestype, int mes_id) throws RemoteException
	{
		this.orig_proc=orig_proc;
		this.mestype=mestype;
		this.mes_id=mes_id;
		this.timestamp=-1;
	}

	public int gettimestamp()
	{ 
		return timestamp;
	}

	public int getmessageid()
	{
		return mes_id;
	}

	public char getmessagetype()
	{
		return mestype;
	}

	public int getprocid()
	{
		return orig_proc;
	}

	public void timestamp(int sclk)
	{
		this.timestamp=sclk;
	}
}