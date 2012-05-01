package com.csc780.multipacmon;

import java.util.ArrayList;

import com.csc780.pacmon.Monster;
import com.csc780.pacmon.Pacmon;
import com.csc780.pacmon.R;
import com.csc780.pacmon.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class MGameSurfaceView extends SurfaceView implements Runnable {

	private final static int    MAX_FPS = 40;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	private final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4, SEARCHING=5;
	
	
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	int mCurrentFrame = 0;
	int movingTextX, movingTextY;   // for ready and gameover screen
	
	private Pacmon pacmon, pacmon2;
	private int direction;

	private MGameEngine mgameEngine;
	private ArrayList<Monster> ghosts;
	
	// bitmap
	private Bitmap pac_img2, pac_img, wall, door, bluey_img, redy_img, yellowy_img, food, power ;
	
	//maze info
	private int[][] mazeArray;
	private int mazeRow, mazeColumn;
	private int blockSize;
	
	private Paint paint, paint2;
	
	private Context mContext;
	
	//private int gameState;
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped
	
	private int countForSearching=0;


	
	public MGameSurfaceView(Context context, MGameEngine gameEngine, int width, int height) {
		super(context);
		this.pacmon = gameEngine.pacmon;
		this.pacmon2 = gameEngine.pacmon2;
		this.mgameEngine = gameEngine;
		
	//	gameState = READY;  //never used, this was use in single player
		
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
		pac_img = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite_green);
		pac_img2 = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite_orange);
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
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;
		int i=0;

		while (isRunning) {
			canvas = null;
			if(mgameEngine.getGameState()==SEARCHING) {updateSearching(canvas); }
			if (mgameEngine.getGameState() == READY)    updateReady(canvas);
			if (mgameEngine.getGameState() == RUNNING)  {updateRunning(canvas);}
			if (mgameEngine.getGameState() == GAMEOVER) updateGameOver(canvas);
			if (mgameEngine.getGameState() == WON)	   updateWon(canvas);
		
		}
	}
	
	private void updateSearching(Canvas canvas)
	{
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
					canvas.drawRGB(0, 0, 0);
					//drawMaze(canvas); // draw updated maze
					
					
 					
 					
 					if(mgameEngine.clientDiscoverer.isFinish)
					{  
					 
					   canvas.drawText("found server", 45, 350, paint2);
					   if(countForSearching > 30)
					   {
						   mgameEngine.callDispatcher();
						   mgameEngine.setGameState(READY);
					   }
					   
					   //for sleeping this thread. so we can see found server for a couple of second
					   countForSearching++;
						   
					}
 					else
 						canvas.drawText("searching", 45, 350, paint2);	
	
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
	
	// when game is in ready mode
	private void updateReady(Canvas canvas){
		
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					drawPacmon(canvas);
					drawPacmon2(canvas);
					drawGhost(canvas);

					//drawScore(canvas);
			
					int x=mgameEngine.getCountDown();
 					canvas.drawText("Ready in " + x, 45, 350, paint2);	
 					
 					if(x==0)
					{  mgameEngine.setGameState(RUNNING); }
	
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
		
		//mgameEngine.setGameState(RUNNING);
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

					mgameEngine.eatFoodPower();
					mgameEngine.eatFoodPower2();
					
					drawMaze(canvas); // draw updated maze

					drawPacmon(canvas); // draw Pacman
					drawPacmon2(canvas); // draw Pacman

					drawGhost(canvas); // draw ghosts
					
					mgameEngine.checkLives();
					
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
				
			//	System.out.println("finish drawing");
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
		String [] scores=mgameEngine.getScores();
		canvas.drawText(scores[0], 150, 420, paint2);
		canvas.drawText(scores[1], 150, 520, paint2);
		
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
		String [] scores=mgameEngine.getScores();
		
		canvas.drawText(scores[0], 130, 450, paint2);
		canvas.drawText(scores[1], 130, 550, paint2);
		
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
		for (int i = 0; i < mgameEngine.ghosts.size(); i++) {
			int direction = ghosts.get(i).getDir();
			int n;
			if (direction == UP)	n = 0;
			else if (direction == DOWN)		n = 1;
			else if (direction == RIGHT)		n = 2;
			else 		n = 3;
			
			int srcY = n * blockSize;
			int srcX = mCurrentFrame * blockSize;
			
			mgameEngine.setxyGhost(i);
			
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
		
		mgameEngine.setxyp1();
		
		
		int srcY = n * blockSize;
		int srcX = currentFrame * blockSize;
		int pX = pacmon.getpX();
		int pY = pacmon.getpY();
		
		//System.out.println("pxPy::" + pX + "::"+ pY);
		
		Rect src = new Rect(srcX, srcY, srcX + blockSize, srcY + blockSize);
		Rect dst = new Rect(pX, pY, pX + blockSize , pY + blockSize);
		canvas.drawBitmap(pac_img, src, dst, null);
		
	}
	private void drawPacmon2(Canvas canvas) {
		currentFrame = ++currentFrame % 3;
		int n;
		int direction = pacmon2.getDir(); // get current direction of pacmon
		
		if (direction == UP)	n = 0;
		else if (direction == DOWN)		n = 1;
		else if (direction == RIGHT)		n = 2;
		else 		n = 3;
		
		mgameEngine.setxyp2();
		
		int srcY = n * blockSize;
		int srcX = currentFrame * blockSize;
		int pX = pacmon2.getpX();
		int pY = pacmon2.getpY();
		
		Rect src = new Rect(srcX, srcY, srcX + blockSize, srcY + blockSize);
		Rect dst = new Rect(pX, pY, pX + blockSize , pY + blockSize);
		canvas.drawBitmap(pac_img2, src, dst, null);
		
	}
	
	// draw score
	public void drawScore(Canvas canvas){
		String x[]=mgameEngine.getScores();
		String  lives[]=mgameEngine.checkLives();
		canvas.drawText(x[0], 20, 736, paint);
		canvas.drawText(lives[0], 150, 736, paint);
		canvas.drawText(mgameEngine.getTimer(), 350, 749, paint);
		
		canvas.drawText(x[1], 20, 760, paint);
		canvas.drawText(lives[1], 150, 760, paint);
		
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


