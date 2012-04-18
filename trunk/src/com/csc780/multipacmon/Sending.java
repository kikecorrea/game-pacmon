package com.csc780.multipacmon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sending extends Thread {
	
	private String serverHostname;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private InetAddress IPAddress;
	private int port;
	private byte[] sendData;
	
	protected AtomicBoolean ready;
	
	protected boolean isRunning=true;
	
	
	public volatile String data="1";

	
	public Sending(int port)
	{
		//set the sending port;
		this.port=port;
		
		ready=new AtomicBoolean(true);
		serverHostname = new String ("192.168.0.195");
		sendData = new byte[24]; 
		
		try {
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName(serverHostname);

		} catch (SocketException e) {
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void run()
	{
		boolean go=false;
		//sendPacket =  new DatagramPacket(sendData, sendData.length, IPAddress, port); 
		//Runtime rc=Runtime.getRuntime();
		 String temp;
		while(isRunning)
		 {
		
		//	sendData = new byte[12]; 
			//synchronized(this)
		    if(this.ready.get())
				{   
				go=true; ready.set(false); 
				}
			
			if(go)
			{
			  synchronized(this)
			  {
				  temp=this.data;
				sendData=temp.getBytes();
	     	  }

		      sendPacket =  new DatagramPacket(sendData, sendData.length, IPAddress, port); 
		      try {
				clientSocket.send(sendPacket);
			} catch (IOException e) {
				System.out.println("Error in sending socket::");
				e.printStackTrace();
			}
		
			go=false;
			//rc.gc();
		   }
		 }
		
	}
}
