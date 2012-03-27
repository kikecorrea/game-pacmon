package com.csc780.pacmon;

import java.util.ArrayList;

public class Maze {

	private int mazeMaxRow, mazeMaxColumn; //by grids
	
	private static int maze1[][] = {
		{0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0},
		{0,1,1,1,0,1,1,0,0,0,0,0,0,1,1,0,1,1,1,0},
		{0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0},
		{0,0,1,1,1,0,1,1,0,0,0,0,1,1,0,1,1,1,0,0},
		{1,0,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,0,1},
		{0,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,0},
		{0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0},
		{0,0,0,1,0,1,0,1,1,1,1,1,1,0,1,0,1,0,0,0},
		{1,1,0,1,0,1,0,1,0,0,0,0,1,0,1,0,1,0,1,1},
		{1,1,0,0,0,1,0,1,1,1,1,1,1,0,1,0,0,0,1,1},
		{1,1,0,0,0,1,0,1,1,1,1,1,1,0,1,0,0,0,1,1},
		{1,1,0,1,0,1,0,1,0,0,0,0,1,0,1,0,1,0,1,1},
		{0,0,0,1,0,1,0,1,1,1,1,1,1,0,1,0,1,0,0,0},
		{0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0},
		{0,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,0},
		{1,0,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,0,1},
		{1,0,1,0,1,0,1,1,0,0,0,0,1,1,0,1,0,1,0,1},
		{0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0},
		{0,1,1,1,0,1,1,0,0,0,0,0,0,1,1,0,1,1,1,0},
		{0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0}
		
	};
	
	static int maze2[][] = {
		{0,0,0,0,1,1,0,0,0,0},
		{1,0,1,0,0,0,0,1,0,1},
		{1,0,1,0,1,1,0,1,0,1},
		{1,0,1,0,1,1,0,1,0,1},
		{0,0,0,0,0,0,0,0,0,0},
		{0,1,0,1,1,1,1,0,1,0},
		{0,1,0,1,0,0,1,0,1,0},
		{0,1,0,1,1,1,1,0,1,0},
		{0,0,0,0,0,0,0,0,0,0},
		{1,0,1,0,1,1,0,1,0,1},
		{1,0,1,0,1,1,0,1,0,1},
		{1,0,1,0,0,0,0,1,0,1},
		{0,0,0,0,1,1,0,0,0,0}
	};
	
