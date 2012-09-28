package com.pmon.multipacmon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class handles the sending data to server
 */
public class Sender extends Thread {
	
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
	
	public Sender()
	{
		ready=new AtomicBoolean(true);
	}

	public Sender(int port, String ip)
	{
		//set the sending port;
		this.port=port;
		ready=new AtomicBoolean(true);
		serverHostname = new String (ip);
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
	
	public void notifyTheThread()
	{
		this.notifyAll();
	}
	
	public void closeSocket()
	{
		this.isRunning=false;
		clientSocket.close();
		sendData=null;
		sendPacket=null;
	}
	
	@Override
	public void run()
	{
		boolean go=false;
		String temp;
		while(isRunning)
		 {

			try {
				this.sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		    if(this.ready.get())
			{   go=true; ready.set(false); }
			
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
//				System.out.println("Error in sending socket:: Sender.java, multipacmon");
				e.printStackTrace();
			}
		
			go=false;
		   }
		 }
		
	}
}
