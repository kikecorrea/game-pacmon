package com.csc780.multipacmon;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;



public class ClientDiscoverer extends Thread{

	private MulticastSocket clientSocket;
	private InetAddress group;
	private int port;
	private String ipAddress;
	
	public volatile boolean isFinish=false;
	
	public ClientDiscoverer()
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
	
	public void run()
	{
		try {
			  clientSocket.joinGroup(group);
		    
		      byte[] sendData = new byte[24]; 
		      byte[] receiveData = new byte[24]; 
		  
		      String temp="password";
		      sendData=temp.getBytes();

		      DatagramPacket sendPacket = 
		         new DatagramPacket(sendData, sendData.length, group, port); 
		  
		      
		  
		      DatagramPacket receivePacket = 
		         new DatagramPacket(receiveData, receiveData.length); 
		  
		   //   System.out.println ("Waiting for return packet");
		      clientSocket.setSoTimeout(2000);

		      
		     // clientSocket.send(sendPacket); 
		      
		      boolean isRunning=true;
		      int i=0;
		      while(isRunning)
		      {
		    	 
			     //sendPacket=null;
		        
			     try {
			       if(i%2==0)
			          clientSocket.send(sendPacket); 
			       i++;
		           clientSocket.receive(receivePacket); 
		           String modifiedSentence = new String(receivePacket.getData()); 
		           
		           modifiedSentence=modifiedSentence.substring(0,receivePacket.getLength());
		           
		           //we don't want to get the same data from client
		           if(modifiedSentence.equals(temp))
		           {
		               System.out.println("same in client");
		           }
		           else
		           {
		        	   if(modifiedSentence.equals("error"))
		        	   {
		        		   System.out.println("Stil trying to find server");
		        	   }
		        	   else
		        	   {
		        		   InetAddress returnIPAddress = receivePacket.getAddress();
		      		     
		 		          // int port = receivePacket.getPort();
		 		           ipAddress = returnIPAddress.getHostAddress();
		 		          // System.out.println ("From server at: " + returnIPAddress.getHostAddress() + 
		 		          //                     ":" + port);
		 		          // System.out.println("Message: " + modifiedSentence); 
		 		           
		 		           clientSocket.leaveGroup(group);
		 		          
		 		           
		 		           try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		 		           isFinish=true;
		 		           isRunning=false;
		        	   }
		        
		           }
		   

		          }
		        catch (SocketTimeoutException ste)
		        {
		           System.out.println ("Timeout Occurred: Packet assumed lost");
		        } 
		      }
		     clientSocket.close(); 
		   }
		   catch (UnknownHostException ex) { 
		     System.err.println(ex);
		    }
		   catch (IOException ex) {
		     System.err.println(ex);
		    }
	}
//	public void startDiscovery()
//	{
//		
//		  try {
//			  clientSocket.joinGroup(group);
//		    
//		      byte[] sendData = new byte[24]; 
//		      byte[] receiveData = new byte[24]; 
//		  
//		      String temp="password";
//		      sendData=temp.getBytes();
//
//		      DatagramPacket sendPacket = 
//		         new DatagramPacket(sendData, sendData.length, group, port); 
//		  
//		      
//		  
//		      DatagramPacket receivePacket = 
//		         new DatagramPacket(receiveData, receiveData.length); 
//		  
//		   //   System.out.println ("Waiting for return packet");
//		      clientSocket.setSoTimeout(20000);
//
//		      clientSocket.send(sendPacket); 
//		     // clientSocket.send(sendPacket); 
//		      
//		      boolean isRunning=true;
//		      while(isRunning)
//		      {
//		    	 
//			     //sendPacket=null;
//		        
//			     try {
//		          
//		           clientSocket.receive(receivePacket); 
//		           String modifiedSentence = new String(receivePacket.getData()); 
//		           
//		           modifiedSentence=modifiedSentence.substring(0,receivePacket.getLength());
//		           
//		           //we don't want to get the same data from client
//		           if(modifiedSentence.equals(temp))
//		           {
//		               System.out.println("same in client");
//		           }
//		           else
//		           {
//		        	   if(modifiedSentence.equals("error"))
//		        	   {
//		        		   System.out.println("Stil trying to find server");
//		        	   }
//		        	   else
//		        	   {
//		        		   InetAddress returnIPAddress = receivePacket.getAddress();
//		      		     
//		 		          // int port = receivePacket.getPort();
//		 		           ipAddress = returnIPAddress.getHostAddress();
//		 		          // System.out.println ("From server at: " + returnIPAddress.getHostAddress() + 
//		 		          //                     ":" + port);
//		 		          // System.out.println("Message: " + modifiedSentence); 
//		 		           
//		 		           clientSocket.leaveGroup(group);
//		 		           System.out.println("leaving discoverer");
//		 		           isRunning=false;
//		        	   }
//		        
//		           }
//		   
//
//		          }
//		        catch (SocketTimeoutException ste)
//		        {
//		           System.out.println ("Timeout Occurred: Packet assumed lost");
//		        } 
//		      }
//		     clientSocket.close(); 
//		   }
//		   catch (UnknownHostException ex) { 
//		     System.err.println(ex);
//		    }
//		   catch (IOException ex) {
//		     System.err.println(ex);
//		    }
//	}
}
