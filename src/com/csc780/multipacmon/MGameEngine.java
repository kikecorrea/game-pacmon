package com.csc780.multipacmon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.csc780.pacmon.Maze;
import com.csc780.pacmon.Monster;
import com.csc780.pacmon.Pacmon;
import com.csc780.pacmon.SoundEngine;


// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster) as well as call drawing.
 * 		
 */

public class MGameEngine  {
	private final static int    MAX_FPS = 40;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	static final int RD = 9, LD = 10, RU = 5, LU = 6, RDU = 13, LDU = 14, RLD = 11, RLU = 7, RLUD = 15;
	
	private final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, SEARCHING=5;
	
	private Maze maze;
	private Thread mThread;
	Pacmon pacmon, pacmon2;
	ArrayList<Monster> ghosts;
	
	//int playerScore, playerScore2;
	int timer; int timerCount;
	int lives, lives2;
	private int gameState;    // ready = 0; running = 1; lost == 2; won = 3;
	
        //use by sending and receiving server
	volatile int inputDirection, inputDirection2;
	volatile int pX, pY, pX2, pY2;
	int newDirection, newDirection2;
	
	ArrayList<Integer> ghostArray[];
	int directionMaze[][];
	int mazeArray[][];
	int blockSize = 32;
	int mazeRow, mazeColumn;
	
	private boolean isRunning;
	
	private int checkCounter=0;
	
	
	private int pacCounter=0;
	
	private Receiving receiver;
	private Sending sending;
	private ClientDispatcher clientDispatcher;
	
	volatile private int totalScores=0;
	
	
	//timer
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped
	
	private long readyCountDown;
	
	private SoundEngine soundEngine;
	
	public ClientDiscoverer clientDiscoverer;
	
	//for dialog progress
	protected AtomicBoolean serverReady;
	
	//Constructor create players, ghosts and Maze
	public MGameEngine(SoundEngine soundEngine, AtomicBoolean ready){
		
		this.soundEngine = soundEngine;
		serverReady = ready;
		
		pacmon = new Pacmon();  // new pacmon
		pacmon2 = new Pacmon();
	
		pacmon.setpX(32);
		pacmon.setpY(32);
	
		pacmon2.setpX(416);
		pacmon2.setpY(640);
		
		lives = pacmon.getpLives();
		lives2=pacmon2.getpLives();
		
		
		//playerScore = 0;
		timer = 90;
		timerCount = 0;
		gameState = 5;
		
		ghosts = new ArrayList<Monster>();
		
		ghosts.add(new Monster());
		ghosts.add(new Monster());
		ghosts.add(new Monster());
		
		// maze stuff
		maze = new Maze();
		mazeArray = maze.getMaze(1);
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();
		directionMaze = maze.getDirectionMaze(1);
		ghostArray = maze.getGhostArray();
		
		isRunning = true;
	
		
//		receiver=new Receiving();
//		///gets the receiver port, then needs to send to server
//		int port=receiver.getPortReceive();
		
		
		//client discoverer
	   clientDiscoverer = new ClientDiscoverer();
		clientDiscoverer.start();
		
		//String ip=clientDiscoverer.getipAddress();
		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		clientDispatcher=new ClientDispatcher(ip);
//		clientDispatcher.connectToServer(port);
//		
//		//returns port for sending to server
//		//clientDispatcher.connectToServer(port);
//		int sendPort=clientDispatcher.sendPort;
//		int id=clientDispatcher.id;
//		
//		receiver.setID(id);   ///########################### need to remove, this didn't use it at all
//	
//		sending=new Sending(sendPort, ip);
//		
//		sending.start();
//		receiver.start();
		
		//start the gameEngine
		//mThread.start();
		
	}
	
	public void callDispatcher()
	{
		String ip=clientDiscoverer.getipAddress();
		
		receiver=new Receiving();
		///gets the receiver port, then needs to send to server
		int port=receiver.getPortReceive();
		
		
		clientDispatcher=new ClientDispatcher(ip);
		clientDispatcher.connectToServer(port);
		
		//returns port for sending to server
		//clientDispatcher.connectToServer(port);
		int sendPort=clientDispatcher.sendPort;
		int id=clientDispatcher.id;
		
		receiver.setID(id);   ///########################### need to remove, this didn't use it at all
	
		sending=new Sending(sendPort, ip);
		
		sending.start();
		receiver.start();
	
	}
	
	
	// loop through ready if gameState is READY
		private void updateReady(){
			beginTime = System.currentTimeMillis();

			readyCountDown = 3L - timeDiff/1000;		
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
			
			timeDiff += System.currentTimeMillis() - beginTime;
			if(timeDiff >= 3000)
				gameState = RUNNING;
			
		}
	
	
	
