package com.csc780.pacmon;


// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster)
 * 		
 */

public class GameEngine {

	
	Maze maze;
	Pacmon pacmon;
	Monster ghosts;
	
	int pacDir;
	int pX, pY;
	int newDirection;
	int mazeArray[][];
	int blockSize = 32;
	int mazeRow, mazeColumn;
	
	public GameEngine(){
		
	}
	
	
	public void updatePac(int direction){
		int XmodW, YmodH;
		int boxX, boxY;
		XmodW = pX % blockSize;
		YmodH = pY % blockSize;
		boolean canMove = true;
		
		// check direction and change if it is allowed
		if(XmodW == 0 && YmodH == 0){
			boxX = pX / blockSize;
			boxY = pY / blockSize;

			if (direction == 4){  // move left allowed if can move to left
		        if (boxX > 0 )
		            if ( mazeArray[boxY][boxX - 1] != 0)
		                newDirection = direction;
			}
			if (direction == 3){   // move right
		        if (boxX < mazeColumn )
		            if (mazeArray[boxY][boxX + 1] != 0) 
		                newDirection = direction;
			}	
			if (direction == 2){ // move down
				if (boxY < mazeRow)
					if (mazeArray[boxY + 1][boxX] != 0 && mazeArray[boxY + 1][boxX] != 3)
						newDirection = direction;
			}
			if (direction == 1) { // move up
		        if (boxY > 0 )
		            if (mazeArray[boxY - 1][boxX] != 0 && mazeArray[boxY - 1][boxX] != 3)
		                newDirection = direction;
			}
		} else {
			if (newDirection != direction){
		        if (((direction == 1) || (direction == 2)) && (XmodW==0) && (YmodH!=0)){
		            newDirection = direction;
		        }
		        if (((direction == 3) || (direction == 4)) && (YmodH==0) && (XmodW!=0) ){
		            newDirection = direction;
		        }
		    }
		}
		
		//evaluate at intersection, collision detection
		if(XmodW == 0 && YmodH == 0){
			boxX = pX / blockSize;
			boxY = pY / blockSize;
		
			canMoveU = canMoveD = canMoveL = canMoveR = true;
			
			if (newDirection == 4){  // move left
                if (boxX > 0 )
                    if ( mazeArray[boxY][boxX - 1] == 0){
                        canMoveL = false;
                    }
			}
			
			if (newDirection == 3){   // move right
                if (boxX < mazeColumn -1 ) 
                    if ( mazeArray[boxY][boxX + 1] == 0) {
                        canMoveR = false;
                    }
			}	
			
			if (newDirection == 2){ // move down
				if (boxY < mazeRow - 1)
					if (mazeArray[boxY + 1][boxX] == 0 || mazeArray[boxY + 1][boxX] == 3){
						canMoveD = false;
				}
			}
			if (newDirection == 1) { // move up
                if (boxY > 0 ) 
					if (mazeArray[boxY - 1][boxX] == 0 || mazeArray[boxY - 1][boxX] == 3){
						canMoveU = false;
					}
			}

		}
		
			if (newDirection == 1 && canMoveU) // up
				pY = pY - pNormalSpeed;
			if (newDirection == 2 && canMoveD) // down
				pY = pY + pNormalSpeed;
			if (newDirection == 3 && canMoveR) // right
				pX = pX + pNormalSpeed;
			if (newDirection == 4 && canMoveL) // left
				pX = pX - pNormalSpeed;
	}
	
	public void updateGhost(){
		
	}
	
	
	
}
