package com.csc780.pacmon;

public class Monster {

	private int x;
	private int y;
	private int dir;
	private int normalSpeed;
	
	
	public Monster(){
		x = 7 * 32;
		y = 9 * 32;
		dir = 2;
	}
	
	
	
	public void spawnMonster(){
		
	}


	

	public int getX() {
		return x;
	}



	public void setX(int x) {
		this.x = x;
	}



	public int getY() {
		return y;
	}



	public void setY(int y) {
		this.y = y;
	}



	public int getDir() {
		return dir;
	}



	public int getNormalSpeed() {
		return normalSpeed;
	}



	public void setNormalSpeed(int normalSpeed) {
		this.normalSpeed = normalSpeed;
	}



	public void setDir(int newDirection) {
		// TODO Auto-generated method stub
		
	}
	
}
