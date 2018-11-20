package lab1;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import java.net.MalformedURLException;

public class Process extends UnicastRemoteObject implements CommInterface , Runnable {
    // RMI stuff
	private static final long serialVersionUID = 1L;
    
    // PROCESS VARIABLES
	
	// Process ID for each process
    private int processID;
    // Bool to check if ACK received from each process for message m
    boolean ackReceived = false;
    // Message Buffer- FIFO to store ordered list of received messages
    private ArrayList<CommInterface> messageBuffer;
    // Scalar clock maintained by each Process
    private int sCLK = 0;
    
    // PROCESS METHODS
    
    // Update Scalar Clock by 1
    private void clockTick() {
    	sCLK = sCLK + 1;
    }
    
    // Broadcast message with scalar clock sclk, process ID
    public void broadcastMessage(String sCLK,String processID) throws RemoteException{
    	clockTick();
    	// This part broadcasts the message 
    	// Call using RMI each process' receive method and update buffer
    }
    
    // Receive message with scalar clock sclk, process ID
    public void receive(int sclk, int procID ) {
    // This is an RMI Method
    
    	// Store the message in buffer
    	
    	// Order the buffer	   
    	orderBuffer();
    }
    
    // Order received messages in messageBuffer array according to (sCLK,processID)
    private void orderBuffer () {
    	// ORDER BUFFER
    	ArrayList<CommInterface> messageBuffer;
    }
    
    //
    public boolean checkMessageBuffer(CommInterface ci,String name, String pass) throws RemoteException {
    	/*
    	for( String message : this.messageBuffer(ci) ) {
    	    
    	}
    	;
    	return false;
    */
    }
    
    /*OLD STUFF
    private CommInterface server;
    private String ClientName;
    */
    
    protected Process(CommInterface comminterface,String clientname,String password) throws RemoteException {
        //this.server = comminterface;
        //this.ClientName = clientname;
    }
 
    public void run() {
            System.out.println("Successfully Connected To RMI Server");
            System.out.println("NOTE : Type LOGOUT to Exit From The Service");
            System.out.println("Now You are Online To Chat\n");
            String message;
            
            try {
            	server.broadcastMessage(ClientName , message);
            	}
            catch(RemoteException e) {
            	e.printStackTrace();
            }  
    }
    
    public static void main(String[] args) throws MalformedURLException,RemoteException,NotBoundException {
    	System.out.println("\n Process [i] starting ~~\n");  
        
        CommInterface comminterface = (CommInterface)Naming.lookup("//binaryboombox/chat:1099");
        //new Thread(new Process(comminterface , clientName , clientPassword)).start();
    }
 
}