	// eat food ==> score and power ==> speed
	public void eatFoodPower() {
		
		int x=receiver.mazeData1;
		
		
		int boxX = x%100, boxY= x/100;
		
		if (mazeArray[boxY][boxX] == 1){
			mazeArray[boxY][boxX] = 5;
			
			soundEngine.playEatCherry();
			
			//playerScore++;   // increase score
			//if ( (playerScore + playerScore2)== maze.getFoodCount())
			if ( this.totalScores== maze.getFoodCount())
				gameState = WON;
			//maze.clearFood(boxX, boxY);
		}
		
		if (mazeArray[boxY][boxX] == 2){
			mazeArray[boxY][boxX] = 5; // blank
		}
	}
        // eat food ==> score and power ==> speed
	public void eatFoodPower2() {
		int x=receiver.mazeData2;
		
		int boxX = x%100, boxY= x/100;
		
		if (mazeArray[boxY][boxX] == 1){
			mazeArray[boxY][boxX] = 5;
			//playerScore2++;   // increase score
			//if ( (playerScore  + playerScore2)== maze.getFoodCount())
			if ( this.totalScores== maze.getFoodCount())
				gameState = WON;
			//maze.clearFood(boxX, boxY);
		}
		
		if (mazeArray[boxY][boxX] == 2){
			mazeArray[boxY][boxX] = 5; // blank
		}
	}
        
	
	// using accelerometer to set direction of player
	public void setInputDir(int dir){

		this.inputDirection = dir;
		
		sending.data=String.valueOf(dir);
		sending.ready.set(true);
		//sending.notifyTheThread();
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

//	public String getTimer() {
//		return "Time: " + timer;
//	}

//	public String getLives() {
//		return "Life remaining: " + lives;
//	}

//	public String getPlayerScore() {
//		return "Score: " + playerScore;
//	}
	
	public int getGameState(){
		return gameState;
	}

	public long getReadyCountDown(){
		return this.readyCountDown;
	}
	
	public void setGameState(int state)
	{
		this.gameState=state;
	}
	

	
	public void setxyp1()
	{
		//if(!receiver.checkListEmpty())
			{
				  //int xy[]=receiver.getReceiveData();
				   int p1[]=receiver.deQueP1();
				
				  if(p1[0]!=-1 && p1[1]!=-2)
				  {
				  pacmon.setpX(p1[0]);
				  pacmon.setpY(p1[1]);		
				  pacmon.setDir(p1[2]);
				  }
			}
	}
	public void setxyp2()
	{
		//if(!receiver.checkListEmpty())
			{
				  //int xy[]=receiver.getReceiveData();
				   int p2[]=receiver.deQueP2();
				
				 //x and y will be set -1,2 if readPointer=writePointer
				 //so just set pacmon's x,y will use old values
				 if(p2[0]!=-1 && p2[1]!=-2)
				 {
				
				  pacmon2.setpX(p2[0]);
				  pacmon2.setpY(p2[1]);
				  pacmon2.setDir(p2[2]);
				  
				 }

				 
			}
	}
	
	//for ghost
	public void setxyGhost(int i)
	{
		int g[];
	
		if(i==0)
		   g=receiver.deQueG1();
		else if(i==1)
		   g=receiver.deQueG2();
		else
		   g=receiver.deQueG3();
		
		//x and y will be set -1,2 if readPointer=writePointer
		 //so just set ghost's x,y to old values
		 if(g[0]!=-1 && g[1]!=-2)
		 {
		    ghosts.get(i).setX(g[0]); 
            ghosts.get(i).setY(g[1]); 
            ghosts.get(i).setDir(g[2]);
		 }

	}
	
	public String[] checkLives()
	{
		if(receiver.pacmonLives[0]==0 || receiver.pacmonLives[1]==0 || receiver.timer<0 )
		{
			gameState=GAMEOVER;
			
			receiver.isRunning=false;
			sending.isRunning=false;
			
		}
		
		String x[]={"P1 lives:" + String.valueOf(receiver.pacmonLives[0]), "P2 lives:"+String.valueOf(receiver.pacmonLives[1]) };
		return x;
	}
	
	public int getCountDown()
	{
		int x=receiver.countDown;
		if(x>80)
			return 3;
		else if(x<=80 && x>=40)
		{
			return 2;
		}
		else if(x<40 && x>0)
			return 1;
			
		else
			return 0;
	
	}
	
	public String[] getScores()
	{
		int y[]=receiver.pacmonScores;
		String x[]={"Score:" +String.valueOf(y[0]), "Score:"+String.valueOf(y[1])};
		
		this.totalScores=y[0] + y[1];
		return x;
	}
	
	public String getTimer()
	{ return "Time:" + receiver.timer;}
	
	public void killAllThread()
	{
		receiver.isRunning=false;
		sending.isRunning=false;
	}
	
	
}
