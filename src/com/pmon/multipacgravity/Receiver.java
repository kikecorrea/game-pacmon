package com.pmon.multipacgravity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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

/**
 * This class handles the receiving data from server
 */
public class Receiver extends Thread {

	private final String serverHostname;
	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private InetAddress IPAddress;
	private byte[] receiveData;
	
	public CircularQue pac1que=new CircularQue(2);
//	public CircularQue pac2que=new CircularQue(2);
//	public CircularQue ghost1Que=new CircularQue(2);
//	public CircularQue ghost2Que=new CircularQue(2);
//	public CircularQue ghost3Que=new CircularQue(2);
//	public CircularQue ghost4Que=new CircularQue(2);
	
//	public CircularQue2 mazeData1=new CircularQue2(4);
//	public CircularQue2 mazeData2=new CircularQue2(4);
	
	//store the lives of pacmon
//	public volatile int pacmonLives[]=new int [2];
	public volatile int p1life, p2life;
	volatile int countDown=120;
	
	//scores
	public volatile int p1score;
	public volatile int p2score;
	
	//timer
	public volatile int timer=180;
	public boolean isRunning=true;
	protected AtomicBoolean ready;
	private String previousTick="0";
	private int id;
	private int socketPort;
	//volatile int mazeData1, mazeData2;
	volatile int status;
	private volatile int counter;
	
	ByteArrayInputStream bais;
    DataInputStream dais;
    
    public volatile int p1x,p1y,p1z,p2x,p2y,p2z,g1x,g1y,g1z,g2x,g2y,g2z,g3x,g3y,g3z,g4x,g4y,g4z,mazeX,mazeY;
    public volatile int [] row= new int[20];
    public volatile int p2eatcherry=0;
    private int temp;
    
	public Receiver()
	{
		
		dais = new DataInputStream(bais);
		
		
		ready=new AtomicBoolean(true);
		serverHostname = new String ("192.168.0.199");
		receiveData = new byte[118]; 
		
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
	
	public void closeSocket()
	{
		this.isRunning=false;
		this.clientSocket.close();
		receivePacket=null;
		bais=null;
		dais=null;
		receiveData=null;
	}
	
	public void addQue(DataInputStream dais, int length)
	{
		try {
//		counter=dais.readInt();
//		  if(counter<=0)
//		  {
//			this.countDown--;
//		  }
//		  else 
		  {
			  temp = dais.readInt();
			  p1z=temp/1000000;
			  p1y=temp%1000;
			  p1x=(temp/1000) % 1000;
			  
			  
			  temp = dais.readInt();
			  p2z=temp/1000000;
			  p2y=temp%1000;
			  p2x=(temp/1000) % 1000;
			  
			  temp = dais.readInt();
			  g1z=temp/1000000;
			  g1y=temp%1000;
			  g1x=(temp/1000) % 1000;

			  temp = dais.readInt();
			  g2z=temp/1000000;
			  g2y=temp%1000;
			  g2x=(temp/1000) % 1000;
			  
			  temp = dais.readInt();
			  g3z=temp/1000000;
			  g3y=temp%1000;
			  g3x=(temp/1000) % 1000;
			  
			  temp = dais.readInt();
			  g4z=temp/1000000;
			  g4y=temp%1000;
			  g4x=(temp/1000) % 1000;

			  
			  temp = dais.readInt();
			  
			  p1life = temp / 10000000;
			  p2life = (temp / 1000000) % 10;
			  p2score = temp % 1000;
			  p1score =(temp / 1000) % 1000;
			  
			  temp = dais.readInt();
			  this.status = temp / 1000;
			 
			  this.timer = temp %1000;
			  
			  pac1que.write(p1x, p1y, p1z,p2x, p2y, p2z,g1x, g1y, g1z,g2x, g2y, g2z,g3x, g3y, g3z,g4x, g4y, g4z);
//			  pac2que.write(p2x, p2y, p2z);
//			  ghost1Que.write(g1x, g1y, g1z);
//			  ghost2Que.write(g2x, g2y, g2z);
//			  ghost3Que.write(g3x, g3y, g3z);
//			  ghost4Que.write(g4x, g4y, g4z);

			  //for maze data
				for(int i=0; i<20; i++)
					row[i]=dais.readInt();
				
				this.p2eatcherry=dais.readInt();
				
				
				//previousTick=String.valueOf(x);
				dais.close();
		
		  }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	
	@Override
	public void run()
	{
		while(isRunning)
		{
			try {
				receivePacket = new DatagramPacket(receiveData, receiveData.length);    
			//	clientSocket.setSoTimeout(10000);
			
				try {
			          clientSocket.receive(receivePacket);
			          
			          bais=new ByteArrayInputStream(receivePacket.getData());
			          //String receiveData =   new String(receivePacket.getData()); 
			          dais=new DataInputStream(bais);
		        	  this.addQue(dais, receivePacket.getLength());
	
			         }
			      catch (SocketTimeoutException ste){
			           //System.out.println ("Timeout Occurred: Packet assumed lost");
			      }
			} catch (IOException e) 
			{	e.printStackTrace();  }
		}
	}
}

