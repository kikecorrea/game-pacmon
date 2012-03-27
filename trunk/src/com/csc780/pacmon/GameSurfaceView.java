package com.csc780.pacmon;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

public class GameSurfaceView extends SurfaceView implements Runnable {

	private final static int    MAX_FPS = 40;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	
	private float dx, dy;
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	int mCurrentFrame = 0;
	
	//pacman data
	//direction 1 = up, 2 = down, 3 = right, 4 = left
	private Pacmon pacmon;
	private int direction;

	private GameEngine gameEngine;
	private ArrayList<Monster> ghosts;
	//drawing bitmap
	private Bitmap ball, pac_img, wall, door, ghost, bluey_img, redy_img, food, power ; // bitmap 
	private int pacSprite_height, pacSprite_width;
	
	//maze info
	private int[][] mazeArray;
	private Maze maze;
	private int mazeRow, mazeColumn;
	private int blockSize;
	
	public GameSurfaceView(Context context, Pacmon pacmon, GameEngine gameEngine) {
		super(context);
		this.pacmon = pacmon;
		this.gameEngine = gameEngine;
		
		blockSize = 32;  // size of block
		dx = dy = 0;
		maze = gameEngine.getMaze();
		mazeArray = gameEngine.getMazeArray();
		mazeRow = gameEngine.getMazeRow();
		mazeColumn = gameEngine.getMazeColumn();

		
		initBitmap();  // init all Bitmap and its components

		ghosts = gameEngine.ghosts;
		
		surfaceHolder = getHolder();
		isRunning = true;
		setKeepScreenOn(true);
		
	}
	
	private void initBitmap(){
		ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
		wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		door = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_door);
		food = BitmapFactory.decodeResource(getResources(), R.drawable.food);
		power = BitmapFactory.decodeResource(getResources(), R.drawable.power);
		pac_img = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite);
		bluey_img = BitmapFactory.decodeResource(getResources(), R.drawable.bluey_sprite);
		redy_img = BitmapFactory.decodeResource(getResources(), R.drawable.redy_sprite);
		
		pacSprite_width = pac_img.getWidth();
		pacSprite_height = pac_img.getHeight();
	}
	
	//thread to update and draw 
	public void run() {
		Canvas canvas;
		long beginTime; // the time when the cycle begun
		long timeDiff; // the time it took for the cycle to execute
		int sleepTime; // ms to sleep (<0 if we're behind)
		int framesSkipped; // number of frames being skipped

		sleepTime = 0;

		while (isRunning) {
			canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0; // resetting the frames skipped
					
					int screenWidth = canvas.getWidth();
					
					gameEngine.update();

					canvas.drawRGB(0, 0, 0);

					drawMaze(canvas); // draw updated maze

					drawPacmon(canvas, direction); // draw Pacman

					drawGhost(canvas); // draw ghosts

					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int) (FRAME_PERIOD - timeDiff);

					if (sleepTime > 0) {
						// if sleepTime > 0 we're OK
						try {
							// send the thread to sleep for a short period
							// very useful for battery saving
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
					}

					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						// we need to catch up
						// update without rendering
						gameEngine.update();
						// add frame period to check if in next frame
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
				}
			} finally {
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	// draw current location of ghosts
	private void drawGhost(Canvas canvas) {
		mCurrentFrame = ++mCurrentFrame % 2;
		for (int i = 0; i < gameEngine.ghosts.size(); i++) {
			int direction = ghosts.get(i).getDir();
			int n;
			if (direction == UP)	n = 0;
			else if (direction == DOWN)		n = 1;
			else if (direction == RIGHT)		n = 2;
			else 		n = 3;
			
			int srcY = n * blockSize;
			int srcX = mCurrentFrame * blockSize;
			int gX = ghosts.get(i).getX();
			int gY = ghosts.get(i).getY();
			Rect src = new Rect(srcX, srcY, srcX + blockSize, srcY + blockSize);
			Rect dst = new Rect(gX, gY, gX + blockSize, gY + blockSize);
			
			if (i == 0)
				canvas.drawBitmap(bluey_img, src, dst, null);
			else if (i == 1)
				canvas.drawBitmap(redy_img, src, dst, null);
		}
	}

	// draw pacmon 
	private void drawPacmon(Canvas canvas, int dir) {
		currentFrame = ++currentFrame % 3;
		int n;
		int direction = pacmon.getDir(); // get current direction of pacmon
		
		if (direction == UP)	n = 0;
		else if (direction == DOWN)		n = 1;
		else if (direction == RIGHT)		n = 2;
		else 		n = 3;
		
		int srcY = n * blockSize;
		int srcX = currentFrame * blockSize;
		int pX = pacmon.getpX();
		int pY = pacmon.getpY();
		Rect src = new Rect(srcX, srcY, srcX + blockSize, srcY + blockSize);
		Rect dst = new Rect(pX, pY, pX + blockSize , pY + blockSize);
		canvas.drawBitmap(pac_img, src, dst, null);
		
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
	
	// draw current maze with food
	public void drawMaze(Canvas canvas){
		for (int i = 0; i < mazeRow; i++){
			for (int j = 0; j < mazeColumn; j++){
				if (mazeArray[i][j] == 0)
					canvas.drawBitmap(wall, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 3)
					canvas.drawBitmap(door, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 1)
					canvas.drawBitmap(food, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 2)
					canvas.drawBitmap(power, j*blockSize, i*blockSize, null);
			}
		}
	}
	
	public void setDir(int dir){
		this.direction = dir;
	}

}


