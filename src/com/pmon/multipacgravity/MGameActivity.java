package com.pmon.multipacgravity;

import java.util.concurrent.atomic.AtomicBoolean;

import com.pmon.clientmultiserverpacgravity.CMGameActivity;
import com.pmon.pacgravity.SoundEngine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
public class MGameActivity extends Activity implements SensorEventListener{
	final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	
	private MGameSurfaceView mgameView;
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	
	//change in x and y of pac-mon
	private float xAccel;
	private float yAccel;
	private MGameEngine mgameEngine;
	private SoundEngine soundEngine;
	
	//progress dialog
	private static final int SEARCHING_SERVER = 0;
	private static final int SERVER_CONNECTED = 1;
	private ProgressDialog mProgressDialog;
    private Handler mProgressHandler;
    private volatile boolean progressStatus=false;
    AlertDialog connectedDialog;
    private AtomicBoolean serverReady;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String ip = getIntent().getStringExtra("ipaddress");
       
        soundEngine = new SoundEngine(this);
       
        mgameEngine = new MGameEngine(soundEngine, ip);
        mgameEngine.start();
        
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        mgameView = new MGameSurfaceView(this, mgameEngine, width, height);

        setContentView(mgameView);
        
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
  }
        
    
//    protected void handlerThreadClient()
//    {
//    	Thread t= new Thread(new Runnable()
//    	{
//    		public void run()
//    		{
//    			while(!serverReady.get())
//    			{
//    				try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//    			}
//    			progressStatus=true;
//    			mProgressHandler.sendEmptyMessage(0);
//    		}
//    	});
//    	t.start();
//    }
//    
//    protected Dialog onCreateDialog(int id) {
//        switch(id) {	
//        case SEARCHING_SERVER:
//        	mProgressDialog = new ProgressDialog(this);
//
//          //  mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
//            mProgressDialog.setMessage("Searching for server...");
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.setButton("CANCEL", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	mProgressDialog.dismiss();
//                	//kills the activity
//                	MGameActivity.this.finish();
//                    /* User clicked Yes so do some stuff */
//                }
//            });
//        return mProgressDialog;
//        
//        case SERVER_CONNECTED:
//        	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
//            connectedDialog = builder2.setTitle("Get Ready")
//                .setMessage("Found server")
//                .create();
//            connectedDialog.show();
//        return connectedDialog;
//            
//        default:
//            return null;
//        }
//    }

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
		// TODO Auto-generated method stub
		
		mgameEngine.closeConnection();
		mgameEngine.killAllThread();
		super.onDestroy();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		mySensorManager.unregisterListener(this);
		soundEngine.endMusic();
		super.finish();
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
				        
				        intent.putExtra("CALLS", 5);
				        
				        setResult(RESULT_OK, intent);
				        
				        //notifies server client has quit
				        mgameEngine.setInputDir(-99);
				        try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				        
						MGameActivity.this.finish();
						MGameActivity.this.onDestroy();
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
		
	}

	//get values of accelerometer
	public void onSensorChanged(SensorEvent event) {
		
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		xAccel = event.values[0];
		yAccel = event.values[1];
		//float z = event.values[2];
		
		if(yAccel < 2.8F){ // tilt up
			mgameEngine.setInputDir(UP);
			//gameView.setDir(1);
		}
		if(yAccel > 7.5F){ // tilt down
			mgameEngine.setInputDir(DOWN);
			//gameView.setDir(2);
		}
		if (xAccel < -1.8F ) { // tilt to
																	// right
			mgameEngine.setInputDir(RIGHT);
			//gameView.setDir(3);
		}
		if (xAccel > 1.8F ) { // tilt to
																	// left
			mgameEngine.setInputDir(LEFT);
			//gameView.setDir(4);
		}
	
	}
    
}