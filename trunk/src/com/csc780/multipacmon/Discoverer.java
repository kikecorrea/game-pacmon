package com.csc780.multipacmon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Discoverer extends Thread{

	private Context mContext;
	private static final String TAG = "Discovery";
	private static final String mChallenge = "myvoice";
	private static final int DISCOVERY_PORT = 2562;
	private static final int TIMEOUT_MS = 500;
	
	public void Discoverer()
	{
		
	}
	
	public InetAddress getBroadcastAddress() throws IOException
	{
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}
	
	private void listenForResponses(DatagramSocket socket) throws IOException {
	    byte[] buf = new byte[24];
	    try {
	      while (true) {
	        DatagramPacket packet = new DatagramPacket(buf, buf.length);
	        socket.receive(packet);
	        String s = new String(packet.getData(), 0, packet.getLength());
	        Log.d(TAG, "Received response " + s);
	      }
	    } catch (SocketTimeoutException e) {
	      Log.d(TAG, "Receive timed out");
	    }
	  }
	
	private void sendDiscoveryRequest(DatagramSocket socket) throws IOException {
	    String data ="kobe";
	    Log.d(TAG, "Sending data " + data);

	    DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
	        getBroadcastAddress(), DISCOVERY_PORT);
	    socket.send(packet);
	  }
	
	
	public void run()
	{
		try {
		      DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT);
		      socket.setBroadcast(true);
		      socket.setSoTimeout(TIMEOUT_MS);

		      sendDiscoveryRequest(socket);
		      listenForResponses(socket);
		    } catch (IOException e) {
		      Log.e(TAG, "Could not send discovery request", e);
		    }
	}
	
}
