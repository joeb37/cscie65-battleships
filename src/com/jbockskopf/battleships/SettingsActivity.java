package com.jbockskopf.battleships;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity 
                              implements OnSharedPreferenceChangeListener{

	PreferenceFragment frag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the settings fragment as the main content.
		frag = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, frag)
                .commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
        // The game board setting should show the current selection on initial display
        SharedPreferences sp = frag.getPreferenceManager().getSharedPreferences();
        setBoardPrefSummary(sp);
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

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_boardSelection")) {
        	setBoardPrefSummary(sharedPreferences);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        frag.getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        frag.getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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

	private void setBoardPrefSummary(SharedPreferences sp) {
        Preference boardPref = frag.findPreference("pref_boardSelection");
        // Set summary to be the user-description for the selected value
        String[] boardList = getBaseContext().getResources().getStringArray(R.array.pref_boardSelection_entries);
    	String keyValue = sp.getString("pref_boardSelection", "");
        try {
        	int boardIndex = Integer.parseInt(keyValue);
            boardPref.setSummary(boardList[boardIndex]);
        } catch (NumberFormatException nfe) {
            Log.d("Battleships", keyValue + " not a valid key value.");
        }
	}
	
	public static class SettingsFragment extends PreferenceFragment {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.layout.activity_settings);
	    }
	}
}
