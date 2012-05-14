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
public class DispatcherReceiver {
    private DatagramSocket serverSocket;
    private byte[] receiveData;
    private byte[] sendData;
 
    
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    
    private boolean firstPlayer=true;
    
    private boolean playerSet=false;
    
    public int firstPlayerData;
    public int secondPlayerData;
    
    public InetAddress firstIPAddress;
    public InetAddress secondIPAddress;
    
    
//    private GameEngine gameEngine;
//    private ServerSending serversending;
//    private ServerSending serversending2;
//    
//    private boolean firstTime=true, secondTime=true;
    
    //Constructor
    public DispatcherReceiver()
    {   
       receiveData = new byte[24]; 
       sendData = new byte[24]; 
        
       try {
            serverSocket = new DatagramSocket(9800);
        } catch (SocketException ex) {
            Logger.getLogger(DispatcherReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    //returns the port for receiver server
    public String assignReceiver(DatagramPacket receivePacket)
    {
        String y=new String(receivePacket.getData());
        String temp=y.substring(0, receivePacket.getLength());
        
        
        //x is the port of receiving client
        int x=Integer.parseInt(temp);
        
        if(firstPlayer)
        {
            firstPlayerData=x;
            this.firstIPAddress=receivePacket.getAddress();
            
            System.out.println("Player1 receive from::" + this.firstIPAddress + "::port::" + x);
            firstPlayer=false;
            return "1:9876";   //returns player1 idand port
        }
        else
        {
            secondPlayerData=x;
            this.secondIPAddress=receivePacket.getAddress();
            System.out.println("Player2 receive from::" + this.secondIPAddress + "::port::" + x);
            return "2:9870";  //returns player2 id and port
        }
        
    }
    

    public void runReceivingDispather()
    {  
       int i=0;
      
   
         //change this to 2 loops
         while(i<2)
         {
         //  receiveData  = new byte[12]; 
            
           receivePacket = 
             new DatagramPacket(receiveData, receiveData.length); 

           System.out.println("Dispatcher");
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException ex) {
                Logger.getLogger(DispatcherReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
           
           
           String temp=this.assignReceiver(receivePacket);
           
           //returns the port number for receiver server
           sendData=temp.getBytes();
           
           
           InetAddress address=receivePacket.getAddress();
           int tempPort=receivePacket.getPort();
           
           sendPacket = 
                    new DatagramPacket(sendData, sendData.length, address, 
                               tempPort); 
            try {
                serverSocket.send(sendPacket);
            } catch (IOException ex) {
                Logger.getLogger(DispatcherReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
      
         i++;
         }
          
    }
    
}

