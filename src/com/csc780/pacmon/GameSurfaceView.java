package com.csc780.pacmon;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Runnable {

	Thread surfaceThread = null;
	boolean isRunning;
	
	public GameSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		isRunning = true;
		surfaceThread = new Thread(this);
		surfaceThread.start();
	}

	public void run() {
		
		while(isRunning){
			
		}
	}

	public void pause() {

	}
	
	public void resume() {
		isRunning = true;
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		
		
	}
	

}
