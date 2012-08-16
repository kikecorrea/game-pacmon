package com.csc780.clientmultiserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import com.csc780.pacmon.SoundEngine;

// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * CMGameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster) as well as call drawing.
 * 
 * This class is similar to GameEngine in the pacmon package but with minor modification to work with server
 * 		
 */
public class CMGameEngine implements Runnable { 

    private final static int MAX_FPS = 55;
    // maximum number of frames to be skipped
    private final static int MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;
    static final int RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
    static final int RD = 9, LD = 10, RU = 5, LU = 6, RDU = 13, LDU = 14, RLD = 11, RLU = 7, RLUD = 15;
    private final static int READY = 0, RUNNING = 1, GAMEOVER = 2, WON = 3, SEARCHING=5, DISCONNECTED=6, DIE=7;
    private CMMaze maze;
    private Thread mThread;
    public CMPacmon pacmon, pacmon2;
    ArrayList<CMMonster> ghosts;
    public volatile int playerScore, playerScore2;
    private volatile int totalScore;
    
    
    volatile int timer;
    int timerCount;
    volatile int lives, lives2;
    public int gameState;    // ready = 0; running = 1; lost == 2; won = 3;
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
    private int checkCounter = 0;
    //private ClientConnection client=new ClientConnection();
    private int pacCounter = 0;
    volatile protected int tickCounter = -60;
    //maze data
    public int mazeData1, mazeData2;
    //timer
    private long beginTime; // the time when the cycle begun
    private long timeDiff; // the time it took for the cycle to execute
    private int sleepTime; // ms to sleep (<0 if we're behind)
    private int framesSkipped; // number of frames being skipped
    volatile protected long readyCountDown;
    
    public int powerMode1, powerMode2;	
    int pPowerSpeed, pNormalSpeed;
    
    public SoundEngine soundEngine;
    
    //data for maze
    volatile int p2eatcherry=0;
    public MazeDataCompressor mazeDataCompressor;
    
    int key,value;
    
    //Constructor create players, ghosts and Maze
    public CMGameEngine(SoundEngine se) {
    	
    	mazeDataCompressor = new MazeDataCompressor();
    	
    	soundEngine = se;
    	soundEngine.playReady();
    	
        pacmon = new CMPacmon(32, 32);  // new pacmon
        pacmon2 = new CMPacmon(416, 640);
        lives = pacmon.getpLives();
        lives2 = pacmon2.getpLives();
        
        pNormalSpeed = pacmon.getpNormalSpeed();
		pPowerSpeed = pacmon.getpPowerSpeed();
		

        playerScore = 0;
        playerScore2 = 0;
        timer = 180;
        timerCount = 0;
        
        
        gameState = SEARCHING;

        ghosts = new ArrayList<CMMonster>();

        ghosts.add(new CMMonster());
        ghosts.add(new CMMonster());
        ghosts.add(new CMMonster());
        ghosts.add(new CMMonster());

        // maze stuff
        maze = new CMMaze();
      
        int randLevel = (int)(Math.random()*2) + 1;
        mazeArray = maze.getMaze(1);    //change
        mazeRow = maze.getMazeRow();
        mazeColumn = maze.getMazeColumn();
        directionMaze = maze.getDirectionMaze(1);   //change
        ghostArray = maze.getGhostArray();

        isRunning = true;
        mThread = new Thread(this);
    }

    public void startTheEngine() {
        mThread.start();
        this.gameState=READY;
    }

    //update
    public void update() {
        updateTimer();
        updatePac();
        updatePac2();
        updateGhost();

        this.tickCounter++;
        if (this.tickCounter >= 80) {
            this.tickCounter = 0;
        }
    }

