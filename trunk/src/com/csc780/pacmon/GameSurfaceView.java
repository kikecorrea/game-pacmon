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

	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	
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
	private Bitmap ball, pac_img, wall, door, ghost, bluey_img, redy_img ; // bitmap 
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
		maze = new Maze();
		mazeArray = maze.getMaze();
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();

		
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
			int srcY = (direction - 1) * blockSize;
			int srcX = mCurrentFrame * blockSize;
			int gX = ghosts.get(i).getX();
			int gY = ghosts.get(i).getY();
			Rect src = new Rect(srcX, srcY, srcX + blockSize, srcY + blockSize);
			Rect dst = new Rect(gX, gY, gX + blockSize, gY + blockSize);
			canvas.drawBitmap(bluey_img, src, dst, null);
		}
	}

	// draw pacmon 
	private void drawPacmon(Canvas canvas, int dir) {
		currentFrame = ++currentFrame % 3;
		int direction = pacmon.getDir(); // get current direction of pacmon
		int srcY = (direction-1) * blockSize;
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
	
	// draw current maze
	public void drawMaze(Canvas canvas){
		for (int i = 0; i < mazeRow; i++){
			for (int j = 0; j < mazeColumn; j++){
				if (mazeArray[i][j] == 0)
					canvas.drawBitmap(wall, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 3)
					canvas.drawBitmap(door, j*blockSize, i*blockSize, null);
			}
		}
	}
	
	public void setDir(int dir){
		this.direction = dir;
	}

}













/*
//check direction
if(XmodW == 0 && YmodH == 0){
	boxX = pX / blockSize;
	boxY = pY / blockSize;

	if (direction == 4){  // move left
        if (boxX > 0 )
            if ( mazeArray[boxY][boxX - 1] == 0)
                newDirection = direction;
	}
	
	if (direction == 3){   // move right
        if (boxX < mazeColumn )
            if ( mazeArray[boxY][boxX + 1] == 0) 
                newDirection = direction;

	}	
	
	if (direction == 2){ // move down
		if (boxY < mazeRow)
			if (mazeArray[boxY + 1][boxX] == 0)
				newDirection = direction;
	}
	if (direction == 1) { // move up
        if (boxY > 0 )
            if (mazeArray[boxY - 1][boxX] == 0)
                newDirection = direction;
	}
} else {
	if (newDirection != direction){
        if (((direction == 1) || (direction == 2)) && (XmodW!=0) && (YmodH==0)){
            newDirection = direction;
        }
        if (((direction == 3) || (direction == 4)) && (YmodH!=0) && (XmodW==0) ){
            newDirection = direction;
        }
    }
}













			if (oldDirection == 1 || oldDirection == 2){
				if (newDirection == 1) // up		
					pY = pY - pNormalSpeed;
				if (newDirection == 2) // down
					pY = pY + pNormalSpeed;
			} else {
				if (oldDirection == 1) // up		
					pY = pY - pNormalSpeed;
				if (oldDirection == 2) // down
					pY = pY + pNormalSpeed;
			}
			
			if (oldDirection == 3 || oldDirection == 4){
				if (newDirection == 3) // right
					pX = pX + pNormalSpeed;
				if (newDirection == 4) // left
					pX = pX - pNormalSpeed;
			} else {
				if (oldDirection == 3) // right
					pX = pX + pNormalSpeed;
				if (oldDirection == 4) // left
					pX = pX - pNormalSpeed;
			}
			
			
			
			
			
			
			
			
					if(XmodW == 0 && YmodH == 0){
			oldDirection = direction;
			boxX = pX / blockSize;
			boxY = pY / blockSize;
		
			if (newDirection == 4){  // move left
                if (boxX > 0 ) {
                    if ( mazeArray[boxY][boxX - 1] == 0){
                        canMove = false;
                    }
                } else 
                    pX = 0;
			}
			
			if (newDirection == 3){   // move right
                if (boxX < mazeColumn -1 ) {
                    if ( mazeArray[boxY][boxX + 1] == 0) {
                        canMove = false;
                    }
                } else 
                    pX = (mazeColumn - 1) * blockSize;
			}	
			
			if (newDirection == 2){ // move down
				if (boxY < mazeRow - 1){
					if (mazeArray[boxY + 1][boxX] == 0){
						canMove = false;
					}
				}
				else {
					pY = 0;
				}
			}
			if (newDirection == 1) { // move up
                if (boxY > 0 ) {
					if (mazeArray[boxY - 1][boxX] == 0) {
						canMove = false;
					}
				} else
					pY = (mazeRow - 1) * blockSize;
			}

		}

		if (canMove) {
			oldX = pX;
			oldY = pY;

				if (oldDirection == 1) // up		
					pY = pY - pNormalSpeed;
				if (oldDirection == 2) // down
					pY = pY + pNormalSpeed;

				if (oldDirection == 3) // right
					pX = pX + pNormalSpeed;
				if (oldDirection == 4) // left
					pX = pX - pNormalSpeed;
			
			
*/


