package com.csc780.pacmon;

import java.util.ArrayList;
import java.util.Random;


// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster) as well as call drawing.
 * 		
 */

public class GameEngine {
	static final int UP = 0, DOWN = 1, RIGHT = 3, LEFT = 4;
	
	private Maze maze;
	Pacmon pacmon;
	ArrayList<Monster> ghosts;
	
	int inputDirection;
	int pX, pY;
	int newDirection;
	
	int directionMaze[][];
	int mazeArray[][];
	int blockSize = 32;
	int mazeRow, mazeColumn;
	
	public GameEngine(){
		pacmon = new Pacmon();  // new pacmon
		
		ghosts = new ArrayList<Monster>();
		
		ghosts.add(new Monster());
		ghosts.add(new Monster());
		
		// maze stuff
		Maze maze = new Maze();
		mazeArray = maze.getMaze();
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();
		directionMaze = maze.getDirectionMaze();
	}
	
	//update
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
		} else {  // change opposite direction
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
			eatFoodPower(boxX, boxY);
			
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
		int gNormalSpeed = ghosts.get(0).getNormalSpeed();
		int XmodW, YmodH;
		int boxX, boxY;
		int gX, gY;
		
		
		for (int i = 0; i < ghosts.size(); i++) {
			gX = ghosts.get(i).getX();
			gY = ghosts.get(i).getY();
			XmodW = gX % blockSize;
			YmodH = gY % blockSize;
			boolean movable = true;

			int ghostNewDir = ghosts.get(i).getDir();
			int randDirection = ghostNewDir;
			
			// check direction and change if it is allowed
			if (XmodW == 0 && YmodH == 0) {
				randDirection = ((int)Math.random()*4) + 1;
				boxX = gX / blockSize;
				boxY = gY / blockSize;

				if (randDirection == 4) { // move left allowed if can move to left
					if (boxX > 0)
						if (mazeArray[boxY][boxX - 1] != 0)
							ghostNewDir = randDirection;
				}
				if (randDirection == 3) { // move right
					if (boxX < mazeColumn)
						if (mazeArray[boxY][boxX + 1] != 0)
							ghostNewDir = randDirection;
				}
				if (randDirection == 2) { // move down
					if (boxY < mazeRow)
						if (mazeArray[boxY + 1][boxX] != 0)
							ghostNewDir = randDirection;
				}
				if (randDirection == 1) { // move up
					if (boxY > 0)
						if (mazeArray[boxY - 1][boxX] != 0)
							ghostNewDir = randDirection;
				}
			} else {
				if (newDirection != randDirection) {
					if (((randDirection == 1) || (randDirection == 2))
							&& (XmodW == 0) && (YmodH != 0)) {
						ghostNewDir = randDirection;
					}
					if (((randDirection == 3) || (randDirection == 4))
							&& (YmodH == 0) && (XmodW != 0)) {
						ghostNewDir = randDirection;
					}
				}
			}

			ghosts.get(i).setDir(ghostNewDir);

			// evaluate at intersection, collision detection
			if (XmodW == 0 && YmodH == 0) {
				boxX = gX / blockSize;
				boxY = gY / blockSize;

				movable = true;

				if (ghostNewDir == 4) { // move left
					if (boxX > 0)
						if (mazeArray[boxY][boxX - 1] == 0) {
							movable = false;
						}
				}

				if (ghostNewDir == 3) { // move right
					if (boxX < mazeColumn - 1)
						if (mazeArray[boxY][boxX + 1] == 0) {
							movable = false;
						}
				}

				if (ghostNewDir == 2) { // move down
					if (boxY < mazeRow - 1)
						if (mazeArray[boxY + 1][boxX] == 0) {
							movable = false;
						}
				}
				if (ghostNewDir == 1) { // move up
					if (boxY > 0)
						if (mazeArray[boxY - 1][boxX] == 0) {
							movable = false;
						}
				}

			}

			if (movable) {
				if (ghostNewDir == 1) // up
					gY = gY - gNormalSpeed;
				if (ghostNewDir == 2) // down
					gY = gY + gNormalSpeed;
				if (ghostNewDir == 3) // right
					gX = gX + gNormalSpeed;
				if (ghostNewDir == 4) // left
					gX = gX - gNormalSpeed;
			}

			ghosts.get(i).setX(gX);
			ghosts.get(i).setY(gY);

		}
	}
	
	private void eatFoodPower(int boxX, int boxY) {
		if (mazeArray[boxY][boxX] == 1){
			System.out.println(boxX + " boxY is " + boxY);
			mazeArray[boxY][boxX] = 5;
			//maze.clearFood(boxX, boxY);
		}
		
		if (mazeArray[boxY][boxX] == 2){
			mazeArray[boxY][boxX] = 5;
		}
	}
	
	
	// using accelerometer to set direction of player
	public void setInputDir(int dir){
		this.inputDirection = dir;
	}
	
	public Maze getMaze(){
		return this.maze;
	}
	
	public int[][] getMazeArray(){
		return this.mazeArray;
	}

	public int getMazeRow() {
		return this.mazeRow;
	}

	public int getMazeColumn() {
		return this.mazeColumn;
	}
	
}
