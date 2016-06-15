package com.jbockskopf.battleships;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
	
	private static long SPLASH_DELAY = 4000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// Delay launching the main activity for a few seconds...
		TimerTask task = new TimerTask()
		{
			@Override
			public void run() {
				finish();
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, SPLASH_DELAY);
	}
}
