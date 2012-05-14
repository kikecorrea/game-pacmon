/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver_pacmon;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mamon
 */
public class MainThread extends Thread{
 
    private GameEngine ges;
    private ServerSending sendThread, sendThread2;
    private ServerReceiving receiveThread, receiveThread2;
    private DispatcherReceiver dispatchReceiver;
    private ServerDiscovery serverDiscovery;
    
    
    public MainThread()
    {
        ges=new GameEngine();
        
//        sendThread=new ServerSending(6000, ges);
//        sendThread2=new ServerSending(6100, ges);
        
        
        //we want to create receiveThread so incase client sends data
        receiveThread=new ServerReceiving(ges,  9876);
        receiveThread2=new ServerReceiving(ges, 9870);
        
        //run the server discover
        serverDiscovery=new ServerDiscovery();
        serverDiscovery.start();
       
       
        //dispatcher, listens and sends the info of receivingServer to client
        dispatchReceiver=new DispatcherReceiver();
        
        dispatchReceiver.runReceivingDispather();
        
        //returns port of client,
        int first=dispatchReceiver.firstPlayerData;
        int second=dispatchReceiver.secondPlayerData;
        
        sendThread=new ServerSending(first, dispatchReceiver.firstIPAddress, ges);
        sendThread2=new ServerSending(second, dispatchReceiver.secondIPAddress,  ges);
        
        System.out.println("ADAF FIRST:"+ dispatchReceiver.firstIPAddress);
         System.out.println("ADAF FIRST:"+ dispatchReceiver.secondIPAddress);
        
        //sets the ipaddress of client
        //remember they shared the variable firstIPAddress, secondIPAddress
        receiveThread.setPlayer(dispatchReceiver.firstIPAddress, dispatchReceiver.secondIPAddress);
        receiveThread2.setPlayer(dispatchReceiver.firstIPAddress, dispatchReceiver.secondIPAddress);


        
        //start receive server
        receiveThread.start();
        receiveThread2.start();
      
        
        while(!receiveThread.ready && !receiveThread2.ready)
        {
           // System.out.println("Waiting for each players");
        }
        
        System.out.println("Starting");
        //remem gameEngine has its own thread running
        ges.startTheEngine();
        
        sendThread.start();
        sendThread2.start();
        
      
        //wait for thread to die
        try {
            sendThread.join();
            sendThread2.join();
            receiveThread.join();
            receiveThread2.join();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static void main(String args[])
    {
        System.out.println("RUNNING MAIN THREAD");
        MainThread mainthread=new MainThread();
    }
}
