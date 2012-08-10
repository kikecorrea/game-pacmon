package com.csc780.multipacmon;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

public class AutoDiscoverer extends Thread{

	private MulticastSocket clientSocket;
	private InetAddress group;
	private int port;
	private String ipAddress;
	
	public boolean isRunning=true;
	public volatile boolean isFinish=false;
	
	public AutoDiscoverer()
	{      
		try {
			clientSocket = new MulticastSocket(4322);
			group=InetAddress.getByName("230.0.0.1");
			port=4322;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getipAddress()
	{  return ipAddress; } 
	
	public void closeSockset()
	{
		clientSocket.close();
		isRunning = false;
	}
	
	public void run()
	{
	try {
		  clientSocket.joinGroup(group);
	    
	      byte[] sendData = new byte[24]; 
	      byte[] receiveData = new byte[24]; 
	  
	      String temp="password";
	      sendData=temp.getBytes();

		  DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, port); 
		  DatagramPacket receivePacket =  new DatagramPacket(receiveData, receiveData.length); 
		  
		  clientSocket.setSoTimeout(2000);
  
		  boolean isRunning=true;
		  int i=0;
		  while(isRunning)
		  {    	 

		     try {
		       if(i%2==0)
		          clientSocket.send(sendPacket); 
		       i++;
	           clientSocket.receive(receivePacket); 
	           String modifiedSentence = new String(receivePacket.getData()); 
	           
	           modifiedSentence=modifiedSentence.substring(0,receivePacket.getLength());
		           
		       //we don't want to get the same data from client
	           //in a multicast broadcast whatever you send, you will receive
	           //so we don't want to receive what we sent
	           if(modifiedSentence.equals(temp))
	           {  
	        	  //System.out.println("same in client"); 
	           }
	           else
	           {
	        	   if(modifiedSentence.equals("error"))
	        	   {
	        		  // System.out.println("Stil trying to find server");
	        	   }
	        	   else
	        	   {
	        		   InetAddress returnIPAddress = receivePacket.getAddress();
	 		           ipAddress = returnIPAddress.getHostAddress();
	 		           clientSocket.leaveGroup(group);
		       
	 		           try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {		
						e.printStackTrace();
					}
	 		           //this variable is used in MGameSurfaceView in updateSearching
	 		           isFinish=true;
	 		           isRunning=false; 
	 		           clientSocket.close(); 
	        	   }   
	           }
	          }
	        catch (SocketTimeoutException ste)
	        {
	           System.out.println ("Timeout Occurred: Packet assumed lost, AutoDiscovery.java multipacmon");
	           System.out.println("Socket exception occured");
	        } 
	      }
	   
	   }
	   catch (UnknownHostException ex) { 
	     System.err.println(ex);
	    }
	   catch (IOException ex) {
	     System.err.println(ex);
	    }
	}
}
