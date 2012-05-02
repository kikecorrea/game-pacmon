package com.csc780.multipacmon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class ClientDispatcher {

	
	private String serverHostname;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private InetAddress IPAddress;
	private byte[] sendData;
	private byte[] receiveData;
	
	
	//player id either player1 or player2
	public int id;
	//sets the sending port
	public int sendPort;
	
	public ClientDispatcher(String ip)
	{

		serverHostname = new String (ip);
		sendData = new byte[24]; 
		receiveData = new byte[24];
		
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
	
	public void parseData(String data, int length)
	{
		String temp=data.substring(0, length);
		String xy[]=temp.split(":", 2);
		
		this.id=Integer.parseInt(xy[0]);
		this.sendPort=Integer.parseInt(xy[1]);
		
	}
	
	
	public void connectToServer(int portForReceiving)
	{
		boolean keepRunning=true;

		try {
		
			clientSocket.setSoTimeout(2000);
			
		while(keepRunning)
		 {
			try{
			sendData=String.valueOf(portForReceiving).getBytes();
			sendPacket =  new DatagramPacket(sendData, sendData.length, IPAddress, 9800); 
		  clientSocket.send(sendPacket);

		  
		  System.out.println("sent dispatcher");
		  receivePacket = 
			         new DatagramPacket(receiveData, receiveData.length); 
		  
		  clientSocket.receive(receivePacket);
		  
		  keepRunning=false;
		  System.out.println("Executed keepRunning");
		  String temp=new String(receivePacket.getData());
		  System.out.println("This is data in dispatchet::"+ temp);
		  this.parseData(temp, receivePacket.getLength());
			}catch (SocketTimeoutException ste){
		           System.out.println ("Timeout Occurred: Packet assumed lost");
		    } 
		 }
		}
		catch (IOException e) {
			System.out.println("Error in sending socket::");
			e.printStackTrace();
		}

	}
}
