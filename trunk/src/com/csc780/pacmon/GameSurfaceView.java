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

//deals with rendering the game data
public class GameSurfaceView extends SurfaceView implements Runnable {

	private final static int    MAX_FPS = 60;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	private final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4;
	
	
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	int mCurrentFrame = 0;
	int movingTextX, movingTextY;   // for ready and gameover screen
	
	private Pacmon pacmon;
	
	
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
	private Rect srcRect;
	private Rect dstRect;
	private Rect[] pSrcUp = new Rect[3];
	private Rect[] pSrcDown = new Rect[3];
	private Rect[] pSrcLeft = new Rect[3];
	private Rect[] pSrcRight = new Rect[3];
	private Rect[] pDst = new Rect[12];
	
	private Rect[] gSrcUp = new Rect[2];
	private Rect[] gSrcDown = new Rect[2];
	private Rect[] gSrcLeft = new Rect[2];
	private Rect[] gSrcRight = new Rect[2];
	private Rect[] gDst = new Rect[8];
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped

	private SoundEngine soundEngine; // sound manager
	private boolean isPlayOn;
	
	public GameSurfaceView(Context context, Pacmon pacmon, GameEngine gameEngine) {
		super(context);
		this.pacmon = pacmon;
		this.gameEngine = gameEngine;
		
		gameState = READY;
		
		mContext = context;
		
		soundEngine = new SoundEngine(context);
		isPlayOn = true;
		
		blockSize = 32;  // size of block
		
		mazeArray = gameEngine.getMazeArray();
		mazeRow = gameEngine.getMazeRow();
		mazeColumn = gameEngine.getMazeColumn();

		
		initBitmap();  // init all Bitmap and its components
		initSprite();
		
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
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(24);
		
		paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
		paint2.setTextSize(50);
		
	}
	
	private void initSprite(){
		int offset = 32;
		pSrcUp[0] = new Rect(0, 0, 32, 32);
		pSrcUp[1] = new Rect(32, 0, 64, 32);
		pSrcUp[2] = new Rect(64, 0, 96, 32);
		
		pSrcDown[0] = new Rect(0, 32, 32, 64);
		pSrcDown[1] = new Rect(32, 32, 64, 64);
		pSrcDown[2] = new Rect(64, 32, 96, 64);
		
		pSrcRight[0] = new Rect(0, 64, 32, 96);
		pSrcRight[1] = new Rect(32, 64, 64, 96);
		pSrcRight[2] = new Rect(64, 64, 96, 96);
		
		pSrcLeft[0] = new Rect(0, 96, 32, 128);
		pSrcLeft[1] = new Rect(32, 96, 64, 128);
		
		gSrcUp[0] = new Rect(0, 0, 32, 32);
		gSrcUp[1] = new Rect(32, 0, 64, 32);
		
		gSrcDown[0] = new Rect(0, 32, 32, 64);
		gSrcDown[1] = new Rect(32, 32, 64, 64);
		
		gSrcRight[0] = new Rect(0, 64, 32, 96);
		gSrcRight[1] = new Rect(32, 64, 64, 96);;
		
		gSrcLeft[0] = new Rect(0, 96, 32, 128);
		gSrcLeft[1] = new Rect(32, 96, 64, 128);
		
	}
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;
		
		while (isRunning) {
			canvas = null;
			if (gameEngine.getGameState() == READY){
				if (isPlayOn){
					soundEngine.play(4);
					isPlayOn = false;
				}
				updateReady(canvas);

			}
			if (gameEngine.getGameState() == RUNNING)  updateRunning(canvas);
			if (gameEngine.getGameState() == GAMEOVER) updateGameOver(canvas);
			if (gameEngine.getGameState() == WON)	   updateWon(canvas);
			if (gameEngine.getGameState() == DIE)	   updateDie(canvas);
		}
	}
	
	// when game is in ready mode
	private void updateReady(Canvas canvas){
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
					drawPacmon(canvas);
					drawGhost(canvas);
					drawScore(canvas);
					
					//long time = 5L - timeDiff/1000;
					canvas.drawText("Getting Ready in " + gameEngine.getReadyCountDown(),
									45, 350, paint2);	
					
					try {
						Thread.sleep(25);
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
				
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					drawPacmon(canvas); // draw Pacman
					drawGhost(canvas); // draw ghosts
					drawScore(canvas); // draw score and lives
					
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
	
	private void updateDie(Canvas canvas){
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
					drawPacmon(canvas);
					drawGhost(canvas);
					drawScore(canvas);
					
					try {
						Thread.sleep(25);
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
	
	private void updateGameOver(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		canvas.drawText("GAME OVER", 125, 350, paint2);
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
		canvas.drawText("Congratulations!", 70, 350, paint2);
		canvas.drawText("You won", 130, 400, paint2);
		canvas.drawText(gameEngine.getPlayerScore(), 130, 450, paint2);
		
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
			if (direction == UP)	srcRect = gSrcUp[currentFrame];
			else if (direction == DOWN)		srcRect = gSrcDown[currentFrame];
			else if (direction == RIGHT)		srcRect = gSrcRight[currentFrame];
			else 	srcRect = gSrcRight[currentFrame];	
			
			int gX = ghosts.get(i).getX();
			int gY = ghosts.get(i).getY();
			Rect dst = new Rect(gX, gY, gX + blockSize, gY + blockSize);
			
			if (i == 0)
				canvas.drawBitmap(bluey_img, srcRect, dst, null);
			else if (i == 1)
				canvas.drawBitmap(redy_img, srcRect, dst, null);
			else if (i == 2)
				canvas.drawBitmap(yellowy_img, srcRect, dst, null);
		}
	}

	// draw pacmon 
	private void drawPacmon(Canvas canvas) {
		currentFrame = ++currentFrame % 3;
		
		int direction = pacmon.getDir(); // get current direction of pacmon
		
		if (direction == UP)	srcRect = pSrcUp[currentFrame];
		else if (direction == DOWN)		srcRect = pSrcDown[currentFrame];
		else if (direction == RIGHT)		srcRect = pSrcRight[currentFrame];
		else 	srcRect = pSrcRight[currentFrame];	
		
		int pX = pacmon.getpX();
		int pY = pacmon.getpY();
		Rect dst = new Rect(pX, pY, pX + blockSize , pY + blockSize);
		canvas.drawBitmap(pac_img, srcRect, dst, null);
		
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
}


