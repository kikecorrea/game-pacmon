/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csc780.clientmultiserver;

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
        
    public String getData()
    {
      //waits for for the next tick to occur
      while(tickCounter==gameEngine.tickCounter)
      {        }

      int x=gameEngine.tickCounter;
      
      //need to fix synchronization problem what if gameEngine.tickCounter change after getting pX, pY
      int p1xy[]=gameEngine.returnPacmonPxPy();
      int p2xy[]=gameEngine.returnPacmon2PxPy();
      
      int g1xy[]=gameEngine.returnGhost(0);
      int g2xy[]=gameEngine.returnGhost(1);
      int g3xy[]=gameEngine.returnGhost(2);
      int pLives[]=gameEngine.returnPacLives();
      int scores[]=gameEngine.returnScores();
      int maze[]=gameEngine.returnMazeData();
      String timer=gameEngine.getTimer();
      
      tickCounter=x;   
      String temp;       
          temp=x + ":" + p1xy[0] + ":" + p1xy[1] + ":" + p1xy[2] + ":" 
                       + p2xy[0] + ":" + p2xy[1] + ":" + p2xy[2] + ":" 
                       + g1xy[0] + ":" + g1xy[1] + ":" + g1xy[2] + ":" 
                       + g2xy[0] + ":" + g2xy[1] + ":" + g2xy[2] + ":" 
                       + g3xy[0] + ":" + g3xy[1] + ":" + g3xy[2] + ":" 
                       + pLives[0] + ":" + pLives[1] + ":"
                       + scores[0] + ":" + scores[1] + ":" + timer + ":"
                       + maze[0] + ":" + maze[1];          

      return temp;       
    }
 
    @Override
    public void run()
    {    
       sendData  = new byte[128];  
       while(isRunning.get())
       {          
    	   try {
			Thread.sleep(30);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	   try {
                String temp;
               temp=this.getData();
      
                sendData=temp.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 
             
			    serverSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
       }
    }
    
}
