package com.csc780.pacmon;


/* Pacman class contains information about pacman
 * 
 * 
 */
public class Pacmon {

	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	public static final int LEFT = 4;
	
	//position by grid
	private int pX;
	private int pY;
	
	private int pXOrigin;
	private int pYOrigin;
	
	private int pLives;
	private int pNormalSpeed;
	private int pPowerSpeed;
	
	private int dir; // direction of movement 0 = not moving
					 // 1 = left, 2 = right, 3 = up, 4 = down
	
	public Pacmon (){
		pX = pY = 0;
		pXOrigin = pYOrigin = 1;
		pLives = 3;
		pNormalSpeed = 2;
		pPowerSpeed = 4;
	}

	public int getpX() {
		return pX;
	}

	public int getpY() {
		return pY;
	}

	public int getpXOrigin() {
		return pXOrigin;
	}

	public int getpYOrigin() {
		return pYOrigin;
	}

	public int getpLives() {
		return pLives;
	}

	public int getpNormalSpeed() {
		return pNormalSpeed;
	}

	public int getpPowerSpeed() {
		return pPowerSpeed;
	}

	public int getDir() {
		return dir;
	}
	
	
}
