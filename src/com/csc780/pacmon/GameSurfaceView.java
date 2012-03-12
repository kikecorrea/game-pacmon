package com.csc780.pacmon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
	private int[][] maze;
	
	public GameSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		//screenWidth = 480;//this.getWidth();
		//screenHeight = 800;//this.getHeight();

		dx = dy = 0;
		maze = Maze.getMaze();
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
			canvas.drawRGB(02, 02, 150);
			
			drawMaze(canvas);
			
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
			
			//canvas.drawCircle(ballTopX, ballTopY, screenWidth/13, null);
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
	
	public void drawMaze(Canvas canvas){
		Paint myPaint = new Paint();
		myPaint.setColor(Color.GRAY);
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		for (int i = 0; i < 13; i++){
			for (int j = 0; j < 10; j++){
				if (maze[i][j] > 0){
					float left = width/13*i;
					float top = height/10*j;
					float right = left + width/13;
					float bottom = top + height/10;
					canvas.drawRect(left, top, right, bottom, myPaint);
				}
			}
		}
	}
	

	public void setDx(float dx) {
		this.dx = dx;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}
	

}
