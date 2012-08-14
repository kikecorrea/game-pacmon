package com.csc780.multipacmon;

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
	
	public CircularQue pac1que=new CircularQue(6);
	public CircularQue pac2que2=new CircularQue(6);
	public CircularQue ghost1Que=new CircularQue(6);
	public CircularQue ghost2Que=new CircularQue(6);
	public CircularQue ghost3Que=new CircularQue(6);
	public CircularQue ghost4Que=new CircularQue(6);
	
	public CircularQue2 mazeData1=new CircularQue2(6);
	public CircularQue2 mazeData2=new CircularQue2(6);
	
	//store the lives of pacmon
//	public volatile int pacmonLives[]=new int [2];
	public volatile int p1life, p2life;
	volatile int countDown=120;
	
	//scores
	public volatile int p1score;
	public volatile int p2score;
	
	//timer
	public volatile int timer;
	public boolean isRunning=true;
	protected AtomicBoolean ready;
	private String previousTick="0";
	private int id;
	private int socketPort;
	//volatile int mazeData1, mazeData2;
	volatile int status;
	
	ByteArrayInputStream bais;
    DataInputStream dais;
    public int p1x,p1y,p1z,p2x,p2y,p2z,g1x,g1y,g1z,g2x,g2y,g2z,g3x,g3y,g3z,g4x,g4y,g4z,mazeX,mazeY;
    
	public Receiver()
	{
		dais = new DataInputStream(bais);
		
		p1life=3;
		p2life=3;
		
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
	
	public void closeSocket()
	{
		this.isRunning=false;
		this.clientSocket.close();
	}
	
	//add data in circularQue
//	public void addQue(String data, int length)
//	{
//		String temp[]=data.substring(0, length).split(":", 24); 
//		
//		int x=Integer.valueOf(temp[0]);
//		
//		if(x<=0)
//		{
//			this.countDown--;
//		}
//		else if(!temp[0].equals(previousTick))
//		{
//			
//			pac1que.write(Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
//			pac2que2.write(Integer.parseInt(temp[4]), Integer.parseInt(temp[5]),Integer.parseInt(temp[6]));
//			
//			ghost1Que.write(Integer.parseInt(temp[7]), Integer.parseInt(temp[8]),Integer.parseInt(temp[9]));
//			ghost2Que.write(Integer.parseInt(temp[10]), Integer.parseInt(temp[11]),Integer.parseInt(temp[12]));
//			ghost3Que.write(Integer.parseInt(temp[13]), Integer.parseInt(temp[14]),Integer.parseInt(temp[15]));
//			
//			p1life=Integer.parseInt(temp[16]);
//			p2life=Integer.parseInt(temp[17]);
//			
////			pacmonScores[0]=Integer.parseInt(temp[18]);
////			pacmonScores[1]=Integer.parseInt(temp[19]);
//			p1score=Integer.parseInt(temp[18]);
//			p2score=Integer.parseInt(temp[19]);
//			
//			
//			this.timer=Integer.parseInt(temp[20]);
//			this.mazeData1=Integer.parseInt(temp[21]);
//			this.mazeData2=Integer.parseInt(temp[22]);
//			this.status=Integer.parseInt(temp[23]);
//			previousTick=temp[0];
//		}
//	}
	
	public void addQue(DataInputStream dais, int length)
	{
		try {
		int x=dais.readInt();
		  if(x<=0)
		  {
			this.countDown--;
		  }
		  else 
		  {
			  //public int status=0,p1x,p1y,p1z,p2x,p2y,p2z,g1x,g1y,g1z,g2x,g2y,g2z,g3x,g3y,g3z,p1life,p2life,p1score,p2score,mazeX,mazeY;
			  p1x=dais.readInt();p1y=dais.readInt();  p1z=dais.readInt();
			  p2x=dais.readInt();  p2y=dais.readInt();p2z=dais.readInt();
			  g1x=dais.readInt();g1y=dais.readInt();  g1z=dais.readInt();
			  g2x=dais.readInt();g2y=dais.readInt();  g2z=dais.readInt();
			  g3x=dais.readInt();g3y=dais.readInt();  g3z=dais.readInt();
			  p1life=dais.readInt();p2life=dais.readInt(); 
			  p1score=dais.readInt(); p2score=dais.readInt();
			  
				pac1que.write(p1x,p1y,p1z);
				pac2que2.write(p2x,p2y,p2z);	
				ghost1Que.write(g1x, g1y, g1z);
				ghost2Que.write(g2x, g2y, g2z);
				ghost3Que.write(g3x, g3y, g3z);
				
				p1life=p1life;
				p2life=p2life;
				
//				pacmonScores[0]=Integer.parseInt(temp[18]);
//				pacmonScores[1]=Integer.parseInt(temp[19]);
				p1score=p1score;
				p2score=p2score;
				
				
				this.timer=dais.readInt();
				this.mazeData1.write(dais.readInt());
				this.mazeData2.write(dais.readInt());
				this.status=dais.readInt();
				
				//for ghost number 4
				g4x=dais.readInt();
				g4y=dais.readInt();
				g4z=dais.readInt();
				ghost4Que.write(g4x, g4y, g4z);
				
				previousTick=String.valueOf(x);
				dais.close();
		
		  }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
//	public int [] deQueP1()
//	{
//		int p1[]=pac1que.read();
//		return p1;
//	}
//	public int [] deQueP2()
//	{
//		int p2[]=pac2que2.read();
//		return p2;
//	}
//	//deQue GHOST
//	public int [] deQueG1()
//	{
//		int g1[]=ghost1Que.read();
//		return g1;
//	}
//	public int [] deQueG2()
//	{
//		int g2[]=ghost2Que.read();
//		return g2;
//	}
//	public int [] deQueG3()
//	{
//		int g3[]=ghost3Que.read();
//		return g3;
//	}
	
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

