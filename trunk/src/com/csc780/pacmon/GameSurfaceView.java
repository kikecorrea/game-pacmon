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
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	//pacman data
	private int pX, pY;
	private int direction;
	private int pNormalSpeed;
	private int newDirection;
	private int oldDirection;

	private Bitmap ball, wall; // bitmap 
	
	//maze info
	private int[][] mazeArray;
	private Maze maze;
	private int mazeRow, mazeColumn;
	
	private int blockSize;
	
	
	
	public GameSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		//screenWidth = 480;//this.getWidth();
		//screenHeight = 800;//this.getHeight();

		
		blockSize = 32;  // size of block
		dx = dy = 0;
		maze = new Maze();
		mazeArray = maze.getMaze();
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();
		surfaceHolder = getHolder();
		
		pX = pY = 1 * blockSize; // init Pacman
		direction = 0;
		newDirection = 1;
		pNormalSpeed = 2;
		
		ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
		wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		isRunning = true;
		setKeepScreenOn(true);
		
		
	}
	
	//thread to draw 
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(isRunning){
			if(!surfaceHolder.getSurface().isValid())
				continue;
			
			Canvas canvas = surfaceHolder.lockCanvas();

			screenWidth = canvas.getWidth();
			screenHeight = canvas.getHeight();
			canvas.drawRGB(0, 0, 0);
			
			// draw maze
			drawMaze(canvas);
			
			//draw Pacman
			drawPacman(canvas, direction);

			
			//canvas.drawCircle(ballTopX, ballTopY, screenWidth/13, null);

			
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	private void drawPacman(Canvas canvas, int direction) {
		int deltaX, deltaY;
		int XmodW, YmodH;
		int boxX, boxY;
		int oldX, oldY;
		int x, y;
		boolean canMove = true;
		
		XmodW = pX % blockSize;
		YmodH = pY % blockSize;
		
		
		newDirection = direction;

		//evaluate at intersection, collision detection
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
			
		}

		canvas.drawBitmap(ball, pX, pY, null);
		
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
				
		for (int i = 0; i < mazeRow; i++){
			for (int j = 0; j < mazeColumn; j++){
				if (mazeArray[i][j] == 0){
					
					canvas.drawBitmap(wall, j*32, i*32, null);
				}
			}
		}
	}
	

	public void setDir(int direction){
		this.direction = direction;
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
*/