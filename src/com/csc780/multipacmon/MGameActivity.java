package com.csc780.multipacmon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class MGameActivity extends Activity implements SensorEventListener{
	final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	
	private MGameSurfaceView mgameView;
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	
	//change in x and y of pac-mon
	private float xAccel;
	private float yAccel;
	private MGameEngine mgameEngine;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        mgameEngine = new MGameEngine();
        mgameView = new MGameSurfaceView(this, mgameEngine.pacmon, mgameEngine.pacmon2, mgameEngine);

        setContentView(mgameView);
        
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
		// TODO Auto-generated method stub
		super.onDestroy();
		//gameView.pause();
		//mgameView.pause();
	}


	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to quit?").setCancelable(false)
				.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MGameActivity.this.finish();
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
			mgameEngine.setInputDir(UP);
			//gameView.setDir(1);
		}
		if(yAccel > 1.8F && yAccel*yAccel > xAccel*xAccel){ // tilt down
			mgameEngine.setInputDir(DOWN);
			//gameView.setDir(2);
		}
		if (xAccel < -1.8F && xAccel * xAccel > yAccel * yAccel) { // tilt to
																	// right
			mgameEngine.setInputDir(RIGHT);
			//gameView.setDir(3);
		}
		if (xAccel > 1.8F && xAccel * xAccel > yAccel * yAccel) { // tilt to
																	// left
			mgameEngine.setInputDir(LEFT);
			//gameView.setDir(4);
		}

		
		
	}
    
    
    
    
}