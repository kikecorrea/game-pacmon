package com.csc780.clientmultiserver;

import java.util.concurrent.atomic.AtomicBoolean;

import com.csc780.multipacmon.Searching;
import com.csc780.pacmon.R;
import com.csc780.pacmon.SoundEngine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;


public class ServerStarted extends Activity {
	
		private static final int SERVER_CLIENT = 0;
		private static final int DIALOG_PROGRESS = 1;
		private static final int CLIENT_CONNECTED = 2;
		private static final int CLIENT_SEARCHING = 3;
		private static final int SERVER_CONNECTED = 4;
		private ProgressDialog mProgressDialog;
	    private Handler mProgressHandler;
	    private volatile boolean progressStatus=false;
	    private AtomicBoolean clientReady;
	    private ServerConnectionInfo dispatchReceiver;
	    private ServerAutoDiscovery serverDiscovery;
	    WifiManager wm;
	    
	    //use in handlerThreadServer() method
	    private boolean htsKill=false;
	    
	    //dialog for client connected message
	    AlertDialog connectedDialog;
	 
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.serverstarted);
	        
	        clientReady=new AtomicBoolean(false);
	    
//	        soundEngine = new SoundEngine(this);
	        
	        //hack for enabling UDP broadcasting
	         wm = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
		       WifiManager.MulticastLock multicastLock = wm.createMulticastLock("mydebuginfo"); 
		       multicastLock.acquire();
		       
		       showDialog(DIALOG_PROGRESS);
		        mProgressDialog.setProgress(0);
		        handlerThreadServer();		
		       
		     //run the server discover
		     serverDiscovery=new ServerAutoDiscovery();
		     //dispatcher, listens and sends the info of receivingServer to client
		     dispatchReceiver=new ServerConnectionInfo(clientReady, serverDiscovery.isRunning);   
		     
		     //going to start broadcasting IP address
		     serverDiscovery.start();
		     
	         new Thread(new Runnable()
         	 {
         		public void run()
         		{
         			 dispatchReceiver.runReceivingDispather();
         		}
         	 }).start();

	        //handler for server
	        //handler is use to get message from the message pool
	        mProgressHandler = new Handler() {
	            
	            public void handleMessage(Message msg) {
	                super.handleMessage(msg);
	                if (progressStatus) {
	                    mProgressDialog.dismiss();
	                    
	                    //remember to put connectedDialog.dismiss() somewhere
	                    showDialog(CLIENT_CONNECTED);       
	                    new Thread(new Runnable()
	                	{
	                		public void run()
	                		{
	                			try {
									Thread.sleep(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								} 
	                			ServerStarted.this.connectedDialog.dismiss();
	                			
	                			serverDiscovery.DestroySocket();
	                			
	                			//start cmgameactivity
	                			Intent cmGame = new Intent("com.csc780.clientmultiserver.CMGAMEACTIVITY");
	                			cmGame.putExtra("ipaddress", ServerStarted.this.dispatchReceiver.firstIPAddress.getHostAddress());
	                			cmGame.putExtra("port", ServerStarted.this.dispatchReceiver.firstPlayerData);
	                			
	    						startActivityForResult(cmGame, 0);
	                			
	                		}
	                	}).start();
	         
	                }
	            }
	        };
	        
	    }
	    
	    protected void handlerThreadServer()
	    {
	    	Thread t= new Thread(new Runnable()
	    	{
	    		public void run()
	    		{
	    			while(!clientReady.get() )
	    			{
	    				try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    				if(htsKill)
	    					return;
	    			}
	    			
	    			if(clientReady.get()){
	    			progressStatus=true;
	    			mProgressHandler.sendEmptyMessage(0);
	    			}
	    		}
	    	});
	    	t.start();
	    }
	    
	    protected Dialog onCreateDialog(int id) {
	        switch(id) {	
	        case DIALOG_PROGRESS:
	        	mProgressDialog = new ProgressDialog(this);

	          //  mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
	            mProgressDialog.setTitle("server started");
	            mProgressDialog.setMessage("Waiting for client...");
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            
	            mProgressDialog.setButton("CANCEL", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                   	mProgressDialog.dismiss();
	                	mProgressHandler=null;
	                	clientReady=null;
	                	wm=null;
	                	ServerStarted.this.finish();
	                    /* User clicked Yes so do some stuff */
	                }
	            });
	        return mProgressDialog;
	            
	        case CLIENT_CONNECTED:
	        	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
	            connectedDialog = builder2.setTitle("Get Ready")
	                .setMessage("Client has connected")
	                .create();
	            connectedDialog.show();
	        return connectedDialog;
	         
	        default:
	            return null;
	        }
	    }
	    
	    @Override
		public void finish() {
			this.htsKill = true;
			this.dispatchReceiver.DestroySocket();
			this.serverDiscovery.DestroySocket();
			//serverThread.killSendingReceiving();
			super.finish();
			//mgameView=null;
		}
	    
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        this.finish();

	    }
	

}
