package com.csc780.pacmon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Runnable {

	private float screenWidth, screenHeight;
	private float dx, dy;
	private float ballTopX, ballTopY;
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	private Bitmap ball;
	
	public GameSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		//screenWidth = 480;//this.getWidth();
		//screenHeight = 800;//this.getHeight();

		
		dx = dy = 0;
		
		surfaceHolder = getHolder();
		ballTopX = screenWidth/2;
		ballTopY = screenHeight/2;
		ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
		isRunning = true;
		setKeepScreenOn(true);
		

	}
	
	//thread to draw 
	public void run() {
		
		while(isRunning){
			if(!surfaceHolder.getSurface().isValid())
				continue;
			
			Canvas canvas = surfaceHolder.lockCanvas();

			screenWidth = canvas.getWidth();
			screenHeight = canvas.getHeight();
			
			if (ballTopX + dx < 0)
				ballTopX = 0;
			else if (ballTopX + ball.getWidth() + dx > screenWidth)
				ballTopX = screenWidth - ball.getWidth();
			else 
				ballTopX += dx;
			
			if (ballTopY + dy < 0)
				ballTopY = 0;
			else if (ballTopY + ball.getHeight() + dy > screenHeight)
				ballTopY = screenHeight - ball.getHeight();
			else
				ballTopY += dy;
			
			canvas.drawRGB(02, 02, 150);
			
			canvas.drawBitmap(ball, ballTopX, ballTopY, null);
			
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	public void pause() {
		isRunning = false;
		while(true){
			try {
				surfaceThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		surfaceThread = null;
	}
	
	public void resume() {
		isRunning = true;
		surfaceThread = new Thread(this);
		surfaceThread.start();
		setKeepScreenOn(true);
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}
	

}
