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
 *This class handles the auto discovery
 *This class will broadcast its ip address until a clients connects to it
 */
public class ServerAutoDiscovery extends Thread {
    
    private MulticastSocket serverSocket;
    private InetAddress group;
    private int port;

    public void DestroySocket()
    {
    	serverSocket.close();
    }
 
    public ServerAutoDiscovery()
    {
        try {
           serverSocket = new MulticastSocket(4322);
           
           //server will broadcast to this generic address then if a clients listens to this 
           //address it will receive info about server's real IP address
           group=InetAddress.getByName("230.0.0.1");
           port=4322;
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerAutoDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(ServerAutoDiscovery.class.getName()).log(Level.SEVERE, null, ex);
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
     // serverSocket.setSoTimeout(2000);
     
      while(i<1) 
        { 
          sendData  = new byte[24]; 
          receiveData = new byte[24]; 

          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 

          serverSocket.receive(receivePacket); 
         
         String temp= new String(receivePacket.getData());
         temp=temp.substring(0, receivePacket.getLength());
      
          if(ipAddr.equals(temp))
          {
              //System.out.println("the same old");
          }
          else
          {
            sentence = new String(receivePacket.getData()); 
            sentence=sentence.substring(0, receivePacket.getLength());
  
            if(sentence.equals("password"))
            {
                InetAddress addr = InetAddress.getLocalHost();
                ipAddr=addr.getHostAddress();
        
                 sendData = ipAddr.getBytes(); 
  
                 DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, port); 
  
                 serverSocket.send(sendPacket); 
                 sendPacket=null;
               i++;
            }
            else
            {
                String x="error";
                sendData = x.getBytes(); 
 
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, group, port); 
                 serverSocket.send(sendPacket); 
                 sendPacket=null;
            }
          }
        }
      }
      catch (SocketException ex) {
        //System.out.println("Socket exception occured in ServerAutoDiscovery.java");
        //System.exit(1);
      }catch (UnknownHostException ex) {
                        Logger.getLogger(ServerAutoDiscovery.class.getName()).log(Level.SEVERE, null, ex);
      }catch (IOException ex) {
                        Logger.getLogger(ServerAutoDiscovery.class.getName()).log(Level.SEVERE, null, ex);
      }
    } 
}