    public void updatePac2() {
    
        int XmodW, YmodH;
        int boxX, boxY;
        pX2 = pacmon2.getpX();
        pY2 = pacmon2.getpY();

        XmodW = pX2 % blockSize;
        YmodH = pY2 % blockSize;
        boolean movable = true;

        // check direction and change if it is allowed
        if (XmodW == 0 && YmodH == 0) {
            boxX = pX2 / blockSize;
            boxY = pY2 / blockSize;

            if (inputDirection2 == LEFT) {  // move left allowed if can move to left
                if (boxX > 0) {
                    if (boxX == 0|| mazeArray[boxY][boxX - 1] != 0) {
                        newDirection2 = inputDirection2;
                    }
                }
            }
            if (inputDirection2 == RIGHT) {   // move right
                if (boxX == mazeColumn - 1 || boxX < mazeColumn) {
                    if (mazeArray[boxY][boxX + 1] != 0) {
                        newDirection2 = inputDirection2;
                    }
                }
            }
            if (inputDirection2 == DOWN) { // move down
                if (boxY < mazeRow) {
                    if (mazeArray[boxY + 1][boxX] != 0 && mazeArray[boxY + 1][boxX] != 3) {
                        newDirection2 = inputDirection2;
                    }
                }
            }
            if (inputDirection2 == UP) { // move up
                if (boxY > 0) {
                    if (mazeArray[boxY - 1][boxX] != 0 && mazeArray[boxY - 1][boxX] != 3) {
                        newDirection2 = inputDirection2;
                    }
                }
            }
        } else {  // change opposite direction
            if (newDirection2 != inputDirection2) {
                if (((inputDirection2 == UP) || (inputDirection2 == DOWN)) && (XmodW == 0) && (YmodH != 0)) {
                    newDirection2 = inputDirection2;
                }
                if (((inputDirection2 == RIGHT) || (inputDirection2 == LEFT)) && (YmodH == 0) && (XmodW != 0)) {
                    newDirection2 = inputDirection2;
                }
            }
        }

        pacmon2.setDir(newDirection2);

        //evaluate at intersection, collision detection
        if (XmodW == 0 && YmodH == 0) {

            boxX = pX2 / blockSize;
            boxY = pY2 / blockSize; 
            boxX %= mazeColumn;
            boxY %= mazeRow;
            
            eatFoodPower2(boxX, boxY);

            movable = true;

            if (newDirection2 == LEFT) {  // move left
                if (boxX > 0) {
                    if (mazeArray[boxY][boxX - 1] == 0) {
                        movable = false;
                    }
                }
            }

            if (newDirection2 == RIGHT) {   // move right
                if (boxX < mazeColumn - 1) {
                    if (mazeArray[boxY][boxX + 1] == 0) {
                        movable = false;
                    }
                }
            }

            if (newDirection2 == DOWN) { // move down
                if (boxY < mazeRow - 1) {
                    if (mazeArray[boxY + 1][boxX] == 0 || mazeArray[boxY + 1][boxX] == 3) {
                        movable = false;
                    }
                }
            }
            if (newDirection2 == UP) { // move up
                if (boxY > 0) {
                    if (mazeArray[boxY - 1][boxX] == 0 || mazeArray[boxY - 1][boxX] == 3) {
                        movable = false;
                    }
                }
            }

        }
        
    	if( movable){
			if (newDirection2 == UP) { // up
				if (powerMode2 > 0) 		pY2 = pY2 - pPowerSpeed; // in power mode
				else			pY2 = pY2 - pNormalSpeed;		   // normal mode
			}
			else if (newDirection2 == DOWN){  // down
				if (powerMode2 > 0) 		pY2 = pY2 + pPowerSpeed; // power mode
				else			pY2 = pY2 + pNormalSpeed;		   // normal mode
			}
			else if (newDirection2 == RIGHT){ // right
				if (powerMode2 > 0) 		pX2 = pX2 + pPowerSpeed; // power mode
				else			pX2 = pX2 + pNormalSpeed;		   // normal mode
			}
			else if (newDirection2 == LEFT) {// left
				if (powerMode2 > 0) 		pX2 = pX2 - pPowerSpeed;	// power mode
				else			pX2 = pX2 - pNormalSpeed;			// normal mode
			}
		}

//        if (movable) {
//            if (newDirection2 == UP) // up
//            {
//                pY2 = pY2 - pNormalSpeed;
//            }
//            if (newDirection2 == DOWN) // down
//            {
//                pY2 = pY2 + pNormalSpeed;
//            }
//            if (newDirection2 == RIGHT) // right
//            {
//                pX2 = pX2 + pNormalSpeed;
//            }
//            if (newDirection2 == LEFT) // left
//            {
//                pX2 = pX2 - pNormalSpeed;
//            }
//        }
        
        // for pass through wall
        if(pX2 == 448)
            pX2 = 4;
        if(pX2 == 0)
            pX2 = 444;

        pacmon2.setpX(pX2);
        pacmon2.setpY(pY2);


    }

