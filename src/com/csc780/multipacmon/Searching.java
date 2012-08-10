package com.csc780.multipacmon;

import java.util.concurrent.atomic.AtomicBoolean;

import com.csc780.clientmultiserver.CMGameActivity;
import com.csc780.pacmon.ClientOrServer;
import com.csc780.pacmon.R;
import com.csc780.pacmon.SoundEngine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Searching extends Activity {
	
	private static final int SEARCHING = 0, FOUND_SERVER=1;
	private ProgressDialog mProgressDialog;
    private Handler mProgressHandler;
    private volatile boolean progressStatus=false;
    private AlertDialog connectedDialog;
    private MGameEngine mgameEngine;
    private SoundEngine soundEngine;
    public AutoDiscoverer clientDiscoverer;
    public ClientConnectionSetUp clientSetup;
    private Receiver receiving;
    private Sender sending;
    
    //use in handelThreadClient() method
    private boolean isThreadDead=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searching);
		
		//This is to overwrite and enable UDP broadcast on android hardware
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
	    WifiManager.MulticastLock multicastLock = wm.createMulticastLock("mydebuginfo"); 
	    multicastLock.acquire();
	 
	    //initialize auto discovery
	    clientDiscoverer = new AutoDiscoverer();
	    clientDiscoverer.start();
		
		this.showDialog(SEARCHING);
	        mProgressDialog.setProgress(0);
	        handlerThreadClient();	
	        
	        //handler for client
	        //handler is use to get message from the message pool
	        mProgressHandler = new Handler() {
	            
	            public void handleMessage(Message msg) {
	                super.handleMessage(msg);
	                if (progressStatus) {
	                    mProgressDialog.dismiss();
	                    
	                    showDialog(FOUND_SERVER);       
	                    new Thread(new Runnable()
	                	{
	                		public void run()
	                		{
	                			try {
									Thread.sleep(800);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
	                			Searching.this.connectedDialog.dismiss();
	                			//start mgameactivity
	                			Intent cmGame = new Intent("com.csc780.multipacmon.MGAMEACTIVITY");
	                			cmGame.putExtra("ipaddress", Searching.this.clientDiscoverer.getipAddress());
	    						startActivityForResult(cmGame, 0);
	                		}
	                	}).start();
	                }
	            }
	        };
	}
	
	
	 protected void handlerThreadClient()
	    {
	    	Thread t= new Thread(new Runnable()
	    	{
	    		public void run()
	    		{
	    			while(!clientDiscoverer.isFinish)
	    			{
	    				try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    				
	    				if(isThreadDead)
	    					return;
	    				
	    			}
	    			
	    			if(clientDiscoverer.isFinish){
	    				progressStatus=true;
	    				mProgressHandler.sendEmptyMessage(0);
	    			}
	    		}
	    	});
	    	t.start();
	    }

	protected Dialog onCreateDialog(int id) {
		switch(id) {	
        case SEARCHING:
        	mProgressDialog = new ProgressDialog(this);

          //  mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
            mProgressDialog.setMessage("Searching for server...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                	Searching.this.finish();
                	mProgressDialog.dismiss();
                	//kills the activity, when user clicks cancel
                
                    /* User clicked Yes so do some stuff */
                }
            });
        return mProgressDialog;
        
        case FOUND_SERVER:
        	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            connectedDialog = builder2.setTitle("Get Ready")
                .setMessage("Found server")
                .create();
            connectedDialog.show();
        return connectedDialog;
            
        default:
            return null;
        }
    }
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        this.finish();
//	 
//	        if (resultCode == RESULT_OK) {
//	            /**
//	             * If no error occurred, use the Intent to retrieve an integer
//	             * stored under ID "CALLS".
//	             */
//	            int calls = data.getExtras().getInt("CALLS");
//	            /**
//	             * Display the integer in the BlueActivity's TextView.
//	             */
//	            resultField.setText("Calls to YellowActivity: " + calls);
//	        }
	    }
	 
	 @Override
	 public void finish()
	 {
		 isThreadDead=true;
		 clientDiscoverer.closeSockset();
		 super.finish();
		 
	 }
	 
	 @Override
		public void onBackPressed() {
		    clientDiscoverer.closeSockset();
		    
			this.finish();
		}

}
