package com.csc780.multipacmon;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.util.Log;

public class CircularQue {
	
	private ModelData que[];
        private AtomicInteger readPointer;
        private AtomicInteger writePointer;
        volatile public int QueSize;
    private int defaultData[]={-1,-2,-3};
    public int x[]=new int[18];
	
	public CircularQue(int size)
	{
			readPointer = new AtomicInteger(0);
			writePointer = new AtomicInteger(0);
			
			
            que= new ModelData[size];
            for(int i=0; i<size; i++)
                que[i]=new ModelData();        
            //-1 cause end of que is size-1
            QueSize=size-1;
	}
        
		public synchronized void write(int p1x, int p1y, int p1z, int p2x,int p2y,int p2z,int g1x, int g1y,int g1z,
     		int g2x, int g2y,int g2z, int g3x, int g3y,int g3z,int g4x, int g4y,int g4z)
        {
//            que[writePointer].setData(x, y, z);
//        	
//        	//check if at the end of list, if true we move pointer to the beginning of que
//           if(writePointer%QueSize==0 && writePointer!=0)
//               writePointer=0;
//           else
//               writePointer++;      
        	
            //check if readPOinter is front of me,
//            if(writePointer==(readPointer-1) || (readPointer==0 && writePointer%QueSize==0))
//            {
//            	//move the read pointer 
//            	if(readPointer%QueSize==0 && readPointer!=0)
//            		readPointer=1;
//            	else{
//            		readPointer+=1;
//            	}
//            	Log.d("move pointer", "move pointer");
//            	
//             que[writePointer].setData(x, y, z);
//            }
//            else{
        	 que[writePointer.get()].setData(p1x, p1y, p1z, p2x,p2y,p2z,g1x,g1y,g1z,g2x,g2y,g2z,g3x,g3y,g3z, g4x,g4y,g4z);
            //}
            
            //check if at the end of list, if true we move pointer to the beginning of que
//            if(writePointer.get()%QueSize==0 && writePointer.get()!=0)
//                writePointer.set(0);
//            else
//            	 writePointer.set(writePointer.get()+1);            
        }
        
        public synchronized int [] read()
        {
//            if(readPointer==writePointer)
//            {
//            	//default values is -1,-2, -3 so gameView just render old values
//            	Log.d("default data", "default data");
//            	return defaultData;
//            }
//
//            if(readPointer.get()%QueSize==0 && readPointer.get()!=0)
//                readPointer.set(0);
//            else
//            	  readPointer.set(readPointer.get()+1);
            
            x[0]=que[readPointer.get()].p1x;
            x[1]=que[readPointer.get()].p1y;
            x[2]=que[readPointer.get()].p1z;
            x[3]=que[readPointer.get()].p2x;
            x[4]=que[readPointer.get()].p2y;
            x[5]=que[readPointer.get()].p2z;
            x[6]=que[readPointer.get()].g1x;
            x[7]=que[readPointer.get()].g1y;
            x[8]=que[readPointer.get()].g1z;
            x[9]=que[readPointer.get()].g2x;
            x[10]=que[readPointer.get()].g2y;
            x[11]=que[readPointer.get()].g2z;
            x[12]=que[readPointer.get()].g3x;
            x[13]=que[readPointer.get()].g3y;
            x[14]=que[readPointer.get()].g3z;
            x[15]=que[readPointer.get()].g4x;
            x[16]=que[readPointer.get()].g4y;
            x[17]=que[readPointer.get()].g4z;
        return x;
        }
        
        
        
}

//this class represents the data for each model i.e. pacmon, ghost
class ModelData
{
	
	
	public int p1x, p1y, p1z, p2x,p2y,p2z,g1x,g1y,g1z,g2x,g2y,g2z,g3x,g3y,g3z, g4x,g4y,g4z;
             
	public ModelData(){
		
	}

  public void setData(int p1x, int p1y, int p1z, int p2x,int p2y,int p2z,int g1x, int g1y,int g1z,
  		int g2x, int g2y,int g2z, int g3x, int g3y,int g3z,int g4x, int g4y,int g4z)
  {
  	this.p1x=p1x;
  	this.p1y=p1y;
  	this.p1z=p1z;
  	this.p2x=p2x;
  	this.p2y=p2y;
  	this.p2z=p2z;
  	this.g1x=g1x;
  	this.g1y=g1y;
  	this.g1z=g1z;
  	this.g2x=g2x;
  	this.g2y=g2y;
  	this.g2z=g2z;
  	this.g3x=g3x;
  	this.g3y=g3y;
  	this.g3z=g3z;
  	this.g4x=g4x;
  	this.g4y=g4y;
  	this.g4z=g4z;
  }
}




