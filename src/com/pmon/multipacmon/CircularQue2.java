package com.pmon.multipacmon;


public class CircularQue2 {
	
	private ModelData2 que[];
        volatile private int readPointer=0;
        volatile private int writePointer=0;
        volatile public int QueSize;
    private int defaultData=-1;
    public int x;
	
	public CircularQue2(int size)
	{
            que= new ModelData2[size];
            for(int i=0; i<size; i++)
                que[i]=new ModelData2();        
            //-1 cause end of que is size-1
            QueSize=size-1;
	}
        
        public void write(int x)
        {

            que[writePointer].setData(x);
        	
        	//check if at the end of list, if true we move pointer to the beginning of que
           if(writePointer%QueSize==0 && writePointer!=0)
               writePointer=0;
           else
               writePointer++;      
        	
            //check if readPOinter is front of me,
//            if(writePointer==(readPointer-1) || (readPointer==0 && writePointer%QueSize==0))
//            {
//            	//move the read pointer 
//            	if(readPointer%QueSize==0 && readPointer!=0)
//            		readPointer=1;
//            	else{
//            		readPointer+=1;
//            	}
//             que[writePointer].setData(x);
//            }
//            else{
//            que[writePointer].setData(x);   
//       
//           }
//            
//            //check if at the end of list, if true we move pointer to the beginning of que
//            if(writePointer%QueSize==0 && writePointer!=0)
//                writePointer=0;
//            else
//                writePointer++;      
        }
        
        public int read()
        {
            if(readPointer==writePointer)
            {
            	//default values is -1,-2, -3 so gameView just render old values
            	return defaultData;
            }

            if(readPointer%QueSize==0 && readPointer!=0)
                readPointer=0;
            else
                readPointer++;
            
             x=que[readPointer].x;
         
        return x;
        }
        
}

//this class represents the data for each model i.e. pacmon, ghost
class ModelData2
{
	public int x;
         
	public ModelData2(){
		
	}
	
    public ModelData2(int x)
    {
        this.x=x;
    }
    
    public void setData(int x)
    {
    	this.x=x;
    }
}




