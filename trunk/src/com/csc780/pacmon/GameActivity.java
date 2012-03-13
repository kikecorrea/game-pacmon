package com.csc780.pacmon;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class GameActivity extends Activity implements SensorEventListener{
	
	
	private GameSurfaceView gameView;
	private SensorManager mySensorManager;
	private Sensor myAccelerometer;
	
	//change in x and y of pac-mon
	private float xAccel;
	private float yAccel;
	private float accelerometerNoise = 1.F; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        gameView = new GameSurfaceView(this);
        
        setContentView(gameView);
        
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		gameView.pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gameView.resume();
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	//get values of accelerometer
	public void onSensorChanged(SensorEvent event) {
		
		xAccel = event.values[0];
		yAccel = event.values[1];
		float z = event.values[2];
		
		if(yAccel < -2.5F) // tilt up
			gameView.setDir(1);
		if(yAccel > 2.5F)  // tilt down
			gameView.setDir(2);
		if(xAccel < -2.5F) // tilt to right
			gameView.setDir(3); 
		if(xAccel > 2.5F)  // tilt to left
			gameView.setDir(4);

		
		
	}
    
    
    
    
}