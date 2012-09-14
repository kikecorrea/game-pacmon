package com.csc780.multipacmon;

import java.util.ArrayList;

import com.csc780.pacmon.Monster;
import com.csc780.pacmon.Pacmon;
import com.csc780.pacmon.R;
import com.csc780.pacmon.SoundEngine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class handles the rendering of screen
 *
 */
public class MGameSurfaceView extends SurfaceView implements Runnable {

	private final static int    MAX_FPS =50;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	private final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, SEARCHING=5, DISCONNECTED=6, DIE=7;
	private final static String textOver = "GAME OVER", textCongrats = "Congratulations"
			, textNextLevel = "You unlocked next level", textReady = "Ready! Go";
	
	
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
	private Bitmap pac_img2, pac_img, wall, door, bluey_img, redy_img, yellowy_img, violet_img, food, power ;
	
	//maze info
	private int[][] mazeArray;
	private int mazeRow, mazeColumn;
	private float blockSize;
	
	private Paint paint, paint2, paint3;
	
	private Context mContext;
	
	private float screenWidth;
	private float screenHeight;
	private float blockScaleFactor;
	private float sentenceWidth, drawTextStartingX;
	private String drawS;
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped
	
	private int countForSearching=0;
	
	private SoundEngine soundEngine; // sound manager
	private boolean isPlayOn;
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
	private Rect srcRect;
	private Rect dstRect;
	
	//variable for drawing score
	private int p1score, p2score, p1life, p2life, timer;
	
	//variable use for testing change of game state
	int soundState;
	

	public MGameSurfaceView(Context context, MGameEngine gameEngine, int sWidth, int sHeight) {
		super(context);
		this.pacmon = gameEngine.pacmon;
		this.pacmon2 = gameEngine.pacmon2;
		this.mgameEngine = gameEngine;
		
		//gameState = READY;  //never used, this was use in single player
		mContext = context;
		
		soundEngine = gameEngine.soundEngine;
		isPlayOn = true;
		
		screenWidth = sWidth;
		screenHeight = sHeight;
		
		blockSize = screenWidth / 15.f;  // size of block
		blockScaleFactor = blockSize / 32.f;  // scale factor for block
		
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
		pac_img = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite_green);
		pac_img2 = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite_orange);
		bluey_img = BitmapFactory.decodeResource(getResources(), R.drawable.bluey_sprite);
		redy_img = BitmapFactory.decodeResource(getResources(), R.drawable.redy_sprite);
		yellowy_img = BitmapFactory.decodeResource(getResources(), R.drawable.yellowy_sprite);
		violet_img = BitmapFactory.decodeResource(getResources(), R.drawable.violet_sprite);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int)(blockSize * .6));  // make smaller than 1.5 size of block
		
		paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
		paint2.setTextSize(blockSize*2); // 2 times the size of block width
		
		paint3 = new Paint();
		paint3.setAntiAlias(true);
		paint3.setColor(Color.WHITE);
		paint3.setTextSize((int) (blockSize * 1.5));
	
	}
	private void initSprite(){
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
		pSrcLeft[2] = new Rect(64, 96, 96, 128);
		
		gSrcUp[0] = new Rect(0, 0, 32, 32);
		gSrcUp[1] = new Rect(32, 0, 64, 32);
		
		gSrcDown[0] = new Rect(0, 32, 32, 64);
		gSrcDown[1] = new Rect(32, 32, 64, 64);
		
		gSrcRight[0] = new Rect(0, 64, 32, 96);
		gSrcRight[1] = new Rect(32, 64, 64, 96);
		
		gSrcLeft[0] = new Rect(0, 96, 32, 128);
		gSrcLeft[1] = new Rect(32, 96, 64, 128);
		
	}
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;
		int i=0;

		while (isRunning) {
			canvas = null;
			
			if(soundState != mgameEngine.getGameState())
			{
				isPlayOn=true;
				soundState = mgameEngine.getGameState();
			}
			
			if (mgameEngine.getGameState() == READY){
				updateReady(canvas);
				if (isPlayOn){
					//soundEngine.play(4);
					soundEngine.playMusic();
					isPlayOn=false;
				}				
			}
			if (mgameEngine.getGameState() == RUNNING)  {
				updateRunning(canvas);
				if (isPlayOn){
					soundEngine.playMusic();
					isPlayOn=false;
				}
	
			}
			if (mgameEngine.getGameState() == GAMEOVER) {
				soundEngine.stopMusic();
				soundEngine.playGameOver();
				updateGameOver(canvas);
			}
			if (mgameEngine.getGameState() == WON)	   updateWon(canvas);
			if (mgameEngine.getGameState() == DISCONNECTED)	   updateDisconnected(canvas);
			if (mgameEngine.getGameState() == DIE)	
			{
				updateDie(canvas);
				if (isPlayOn){
					soundEngine.stopMusic();
					soundEngine.playDie();
					isPlayOn=false;
				}
				
			}
		
		}
	}
	
