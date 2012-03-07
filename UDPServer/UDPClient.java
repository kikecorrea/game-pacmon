import java.io.*; 
import java.net.*; 
import java.nio.ByteBuffer;
  
class UDPClient { 
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
}
    
//    public static void main (String args[])
//    {
//        byte [] xy=intToByteArray(1);
//        
//        System.out.println(xy.length);
//    }
//    
    
    public static void main(String args[]) throws Exception 
    { 
     try {
        String serverHostname = new String ("130.212.122.97");

        if (args.length > 0)
           serverHostname = args[0];
  
      BufferedReader inFromUser = 
        new BufferedReader(new InputStreamReader(System.in)); 
  
      DatagramSocket clientSocket = new DatagramSocket(); 
  
      InetAddress IPAddress = InetAddress.getByName(serverHostname); 
      System.out.println ("Attemping to connect to " + IPAddress + 
                          ") via UDP port 9876");
  
      byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[1024]; 
      int i=3;
      while(i==3)
      {
          
          sendData = new byte[1024];
          receiveData=new byte[1024];
      System.out.print("Enter Message: ");
      String sentence = inFromUser.readLine(); 
      
      
      
      String [] bb={"sd", "44"};
     
      
      
      sendData = sentence.getBytes();         

      System.out.println ("Sending data to " + sendData.length + 
                          " bytes to server.");
      DatagramPacket sendPacket = 
         new DatagramPacket(sendData, sendData.length, IPAddress, 9876); 
  
      clientSocket.send(sendPacket); 
  
      DatagramPacket receivePacket = 
         new DatagramPacket(receiveData, receiveData.length); 
  
      System.out.println ("Waiting for return packet");
      clientSocket.setSoTimeout(10000);

      try {
           clientSocket.receive(receivePacket); 
           String modifiedSentence = 
               new String(receivePacket.getData()); 
  
           InetAddress returnIPAddress = receivePacket.getAddress();
     
           int port = receivePacket.getPort();

           System.out.println ("From server at: " + returnIPAddress + 
                               ":" + port);
           System.out.println("Message: " + modifiedSentence); 

          }
      catch (SocketTimeoutException ste)
          {
           System.out.println ("Timeout Occurred: Packet assumed lost");
       }
      } 
     clientSocket.close(); 
     }
   catch (UnknownHostException ex) { 
     System.err.println(ex);
    }
   catch (IOException ex) {
     System.err.println(ex);
    }
  } 
} 
