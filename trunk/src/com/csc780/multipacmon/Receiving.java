package com.csc780.multipacmon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;


import android.util.Log;

public class Receiving extends Thread {

	private final String serverHostname;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private InetAddress IPAddress;
	private byte[] receiveData;
	
	private CircularQue pac1que=new CircularQue(4);
	private CircularQue pac2que2=new CircularQue(4);
	private CircularQue ghost1Que=new CircularQue(4);
	private CircularQue ghost2Que=new CircularQue(4);
	private CircularQue ghost3Que=new CircularQue(4);
	
	//store the lives of pacmon
	public volatile int pacmonLives[]=new int [2];
	volatile int countDown=120;
	
	//scores
	public volatile int pacmonScores[]=new int [2];
	
	//timer
	public volatile int timer;
	
	public boolean isRunning=true;
	
	
	protected AtomicBoolean ready;
	
	private String previousTick="0";
	
	private int id;
	
	private int socketPort;
	
	volatile int mazeData1, mazeData2;

	
	public Receiving()
	{
		pacmonLives[0]=3;
		pacmonLives[1]=3;
		
		ready=new AtomicBoolean(true);
		serverHostname = new String ("192.168.0.199");
		receiveData = new byte[128]; 
		
		try {
			
			socketPort=5000+(int)(Math.random()*(6000-5000));
			clientSocket = new DatagramSocket(socketPort);
			
			
			IPAddress = InetAddress.getByName(serverHostname);

		} catch (SocketException e) {
			e.printStackTrace();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	
	//get port number to be sent to server 
	public int getPortReceive()
	{
		return socketPort;
	}
	
	//sets player id
	//need to remove this method, didn't use it, forgot how to use it
	public void setID(int id)
	{
		this.id=id;
	}
	
	//for circular Que
	public void addQue(String data, int length)
	{
		String temp[]=data.substring(0, length).split(":", 23); 
		
		int x=Integer.valueOf(temp[0]);
		if(x<=0)
		{
			this.countDown--;
		}
		else if(!temp[0].equals(previousTick))
		{
			pac1que.write(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
			pac2que2.write(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]),Integer.parseInt(temp[6]));
			
			ghost1Que.write(Integer.parseInt(temp[7]), Integer.parseInt(temp[8]),Integer.parseInt(temp[9]));
			ghost2Que.write(Integer.parseInt(temp[10]), Integer.parseInt(temp[11]),Integer.parseInt(temp[12]));
			ghost3Que.write(Integer.parseInt(temp[13]), Integer.parseInt(temp[14]),Integer.parseInt(temp[15]));
			
			pacmonLives[0]=Integer.parseInt(temp[16]);
			pacmonLives[1]=Integer.parseInt(temp[17]);
			
			pacmonScores[0]=Integer.parseInt(temp[18]);
			pacmonScores[1]=Integer.parseInt(temp[19]);
			
			this.timer=Integer.parseInt(temp[20]);
			this.mazeData1=Integer.parseInt(temp[21]);
			this.mazeData2=Integer.parseInt(temp[22]);
	
			previousTick=temp[0];
		}
	}
	
	public int [] deQueP1()
	{
		int p1[]=pac1que.read();
		return p1;
	}
	public int [] deQueP2()
	{
		int p2[]=pac2que2.read();
		return p2;
	}
	//deQue GHOST
	public int [] deQueG1()
	{
		int g1[]=ghost1Que.read();
		return g1;
	}
	public int [] deQueG2()
	{
		int g2[]=ghost2Que.read();
		return g2;
	}
	public int [] deQueG3()
	{
		int g3[]=ghost3Que.read();
		return g3;
	}
	
	@Override
	public void run()
	{
		//receivePacket = 
		  //       new DatagramPacket(receiveData, receiveData.length); 
		
		while(isRunning)
		{
		//	receiveData = new byte[64]; 
			try {
				receivePacket = 
				         new DatagramPacket(receiveData, receiveData.length);  
				  
				clientSocket.setSoTimeout(10000);
				try {
			          clientSocket.receive(receivePacket); 
			       
			           String receiveData =   new String(receivePacket.getData()); 
			          
			         // System.out.println("RECEIVE:" + receiveData);
			
		        	  this.addQue(receiveData, receivePacket.getLength());
			  
			         }
			      catch (SocketTimeoutException ste){
			           //System.out.println ("Timeout Occurred: Packet assumed lost");
			      }
				
			} catch (IOException e) 
			{	e.printStackTrace();  }
		}
	}
}

