package com.csc780.clientmultiserver;

import java.util.concurrent.atomic.AtomicBoolean;

import com.csc780.pacmon.GameActivity;
import com.csc780.pacmon.SoundEngine;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;

public class CMGameActivity extends Activity implements SensorEventListener{
	final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	
	private CMGameSurfaceView mgameView;
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	
	//change in x and y of pac-mon
	private float xAccel;
	private float yAccel;
	private CMGameEngine gameEngine;
	private SoundEngine soundEngine;
	
	//progress dialog
	private static final int SERVER_CLIENT = 0;
	private static final int DIALOG_PROGRESS = 1;
	private static final int CLIENT_CONNECTED = 2;
	private static final int CLIENT_SEARCHING = 3;
	private static final int SERVER_CONNECTED = 4;
	private ProgressDialog mProgressDialog;
    private Handler mProgressHandler;
    private volatile boolean progressStatus=false;
    private AtomicBoolean clientReady;
    
    //dialog for client connected message
    AlertDialog connectedDialog;
    
    //for server
    ServerThread serverThread;
   
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        clientReady=new AtomicBoolean(false);
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        soundEngine = new SoundEngine(this);
        
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
	       WifiManager.MulticastLock multicastLock = wm.createMulticastLock("mydebuginfo"); 
	       multicastLock.acquire();
	       
	       
        gameEngine = new CMGameEngine();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        mgameView = new CMGameSurfaceView(this, gameEngine, width, height);

        setContentView(mgameView);
        
        //start the server
    	//we will pass clientReady so that we can check if progress dialog is finish
        //we need to initialize it here bec. if killAllThread is called then it will
        //give nullPointerException, which means receiving, sending thread hasn't been initialize 
    	serverThread=new ServerThread(gameEngine, clientReady);
        
    	//start the server, client discoverer and dispatcher
		serverThread.start();
		showDialog(DIALOG_PROGRESS);
        mProgressDialog.setProgress(0);
        handlerThreadServer();		
    	
     
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
								Thread.sleep(1500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
                			CMGameActivity.this.connectedDialog.dismiss();
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
    			while(!clientReady.get())
    			{
    				try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    			}
    			progressStatus=true;
    			mProgressHandler.sendEmptyMessage(0);
    		}
    	});
    	t.start();
    }
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {	
        case DIALOG_PROGRESS:
        	mProgressDialog = new ProgressDialog(this);

          //  mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
            mProgressDialog.setTitle("Server started");
            mProgressDialog.setMessage("Waiting for client...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	mProgressDialog.dismiss();
                	CMGameActivity.this.finish();
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//mgameEngine.pause();
		mgameView.pause();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//mgameEngine.resume();
		mgameView.resume();
	}
	
	@Override
	protected void onDestroy() {
		
		// stop receiving and sending thread
		serverThread.killSendingReceiving();
		//mgameView.pause();
		super.onDestroy();
		//gameView.pause();
		
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CMGameActivity.this.finish();
					}
				})
				.setNegativeButton("Resume", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	//get values of accelerometer
	public void onSensorChanged(SensorEvent event) {
		
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		xAccel = event.values[0];
		yAccel = event.values[1];
		//float z = event.values[2];
		
		if(yAccel < -1.8F && yAccel*yAccel > xAccel*xAccel){ // tilt up
			gameEngine.setInputDirPlayer2(UP);
			//gameView.setDir(1);
		}
		if(yAccel > 1.8F && yAccel*yAccel > xAccel*xAccel){ // tilt down
			gameEngine.setInputDirPlayer2(DOWN);
			//gameView.setDir(2);
		}
		if (xAccel < -1.8F && xAccel * xAccel > yAccel * yAccel) { // tilt to
																	// right
			gameEngine.setInputDirPlayer2(RIGHT);
			//gameView.setDir(3);
		}
		if (xAccel > 1.8F && xAccel * xAccel > yAccel * yAccel) { // tilt to
																	// left
			gameEngine.setInputDirPlayer2(LEFT);
			//gameView.setDir(4);
		}

		
		
	}
    
    
    
    
}