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

/**
 * This class handles the accelerometer, MGameEngine, MGameSurfaceView
 */
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
    WifiManager wm;
    private ServerSending sendThread;
    private ServerReceiving receiveThread;
    
    //use in handlerThreadServer() method
    private boolean htsKill=false;
    
    //dialog for client connected message
    AlertDialog connectedDialog;
    
    //for server
    ServerThread serverThread;
    
    private boolean isThreadDead=false;
   	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String ip = getIntent().getStringExtra("ipaddress");
        int port = getIntent().getIntExtra("port", 0);
        
//        clientReady=new AtomicBoolean(false);
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        soundEngine = new SoundEngine(this);
        
        gameEngine = new CMGameEngine(soundEngine);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        mgameView = new CMGameSurfaceView(this, gameEngine, width, height);

        setContentView(mgameView);
        
        //start the server
    	//we will pass clientReady so that we can check if progress dialog is finish
        //we need to initialize it here bec. if killAllThread is called then it will
        //give nullPointerException, which means receiving, sending thread hasn't been initialize 
//    	serverThread=new ServerThread(gameEngine, clientReady);
//        
    	//start the server, client discoverer and dispatcher
//		serverThread.start();
        
        

        receiveThread=new ServerReceiving(gameEngine,  9876);
        sendThread = new ServerSending();
        
        sendThread=new ServerSending(port, ip, gameEngine);
        
        //sets the ipaddress of client
        //remember they shared the variable firstIPAddress, secondIPAddress
        receiveThread.setPlayer(ip);
       
        //start receive server
        receiveThread.start();

        
//        new Thread(new Runnable()
//    	 {
//    		public void run()
//    		{
//    			//waiting for client players to be ready before starting game engine
//    	        while(!receiveThread.ready )
//    	        {  	
//    	        	try {
//    					Thread.sleep(10);
//    				} catch (InterruptedException e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				}
//    	        	
//    	        	if(isThreadDead)
//    	        		return;
//    	        }
//    	        if(receiveThread.ready)
//    	        {
//    	        	//put a sleep here because clientReady dialog box will show for 1500ms
//    	        	//so we don't want to start gameEngine right away
////    	        	try {
////    				Thread.sleep(1000);
////    	        	} catch (InterruptedException e) {
////    				// TODO Auto-generated catch block
////    				e.printStackTrace();
////    				}
//    	       
//    	        	//remem gameEngine has its own thread running
//    	           CMGameActivity.this.gameEngine.startTheEngine();
//    	           sendThread.start();
//    	        
//    	        }
//    		}
//    	 }).start();
        CMGameActivity.this.gameEngine.startTheEngine();
        sendThread.start();
        
        
        
        
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
		
		//mgameView.pause();
		super.onDestroy();
		//gameView.pause();
		
	}
	
	
	
	@Override
	public void finish() {
		mySensorManager.unregisterListener(this);
		this.htsKill = true;
		soundEngine.endMusic();

    	sendThread.isRunning.set(false);
    	sendThread.DestroySocket();
     	receiveThread.isRunning.set(false);
    	receiveThread.DestroySocket();
		

		super.finish();
		serverThread=null;
		soundEngine=null;

	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						isThreadDead=true;
						
						//set gamestate to disconnected
						CMGameActivity.this.gameEngine.gameState=6;
						
						//CMGameActivity.this.finish();	
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
		
		if(yAccel < 2.8F ){ // tilt up
			gameEngine.setInputDirPlayer1(UP);
			//gameView.setDir(1);
		}
		if(yAccel > 7.5F ){ // tilt down"
			gameEngine.setInputDirPlayer1(DOWN);
			//gameView.setDir(2);
		}
		if (xAccel < -1.8F ) { // tilt to
																	// right
			gameEngine.setInputDirPlayer1(RIGHT);
			//gameView.setDir(3);
		}
		if (xAccel > 1.8F ) { // tilt to
																	// left
			gameEngine.setInputDirPlayer1(LEFT);
			//gameView.setDir(4);
		}
	}
    
}