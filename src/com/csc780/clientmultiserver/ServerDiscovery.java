/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csc780.clientmultiserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mamon
 */
public class ServerDiscovery extends Thread {
    
    private MulticastSocket serverSocket;
    private InetAddress group;
    private int port;
    
    
    public void DestroySocket()
    {
    	serverSocket.close();
    }
    
    
    public ServerDiscovery()
    {
        try {
           serverSocket = new MulticastSocket(4322);
           group=InetAddress.getByName("230.0.0.1");
           port=4322;
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(ServerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void run()
    {
        try
     { 
      serverSocket.joinGroup(group);
  
      byte[] receiveData = new byte[24]; 
      byte[] sendData  = new byte[24]; 
  
      String sentence=null;
      String ipAddr="";
      boolean isRunning=true;
      int i=0;
     
      while(i<1) 
        { 
          sendData  = new byte[24]; 
          receiveData = new byte[24]; 

          DatagramPacket receivePacket = 
             new DatagramPacket(receiveData, receiveData.length); 

          System.out.println ("Started server discovery waiting for client");

          serverSocket.receive(receivePacket); 
          

         String temp= new String(receivePacket.getData());
         System.out.println("boo::"+temp);
         temp=temp.substring(0, receivePacket.getLength());
         
          
          if(ipAddr.equals(temp))
          {
              System.out.println("the same old");
          }
          else
          {
            sentence = new String(receivePacket.getData()); 
            sentence=sentence.substring(0, receivePacket.getLength());
            //System.out.println("this is data:"+ sentence);
            
            if(sentence.equals("password"))
            {
                InetAddress addr = InetAddress.getLocalHost();
                ipAddr=addr.getHostAddress();
        
                 sendData = ipAddr.getBytes(); 
  
                 DatagramPacket sendPacket = 
                  new DatagramPacket(sendData, sendData.length, group, 
                               port); 
  
                 serverSocket.send(sendPacket); 
                // serverSocket.send(sendPacket); 
                 sendPacket=null;
               i++;
               System.out.println("One clients receive");
            }
            else
            {
                String x="error";
        
                 sendData = x.getBytes(); 
  
                 DatagramPacket sendPacket = 
                  new DatagramPacket(sendData, sendData.length, group, 
                               port); 
  
                 serverSocket.send(sendPacket); 
                 sendPacket=null;
             
            }

          //InetAddress IPAddress = receivePacket.getAddress(); 
  
          //int port = receivePacket.getPort(); 
  
          //System.out.println ("From: " + IPAddress + ":" + port);
          //System.out.println ("Message: " + sentence);

          //capitalizedSentence = sentence.toUpperCase(); 
          
          }
       

        }
        System.out.println("Exiting server");

      }
      catch (SocketException ex) {
        System.out.println("UDP Port 9876 is occupied.");
        System.exit(1);
      }catch (UnknownHostException ex) {
                        Logger.getLogger(ServerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
      }catch (IOException ex) {
                        Logger.getLogger(ServerDiscovery.class.getName()).log(Level.SEVERE, null, ex);
                    }

    }
    
   
}
