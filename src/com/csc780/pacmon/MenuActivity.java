package com.csc780.pacmon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener{
	
	
	private Button singlePlayButton;
	private Button multiPlayButton;
	private Button optionsButton;
	private Button aboutButton;
	private Button exitButton;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		singlePlayButton = (Button) findViewById(R.id.bSingle);
		multiPlayButton = (Button) findViewById(R.id.bMulti);
		optionsButton = (Button) findViewById(R.id.bOptions);
		aboutButton = (Button) findViewById(R.id.bAbout);
		exitButton = (Button) findViewById(R.id.bExit);
		
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	
}
