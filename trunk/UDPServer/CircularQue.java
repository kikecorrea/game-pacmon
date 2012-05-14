/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver_pacmon;

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
        
        public void write(int x, int y)
        {
            //check if readPOinter is behind me,
            if(writePointer==(readPointer-1))
            {
                readPointer=(QueSize/2) - 1;
                
                que[writePointer]=new pairXY(x,y);
            }
            else
                que[writePointer]=new pairXY(x,y);
            
            
            //check if at the end of list, so that we switch it to the beginning
            if(writePointer%QueSize==0 && writePointer!=0)
                writePointer=0;
            else
                writePointer++;
//        this.notify();
    
           
        }
        
        public int [] read()
        {
            while(readPointer==writePointer)
            {
 
//                try {
//                    t.wait();
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(CircularQue.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
            pairXY temp=que[readPointer];
            
            int x[]={temp.x, temp.y};
            
            if(readPointer%QueSize==0 && readPointer!=0)
                readPointer=0;
            else
                readPointer++;
        return x;
        }
        
        
        
//        public static void main(String args[])
//        {
//            
//            
//            CircularQue qq=new CircularQue(6);
//            
//            System.out.println(qq.que[5].x);
//        }
	

}

class pairXY
{
	public int x;
	public int y;
        
        public pairXY()
        {
            
        }
        
        public pairXY(int x, int y)
        {
            this.x=x;
            this.y=y;
        }
}




