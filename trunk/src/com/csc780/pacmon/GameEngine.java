package com.csc780.pacmon;

import java.util.ArrayList;


// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster) as well as call drawing.
 * 		
 */

public class GameEngine {

	Maze maze;
	Pacmon pacmon;
	ArrayList<Monster> ghosts;
	
	int inputDirection;
	int pX, pY;
	int newDirection;
	int mazeArray[][];
	int blockSize = 32;
	int mazeRow, mazeColumn;
	
	public GameEngine(){
		pacmon = new Pacmon();  // new pacmon
		
		ghosts = new ArrayList<Monster>();
		
		// maze stuff
		Maze maze = new Maze();
		mazeArray = maze.getMaze();
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();
	}
	
	public void update(){
		updatePac();
		updateGhost();
	}
	
	public void updatePac(){
		int pNormalSpeed = pacmon.getpNormalSpeed();
		int XmodW, YmodH;
		int boxX, boxY;
		pX = pacmon.getpX();
		pY = pacmon.getpY();
		XmodW = pX % blockSize;
		YmodH = pY % blockSize;
		boolean movable = true;
		
		// check direction and change if it is allowed
		if(XmodW == 0 && YmodH == 0){
			boxX = pX / blockSize;
			boxY = pY / blockSize;

			if (inputDirection == 4){  // move left allowed if can move to left
		        if (boxX > 0 )
		            if ( mazeArray[boxY][boxX - 1] != 0)
		                newDirection = inputDirection;
			}
			if (inputDirection == 3){   // move right
		        if (boxX < mazeColumn )
		            if (mazeArray[boxY][boxX + 1] != 0) 
		                newDirection = inputDirection;
			}	
			if (inputDirection == 2){ // move down
				if (boxY < mazeRow)
					if (mazeArray[boxY + 1][boxX] != 0 && mazeArray[boxY + 1][boxX] != 3)
						newDirection = inputDirection;
			}
			if (inputDirection == 1) { // move up
		        if (boxY > 0 )
		            if (mazeArray[boxY - 1][boxX] != 0 && mazeArray[boxY - 1][boxX] != 3)
		                newDirection = inputDirection;
			}
		} else {
			if (newDirection != inputDirection){
		        if (((inputDirection == 1) || (inputDirection == 2)) && (XmodW==0) && (YmodH!=0)){
		            newDirection = inputDirection;
		        }
		        if (((inputDirection == 3) || (inputDirection == 4)) && (YmodH==0) && (XmodW!=0) ){
		            newDirection = inputDirection;
		        }
		    }
		}

		pacmon.setDir(newDirection);
		
		//evaluate at intersection, collision detection
		if(XmodW == 0 && YmodH == 0){
			boxX = pX / blockSize;
			boxY = pY / blockSize;
		
			movable = true;
			
			if (newDirection == 4){  // move left
                if (boxX > 0 )
                    if ( mazeArray[boxY][boxX - 1] == 0){
                        movable = false;
                    }
			}
			
			if (newDirection == 3){   // move right
                if (boxX < mazeColumn -1 ) 
                    if ( mazeArray[boxY][boxX + 1] == 0) {
                        movable = false;
                    }
			}	
			
			if (newDirection == 2){ // move down
				if (boxY < mazeRow - 1)
					if (mazeArray[boxY + 1][boxX] == 0 || mazeArray[boxY + 1][boxX] == 3){
						movable = false;
				}
			}
			if (newDirection == 1) { // move up
                if (boxY > 0 ) 
					if (mazeArray[boxY - 1][boxX] == 0 || mazeArray[boxY - 1][boxX] == 3){
						movable = false;
					}
			}

		}
		
		if( movable){
			if (newDirection == 1) // up
				pY = pY - pNormalSpeed;
			if (newDirection == 2) // down
				pY = pY + pNormalSpeed;
			if (newDirection == 3 ) // right
				pX = pX + pNormalSpeed;
			if (newDirection == 4) // left
				pX = pX - pNormalSpeed;
		}
		
		pacmon.setpX(pX);
		pacmon.setpY(pY);
		
	}
	
	//update ghost movements and locations
	public void updateGhost(){
		
	}
	
	// using accelerometer to set direction of player
	public void setInputDir(int dir){
		this.inputDirection = dir;
	}
	
}
