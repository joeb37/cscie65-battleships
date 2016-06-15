package com.jbockskopf.battleships;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jbockskopf.battleships.model.Board;
import com.jbockskopf.battleships.model.Game;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.jbockskopf.battleships.MESSAGE";

	private Game game;
	private int boardNumber;
	private ArrayList<Board> boards;
	private boolean gameOver = false;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);

        // Set the board name on the view
        TextView tv1 = (TextView) this.findViewById(R.id.boardLabel);
        tv1.setText(game.getBoardName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_hiscores:
                openHiScores();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void openSettings() {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
    }

    public void openHiScores() {
    	Intent intent = new Intent(this, HiScoresActivity.class);
    	startActivity(intent);
    } 
    
    public void clearClicked(View view) {
		
    	// Button doesn't work if the game is over
    	if (gameOver)
			return;

		// Make sure this is really what the user wants
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.clear);
    	builder.setMessage(R.string.clearAlertMessage);
    	builder.setPositiveButton(R.string.continueAlertBtn, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    	        // Clear the player's choices
    			getGame().initPlayerChoices();

    	        // Tell the game grid to redraw itself
    	        GameView gv = (GameView) findViewById(R.id.gameView1);
    	        gv.invalidate();
    		}
		});
    	builder.setNegativeButton(R.string.cancelAlertBtn, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.cancel();
    		}
		});
    	
    	AlertDialog alertDialog = builder.create();
    	alertDialog.show();
    }
    
    public void resignClicked(View view) {
    	// Button doesn't work if the game is over
    	if (gameOver)
			return;

    	// Make sure this is really what the user wants
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.resign);
    	builder.setMessage(R.string.resignAlertMessage);
    	builder.setPositiveButton(R.string.continueAlertBtn, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			
    			// Play the resignation sound, if so configured
    			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    			if (sp.getBoolean("pref_sound", true)) {
    				MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.app_game_interactive_negative_alert_tone_remove_delete_001);
    				mp.start();
    			}
    			
    			// Reveal all cells
    	        getGame().revealAll();
    	        
    	        // The Player does not get credit for a victory	        
    	        gameOver = true; 
    	        
    	        // Tell the game grid to redraw itself
    	        GameView gv = (GameView) findViewById(R.id.gameView1);
    	        gv.invalidate();
    		}
		});
    	builder.setNegativeButton(R.string.cancelAlertBtn, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.cancel();
    		}
		});
    	
    	AlertDialog alertDialog = builder.create();
    	alertDialog.show();
    }
    
    public Game getGame() {
    	return game;
    }
    
    public int getBoardNumber() {
    	return boardNumber;
    }
    
    public boolean getGameOver()
    {
    	return gameOver;
    }
    
    public void setGameOver(boolean gameOver)
    {
    	this.gameOver = gameOver;
    }
    
    private void init() {
    	
    	// Load all the boards 
    	boards = new ArrayList<Board>();
        loadBoards();
        
        // Find out which board is currently selected in the game settings 
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	String keyValue = sp.getString("pref_boardSelection", "");
    	boardNumber = 0;
    	try {
        	boardNumber = Integer.parseInt(keyValue);
        } catch (NumberFormatException nfe) {
            Log.d("Battleships", keyValue + " not a valid key value.");
        }
        
    	// Create a new game using the selected board.
        game = new Game(boards.get(boardNumber));
    }

	private void loadBoards()
    {
        try {
        	String boardDir = "boards";
            String boardFiles[] = getAssets().list(boardDir);

            for (int i = 0; i < boardFiles.length; i ++) {
            	// Synthesize the full path and open an input stream
            	String pathToBoardFile = boardDir + "/" + boardFiles[i];
                InputStream is = getAssets().open(pathToBoardFile);
                
                // Create a new board from the file input stream and add it to the collection.
            	Board board = new Board(is);
            	boards.add(board);
            }
        } catch (IOException ioe) {
        	System.out.println(ioe.getMessage());
        }
    }
	
	@SuppressLint("DefaultLocale")
	public void victoryCheck() {
		
		// Don't check for victory if the game is already over.
		if (gameOver)
			return;

		// Check if the user has won the game 
		if (game.isVictory()) {
			
			setGameOver(true);
			getGame().revealAll();
			
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			
			// Play victory sound (if the setting allows it).
			if (sp.getBoolean("pref_sound", true)) {
				MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.multimedia_system_alert_001);
				mp.start();
			}
			
			// Calculate time of game.
			Date gameEndTime = new Date();
			long elapsedTime = gameEndTime.getTime() - game.getGameStartTime();
			long elapsedSec = elapsedTime / 1000;
			long elapsedMin = elapsedSec / 60;
			long remainSec = elapsedSec - (elapsedMin * 60);
			String timeStr = String.format("%d:%02d", elapsedMin, remainSec); 

			
			// Check if this is a high score. Save it if it is.
	        boolean isRecord = false;
	        String key = "hs_" + boardNumber;
	        long hsValue = sp.getLong(key, 0);
	        if (hsValue == 0 || elapsedTime < hsValue) {
		        SharedPreferences.Editor editor = sp.edit();
		        editor.putLong(key, elapsedTime);
		        if (editor.commit())
		        	Log.d("Battleships", "Hi Score setting " + key + " = " + elapsedTime + "successfully saved");
		        isRecord = true;
	        }

	        // Pop up a toast to let the user know their score
	        CharSequence text;
			if (isRecord)
		        text = "You've won the game in " + timeStr + ". That's a new record!";
			else
		        text = "You've won the game in " + timeStr;
			
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.TOP|Gravity.LEFT, R.dimen.toast_horizontal_margin, R.dimen.toast_vertical_margin);
			toast.show();
		}
	}
}
