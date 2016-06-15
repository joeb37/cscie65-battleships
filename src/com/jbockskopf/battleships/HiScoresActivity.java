package com.jbockskopf.battleships;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class HiScoresActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hi_scores);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	@Override
	protected void onStart() {
		super.onStart();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        
        TextView tv1 = (TextView) this.findViewById(R.id.hiscore_board_1);
        long hs1 = sp.getLong("hs_0", 0);
        if (hs1 != 0)
        	tv1.setText(formatTime(hs1));
        
        TextView tv2 = (TextView) this.findViewById(R.id.hiscore_board_2);
        long hs2 = sp.getLong("hs_1", 0);
        if (hs2 != 0)
        	tv2.setText(formatTime(hs2));
        
        TextView tv3 = (TextView) this.findViewById(R.id.hiscore_board_3);
        long hs3 = sp.getLong("hs_2", 0);
        if (hs3 != 0)
        	tv3.setText(formatTime(hs3));
        
        TextView tv4 = (TextView) this.findViewById(R.id.hiscore_board_4);
        long hs4 = sp.getLong("hs_3", 0);
        if (hs4 != 0)
        	tv4.setText(formatTime(hs4));
        
        TextView tv5 = (TextView) this.findViewById(R.id.hiscore_board_5);
        long hs5 = sp.getLong("hs_4", 0);
        if (hs5 != 0)
        	tv5.setText(formatTime(hs5));
        
        TextView tv6 = (TextView) this.findViewById(R.id.hiscore_board_6);
        long hs6 = sp.getLong("hs_5", 0);
        if (hs6 != 0)
        	tv6.setText(formatTime(hs6));
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("DefaultLocale")
	private String formatTime(long elapsedTime)
	{
		long elapsedSec = elapsedTime / 1000;
		long elapsedMin = elapsedSec / 60;
		long remainSec = elapsedSec - (elapsedMin * 60);
		
		return String.format("%d:%02d", elapsedMin, remainSec); 
	}
}
