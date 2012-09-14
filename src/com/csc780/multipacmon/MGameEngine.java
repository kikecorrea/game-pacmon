package com.csc780.multipacmon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import com.csc780.pacmon.Maze;
import com.csc780.pacmon.Monster;
import com.csc780.pacmon.Pacmon;
import com.csc780.pacmon.SoundEngine;

/* direction notes: 1 = up, 2 = down, 3 = right, 4 = left
 *
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster) as well as call drawing.	
 */

public class MGameEngine extends Thread  {
	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	static final int RD = 9, LD = 10, RU = 5, LU = 6, RDU = 13, LDU = 14, RLD = 11, RLU = 7, RLUD = 15;
	
	private final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, SEARCHING=5, DISCONNECTED=6, DIE=7;
	
	private Maze maze;
	private Thread mThread;
	Pacmon pacmon, pacmon2;
	ArrayList<Monster> ghosts;
	
	//int playerScore, playerScore2;
	int timer; int timerCount;
	volatile int lives, lives2;
	public volatile int gameState;    // ready = 0; running = 1; lost == 2; won = 3;
	
        //use by sending and receiving server
	volatile int inputDirection, inputDirection2;
	volatile int pX, pY, pX2, pY2;
	int newDirection, newDirection2;
	
	ArrayList<Integer> ghostArray[];
	int directionMaze[][];
	int mazeArray[][];
	int blockSize = 32;
	int mazeRow, mazeColumn;
	
	int pNormalSpeed, pPowerSpeed;
	int powerMode1;
	int powerMode2;
	
	private boolean isRunning;
	
	private int checkCounter=0;
	
	
	private int pacCounter=0;
	
	public Receiver receiver;
	public Sender sending;
	private ClientConnectionSetUp clientSetup;
	
	volatile private int totalScores=0;
	private volatile int tempCountDown;
	
	
	//timer
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped
	
	private long readyCountDown;
	
	public SoundEngine soundEngine;
	
	public AutoDiscoverer clientDiscoverer;
	
	//for dialog progress
	protected AtomicBoolean serverReady;
	
	//use in eatfood
	private int tempX, boxX, boxY,tempX2, boxX2, boxY2;
	
	//use for updating pacmon,pacmon2
	private int p1[]=new int[18], p2[]=new int[3], g[];
	
	private String ip;
	
	//use for maze data
	int []bitrow ={0x0001,0x0002,0x0004,0x0008,0x0010,0x0020,0x0040,0x0080,0x0100,0x0200,0x0400,0x0800,0x1000};
//	 int b1 = 0x0001;
//     int b2 = 0x0002;
//     int b3 = 0x0004;
//     int b4 = 0x0008;
//     int b5 = 0x0010;
//     int b6 = 0x0020;
//     int b7 = 0x0040;
//     int b8 = 0x0080;
//     int b9 = 0x0100;
//     int b10 =0x0200;
//     int b11 =0x0400;
//     int b12 =0x0800;
//     int b13 =0x1000;
	
	//Constructor create players, ghosts and Maze
	public MGameEngine(SoundEngine soundEngine, String ip){
		
		this.ip = ip;
		this.soundEngine = soundEngine;
		soundEngine.playReady();

		pacmon = new Pacmon();  // new pacmon
		pacmon2 = new Pacmon();
	
		pacmon.setpX(32);
		pacmon.setpY(32);
	
		pacmon2.setpX(416);
		pacmon2.setpY(640);
		
		lives = pacmon.getpLives();
		lives2=pacmon2.getpLives();
		
		pNormalSpeed = pacmon.getpNormalSpeed();
		pPowerSpeed = pacmon.getpPowerSpeed();
		
		//playerScore = 0;
		timer = 90;
		timerCount = 0;
		gameState = READY;
		
		ghosts = new ArrayList<Monster>();
		
		ghosts.add(new Monster());
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
		
		receiver = new Receiver();
		sending =new Sender();
		
//		clientSetup = new ClientConnectionSetUp(ip);
//		receiver = new Receiver();
//		
//		//send receiving port to server, so server knows where to send date
//		clientSetup.connectToServer(receiver.getPortReceive());
//		
//		sending = new Sender(clientSetup.sendPort, ip);
//
//		sending.start();
//		receiver.start();

	}
	
