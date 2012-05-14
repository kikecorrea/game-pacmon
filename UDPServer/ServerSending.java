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
public class ServerSending extends Thread {
     
    private DatagramSocket serverSocket;
    private byte[] sendData;
    
    private InetAddress IPAddress;
    private int port;
    
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    
    private String data;
    public volatile boolean propertySet=false;
    
    
    private GameEngine gameEngine;
    
    private int tickCounter=0;
    
    private int portServer;
    
    private boolean countDown;
    
    
    //Constructor
    //port is port of client
    public ServerSending(int port, InetAddress address, GameEngine ges)
    {
       this.countDown=true;
       this.gameEngine=ges;
       sendData  = new byte[128]; 
       this.port=port;
       this.IPAddress=address;
        
            try {
                serverSocket = new DatagramSocket();
            } catch (SocketException ex) {
                Logger.getLogger(ServerSending.class.getName()).log(Level.SEVERE, null, ex);
            }

    }
    
//    public void setProperties(InetAddress address, int port, int id)
//    {
//        this.IPAddress=address;
//        this.port=57400;
//        this.id=id;
//        
//        System.out.println("IPadress::"+ this.IPAddress);
//        System.out.println("POrt::" + this.port);
//        System.out.println("id::" + id);
//        
//        this.propertySet=true;
//       // this.notify();
//    }
    
    public String getData()
    {
   
      //waits for for the next tick to occur
      while(tickCounter==gameEngine.tickCounter)
      {
         // System.out.println("boo");
      }
      
      
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
          //string format: tickCounter:(player1)x:(player1)y:(player2)x:(player2)y
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
    
//    public String getCountDown()
//    {
////        while(tickCounter==gameEngine.tickCounter)
////        {
////            
////        }
//        
//        tickCounter=gameEngine.tickCounter;
//        
//        String temp = null;
//        long x=gameEngine.getReadyCountDown();
//              
//            if(x>1)
//                 temp=String.valueOf(x);
//            else
//            {
//                temp=String.valueOf(x);
//                this.countDown=false;
//            }
//        
//        
//     return temp;
//            
//    }
    
    
    @Override
    public void run()
    {  
        
       sendData  = new byte[128];  
       while(true)
       {          
            try {
                //   sendData  = new byte[64]; 
           
               
                       String temp;
                     
                      temp=this.getData();
                  
                       
                       sendData=temp.getBytes();
                       System.out.println("Sending data to Client:"+ temp);
                       
                       sendPacket = 
                           new DatagramPacket(sendData, sendData.length, IPAddress, 
                                      port); 
                       
                        //remember sending data of pacmon, pacmon2
                        serverSocket.send(sendPacket);
                       // System.out.println("sent data to client");
            } catch (IOException ex) {
                Logger.getLogger(ServerSending.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
               
           
       }
    }
    
}
