/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver_pacmon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mamon
 */
public class ServerReceiving extends Thread {
    private DatagramSocket serverSocket;
    private byte[] receiveData;
    
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    
    private GameEngine gameEngine;
   
    public volatile boolean ready=false;
    
    
    //we have two variable bec. these are shared by two threads
    private volatile InetAddress player1Address;
    private volatile InetAddress player2Address;
    
   
    
    
    //Constructor
    public ServerReceiving(GameEngine ges, int socketNo)
    {
       this.gameEngine=ges; 
        
       receiveData = new byte[24]; 
        try {
            serverSocket = new DatagramSocket(socketNo);
        } catch (SocketException ex) {
            Logger.getLogger(ServerReceiving.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    //sets the IPadress of the client
    public void setPlayer(InetAddress address, InetAddress address2)
    {  player1Address=address;    player2Address=address2;  }
   
    
    
    
    //sets the inpuDirection in gameEngine
    public void setDirection(String data, int length, InetAddress address)
    {
        //gives the inputDirection
        String temp=data.substring(0, length);
  
       //System.out.println("this::"+ this.playerAddress + "::dataAddr:"+ address);
        if(this.player1Address.equals(address))
        {
            gameEngine.setInputDirPlayer1(Integer.parseInt(temp));
        }
        else if(this.player2Address.equals(address))
        {
            gameEngine.setInputDirPlayer2(Integer.parseInt(temp));
        }
        
    }
    

    @Override
    public void run()
    { 
      
    
         while(true)
         {  
           receivePacket =  new DatagramPacket(receiveData, receiveData.length); 
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException ex) {
                Logger.getLogger(ServerReceiving.class.getName()).log(Level.SEVERE, null, ex);
            }
           
           String receiveData = new String(receivePacket.getData()); 
           
           //System.out.println("REceiving from client::"+ receiveData);
           this.setDirection(receiveData, receivePacket.getLength(), receivePacket.getAddress());
           
           //flag to let start gameEngine, it means client sends thier first data
           this.ready=true;
      
         }
         
    }
    
}