    public void updatePac() {
        int XmodW, YmodH;
        int boxX, boxY;
        pX = pacmon.getpX();
        pY = pacmon.getpY();

        XmodW = pX % blockSize;
        YmodH = pY % blockSize;
        boolean movable = true;

        // check direction and change if it is allowed
        if (XmodW == 0 && YmodH == 0) {
            boxX = pX / blockSize;
            boxY = pY / blockSize;

            if (inputDirection == LEFT) {  // move left allowed if can move to left
                if (boxX > 0) {
                    if (boxX == 0 || mazeArray[boxY][boxX - 1] != 0) {
                        newDirection = inputDirection;
                    }
                }
            }
            if (inputDirection == RIGHT) {   // move right
                if (boxX < mazeColumn) {
                    if (boxX == mazeColumn - 1 || mazeArray[boxY][boxX + 1] != 0) {
                        newDirection = inputDirection;
                    }
                }
            }
            if (inputDirection == DOWN) { // move down
                if (boxY < mazeRow) {
                    if (mazeArray[boxY + 1][boxX] != 0 && mazeArray[boxY + 1][boxX] != 3) {
                        newDirection = inputDirection;
                    }
                }
            }
            if (inputDirection == UP) { // move up
                if (boxY > 0) {
                    if (mazeArray[boxY - 1][boxX] != 0 && mazeArray[boxY - 1][boxX] != 3) {
                        newDirection = inputDirection;
                    }
                }
            }
        } else {  // change opposite direction
            if (newDirection != inputDirection) {
                if (((inputDirection == UP) || (inputDirection == DOWN)) && (XmodW == 0) && (YmodH != 0)) {
                    newDirection = inputDirection;
                }
                if (((inputDirection == RIGHT) || (inputDirection == LEFT)) && (YmodH == 0) && (XmodW != 0)) {
                    newDirection = inputDirection;
                }
            }
        }

        pacmon.setDir(newDirection);

        //evaluate at intersection, collision detection
        if (XmodW == 0 && YmodH == 0) {

            boxX = pX / blockSize;
            boxY = pY / blockSize;
            
            boxX %= mazeColumn;  // for wall through pacmon 1
            boxY %= mazeRow;
            eatFoodPower(boxX, boxY);

            movable = true;

            if (newDirection == LEFT) {  // move left
                if (boxX > 0) {
                    if (mazeArray[boxY][boxX - 1] == 0) {
                        movable = false;
                    }
                }
            }

            if (newDirection == RIGHT) {   // move right
                if (boxX < mazeColumn - 1) {
                    if (mazeArray[boxY][boxX + 1] == 0) {
                        movable = false;
                    }
                }
            }

            if (newDirection == DOWN) { // move down
                if (boxY < mazeRow - 1) {
                    if (mazeArray[boxY + 1][boxX] == 0 || mazeArray[boxY + 1][boxX] == 3) {
                        movable = false;
                    }
                }
            }
            if (newDirection == UP) { // move up
                if (boxY > 0) {
                    if (mazeArray[boxY - 1][boxX] == 0 || mazeArray[boxY - 1][boxX] == 3) {
                        movable = false;
                    }
                }
            }

        }
        
    	if(movable){
			if (newDirection == UP) { // up
				if (powerMode1 > 0) 		pY = pY - pPowerSpeed; // in power mode
				else			pY = pY - pNormalSpeed;		   // normal mode
			}
			else if (newDirection == DOWN){  // down
				if (powerMode1 > 0) 		pY = pY + pPowerSpeed; // power mode
				else			pY = pY + pNormalSpeed;		   // normal mode
			}
			else if (newDirection == RIGHT){ // right
				if (powerMode1 > 0) 		pX = pX + pPowerSpeed; // power mode
				else			pX = pX + pNormalSpeed;		   // normal mode
			}
			else if (newDirection == LEFT) {// left
				if (powerMode1 > 0) 		pX = pX - pPowerSpeed;	// power mode
				else			pX = pX - pNormalSpeed;			// normal mode
			}
		}

//        if (movable) {
//            if (newDirection == UP) // up
//            {
//                pY = pY - pNormalSpeed;
//            }
//            if (newDirection == DOWN) // down
//            {
//                pY = pY + pNormalSpeed;
//            }
//            if (newDirection == RIGHT) // right
//            {
//                pX = pX + pNormalSpeed;
//            }
//            if (newDirection == LEFT) // left
//            {
//                pX = pX - pNormalSpeed;
//            }
//        }
        
        if(pX == 448)
            pX = 4;
        if(pX == 0)
            pX = 444;

        pacmon.setpX(pX);
        pacmon.setpY(pY);

    }

