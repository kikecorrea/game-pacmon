package com.csc780.pacmon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener {

	private Button howToPlayButton;
	private Button aboutButton;
	private Button backButton;
	private TextView areaTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		howToPlayButton = (Button) findViewById(R.id.bHowToPlay);
		aboutButton = (Button) findViewById(R.id.bAboutAbout);
		backButton = (Button) findViewById(R.id.bBack);
		areaTextView = (TextView) findViewById(R.id.tvDisplayArea);
		/*
		 * singlePlayButton.setTypeface(tf); multiPlayButton.setTypeface(tf);
		 * optionsButton.setTypeface(tf); aboutButton.setTypeface(tf);
		 * exitButton.setTypeface(tf);
		 */
		howToPlayButton.setOnClickListener(this);
		aboutButton.setOnClickListener(this);
		backButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.bHowToPlay:
			areaTextView.setText(R.string.how_to_play_text);
			break;
		case R.id.bAboutAbout:
			areaTextView.setText(R.string.about_game_text);
			break;

		case R.id.bBack: // finish the activity
			this.finish();
			break;

		}
	}

}
