/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csc780.clientmultiserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *This class handles the sending data to client
 */
public class ServerSending extends Thread {
     
    private DatagramSocket serverSocket;
    private byte[] sendData;
    private InetAddress IPAddress;
    private int port;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private String data;
    public volatile boolean propertySet=false;

    private CMGameEngine gameEngine;
    private int tickCounter=0;
    private int portServer;
    private boolean countDown;
    public AtomicBoolean isRunning;
    public int status=0,p1x,p1y,p1z,p2x,p2y,p2z,g1x,g1y,g1z,g2x,g2y,g2z,g3x,g3y,g3z,g4x,g4y,g4z,p1life,p2life,p1score,p2score,mazeX,mazeY;
    public String temp;
    
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
     DataOutputStream daos=new DataOutputStream(baos);

    //we need this blank constructor so that we can instantiate it in serverThread. its also a fix for killAllThread in CMGameActivity
    public ServerSending()
    {
    	isRunning=new AtomicBoolean(true);
    	
    	try {
			serverSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
    
    //Constructor
    //port is port of client
    public ServerSending(int port, InetAddress address, CMGameEngine ges)
    {
    	isRunning=new AtomicBoolean(true);
    	
       this.countDown=true;
       this.gameEngine=ges;
       sendData  = new byte[128]; 
       this.port=port;
       this.IPAddress=address;
       
            try {
				serverSocket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
            
       
    }
    
    public void DestroySocket()
    {
    	serverSocket.close();
    }
        
    public void getData()
    {
      baos=new ByteArrayOutputStream();
      daos=new DataOutputStream(baos);
      //waits for for the next tick to occur
      while(tickCounter==gameEngine.tickCounter && isRunning.get()==true)
      {        }

      tickCounter=gameEngine.tickCounter;
      
      
      p1x=gameEngine.pacmon.getpX();
      p1y=gameEngine.pacmon.getpY();
      p1z=gameEngine.pacmon.getDir();
      
      p2x=gameEngine.pacmon2.getpX();
      p2y=gameEngine.pacmon2.getpY();
      p2z=gameEngine.pacmon2.getDir();
      
      g1x=gameEngine.ghosts.get(0).getX();
      g1y=gameEngine.ghosts.get(0).getY();
      g1z=gameEngine.ghosts.get(0).getDir();
      
      g2x=gameEngine.ghosts.get(1).getX();
      g2y=gameEngine.ghosts.get(1).getY();
      g2z=gameEngine.ghosts.get(1).getDir();
      
      g3x=gameEngine.ghosts.get(2).getX();
      g3y=gameEngine.ghosts.get(2).getY();
      g3z=gameEngine.ghosts.get(2).getDir();
      
      g4x=gameEngine.ghosts.get(3).getX();
      g4y=gameEngine.ghosts.get(3).getY();
      g4z=gameEngine.ghosts.get(3).getDir();
      
      p1life=gameEngine.lives;
      p2life=gameEngine.lives2;
      p1score=gameEngine.playerScore;
      p2score=gameEngine.playerScore2;
      
      mazeX=gameEngine.mazeData1;
      mazeY=gameEngine.mazeData2;
      status = gameEngine.gameState;
      
//      temp=tickCounter + ":" + p1x + ":" + p1y + ":" + p1z + ":" 
//              + p2x + ":" + p2y + ":" + p2z + ":" 
//              + g1x + ":" + g1y + ":" + g1z + ":" 
//              + g2x + ":" + g2y + ":" + g2z + ":" 
//              + g3x + ":" + g3y + ":" + g3z + ":" 
//              + p1life + ":" + p2life + ":"
//              + p1score + ":" + p2score + ":" + gameEngine.timer + ":"
//              + mazeX + ":" + mazeY + ":" + status;   
      
      try {
		daos.writeInt(tickCounter);
		daos.writeInt(p1x);daos.writeInt(p1y);daos.writeInt(p1z);
		daos.writeInt(p2x);daos.writeInt(p2y);daos.writeInt(p2z);
		
		daos.writeInt(g1x);daos.writeInt(g1y);daos.writeInt(g1z);
		daos.writeInt(g2x);daos.writeInt(g2y);daos.writeInt(g2z);
		daos.writeInt(g3x);daos.writeInt(g3y);daos.writeInt(g3z);
		
		daos.writeInt(p1life);daos.writeInt(p2life);
		daos.writeInt(p1score);daos.writeInt(p2score);daos.writeInt(gameEngine.timer);
		daos.writeInt(mazeX);daos.writeInt(mazeY);daos.writeInt(status);
		daos.writeInt(g4x);daos.writeInt(g4y);daos.writeInt(g4z);
		
		daos.close();
		
		
		
		
	} catch (IOException e) {
		e.printStackTrace();
	}
      
    //  return temp;       
    }
 
    @Override
    public void run()
    {    
       //sendData  = new byte[128];  
       while(isRunning.get())
       {          
    	   try {
			Thread.sleep(20);
    	   } catch (InterruptedException e1) {
			e1.printStackTrace();
    	   }
    	   try {
               // String temp;
               //temp=this.getData();
      
               // sendData=temp.getBytes();
    		   this.getData();
    		   sendData=this.baos.toByteArray();
//    		   ByteArrayInputStream bais=new ByteArrayInputStream(sendData);
//               DataInputStream dais=new DataInputStream(bais);
//               System.out.println("XX::"+ dais.readInt());
//               System.out.println("YY::"+ dais.readInt());
//               System.out.println("ZZ::"+ dais.readInt());
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 
               
			    serverSocket.send(sendPacket);
				} catch (IOException e) {
					//e.printStackTrace();
				}

       }
       
       System.out.println("OUT IN SERVER SENDING");
    }
    
}