    //update ghost movements and locations
    public void updateGhost() {
        int gNormalSpeed = ghosts.get(0).getNormalSpeed();
        int XmodW, YmodH;
        int boxX, boxY;
        int gX, gY;

        //for pacmon 1
        for (int i = 0; i < ghosts.size()/2; i++) {
            gX = ghosts.get(i).getX();
            gY = ghosts.get(i).getY();
            XmodW = gX % blockSize;
            YmodH = gY % blockSize;


            // check direction and change if it is allowed
            if (XmodW == 0 && YmodH == 0) {
                int crossing;
                boxX = gX / blockSize;
                boxY = gY / blockSize;

                //check if at crossing using directional maze and update new direction
                crossing = directionMaze[boxY][boxX];
                if (crossing > 0) {
                    if (timer % 9 == 0) {
                        if (crossing == 1) {
                            moveGhostSmart(RD, ghosts.get(i));
                        }
                        if (crossing == 2) {
                            moveGhostSmart(LD, ghosts.get(i));
                        }
                        if (crossing == 3) {
                            moveGhostSmart(RU, ghosts.get(i));
                        }
                        if (crossing == 4) {
                            moveGhostSmart(LU, ghosts.get(i));
                        }
                        if (crossing == 5) {
                            moveGhostSmart(RDU, ghosts.get(i));
                        }
                        if (crossing == 6) {
                            moveGhostSmart(LDU, ghosts.get(i));
                        }
                        if (crossing == 7) {
                            moveGhostSmart(RLD, ghosts.get(i));
                        }
                        if (crossing == 8) {
                            moveGhostSmart(RLU, ghosts.get(i));
                        }
                        if (crossing == 9) {
                            moveGhostSmart(RLUD, ghosts.get(i));
                        }
                    } else {
                        if (crossing == 1) {
                            moveGhostRandom(RD, ghosts.get(i));
                        }
                        if (crossing == 2) {
                        	moveGhostRandom(LD, ghosts.get(i));
                        }
                        if (crossing == 3) {
                        	moveGhostRandom(RU, ghosts.get(i));
                        }
                        if (crossing == 4) {
                        	moveGhostRandom(LU, ghosts.get(i));
                        }
                        if (crossing == 5) {
                        	moveGhostRandom(RDU, ghosts.get(i));
                        }
                        if (crossing == 6) {
                        	moveGhostRandom(LDU, ghosts.get(i));
                        }
                        if (crossing == 7) {
                        	moveGhostRandom(RLD, ghosts.get(i));
                        }
                        if (crossing == 8) {
                        	moveGhostRandom(RLU, ghosts.get(i));
                        }
                        if (crossing == 9) {
                        	moveGhostRandom(RLUD, ghosts.get(i));
                        }

                    }
                }
            }

            //get direction after calculate
            int ghostCurDir = ghosts.get(i).getDir();

            if (ghostCurDir == UP) // up
            {
                gY = gY - gNormalSpeed;
            }
            if (ghostCurDir == DOWN) // down
            {
                gY = gY + gNormalSpeed;
            }
            if (ghostCurDir == RIGHT) // right
            {
                gX = gX + gNormalSpeed;
            }
            if (ghostCurDir == LEFT) // left
            {
                gX = gX - gNormalSpeed;
            }

            // set new location of ghost after moving
            ghosts.get(i).setX(gX);
            ghosts.get(i).setY(gY);

            //check collision for pacmon
            checkCollision(gX, gY);
            //check other collision for pacmon2
            checkCollision2(gX, gY);

        }
        
        //for pacmon 2
        for (int i = ghosts.size()/2; i < ghosts.size(); i++) {
            gX = ghosts.get(i).getX();
            gY = ghosts.get(i).getY();
            XmodW = gX % blockSize;
            YmodH = gY % blockSize;


            // check direction and change if it is allowed
            if (XmodW == 0 && YmodH == 0) {
                int crossing;
                boxX = gX / blockSize;
                boxY = gY / blockSize;

                //check if at crossing using directional maze and update new direction smart
                crossing = directionMaze[boxY][boxX];
                if (crossing > 0) {
                    if (timer % 9 == 0) {
                        if (crossing == 1) {
                            moveGhostSmart2(RD, ghosts.get(i));
                        }
                        if (crossing == 2) {
                            moveGhostSmart2(LD, ghosts.get(i));
                        }
                        if (crossing == 3) {
                            moveGhostSmart2(RU, ghosts.get(i));
                        }
                        if (crossing == 4) {
                            moveGhostSmart2(LU, ghosts.get(i));
                        }
                        if (crossing == 5) {
                            moveGhostSmart2(RDU, ghosts.get(i));
                        }
                        if (crossing == 6) {
                            moveGhostSmart2(LDU, ghosts.get(i));
                        }
                        if (crossing == 7) {
                            moveGhostSmart2(RLD, ghosts.get(i));
                        }
                        if (crossing == 8) {
                            moveGhostSmart2(RLU, ghosts.get(i));
                        }
                        if (crossing == 9) {
                            moveGhostSmart2(RLUD, ghosts.get(i));
                        }
                    } else {
                        if (crossing == 1) {
                            moveGhostRandom(RD, ghosts.get(i));
                        }
                        if (crossing == 2) {
                        	moveGhostRandom(LD, ghosts.get(i));
                        }
                        if (crossing == 3) {
                        	moveGhostRandom(RU, ghosts.get(i));
                        }
                        if (crossing == 4) {
                        	moveGhostRandom(LU, ghosts.get(i));
                        }
                        if (crossing == 5) {
                        	moveGhostRandom(RDU, ghosts.get(i));
                        }
                        if (crossing == 6) {
                        	moveGhostRandom(LDU, ghosts.get(i));
                        }
                        if (crossing == 7) {
                        	moveGhostRandom(RLD, ghosts.get(i));
                        }
                        if (crossing == 8) {
                        	moveGhostRandom(RLU, ghosts.get(i));
                        }
                        if (crossing == 9) {
                        	moveGhostRandom(RLUD, ghosts.get(i));
                        }
                    }
                }
            }

            //get direction after calculate
            int ghostCurDir = ghosts.get(i).getDir();

            if (ghostCurDir == UP) // up
            {
                gY = gY - gNormalSpeed;
            }
            if (ghostCurDir == DOWN) // down
            {
                gY = gY + gNormalSpeed;
            }
            if (ghostCurDir == RIGHT) // right
            {
                gX = gX + gNormalSpeed;
            }
            if (ghostCurDir == LEFT) // left
            {
                gX = gX - gNormalSpeed;
            }

            // set new location of ghost after moving
            ghosts.get(i).setX(gX);
            ghosts.get(i).setY(gY);

            //check collision for pacmon
            checkCollision(gX, gY);
            //check other collision for pacmon2
            checkCollision2(gX, gY);

        }
    }