	// 0 = wall, 1 = food, 2 = power, 3 = ghost door, 5 = blank, 6 = ghost
	private int maze3[][] = {
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},  //row 1
			{0,1,1,1,1,0,1,1,1,0,1,1,1,1,0},
			{0,2,0,0,1,0,1,0,1,0,1,0,0,2,0},
			{0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
			{0,0,1,0,0,0,0,1,0,0,0,0,1,0,0},  
			{0,0,1,1,1,1,1,2,1,1,1,1,1,0,0},
			{0,0,1,0,0,1,0,0,0,1,0,0,1,0,0},
			{0,1,1,5,5,5,5,5,5,5,5,5,1,1,0},
			{0,1,0,0,5,0,0,0,0,0,5,0,0,1,0},
			{0,1,0,0,5,0,6,6,6,0,5,0,0,1,0},  //row 10, monster
			{0,1,0,0,5,0,0,3,0,0,5,0,0,1,0},
			{0,1,1,5,5,5,5,5,5,5,5,5,1,1,0},
			{0,0,1,0,0,1,0,0,0,1,0,0,1,0,0},
			{0,0,1,1,1,1,1,2,1,1,1,1,1,0,0}, //row 14
			{0,0,1,0,0,0,0,1,0,0,0,0,1,0,0},
			{0,0,1,0,1,1,1,1,1,1,1,0,1,0,0},
			{0,1,1,1,1,0,0,1,0,0,1,1,1,1,0},
			{0,1,0,0,0,0,0,1,0,0,0,0,0,1,0},
			{0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
			{0,2,0,0,1,0,1,0,1,0,1,0,0,2,0},
			{0,1,1,1,1,0,1,1,1,0,1,1,1,1,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};
	
  // t1[0]= 0;
  // t1[1]= 9; // rd (right and down)
  // t1[2]=10; // ld
  // t1[3]= 5; // ru
  // t1[4]= 6; // lu
  // t1[5]=13; // rdu
  // t1[6]=14; // ldu
  // t1[7]=11; // rld
  // t1[8]= 7; // rlu
  // t1[9]=15; // rlud
	
	private int directionMaze[][] = {
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // row 1
			{0,1,0,0,2,0,1,0,2,0,1,0,0,2,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,5,7,0,8,0,8,7,8,0,8,0,7,4,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},  
			{0,0,5,0,0,7,0,8,0,7,0,0,6,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,1,8,0,7,6,0,0,0,6,7,0,8,2,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //row 10, ghost cage
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,3,7,0,8,7,0,7,0,7,0,8,7,4,0}, //monster door step, row 12
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,5,0,0,8,0,7,0,8,0,0,6,0,0}, // row 14
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,0,0,9,0,0,2,0,0,0,0},
			{0,1,8,0,4,0,0,0,0,0,3,0,8,2,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,5,0,0,7,0,7,8,7,0,7,0,0,6,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,3,0,0,4,0,3,0,4,0,3,0,0,4,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
	};
/*	
	private int t2[][] = {
			1];
		    t2[2]= [2];
		    t2[4]= [4];
		    t2[8]= [8];
		    t2[3]= [1, 2];
		    t2[9]= [1, 8];
		    t2[10]=[2, 8];
		    t2[12]=[4, 8];
		    t2[5]= [1, 4];
		    t2[6]= [2, 4];
		    t2[7]= [1, 2, 4];
		    t2[11]=[1, 2, 8];
		    t2[13]=[1, 4, 8];
		    t2[14]=[2, 4, 8];
		    t2[15]=[1, 2, 4, 8];;
	}
*/
	private ArrayList<Integer> t2[] = new ArrayList[16];
	
	public Maze() {
		mazeMaxRow = 22;
		mazeMaxColumn = 15;
		initArray();
	}

	public int[][] getMaze() {
		return maze3;
	}
	
	public int getMazeRow(){
		return mazeMaxRow;
	}
	
	public int getMazeColumn(){
		return mazeMaxColumn;
	}
	
	public void clearFood(int x, int y){
		maze3[x][y] = 5;
	}
	
	public int[][] getDirectionMaze(){
		return directionMaze;
	}
	
	public ArrayList<Integer>[] getGhostArray(){
		return t2;
	}
	
	private void initArray(){
		for (int i = 0; i < 16; i++){
			t2[i] = new ArrayList<Integer>();
		}
		
		t2[1].add(1);
	    t2[4].add(4);
	    t2[8].add(8);
	    t2[3].add(1); t2[3].add(2);
	    t2[9].add(1); t2[9].add(8);
	    t2[10].add(2); t2[10].add(8);
	    t2[12].add(4); t2[12].add(8);
	    t2[5].add(1); t2[5].add(4);
	    t2[6].add(2); t2[6].add(4);
	    
	    t2[7].add(1); t2[7].add(2); t2[7].add(4);
	    t2[11].add(1); t2[11].add(2); t2[11].add(8);
	    t2[13].add(1); t2[13].add(4); t2[13].add(8);
	    t2[14].add(2); t2[14].add(4); t2[14].add(8);
	    
	    t2[15].add(1); t2[15].add(2); t2[15].add(4); t2[15].add(8);
	}
}



/*Maze layout
		{"WWWWWWWWWWWWWWW"},
		{"W....W...W....W"},
		{"WoWW.WW.WW.WWoW"},
		{"W.............W"},
		{"WW.WWWW.WWWW.WW"},
		{"WW.....o.....WW"},
		{"WW.WW.WWW.WW.WW"},
		{"W..---------..W"},
		{"W.WW-WWDWW-WW.W"},
		{"W.WW-WGGGW-WW.W"},
		{"W.WW-WWDWW-WW.W"},
		{"W..---------..W"},
		{"WW.WW.WWW.WW.WW"},
		{"WW.....o.....WW"},
		{"WW.WWWW.WWWW.WW"},
		{"WW.W.......W.WW"},
		{"W....WW.WW....W"},
		{"W.WWWWW.WWWWW.W"},
		{"W.............W"},
		{"WoWW.WW.WW.WWoW"},
		{"WWWWWWWWWWWWWWW"}
		
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		{0,1,1,1,1,0,1,1,1,0,1,1,1,1,0},
		{0,2,0,0,1,0,0,1,0,0,1,0,0,2,0},
		{0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
		{0,0,1,0,0,0,0,1,0,0,0,0,1,0,0},
		{0,0,1,0,0,0,0,1,0,0,0,0,1,0,0},
		{0,0,1,1,1,1,1,2,1,1,1,1,1,0,0},
		{0,0,1,0,0,1,0,0,0,1,0,0,1,0,0},
		{0,1,1,5,5,5,5,5,5,5,5,5,1,1,0},
		{0,1,0,0,5,0,0,3,0,0,5,0,0,1,0},
		{0,1,0,0,5,0,6,6,6,0,5,0,0,1,0},
		{0,1,0,0,5,0,0,0,0,0,5,0,0,1,0},
		{0,1,1,5,5,5,5,5,5,5,5,5,1,1,0},
		{0,0,1,0,0,1,0,0,0,1,0,0,1,0,0},
		{0,0,1,1,1,1,1,2,1,1,1,1,1,0,0},
		{0,0,1,0,0,0,0,1,0,0,0,0,1,0,0},
		{0,0,1,0,1,1,1,1,1,1,1,0,1,0,0},
		{0,1,1,1,1,0,0,1,0,0,1,1,1,1,0},
		{0,1,0,0,0,0,0,1,0,0,0,0,0,1,0},
		{0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
		{0,2,0,0,1,0,0,1,0,0,1,0,0,2,0},
		{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
*/

