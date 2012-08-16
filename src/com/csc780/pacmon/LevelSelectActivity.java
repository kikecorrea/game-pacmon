package com.csc780.pacmon;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class LevelSelectActivity extends Activity implements OnClickListener{
	private final int levelCount=5;
	private Button []btnlevel =new Button[levelCount];
	private Button level2;
    private SQLiteDatabase levelDB;
    private int [] levelStatus ={-5,-5,-5,-5,-5};
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_selection);
			
		btnlevel[0] = (Button) findViewById(R.id.bLevel1);
		btnlevel[1] = (Button) findViewById(R.id.bLevel2);
		btnlevel[2] = (Button) findViewById(R.id.bLevel3);
		btnlevel[3] = (Button) findViewById(R.id.bLevel4);
		btnlevel[4] = (Button) findViewById(R.id.bLevel5);
		
		btnlevel[0].setOnClickListener(this);
		btnlevel[1].setOnClickListener(this);
		btnlevel[2].setOnClickListener(this);
		btnlevel[3].setOnClickListener(this);
		btnlevel[4].setOnClickListener(this);
		
		btnlevel[0].setText("level ");
		btnlevel[0].setTextColor(Color.GREEN);
		btnlevel[1].setText("level ");
		btnlevel[1].setTextColor(Color.GREEN);
		btnlevel[2].setText("level ");
		btnlevel[2].setTextColor(Color.GREEN);
		btnlevel[3].setText("level ");
		btnlevel[3].setTextColor(Color.GREEN);
		btnlevel[4].setText("level ");
		btnlevel[4].setTextColor(Color.GREEN);

	}
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            /*
             * If no error occurred, use the Intent to retrieve an integer
             * stored under ID "CALLS".
             */
            int level = data.getExtras().getInt("level");
            int status = data.getExtras().getInt("status");
            System.out.println("RETURN: LEVEL::" + level + "  status::" + status);
            /*
             * Display the integer in the BlueActivity's TextView.
             */
            if(status!=-1)
            {
            this.updateDB(level, status);
     
            }
           
        }
    }
    
    public void updateDB(int level, int status)
    {
    	levelDB = openOrCreateDatabase("level.db",SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.OPEN_READWRITE, null);
    	ContentValues cv = new ContentValues();
    	try{
    	//for first level
    	cv.put("finish", status); 
        levelDB.update("level", cv, "id=" + level, null);
        System.out.println("UPDATED");
    	}catch(SQLException e){
			e.printStackTrace();
		}
    	cv.clear();
    	levelDB.close();
    }
    
    @Override
    protected void onResume()
    {
    	levelDB = openOrCreateDatabase("level.db",SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.OPEN_READWRITE, null);
    	
    	levelDB.execSQL("create table if not exists level (id int primary key, stage int, finish int);");
    //	levelDB.execSQL("drop table if exists level");
    	ContentValues cv = new ContentValues();
    	try{
    	//for first level
    	cv.put("id", 1);
    	cv.put("stage", 1);
    	cv.put("finish", 1); 
    	//levelDB.update("level", cv, "stage=1", null);
        levelDB.insertOrThrow("level", null, cv);
    	}catch(SQLException e){
			//e.printStackTrace();
		}
   	
    	   for(int i=2; i<=levelCount; i++)
    	   {
    		try{
    			cv = new ContentValues();
    			cv.put("id", i);
    			cv.put("stage", i);
    			cv.put("finish", 0); 
    			//levelDB.update("level", cv, "id=" + i, null);
    			levelDB.insertOrThrow("level", null, cv);
    		}catch(SQLException e){
    			//e.printStackTrace();
    		}
       	   }

    	Cursor c = levelDB.query("level", new String[]{"id", "stage", "finish"}, null, null, null, null, null);
  
    	while( c.moveToNext())
    	{
    		int id = c.getInt(0);
    		int level = c.getInt(1);
    		int finish = c.getInt(2);
    		//System.out.println("id::" + id + "  level::" +level + "  status::" + finish);
    		levelStatus[level-1] = finish;
    	}
    	c.close();
    	
     	levelDB.close();
     	
     	this.setLevel();
     	
    	super.onResume();
    }
    
    private void setLevel()
    {
    	for(int i=0; i<levelCount; i++)
    	{
    		if(levelStatus[i]==1)
    		{
    			int temp=i+1;
    			btnlevel[i].setText("level " + temp);
    			btnlevel[i].setTextColor(Color.GREEN);
    			btnlevel[i].setEnabled(true);
    		}
    		else{

    			int temp=i+1;
    			btnlevel[i].setText("level " + temp );
    			btnlevel[i].setTextColor(Color.RED);
    			btnlevel[i].setEnabled(false);
    		}
    	}
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		
		case R.id.bLevel1: //start single player game
			Intent sGame = new Intent("com.csc780.pacmon.GAMEACTIVITY");
			sGame.putExtra("level", 1);
			startActivityForResult(sGame, 0);
			break;
			
		case R.id.bLevel2: //finish the activity
			Intent sGame2 = new Intent("com.csc780.pacmon.GAMEACTIVITY");
			sGame2.putExtra("level", 2);
			startActivityForResult(sGame2, 0);
			break;
			
		case R.id.bLevel3: //finish the activity
			Intent sGame3 = new Intent("com.csc780.pacmon.GAMEACTIVITY");
			sGame3.putExtra("level", 3);
			startActivityForResult(sGame3, 0);
			break;
		
		case R.id.bLevel4: //finish the activity
			Intent sGame4 = new Intent("com.csc780.pacmon.GAMEACTIVITY");
			sGame4.putExtra("level", 4);
			startActivityForResult(sGame4, 0);
			break;
			
		case R.id.bLevel5: //finish the activity
			Intent sGame5 = new Intent("com.csc780.pacmon.GAMEACTIVITY");
			sGame5.putExtra("level", 5);
			startActivityForResult(sGame5, 0);
			break;
		}
	}

	
}