    private void moveGhostSmart(int index, CMMonster ghost) {
        int pX = pacmon.getpX();
        int pY = pacmon.getpY();
        if (ghost.getY() > pY && ghostArray[index].contains(UP)) {
            ghost.setDir(UP);
        } else if (ghost.getY() < pY && ghostArray[index].contains(DOWN)) {
            ghost.setDir(DOWN);
        } else if (ghost.getX() > pX && ghostArray[index].contains(LEFT)) {
            ghost.setDir(LEFT);
        } else if (ghost.getX() < pX && ghostArray[index].contains(RIGHT)) {
            ghost.setDir(RIGHT);
        } else // if no possible smart direction, move ghost randomly
        {
            moveGhostRandom(index, ghost);
        }



    }

    private void moveGhostSmart2(int index, CMMonster ghost) {
        int pX = pacmon2.getpX();
        int pY = pacmon2.getpY();
        if (ghost.getY() > pY && ghostArray[index].contains(UP)) {
            ghost.setDir(UP);
        } else if (ghost.getY() < pY && ghostArray[index].contains(DOWN)) {
            ghost.setDir(DOWN);
        } else if (ghost.getX() > pX && ghostArray[index].contains(LEFT)) {
            ghost.setDir(LEFT);
        } else if (ghost.getX() < pX && ghostArray[index].contains(RIGHT)) {
            ghost.setDir(RIGHT);
        } else // if no possible smart direction, move ghost randomly
        {
            moveGhostRandom(index, ghost);
        }

    }

