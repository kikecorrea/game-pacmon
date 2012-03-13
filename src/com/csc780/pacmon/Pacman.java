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
	
	public Pacman (){
		pX = pY = 0;
		pXOrigin = pYOrigin = 0;
		pLives = 3;
		pNormalSpeed = 3;
		pPowerSpeed = 4;
	}
}