//	private void updateSearching(Canvas canvas)
//	{
//		try {
//			canvas = surfaceHolder.lockCanvas();
//			if (canvas == null) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				surfaceHolder = getHolder();
//			} else {
//
//				synchronized (surfaceHolder) {
//					canvas.drawRGB(0, 0, 0);
//
// 					if(mgameEngine.clientDiscoverer.isFinish)
//					{   
// 						//flag for progress dialog to finish
// 						mgameEngine.serverReady.set(true);
// 						
//					   //canvas.drawText("found server", 45, 350, paint2);
//					   if(countForSearching > 50)
//					   {
//						   mgameEngine.callDispatcher();   
//						   mgameEngine.setGameState(READY);
//					   }   
//					   //for sleeping this thread. so we can see found server for a couple of second
//					   countForSearching++;
//					}
// 		
//					try {
//						Thread.sleep(25);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}	
//				}
//			}
//		} finally {
//			// in case of an exception the surface is not left in
//			// an inconsistent state
//			if (canvas != null) {
//				surfaceHolder.unlockCanvasAndPost(canvas);
//			}
//		}	
//	}
	

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
					
					//mgameEngine.updateDataFromServer();
					
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					drawPacmon(canvas);
					drawPacmon2(canvas);
					drawGhost(canvas);
					drawScore(canvas);
					
					try {
						Thread.sleep(10);
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
					drawScore(canvas);
					
 					sentenceWidth = paint2.measureText("Get ready" );
 				    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
 					canvas.drawText("Get ready", drawTextStartingX, screenHeight/2, paint2);
 					
 					if(mgameEngine.receiver.status==RUNNING)
					{  
 						mgameEngine.setGameState(RUNNING); }
//					try {
//						Thread.sleep(25);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}	
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
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0; // resetting the frames skipped

					//mgameEngine.updateDataFromServer();
					canvas.drawRGB(0, 0, 0);
					
					//check the gamestate from receiver
					//mgameEngine.setGameStateFromServer();
					
//					mgameEngine.eatFoodPower();
//					mgameEngine.eatFoodPower2();
					
					drawMaze(canvas); // draw updated maze

					drawPacmon(canvas); // draw Pacman
					drawPacmon2(canvas); // draw Pacman

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
	
	private void updateGameOver(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		
		drawScore(canvas); // draw score and lives
//		String [] scores=mgameEngine.getScores();
		
		String whoWon="";
		String whoDied="";
		if(mgameEngine.receiver.p1life < mgameEngine.receiver.p2life){
			whoWon="You won";
			whoDied="Enemy lost";
		}
		else{
			whoWon="You lost";
			whoDied="Enemy won";
		}
		
		//measure the text then draw it at center
		sentenceWidth = paint2.measureText(textOver);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(textOver, drawTextStartingX , screenHeight/2 - blockSize*3, paint2);
		
		sentenceWidth = paint2.measureText(whoWon);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(whoWon, drawTextStartingX, screenHeight/2, paint2);
		
		sentenceWidth = paint2.measureText(whoDied);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(whoDied, drawTextStartingX, screenHeight/2 + blockSize*3, paint2);
		
		

		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		((Activity) mContext).finish();
	}
	
	private void updateWon(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		
		drawScore(canvas); // draw score and lives
		
		String whoWon="";

		if(mgameEngine.receiver.p1score < mgameEngine.receiver.p2score){
			whoWon="You Won";
		}	
		else{
			whoWon="You Lose";
		}
		
		sentenceWidth = paint2.measureText(whoWon);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(whoWon, drawTextStartingX , screenHeight/2 - blockSize*3, paint2);
		
		sentenceWidth = paint3.measureText("your score:" + mgameEngine.receiver.p2score);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText("your score:" + mgameEngine.receiver.p2score, drawTextStartingX , screenHeight/2, paint3);
		
		sentenceWidth = paint3.measureText("enemy's score:" + mgameEngine.receiver.p1score);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText("enemy's score:" + mgameEngine.receiver.p1score, drawTextStartingX , screenHeight/2 + blockSize*2, paint3);
		


		
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		((Activity) mContext).finish();
	}
	
	private void updateDisconnected(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		sentenceWidth = paint3.measureText("Connection Error");
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText("Connection Error", drawTextStartingX, screenHeight/2, paint3);
	
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		((Activity) mContext).finish();
	}

	// draw current location of ghosts
	private void drawGhost(Canvas canvas) {
		
		mCurrentFrame = ++mCurrentFrame % 2;
		for (int i = 0; i < mgameEngine.ghosts.size(); i++) {
			int direction = ghosts.get(i).getDir();
			if (direction == UP)	srcRect = gSrcUp[mCurrentFrame];
			else if (direction == DOWN)		srcRect = gSrcDown[mCurrentFrame];
			else if (direction == RIGHT)		srcRect = gSrcRight[mCurrentFrame];
			else 	srcRect = gSrcLeft[mCurrentFrame];	
			
			int gX = Math.round(ghosts.get(i).getX() * blockScaleFactor);
			int gY = Math.round(ghosts.get(i).getY() * blockScaleFactor);
			
			dstRect = new Rect(gX, gY, (int)(gX + blockSize), (int) (gY + blockSize));
			
			if (i == 3)
				canvas.drawBitmap(violet_img, srcRect, dstRect, null);
			else if (i == 1)
				canvas.drawBitmap(redy_img, srcRect, dstRect, null);
			else if (i == 2)
				canvas.drawBitmap(yellowy_img, srcRect, dstRect, null);
			else if ( i==0 )
				canvas.drawBitmap(bluey_img, srcRect, dstRect, null);
		}
	}

	// draw pacmon 
	private void drawPacmon(Canvas canvas) {
//		currentFrame = ++currentFrame % 3;
//		int n;
//		int direction = pacmon.getDir(); // get current direction of pacmon
//		
//		if (direction == UP)	n = 0;
//		else if (direction == DOWN)		n = 1;
//		else if (direction == RIGHT)		n = 2;
//		else 		n = 3;
//		
//		mgameEngine.setxyp1();
//	
//		int srcY = n * blockSize;
//		int srcX = currentFrame * blockSize;
//		int pX = pacmon.getpX();
//		int pY = pacmon.getpY();
//
//		Rect src = new Rect(srcX, srcY, srcX + blockSize, srcY + blockSize);
//		Rect dst = new Rect(pX, pY, pX + blockSize , pY + blockSize);
//		canvas.drawBitmap(pac_img, src, dst, null);
		
		currentFrame = ++currentFrame % 3;
		
		int direction = pacmon.getDir(); // get current direction of pacmon
		
		if (direction == UP)	srcRect = pSrcUp[currentFrame];
		else if (direction == DOWN)		srcRect = pSrcDown[currentFrame];
		else if (direction == RIGHT)		srcRect = pSrcRight[currentFrame];
		else 	srcRect = pSrcLeft[currentFrame];	
	
		int pX = Math.round(pacmon.getpX() * blockScaleFactor);
		int pY = Math.round(pacmon.getpY() * blockScaleFactor);

		dstRect = new Rect(pX, pY, (int)(pX + blockSize), (int) (pY + blockSize));
		canvas.drawBitmap(pac_img, srcRect, dstRect, null);
		
	}
	private void drawPacmon2(Canvas canvas) {
//		currentFrame = ++currentFrame % 3;
//		int n;
//		int direction = pacmon2.getDir(); // get current direction of pacmon
//		
//		if (direction == UP)	n = 0;
//		else if (direction == DOWN)		n = 1;
//		else if (direction == RIGHT)		n = 2;
//		else 		n = 3;
//		
//		mgameEngine.setxyp2();
//		
//		int srcY = n * blockSize;
//		int srcX = currentFrame * blockSize;
//		int pX = pacmon2.getpX();
//		int pY = pacmon2.getpY();
//		
//		Rect src = new Rect(srcX, srcY, srcX + blockSize, srcY + blockSize);
//		Rect dst = new Rect(pX, pY, pX + blockSize , pY + blockSize);
//		canvas.drawBitmap(pac_img2, src, dst, null);
		currentFrame = ++currentFrame % 3;
		
		int direction = pacmon2.getDir(); // get current direction of pacmon
		
		if (direction == UP)	srcRect = pSrcUp[currentFrame];
		else if (direction == DOWN)		srcRect = pSrcDown[currentFrame];
		else if (direction == RIGHT)		srcRect = pSrcRight[currentFrame];
		else 	srcRect = pSrcLeft[currentFrame];	
	
		int pX = Math.round(pacmon2.getpX() * blockScaleFactor);
		int pY = Math.round(pacmon2.getpY() * blockScaleFactor);

		dstRect = new Rect(pX, pY, (int)(pX + blockSize), (int) (pY + blockSize));
		canvas.drawBitmap(pac_img2, srcRect, dstRect, null);
		
	}
	
	// draw score
	public void drawScore(Canvas canvas){
//		String x[]=mgameEngine.getScores();
//		String  lives[]=mgameEngine.checkLives();
		drawS = "Your score        :" + mgameEngine.receiver.p2score + "   Lives:" + mgameEngine.lives2;
		canvas.drawText(drawS, blockSize, blockSize*23 + 1, paint);
		drawS = "Enemy's score  :" + mgameEngine.receiver.p1score + "   Lives:" + mgameEngine.lives;
		canvas.drawText(drawS, blockSize, blockSize*24 + 5, paint);
		canvas.drawText("Time: "+String.valueOf(mgameEngine.receiver.timer), blockSize*9+5, blockSize*23 + 1, paint);
			
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


