package com.csc780.pacmon;

public class Pacman {

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
	
	public Pacman (){
		pX = pY = 0;
		pXOrigin = pYOrigin = 0;
		pLives = 3;
		pNormalSpeed = 3;
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
