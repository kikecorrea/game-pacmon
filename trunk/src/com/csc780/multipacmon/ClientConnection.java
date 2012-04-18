package com.csc780.multipacmon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection extends Thread {

	private String serverHostname;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private InetAddress IPAddress;
	private byte[] sendData;
	private byte[] receiveData;
	
	private String dataSent="3";
	private String dataReceive="";
	private volatile LinkedList<String> dataReceiveL=new LinkedList<String>();
	
	protected AtomicBoolean ready;
	
	public volatile boolean clientGo=false;
	
	//flag for dataReceive
	private boolean FgetDataReceive=true, FsetDataReceive=false;
	
	public ClientConnection()
	{
		ready=new AtomicBoolean(true);
		serverHostname = new String ("192.168.0.199");
		sendData = new byte[64]; 
		receiveData = new byte[64]; 
		
		try {
			clientSocket = new DatagramSocket(6000);
			IPAddress = InetAddress.getByName(serverHostname);

		} catch (SocketException e) {
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	//getter and setter for linkedlist
	public String getLinkedList()
	{
		String temp=dataReceiveL.remove();
	
		System.out.println("REMOVE FROM LINEKD LIST::"+temp);
		return temp;
	}
	public void addLinkedList(String data, int length)
	{
	String x=data.substring(0,length);
	//System.out.println("Remember::" + x);
		try{
			Scanner sc=new Scanner(x).useDelimiter("#");
			while(sc.hasNext())
			{
			String temp=sc.next();
			
			dataReceiveL.add(temp);
			
			}
			}catch(InputMismatchException e)
			{
			//	System.out.println("Error from drawPacman");	
			}
		
	}
	public void GhostaddLinkedList(String data, int length)
	{
		dataReceiveL.add(data.substring(0, 1));
		System.out.println("Size::"+ dataReceiveL.size());
	}
	
	
	
	//Getter and setter for dataReceive
	public synchronized String getDataReceive()
	{	
		return dataReceive;
	}
	public synchronized void setDataReceive(String data, int length)
	{
		dataReceive=data.substring(0, length);
	}
	
	//Getter and setter for dataSent
	public synchronized void setDataSent(String data)
	{
		dataSent=data;
	}
	public synchronized String getDataSent()
	{  return dataSent; }
	
	public int[] getPacPxPy()
	{
		int temp[] =new int[3];
		
		System.out.println("SIZE ::" + dataReceiveL.size());
		String tempData=this.getLinkedList();
		
		try{
			Scanner sc=new Scanner(tempData).useDelimiter(":");
			//for id
			temp[0]=sc.nextInt();
			temp[1]=sc.nextInt();
			temp[2]=sc.nextInt();
			}catch(InputMismatchException e)
			{
				System.out.println("No value");	
			}
		return temp;
	}
	
	
	
	
	@Override
	public void run()
	{
	
	 boolean go=false;
	 long before=0L, after=0L;
	 
	
	 while(true)
	 {
		//synchronized(this)
		{
			if(this.ready.get())
			{  go=true; ready.set(false); }
		}
		if(go)
		{
			try {
				 synchronized(this)
					{
					sendData=this.getDataSent().getBytes();
					}
					
				 	//before=System.currentTimeMillis();
					//sending data to server
					sendPacket = 
					         new DatagramPacket(sendData, sendData.length, IPAddress, 9876); 
					
						clientSocket.send(sendPacket);
				
			
				receivePacket = 
				         new DatagramPacket(receiveData, receiveData.length); 
				  
				clientSocket.setSoTimeout(10000);
				try {
			           clientSocket.receive(receivePacket); 
			           
			           //System.out.println("baklbaklbakl::"+ (System.currentTimeMillis()-before));
			    
			           //notify gameEnginetoRun
			           clientGo=true;
			           String modifiedSentence = 
			               new String(receivePacket.getData()); 
			     	
			           System.out.println("RECEIVE FROM SERVER:"+ modifiedSentence);
			        	   //setDataReceive(modifiedSentence, receivePacket.getLength());	
			           		//this.addLinkedList(modifiedSentence, receivePacket.getLength());
			           this.GhostaddLinkedList(modifiedSentence, receivePacket.getLength());
	
			         }
			      catch (SocketTimeoutException ste){
			           System.out.println ("Timeout Occurred: Packet assumed lost");
			      }
				
			} catch (IOException e) 
			{	e.printStackTrace();  }
		go=false;
	   }
	 }
	}
	
}