	public void run()
	{
		clientSetup = new ClientConnectionSetUp(ip);
		receiver = new Receiver();
		
		//send receiving port to server, so server knows where to send date
		clientSetup.connectToServer(receiver.getPortReceive());
				
		sending = new Sender(clientSetup.sendPort, ip);

		sending.start();
		receiver.start();
		
		isRunning=true;
		
		while(isRunning)
		{
			beginTime = System.currentTimeMillis();
	        framesSkipped = 0; // resetting the frames skipped
	       
	        //this.updateDataFromServer();
	        // calculate how long did the cycle take
	        timeDiff = System.currentTimeMillis() - beginTime;
	        // calculate sleep time
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
	       
	                
	        if(this.receiver.status==RUNNING || receiver.status==DIE || receiver.status ==GAMEOVER || receiver.status ==DIE
	        		&&	receiver.status==WON )
	        		this.updateDataFromServer();
	   
	        
		}
		//wait for thread to die
        try {
			sending.join();
		    receiver.join();
		    
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
	}
	
	
//	
//	// loop through ready if gameState is READY
//		private void updateReady(){
//			beginTime = System.currentTimeMillis();
//
//			readyCountDown = 3L - timeDiff/1000;		
//			sleepTime = (int) (FRAME_PERIOD - timeDiff);
//
//			if (sleepTime > 0) {
//				// if sleepTime > 0 we're OK
//				try {
//					// send the thread to sleep for a short period
//					// very useful for battery saving
//					Thread.sleep(sleepTime);
//				} catch (InterruptedException e) {
//				}
//			}
//			
//			timeDiff += System.currentTimeMillis() - beginTime;
//			if(timeDiff >= 3000)
//				gameState = RUNNING;
//			
//		}
//	
	
	public void closeConnection()
	{
		this.receiver.closeSocket();
		this.sending.closeSocket();
	}
	
	public void updateDataFromServer()
	{
		this.gameState = receiver.status;
		
//		int z = receiver.mazeData1.read();
//		if(z!=-1){
//			tempX = z;
//		}
//		z=receiver.mazeData2.read();
//		if(z!=-1){
//			tempX2=z;;
//		}
		
		this.eatFoodUsingBits();
		//this.eatFoodPower();
		//this.eatFoodPower2();
		
		setxyp1();
//		setxyp2();
//		
//		setxyGhost(0);
//		setxyGhost(1);
//		setxyGhost(2);
//		setxyGhost(3);
		
		checkLives();
	}
	
	public void eatFoodUsingBits()
	{
	
		int x=13;
		for(int y=0; y<20; y++)
		{
		 for(int i=0; i<13; i++)
		 {
			int result =receiver.row[y] & bitrow[i];
			if(result==0)
				mazeArray[y+1][x] = 5;
		 x--;
		 }
		x=13; 
		}
		
		if(receiver.p2eatcherry==1)
			soundEngine.playEatCherry();
		
	}
	
	// eat food ==> score and power ==> speed
	public void eatFoodPower() {	
		//tempX=receiver.mazeData1;
		
		boxX = tempX%100;
		boxY= tempX/100;
		
		if (mazeArray[boxY][boxX] == 1){
			mazeArray[boxY][boxX] = 5;
			
			soundEngine.playEatCherry();
		
			//playerScore++;   // increase score
			//if ( (playerScore + playerScore2)== maze.getFoodCount())
			if ( this.totalScores== maze.getFoodCount())
			{
				gameState = WON;
				soundEngine.stopMusic();
			}
			//maze.clearFood(boxX, boxY);
		}
		
		if (mazeArray[boxY][boxX] == 2){
			mazeArray[boxY][boxX] = 5; // blank
			this.powerMode1 = 5;
		}
	}
        // eat food ==> score and power ==> speed
	public void eatFoodPower2() {
		//tempX2=receiver.mazeData2;
		
		boxX2 = tempX2%100;
		boxY2= tempX2/100;
		
		if (mazeArray[boxY2][boxX2] == 1){
			mazeArray[boxY2][boxX2] = 5;
			
			soundEngine.playEatCherry();
			//playerScore2++;   // increase score
			//if ( (playerScore  + playerScore2)== maze.getFoodCount())
			if ( this.totalScores== maze.getFoodCount()){
				gameState = WON;
				soundEngine.stopMusic();
			}
			//maze.clearFood(boxX, boxY);
		}
		
		if (mazeArray[boxY2][boxX2] == 2){
			mazeArray[boxY2][boxX2] = 5; // blank
			this.powerMode2 = 5;
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
	
//	public void setGameStateFromServer()
//	{
//		this.gameState = receiver.status;
//
//	}

	
	public void setxyp1()
	{
		//if(!receiver.checkListEmpty())
			{
				  //int xy[]=receiver.getReceiveData();
				  p1=receiver.pac1que.read();
				
				  if(p1[0]!=-1 && p1[1]!=-2)
				  {
				  pacmon.setpX(p1[0]);
				  pacmon.setpY(p1[1]);		
				  pacmon.setDir(p1[2]);
				  pacmon2.setpX(p1[3]);
				  pacmon2.setpY(p1[4]);
				  pacmon2.setDir(p1[5]);
				  
				  ghosts.get(0).setX(p1[6]); 
	              ghosts.get(0).setY(p1[7]); 
	              ghosts.get(0).setDir(p1[8]);
	              
	              ghosts.get(1).setX(p1[9]); 
	              ghosts.get(1).setY(p1[10]); 
	              ghosts.get(1).setDir(p1[11]);
	              
	              ghosts.get(2).setX(p1[12]); 
	              ghosts.get(2).setY(p1[13]); 
	              ghosts.get(2).setDir(p1[14]);
	              
	              ghosts.get(3).setX(p1[15]); 
	              ghosts.get(3).setY(p1[16]); 
	              ghosts.get(3).setDir(p1[17]);
				  }
			}
	}
//	public void setxyp2()
//	{
//		//if(!receiver.checkListEmpty())
//			{
//				  //int xy[]=receiver.getReceiveData();
//				   p2=receiver.pac2que.read();
//				
//				 //x and y will be set -1,2 if readPointer=writePointer
//				 //so just set pacmon's x,y will use old values
//				 if(p2[0]!=-1 && p2[1]!=-2)
//				 {
//				
//				  pacmon2.setpX(p2[0]);
//				  pacmon2.setpY(p2[1]);
//				  pacmon2.setDir(p2[2]);
//				  
//				 }
//	 
//			}
//	}
//	
//	//for ghost
//	public void setxyGhost(int i)
//	{
//		if(i==0)
//		   g=receiver.ghost1Que.read();
//		else if(i==1)
//		   g=receiver.ghost2Que.read();
//		else if(i==2)
//		   g=receiver.ghost3Que.read();
//		else
//		   g=receiver.ghost4Que.read();
//		
//		 //x and y will be set -1,2 if readPointer=writePointer
//		 //so just set ghost's x,y to old values
//		 if(g[0]!=-1 && g[1]!=-2)
//		 {
//		    ghosts.get(i).setX(g[0]); 
//            ghosts.get(i).setY(g[1]); 
//            ghosts.get(i).setDir(g[2]);
//		 }
//
//	}
	
	public void checkLives()
	{
		lives=receiver.p1life;
		lives2=receiver.p2life;
		
		if(lives==0 || lives2==0 || receiver.timer<0 )
		{
			gameState=GAMEOVER;
			receiver.isRunning=false;
			sending.isRunning=false;
			
		}
	}
	
//	public int getCountDown()
//	{
//		
//		if(receiver.status==READY)
//		{
//			
//		}
//		else if(receiver.status==RUNNING)
//		{
//			
//		}
//	
////		if(receiver.countDown>80)
////			return 3;
////		else if(receiver.countDown<=80 && receiver.countDown>=40)
////		{
////			return 2;
////		}
////		else if(receiver.countDown<40 && receiver.countDown>1)
////			return 1;
////			
////		else
////			return 0;
//	
//	}
	
//	public String[] getScores()
//	{
//		int y[]=receiver.pacmonScores;
//		String x[]={"Score:" +String.valueOf(y[0]), "Score:"+String.valueOf(y[1])};
//		
//		this.totalScores=y[0] + y[1];
//		return x;
//	}
	
//	public int getp1score()
//	{
//		return receiver.p1score;
//	}
//	public int getp2score()
//	{
//		return receiver.p2score;
//	}
	
//	public String getTimer()
//	{ return "Time:" + receiver.timer;}
	
	public void killAllThread()
	{
		receiver.isRunning=false;
		sending.isRunning=false;
	}
	
	
}