    // move ghost using directional array
    private void moveGhostRandom(int index, CMMonster ghost) {
        int n = (int) (Math.random() * ghostArray[index].size()); // randomize
        int d = ghostArray[index].get(n);  //apply random to get direction

        ghost.setDir(d);


    }

    //check if ghost touch player
    private void checkCollision(int gX, int gY) {
        int pX = pacmon.getpX();
        int pY = pacmon.getpY();
        int radius = 10;

        if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) //ghost touches player
        {
            diePacmon();
        }
    }

    private void checkCollision2(int gX, int gY) {
        int pX = pacmon2.getpX();
        int pY = pacmon2.getpY();
        int radius = 10;

        if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) //ghost touches player
        {
            diePacmon2();
        }
    }

    //when ghost touches player, pacmon dies
    private void diePacmon() {
        lives--;
        gameState = DIE;
//        tickCounter = -120;
        pacmon.reset(32, 32);
        
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).reset();
        }
        
        soundEngine.stopMusic();
		soundEngine.playDie();

        if (lives == 0) {
            gameState = GAMEOVER;
            //soundEngine.endMusic();
			soundEngine.playGameOver();
        }

    }

    private void diePacmon2() {
        lives2--;
        gameState = DIE;
//        tickCounter = -120;
        pacmon2.reset(416, 640);


        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).reset();
        }
        
        soundEngine.stopMusic();
		soundEngine.playDie();

        if (lives2 == 0) {
            gameState = GAMEOVER;
            //soundEngine.endMusic();
         			soundEngine.playGameOver();
        }

    }

    // eat food ==> score and power ==> speed
    private synchronized void eatFoodPower(int boxX, int boxY) {
    	
    	if (mazeArray[boxY][boxX] == 1){
    		key = (boxX*100) + boxY;
    		
    		//the hex value of the cherry eaten
    		value = mazeDataCompressor.mazeMap.get(key);
    		
    		//the hex value of the number cherry in the row
    		int temp = mazeDataCompressor.rowMap.get(boxY) & value;
    		mazeDataCompressor.rowMap.put(boxY, temp);
    		
    		mazeData1 = (boxY * 100) + boxX;
			mazeArray[boxY][boxX] = 5;
			playerScore++;   // increase score
			
			soundEngine.playEatCherry();
				
			if (playerScore + playerScore2 == maze.getFoodCount()){
				gameState = WON;
				soundEngine.stopMusic();
			}	
		}
		
		// eat power
		if (mazeArray[boxY][boxX] == 2){
			key = (boxX*100) + boxY;
    		
    		//the hex value of the cherry eaten
    		value = mazeDataCompressor.mazeMap.get(key);
    		
    		//the hex value of the number cherry in the row
    		int temp = mazeDataCompressor.rowMap.get(boxY) & value;
    		mazeDataCompressor.rowMap.put(boxY, temp);
    		
			
			mazeData1 = (boxY * 100) + boxX;
			mazeArray[boxY][boxX] = 5;
			this.powerMode1 = 5;// blank
		}
    }
    // eat food ==> score and power ==> speed

    private synchronized void eatFoodPower2(int boxX, int boxY) {

    	if (mazeArray[boxY][boxX] == 1){
    		key = (boxX*100) + boxY;
    		
    		//the hex value of the cherry eaten
    		value = mazeDataCompressor.mazeMap.get(key);
    		
    		//the hex value of the number cherry in the row
    		int temp = mazeDataCompressor.rowMap.get(boxY) & value;
    		mazeDataCompressor.rowMap.put(boxY, temp);
    		
    		mazeData2 = (boxY * 100) + boxX;
			mazeArray[boxY][boxX] = 5;
			playerScore2++;   // increase score
			
			p2eatcherry=1;
			
			//soundEngine.playEatCherry();		
			if (playerScore2 + playerScore == maze.getFoodCount()){
				gameState = WON;
				//soundEngine.stopMusic();
			}	
		}
		
		// eat power
		if (mazeArray[boxY][boxX] == 2){
			key = (boxX*100) + boxY;
    		
    		//the hex value of the cherry eaten
    		value = mazeDataCompressor.mazeMap.get(key);
    		
    		//the hex value of the number cherry in the row
    		int temp = mazeDataCompressor.rowMap.get(boxY) & value;
    		mazeDataCompressor.rowMap.put(boxY, temp);
    		
    		
			mazeData2 = (boxY * 100) + boxX;
			mazeArray[boxY][boxX] = 5;
			this.powerMode2 = 5;// blank
		}
    }

    // count down timer once per MAX_FPS
    private void updateTimer() {
        timerCount++;
        if (timerCount % 40 == 0) {
            timer--;
            powerMode1--;
            powerMode2--;
            timerCount = 0;
        }
        if (timer == -1) {
            gameState = GAMEOVER;  // LOST
            soundEngine.stopMusic();
			soundEngine.playGameOver();
        }
    }

    public void run() {
        while (isRunning) {
            if (gameState == READY) {
                updateReady();
            }
            if (gameState == RUNNING) {
                updateRunning();
            }
            if (gameState == GAMEOVER) {
                updateGameOver();
            }
            if (gameState == WON) {
                updateWon();
            }
            if(gameState == SEARCHING) {
            	updateSearching();
            }
            if(gameState == DISCONNECTED) {
            	updateDisconnected();
            }
            if(gameState == DIE) {
            	updateDie();
            }

        }
    }
    
    public void updateDie() {
		beginTime = System.currentTimeMillis();
	
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
		
		this.tickCounter++;
		timeDiff += System.currentTimeMillis() - beginTime;
		//wait for 1.2 second
		if(timeDiff >= 1000){
			soundEngine.playMusic();
			gameState = RUNNING;
		}
	}
    
    private void updateDisconnected(){
    	  beginTime = System.currentTimeMillis();
          framesSkipped = 0; // resetting the frames skipped

          update();
          isRunning=false;
//          // calculate how long did the cycle take
//          timeDiff = System.currentTimeMillis() - beginTime;
//          // calculate sleep time
//          sleepTime = (int) (FRAME_PERIOD - timeDiff);

//          if (sleepTime > 0) {
//              // if sleepTime > 0 we're OK
//              try {
//                  // send the thread to sleep for a short period
//                  // very useful for battery saving
//                  Thread.sleep(sleepTime);
//              } catch (InterruptedException e) {
//              }
//          }
    
    	
    }
    
    
    
    private void updateSearching(){
    	
    	try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    // loop through ready if gameState is READY
    private void updateReady() {
        beginTime = System.currentTimeMillis();

        readyCountDown = 3L - timeDiff / 1000;
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
        try {
            // send the thread to sleep for a short period
            // very useful for battery saving
            Thread.sleep(25);
        } catch (InterruptedException e) {
        }

        
        soundEngine.playMusic();
        if (this.tickCounter >= 0) {

//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        	
            gameState = RUNNING;
            //   System.out.println("GO AWAY");

        }

        this.tickCounter++;
        //System.out.println("THIS IS TICK updatREADY::"+ this.tickCounter);

    }

    // loop through running if gameState is RUNNING
    private void updateRunning() {
        beginTime = System.currentTimeMillis();
        framesSkipped = 0; // resetting the frames skipped

        update();


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
    }

    private void updateGameOver() {
        pause();
    }

    private void updateWon() {
        pause();
    }

    public void pause() {
        isRunning = false;
    }

    public void resume() {
        isRunning = true;
    }

    // using accelerometer to set direction of player
    public void setInputDirPlayer1(int dir) {
        this.inputDirection = dir;

    }

    public void setInputDirPlayer2(int dir) {
    	
    	if(dir==-99)
    	{
    		this.gameState=DISCONNECTED;
    	}
        this.inputDirection2 = dir;
    }

    public CMMaze getMaze() {
        return this.maze;
    }

    public int[][] getMazeArray() {
        return this.mazeArray;
    }

    public int getMazeRow() {
        return this.mazeRow;
    }

    public int getMazeColumn() {
        return this.mazeColumn;
    }

    public String getTimer() {
        return String.valueOf(timer);
    }

    public String getPlayerScore() {
        return "Score: " + playerScore;
    }

    public int getGameState() {
        return gameState;
    }

    public long getReadyCountDown() {
       // return readyCountDown;
    	if (this.tickCounter <-80)
    		return 3;
    	else if(tickCounter >-80 && tickCounter < -40)
    		return 2;
    	else
    		return 1;
    	
    }

    public int[] returnPacmonPxPy() {
        int x[] = {this.pacmon.getpX(), this.pacmon.getpY(), this.pacmon.getDir()};

        return x;
    }

    public int[] returnPacmon2PxPy() {
        int x[] = {this.pacmon2.getpX(), this.pacmon2.getpY(), this.pacmon2.getDir()};

        return x;
    }

    public int[] returnGhost(int i) {
        int x[] = {this.ghosts.get(i).getX(), this.ghosts.get(i).getY(), this.ghosts.get(i).getDir()};

        return x;
    }

    public int[] returnPacLives() {
        int x[] = {this.lives, this.lives2};
        return x;
    }

    public int[] returnScores() {
        int x[] = {this.playerScore, this.playerScore2};
        return x;
    }

    public int[] returnMazeData() {
        int x[] = {this.mazeData1, this.mazeData2};
        return x;
    }
    //methods used for local client side
    public String[] getScores()
	{
		int y[]={this.playerScore, this.playerScore2};
		String x[]={"Score:" +String.valueOf(y[0]), "Score:"+String.valueOf(y[1])};
		
		this.totalScore=this.playerScore+this.playerScore2;
		return x;
	}
    
    public String[] getLives()
	{
		String x[]={"P1 lives:" + String.valueOf(this.lives), "P2 lives:"+String.valueOf(this.lives2) };
		return x;
	}
       
}
