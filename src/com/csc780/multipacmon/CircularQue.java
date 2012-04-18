package com.csc780.multipacmon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.logging.Level;
import java.util.logging.Logger;

public class CircularQue {
	
	private pairXY que[];
        volatile public int readPointer=0;
        volatile public int writePointer=0;
        volatile public int QueSize;
	
	public CircularQue(int size)
	{
            que= new pairXY[size];
            
            for(int i=0; i<size; i++)
                que[i]=new pairXY();
            
            //-1 cause end of que is size-1
            QueSize=size-1;
	}
        
        public void write(int x, int y, int z)
        {
            //check if readPOinter is behind me,
            if(writePointer==(readPointer-1))
            {
                readPointer=(QueSize/2) - 1;
               // System.out.println("move POinter POinter");
                que[writePointer]=new pairXY(x,y,z);
            }
            else
                que[writePointer]=new pairXY(x,y,z);
            
            
            //check if at the end of list, so that we switch it to the beginning
            if(writePointer%QueSize==0 && writePointer!=0)
                writePointer=0;
            else
                writePointer++;
//        this.notify();
    
           
        }
        
        public int [] read()
        {
        	
            if(readPointer==writePointer)
            {
            	//we sent to -1,-2 so gameView just render old values
            	int x[]={-1,-2,-3};
            	return x;
            }
            pairXY temp=que[readPointer];
            
            int x[]={temp.x, temp.y, temp.z};
            
            if(readPointer%QueSize==0 && readPointer!=0)
                readPointer=0;
            else
                readPointer++;
        return x;
        }
        
}

class pairXY
{
	public int x;
	public int y;
	public int z;
        
        public pairXY()
        {
            
        }
        
        public pairXY(int x, int y, int z)
        {
            this.x=x;
            this.y=y;
            this.z=z;
        }
}




