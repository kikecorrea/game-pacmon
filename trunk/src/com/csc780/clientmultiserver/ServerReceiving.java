
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
 *This class handles the receiving data from client
 */
public class ServerReceiving extends Thread {
    private DatagramSocket serverSocket;
    private byte[] receiveData;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private CMGameEngine gameEngine;
    public volatile boolean ready=false;
    private volatile InetAddress player1Address;
    public AtomicBoolean isRunning;

    //Constructor
    public ServerReceiving(CMGameEngine ges, int socketNo)
    {
       isRunning = new AtomicBoolean(true);
       this.gameEngine=ges;       
       receiveData = new byte[24]; 
            try {
				serverSocket = new DatagramSocket(socketNo);
			} catch (SocketException e) {
				e.printStackTrace();
			}  
    }
    
    //close the socket
    public void DestroySocket()
    {
    	serverSocket.close();
    }
    
    //sets the IPadress of the client
    public void setPlayer(InetAddress address)
    {  player1Address=address;   }

    //sets the inpuDirection in gameEngine
    public void setDirection(String data, int length)
    {
        //gives the inputDirection
        String temp=data.substring(0, length);
        gameEngine.setInputDirPlayer1(Integer.parseInt(temp));
      
    }
   
    @Override
    public void run()
    { 
    	 try {
         while(isRunning.get())
         {
           receivePacket = new DatagramPacket(receiveData, receiveData.length); 
         
			serverSocket.receive(receivePacket);
	
           String receiveData = new String(receivePacket.getData()); 
  
           this.setDirection(receiveData, receivePacket.getLength());
           
           //flag to let start gameEngine, it means client sends their first data
           this.ready=true;
    
         }
    	 } catch (IOException e) {
 			e.printStackTrace();
 		 }   
    }  
}


