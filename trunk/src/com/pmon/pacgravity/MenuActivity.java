package com.pmon.pacgravity;

import com.pmon.pacgravity.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener{
	
	
	private Button singlePlayButton;
	private Button multiPlayButton;
	private Button aboutButton;
	private Button exitButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		//Typeface tf = Typeface.createFromAsset(getAssets(),
        //        "fonts/press_start.ttf");
		
		singlePlayButton = (Button) findViewById(R.id.bSingle);
		multiPlayButton = (Button) findViewById(R.id.bMulti);
	//	optionsButton = (Button) findViewById(R.id.bOptions);
		aboutButton = (Button) findViewById(R.id.bAbout);
		exitButton = (Button) findViewById(R.id.bExit);
/*		
		singlePlayButton.setTypeface(tf);
		multiPlayButton.setTypeface(tf);
		optionsButton.setTypeface(tf);
		aboutButton.setTypeface(tf);
		exitButton.setTypeface(tf);
*/	
		singlePlayButton.setOnClickListener(this);
		multiPlayButton.setOnClickListener(this);
//		optionsButton.setOnClickListener(this);
		aboutButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);
		
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		
		case R.id.bSingle: //start single player game
			Intent sGame = new Intent("com.pmon.pacgravity.LEVELSELECTACTIVITY");
			startActivity(sGame);
			break;
			
		case R.id.bMulti:
//			Intent mGame = new Intent("com.csc780.pacmon.MGAMEACTIVITY");
//			startActivity(mGame);
//			break;
			Intent cmGame = new Intent("com.pmon.pacgravity.CLIENTORSERVER");
			startActivity(cmGame);
			break;
			
//		case R.id.bOptions:
////			Intent cmGame = new Intent("com.csc780.pacgravity.CLIENTORSERVER");
////			startActivity(cmGame);
//			break;
			
		case R.id.bAbout:
			Intent iAbout = new Intent("com.pmon.pacgravity.ABOUTACTIVITY");
			startActivity(iAbout);
			break;
			
		case R.id.bExit: //finish the activity
			
			this.finish();
			this.onDestroy();
			System.exit(1);
			break;
		
		}
	}

	
}
