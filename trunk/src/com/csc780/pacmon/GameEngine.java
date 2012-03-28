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
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	static final int RD = 9, LD = 10, RU = 5, LU = 6, RDU = 13, LDU = 14, RLD = 11, RLU = 7, RLUD = 15;
	
	private Maze maze;
	Pacmon pacmon;
	ArrayList<Monster> ghosts;
	
	int playerScore;
	float timer;
	int lives;
	
	int inputDirection;
	int pX, pY;
	int newDirection;
	
	ArrayList<Integer> ghostArray[];
	int directionMaze[][];
	int mazeArray[][];
	int blockSize = 32;
	int mazeRow, mazeColumn;
	
	public GameEngine(){
		pacmon = new Pacmon();  // new pacmon
		
		playerScore = 0;
		
		ghosts = new ArrayList<Monster>();
		
		ghosts.add(new Monster());
		ghosts.add(new Monster());
		ghosts.add(new Monster());
		
		// maze stuff
		Maze maze = new Maze();
		mazeArray = maze.getMaze();
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();
		directionMaze = maze.getDirectionMaze();
		ghostArray = maze.getGhostArray();
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

			if (inputDirection == LEFT){  // move left allowed if can move to left
		        if (boxX > 0 )
		            if ( mazeArray[boxY][boxX - 1] != 0)
		                newDirection = inputDirection;
			}
			if (inputDirection == RIGHT){   // move right
		        if (boxX < mazeColumn )
		            if (mazeArray[boxY][boxX + 1] != 0) 
		                newDirection = inputDirection;
			}	
			if (inputDirection == DOWN){ // move down
				if (boxY < mazeRow)
					if (mazeArray[boxY + 1][boxX] != 0 && mazeArray[boxY + 1][boxX] != 3)
						newDirection = inputDirection;
			}
			if (inputDirection == UP) { // move up
		        if (boxY > 0 )
		            if (mazeArray[boxY - 1][boxX] != 0 && mazeArray[boxY - 1][boxX] != 3)
		                newDirection = inputDirection;
			}
		} else {  // change opposite direction
			if (newDirection != inputDirection){
		        if (((inputDirection == UP) || (inputDirection == DOWN)) && (XmodW==0) && (YmodH!=0)){
		            newDirection = inputDirection;
		        }
		        if (((inputDirection == RIGHT) || (inputDirection == LEFT)) && (YmodH==0) && (XmodW!=0) ){
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
			
			if (newDirection == LEFT){  // move left
                if (boxX > 0 )
                    if ( mazeArray[boxY][boxX - 1] == 0){
                        movable = false;
                    }
			}
			
			if (newDirection == RIGHT){   // move right
                if (boxX < mazeColumn -1 ) 
                    if ( mazeArray[boxY][boxX + 1] == 0) {
                        movable = false;
                    }
			}	
			
			if (newDirection == DOWN){ // move down
				if (boxY < mazeRow - 1)
					if (mazeArray[boxY + 1][boxX] == 0 || mazeArray[boxY + 1][boxX] == 3){
						movable = false;
				}
			}
			if (newDirection == UP) { // move up
                if (boxY > 0 ) 
					if (mazeArray[boxY - 1][boxX] == 0 || mazeArray[boxY - 1][boxX] == 3){
						movable = false;
					}
			}

		}
		
		if( movable){
			if (newDirection == UP) // up
				pY = pY - pNormalSpeed;
			if (newDirection == DOWN) // down
				pY = pY + pNormalSpeed;
			if (newDirection == RIGHT) // right
				pX = pX + pNormalSpeed;
			if (newDirection == LEFT) // left
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
			int crossing;
			
			// check direction and change if it is allowed
			if (XmodW == 0 && YmodH == 0) {
				boxX = gX / blockSize;
				boxY = gY / blockSize;
				
				//check if at crossing using directional maze and update new direction
				crossing = directionMaze[boxY][boxX];
				if (crossing > 0){
					if (crossing == 1) moveGhost(RD, ghosts.get(i));
					if (crossing == 2) moveGhost(LD, ghosts.get(i));
					if (crossing == 3) moveGhost(RU, ghosts.get(i));
					if (crossing == 4) moveGhost(LU, ghosts.get(i));
					if (crossing == 5) moveGhost(RDU, ghosts.get(i));
					if (crossing == 6) moveGhost(LDU, ghosts.get(i));
					if (crossing == 7) moveGhost(RLD, ghosts.get(i));
					if (crossing == 8) moveGhost(RLU, ghosts.get(i));
					if (crossing == 9) moveGhost(RLUD, ghosts.get(i));

				}

			}

			//get direction after calculate
			int ghostCurDir = ghosts.get(i).getDir();
			
			if (movable) {
				if (ghostCurDir == UP) // up
					gY = gY - gNormalSpeed;
				if (ghostCurDir == DOWN) // down
					gY = gY + gNormalSpeed;
				if (ghostCurDir == RIGHT) // right
					gX = gX + gNormalSpeed;
				if (ghostCurDir == LEFT) // left
					gX = gX - gNormalSpeed;
			}
			
			// set new location of ghost after moving
			ghosts.get(i).setX(gX);
			ghosts.get(i).setY(gY);

		}
	}
	
	// move ghost using directional array
	private void moveGhost(int index, Monster ghost){
		int n = (int)(Math.random() * ghostArray[index].size()); // randomize
		int d = ghostArray[index].get(n);  //apply random to get direction
		ghost.setDir(d);
	
	}
	
	// eat food ==> score and power ==> speed
	private void eatFoodPower(int boxX, int boxY) {
		if (mazeArray[boxY][boxX] == 1){
			mazeArray[boxY][boxX] = 5;
			playerScore++;   // increase score
			//maze.clearFood(boxX, boxY);
		}
		
		if (mazeArray[boxY][boxX] == 2){
			mazeArray[boxY][boxX] = 5; // blank
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

	public float getTimer() {
		return timer;
	}

	public String getLives() {
		return "Life remaining: " + pacmon.getpLives();
	}

	public String getPlayerScore() {
		return "Score: " + playerScore;
	}
	
	
}
