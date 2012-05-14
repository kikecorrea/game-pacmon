package com.csc780.multipacmon;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CircularQue {
	
	private ModelData que[];
        volatile public int readPointer=0;
        volatile public int writePointer=0;
        volatile public int QueSize;
	
	public CircularQue(int size)
	{
            que= new ModelData[size];
            for(int i=0; i<size; i++)
                que[i]=new ModelData();        
            //-1 cause end of que is size-1
            QueSize=size-1;
	}
        
        public void write(int x, int y, int z)
        {
            //check if readPOinter is front of me,
            if(writePointer==(readPointer-1))
            {
            	if(readPointer%QueSize==0 && readPointer!=0)
            		readPointer=1;
            	else
            		readPointer+=1;
                que[writePointer]=new ModelData(x,y,z);
            }
            else{
            que[writePointer]=new ModelData(x,y,z);     
            }
            
            //check if at the end of list, if true we move pointer to the beginning of que
            if(writePointer%QueSize==0 && writePointer!=0)
                writePointer=0;
            else
                writePointer++;      
        }
        
        public int [] read()
        {
            if(readPointer==writePointer)
            {
            	//default values is -1,-2, -3 so gameView just render old values
            	int x[]={-1,-2,-3};
            	return x;
            }

            if(readPointer%QueSize==0 && readPointer!=0)
                readPointer=0;
            else
                readPointer++;
            
            ModelData temp=que[readPointer];
            int x[]={temp.x, temp.y, temp.z};
        return x;
        }
        
}

//this class represents the data for each model i.e. pacmon, ghost
class ModelData
{
	public int x;
	public int y;
	public int z;
               
	public ModelData(){
		
	}
	
    public ModelData(int x, int y, int z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }
}




