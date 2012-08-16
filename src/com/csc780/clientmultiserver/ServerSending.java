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
import java.net.UnknownHostException;
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
//    public int status=0,p1life,p2life,p1score,p2score,mazeX,mazeY;
    public volatile int pac1,pac2,ghost1,ghost2,ghost3,ghost4, p1p2lifescore, statustimer;
    public volatile int row1,row2,row3,row4,row5,row6,row7,row8,row9,row10,row11,row12,row13,row14,row15,row16,row17,row18,row19,row20;
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
    public ServerSending(int port, String address, CMGameEngine ges)
    {
    	isRunning=new AtomicBoolean(true);
    	
       this.countDown=true;
       this.gameEngine=ges;
       sendData  = new byte[118]; 
       this.port=port;
       
       try {
		this.IPAddress=InetAddress.getByName(address);
       } catch (UnknownHostException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
       }
       
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
        
    public synchronized void getData()
    {
      baos=new ByteArrayOutputStream();
      daos=new DataOutputStream(baos);
      //waits for for the next tick to occur
      while(tickCounter==gameEngine.tickCounter && isRunning.get()==true)
      {        }

      tickCounter=gameEngine.tickCounter;
      
      pac1=(gameEngine.pacmon.getDir() * 1000000) + (gameEngine.pacmon.getpX() * 1000) + (gameEngine.pacmon.getpY());
      pac2=(gameEngine.pacmon2.getDir() * 1000000) + (gameEngine.pacmon2.getpX() * 1000) + (gameEngine.pacmon2.getpY());
      ghost1=(gameEngine.ghosts.get(0).getDir()* 1000000) + (gameEngine.ghosts.get(0).getX() * 1000) + (gameEngine.ghosts.get(0).getY());
      ghost2=(gameEngine.ghosts.get(1).getDir()* 1000000) + (gameEngine.ghosts.get(1).getX() * 1000) + (gameEngine.ghosts.get(1).getY());
      ghost3=(gameEngine.ghosts.get(2).getDir()* 1000000) + (gameEngine.ghosts.get(2).getX() * 1000) + (gameEngine.ghosts.get(2).getY());
      ghost4=(gameEngine.ghosts.get(3).getDir()* 1000000) + (gameEngine.ghosts.get(3).getX() * 1000) + (gameEngine.ghosts.get(3).getY());
      

      p1p2lifescore = (gameEngine.lives * 10000000) + (gameEngine.lives2 * 1000000) + (gameEngine.playerScore * 1000) + (gameEngine.playerScore2);
      statustimer = (gameEngine.gameState * 1000) + gameEngine.timer;

      row1=gameEngine.mazeDataCompressor.rowMap.get(1);
      row2=gameEngine.mazeDataCompressor.rowMap.get(2);
      row3=gameEngine.mazeDataCompressor.rowMap.get(3);
      row4=gameEngine.mazeDataCompressor.rowMap.get(4);
      row5=gameEngine.mazeDataCompressor.rowMap.get(5);
      row6=gameEngine.mazeDataCompressor.rowMap.get(6);
      row7=gameEngine.mazeDataCompressor.rowMap.get(7);
      row8=gameEngine.mazeDataCompressor.rowMap.get(8);
      row9=gameEngine.mazeDataCompressor.rowMap.get(9);
      row10=gameEngine.mazeDataCompressor.rowMap.get(10);
      row11=gameEngine.mazeDataCompressor.rowMap.get(11);
      row12=gameEngine.mazeDataCompressor.rowMap.get(12);
      row13=gameEngine.mazeDataCompressor.rowMap.get(13);
      row14=gameEngine.mazeDataCompressor.rowMap.get(14);
      row15=gameEngine.mazeDataCompressor.rowMap.get(15);
      row16=gameEngine.mazeDataCompressor.rowMap.get(16);
      row17=gameEngine.mazeDataCompressor.rowMap.get(17);
      row18=gameEngine.mazeDataCompressor.rowMap.get(18);
      row19=gameEngine.mazeDataCompressor.rowMap.get(19);
      row20=gameEngine.mazeDataCompressor.rowMap.get(20);

      
      try {
		//daos.writeInt(tickCounter);
		
		daos.writeInt(pac1);
		daos.writeInt(pac2);
		daos.writeInt(ghost1);
		daos.writeInt(ghost2);
		daos.writeInt(ghost3);
		daos.writeInt(ghost4);
		
		daos.writeInt(p1p2lifescore);
		daos.writeInt(statustimer);
		
		daos.writeInt(row1);daos.writeInt(row2);daos.writeInt(row3);daos.writeInt(row4);daos.writeInt(row5);
		daos.writeInt(row6);daos.writeInt(row7);daos.writeInt(row8);daos.writeInt(row9);daos.writeInt(row10);
		daos.writeInt(row11);daos.writeInt(row12);daos.writeInt(row13);daos.writeInt(row14);daos.writeInt(row15);
		daos.writeInt(row16);daos.writeInt(row17);daos.writeInt(row18);daos.writeInt(row19);daos.writeInt(row20);
		
		//signal for p2 for eating cherry
		daos.writeInt(gameEngine.p2eatcherry);
		gameEngine.p2eatcherry=0;
		
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
