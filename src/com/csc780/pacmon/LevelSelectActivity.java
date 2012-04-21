package com.csc780.pacmon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class LevelSelectActivity extends Activity implements OnClickListener{
	private Button level1;
	private Button level2;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_selection);
		
		level1 = (Button) findViewById(R.id.bLevel1);
		level2 = (Button) findViewById(R.id.bLevel2);

		level1.setOnClickListener(this);
		level2.setOnClickListener(this);
		
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		
		case R.id.bLevel1: //start single player game
			Intent sGame = new Intent("com.csc780.pacmon.GAMEACTIVITY");
			sGame.putExtra("level", 1);
			startActivity(sGame);
			break;
			
		case R.id.bLevel2: //finish the activity
			Intent sGame2 = new Intent("com.csc780.pacmon.GAMEACTIVITY");
			sGame2.putExtra("level", 2);
			startActivity(sGame2);
			break;
		
		}
	}

	
}
