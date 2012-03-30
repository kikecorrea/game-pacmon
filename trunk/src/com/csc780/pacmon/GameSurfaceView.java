package com.csc780.pacmon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Runnable {

	private final static int    MAX_FPS = 40;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	private final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3;
	
	
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	int mCurrentFrame = 0;
	int movingTextX, movingTextY;   // for ready and gameover screen
	
	private Pacmon pacmon;
	private int direction;

	private GameEngine gameEngine;
	private ArrayList<Monster> ghosts;
	
	// bitmap
	private Bitmap pac_img, wall, door, bluey_img, redy_img, yellowy_img, food, power ;
	
	//maze info
	private int[][] mazeArray;
	private int mazeRow, mazeColumn;
	private int blockSize;
	
	private Paint paint, paint2;
	
	private Context mContext;
	
	private int gameState;
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped

	
	public GameSurfaceView(Context context, Pacmon pacmon, GameEngine gameEngine) {
		super(context);
		this.pacmon = pacmon;
		this.gameEngine = gameEngine;
		
		gameState = READY;
		
		mContext = context;
		
		blockSize = 32;  // size of block
		
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
		wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		door = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_door);
		food = BitmapFactory.decodeResource(getResources(), R.drawable.food);
		power = BitmapFactory.decodeResource(getResources(), R.drawable.power);
		pac_img = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite);
		bluey_img = BitmapFactory.decodeResource(getResources(), R.drawable.bluey_sprite);
		redy_img = BitmapFactory.decodeResource(getResources(), R.drawable.redy_sprite);
		yellowy_img = BitmapFactory.decodeResource(getResources(), R.drawable.yellowy_sprite);
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(24);
		
		paint2 = new Paint();
		paint2.setColor(Color.WHITE);
		paint2.setTextSize(50);
		
	}
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;

		while (isRunning) {
			canvas = null;
			if (gameEngine.getGameState() == READY)    updateReady(canvas);
			if (gameEngine.getGameState() == RUNNING)  updateRunning(canvas);
			if (gameEngine.getGameState() == GAMEOVER) updateGameOver(canvas);
			if (gameEngine.getGameState() == WON)	   updateWon(canvas);
			
		}
	}
	
	// when game is in ready mode
	private void updateReady(Canvas canvas){
		//beginTime = System.currentTimeMillis(); // temporary for testing 1 thread
		
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					
					//long time = 5L - timeDiff/1000;
					canvas.drawText("Getting Ready in ", 50, 350, paint2);	
					
					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
	
	private void updateRunning(Canvas canvas){
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0; // resetting the frames skipped

					//gameEngine.update();
						
					canvas.drawRGB(0, 0, 0);

					drawMaze(canvas); // draw updated maze

					drawPacmon(canvas); // draw Pacman

					drawGhost(canvas); // draw ghosts
					
					drawScore(canvas); // draw score and lives

					//if (gameEngine.getGameState() == GAMEOVER)  gameState = GAMEOVER;
					//if (gameEngine.getGameState() == WON)		gameState = WON;
					
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
						//gameEngine.update();
						// add frame period to check if in next frame
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
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
	
	private void updateGameOver(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		canvas.drawText("GAME OVER", 130, 350, paint2);
		canvas.drawText(gameEngine.getPlayerScore(), 150, 420, paint2);
		
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		((Activity) mContext).finish();
	}
	
	private void updateWon(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		canvas.drawText("Congratulations!", 130, 350, paint2);
		canvas.drawText("You won", 160, 400, paint2);
		canvas.drawText(gameEngine.getPlayerScore(), 150, 450, paint2);
		
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		((Activity) mContext).finish();
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
			else if (i == 2)
				canvas.drawBitmap(yellowy_img, src, dst, null);
		}
	}

	// draw pacmon 
	private void drawPacmon(Canvas canvas) {
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
	
	// draw score
	public void drawScore(Canvas canvas){
		canvas.drawText(gameEngine.getPlayerScore(), 20, 736, paint);
		canvas.drawText(gameEngine.getLives(), 150, 736, paint);
		canvas.drawText(gameEngine.getTimer(), 350, 736, paint);
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


