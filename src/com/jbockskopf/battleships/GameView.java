package com.jbockskopf.battleships;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
//import android.util.Log;

import com.jbockskopf.battleships.model.Game;

class GameView extends View {

	MainActivity ctx;
	Bitmap waterBitmap;
	Bitmap shipCtrBitmap;
	Bitmap ship_N_Bitmap;
	Bitmap ship_E_Bitmap;
	Bitmap ship_S_Bitmap;
	Bitmap ship_W_Bitmap;
	Bitmap ship_1_Bitmap;
	Bitmap shipUnkBitmap;
	int cellSize;
	int lineWidth = 1;
	int[] lastTouchCell = new int[2];
	char lastTouchType = 'U';
	private Paint paintLine, paintLtGray, paintDkGray, paintRed;
	private Rect cellRect;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		ctx = (MainActivity) context;
		
        // Create the bitmaps
		waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water);
		shipCtrBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship_center);
		ship_N_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship_north);
		ship_E_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship_east);
		ship_S_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship_south);
		ship_W_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship_west);
		ship_1_Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship_single);
		shipUnkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship_unknown);

		// Create the paint brushes
		paintLine = new Paint();
		paintLine.setARGB(100, 0, 0, 0);
		paintLine.setStrokeWidth((float) lineWidth);
		
		// Red (50% opaque)
		paintRed = new Paint();
		paintRed.setARGB(128, 255, 0, 0);
		paintRed.setTextSize(40);
		
		// Dark gray
		paintDkGray = new Paint();
		paintDkGray.setARGB(255, 64, 64, 64);
		paintDkGray.setTextSize(40);

		// light gray
		paintLtGray = new Paint();
		paintLtGray.setARGB(255, 192, 192, 192);
		paintLtGray.setTextSize(40);

		// Rectangle used to draw cell bitmaps
		cellRect = new Rect();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Log.d("Battleships", "Dimensions = " + MeasureSpec.getSize(widthMeasureSpec) + " by " + MeasureSpec.getSize(heightMeasureSpec));
        cellSize = MeasureSpec.getSize(widthMeasureSpec)/(ctx.getGame().getBoardSize() + 1); 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = MotionEventCompat.getActionMasked(event);
		boolean rtnValue = false;
		
		switch(action) {
		case (MotionEvent.ACTION_DOWN):
			rtnValue =  processTouch(event.getX(), event.getY(), false);
			break;
			
		case (MotionEvent.ACTION_MOVE):
			rtnValue = processTouch(event.getX(), event.getY(), true);
			break;
			
		case (MotionEvent.ACTION_UP):
			// forget the last cell touched
			lastTouchCell[0] = -1;
			lastTouchCell[1] = -1;
			lastTouchType = 'U';
			break;
		
		default:
			return super.onTouchEvent(event);
		}
		
		invalidate(); // do a redraw
		return rtnValue;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	
		Game game = ctx.getGame();

		int gridSize = game.getBoardSize();
	
		// Horizontal lines
		for (int i = 0; i <= gridSize; i++) {
			canvas.drawLine(0, (i * cellSize), (gridSize * cellSize), (i * cellSize), paintLine);
		}

		// Vertical lines
		for (int i = 0; i <= gridSize; i++) {
			canvas.drawLine((i * cellSize), 0, (i * cellSize), (gridSize * cellSize), paintLine);
		}
		
		// Draw the player's selections thus far (plus and hint cells)
		int cellLeftOffset = cellSize / 3;
		int cellTopOffset = 2 * cellSize / 3;
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {

				cellRect.set((j * cellSize) + lineWidth,
						     (i * cellSize) + lineWidth,
						     ((j + 1) * cellSize),
						     ((i + 1) * cellSize));
				
				switch (game.playerChoices[j][i]) {
				case 'W':
				case 'w':
					canvas.drawBitmap(waterBitmap, null, cellRect, null);
					break;
				case 'S':
				case 's':
					canvas.drawBitmap(whichShip(j,i), null, cellRect, null);
					break;
				default:
					break;
				}
			}
		}
		
		// Calculate solution row/col end totals
		int[] rowTotals = new int[gridSize];
		int[] colTotals = new int[gridSize];
		for (int y = 0; y < gridSize; y++) {
			for (int x = 0; x < gridSize; x++) {
				rowTotals[y] += game.boardGrid[x][y] == 0 ? 0 : 1;
				colTotals[x] += game.boardGrid[x][y] == 0 ? 0 : 1;
			}
		}
		
		// Calculate player choice row/col end totals
		// Also check if the rows are complete or not.
		int[] playerRowTotals = new int[gridSize];
		int[] playerColTotals = new int[gridSize];
		boolean[] rowIncomplete = new boolean[gridSize];
		boolean[] colIncomplete = new boolean[gridSize];
		for (int y = 0; y < gridSize; y++) {
			for (int x = 0; x < gridSize; x++) {
				// Force characters to upper case so we don't exclude hints.
				playerRowTotals[y] += Character.toUpperCase(game.playerChoices[x][y]) == 'S' ? 1 : 0;
				playerColTotals[x] += Character.toUpperCase(game.playerChoices[x][y]) == 'S' ? 1 : 0;
				
				// Check if this cell makes this row/col incomplete
				if (game.playerChoices[x][y] == 'U') {
					rowIncomplete[y] = true;
					colIncomplete[x] = true;
				}
			}
		}
		
		// Draw numerals on row ends
		for (int i = 0; i < gridSize; i++) {
			Paint pen = paintDkGray;
			if (rowIncomplete[i] == false) 
				pen = paintLtGray; // row complete 
			if (playerRowTotals[i] > rowTotals[i])
				pen = paintRed; // too many ships

			canvas.drawText(String.valueOf(rowTotals[i]), 
			        cellLeftOffset + (gridSize * cellSize), 
			        cellTopOffset + (i * cellSize), 
			        pen);
		}
		
		// Draw numerals on column bottoms
		for (int i = 0; i < gridSize; i++) {
			Paint pen = paintDkGray; 
			if (colIncomplete[i] == false)
				pen = paintLtGray; // row complete 
			if (playerColTotals[i] > colTotals[i])
				pen = paintRed; // too many ships
			canvas.drawText(String.valueOf(colTotals[i]), 
			        cellLeftOffset + (i* cellSize), 
			        cellTopOffset + (gridSize * cellSize), 
			        pen);
		}
	}

	private boolean processTouch(float x, float y, boolean useLastType)
	{
		if (ctx.getGameOver())
			return false;
		
		// grid coordinates
		int gridX = (int) (x / cellSize);
		int gridY = (int) (y / cellSize);
		
		// ignore the touch if it is outside the grid.
		Game game = ctx.getGame();
		int boardSize = game.getBoardSize() * cellSize;
		if (x < 0 || y < 0 || x > boardSize || y > boardSize)
			return false;

		// Ignore the touch if it is in the same grid cell as the last 
		// touch (and the player hasn't lifted his finger)
		if (lastTouchCell[0] == gridX && lastTouchCell[1] == gridY)
			return false;
		
		// Make sure the player didn't tap a hint square
		if (!isHint(gridX, gridY)) {
		
			// Check if we should set the cell to a certain value. 
			// This happens during drag (ACTION_MOVE) operations
			if (useLastType) {
				game.playerChoices[gridX][gridY] = lastTouchType;
			} else {
				// Otherwise, Cycle between 'U' (undefined) 'W' (water) and 'S' (ship section)
				// The current value for the cell determines what the next value should be.
				char curVal = game.playerChoices[gridX][gridY];
			
				switch (curVal) {
				case 'U':
					game.playerChoices[gridX][gridY] = 'W';
					break;
				case 'W':
					game.playerChoices[gridX][gridY] = 'S';
					break;
				case 'S':
					game.playerChoices[gridX][gridY] = 'U';
					break;
				default:
					break;
				}
				
				lastTouchType = game.playerChoices[gridX][gridY];
			}
		}

		lastTouchCell[0] = gridX;
		lastTouchCell[1] = gridY;
		
		// Check with the controller to see if the player has won the game
		ctx.victoryCheck();
		
		return true;
	}
	
	private boolean isHint(int x, int y)
	{
		boolean rtnValue = false;
		
		Game game = ctx.getGame();
		if (game.playerChoices[x][y] == 'w' || game.playerChoices[x][y] == 's' )
			rtnValue = true;
		
		return rtnValue;
	}
	
	private Bitmap whichShip(int x, int y)
	{
		// This method resolves ship grid cells to specific bitmaps. We often need
		// to check what has been revealed adjacent to a ship cell to know which
		// ship segment bitmap to display.
		
		Game game = ctx.getGame();
		
		Bitmap rtnValue = shipUnkBitmap; // default value is "unknown" (i.e gray box) 
		boolean isHint = isHint(x,y);
		
		// Figure out what's to the left, right, top, and bottom of x,y.
		// 
		// If x,y is a hint cell, look at the solved game board, not the player choices.  
		// If the selection is along the edge or in a corner, treat the off-grid
		// points as water.
		char left, top, right, bottom;
		
		// LEFT
		if (x > 0)
			if (isHint)
				left = game.boardGrid[x-1][y] == 0 ? 'W' : 'S';
			else
				left = Character.toUpperCase(game.playerChoices[x-1][y]);
		else 
			left = 'W';
		
		// TOP
		if (y > 0)
			if (isHint)
				top = game.boardGrid[x][y-1] == 0 ? 'W' : 'S';
			else
				top = Character.toUpperCase(game.playerChoices[x][y-1]);
		else 
			top = 'W';
		
		// RIGHT
		if (x < game.getBoardSize()-1)
			if (isHint)
				right = game.boardGrid[x+1][y] == 0 ? 'W' : 'S';
			else 
				right = Character.toUpperCase(game.playerChoices[x+1][y]);
		else 
			right = 'W';
		
		// BOTTOM
		if (y < game.getBoardSize()-1)
			if (isHint)
				bottom = game.boardGrid[x][y+1] == 0 ? 'W' : 'S';
			else
				bottom = Character.toUpperCase(game.playerChoices[x][y+1]); 
		else 
			bottom = 'W';
		
		// Now figure out which ship segment bitmap to display based on what's 
		// surrounding the current cell.
		
		if (left == 'W' && top == 'W' && right == 'W' && bottom == 'S') {
			rtnValue = ship_N_Bitmap;  // North pointing ship end 
		}
			
		if (left == 'S' && top == 'W' && right == 'W' && bottom == 'W') {
			rtnValue = ship_E_Bitmap;  // East pointing ship end 
		}
				
		if (left == 'W' && top == 'S' && right == 'W' && bottom == 'W') {
			rtnValue = ship_S_Bitmap;  // South pointing ship end 
		}
				
		if (left == 'W' && top == 'W' && right == 'S' && bottom == 'W') {
			rtnValue = ship_W_Bitmap;  // West pointing ship end 
		}
				
		if (left == 'W' && top == 'S' && right == 'W' && bottom == 'S') {
			rtnValue = shipCtrBitmap;  // Center ship segment 
		}
				
		if (left == 'S' && top == 'W' && right == 'S' && bottom == 'W') {
			rtnValue = shipCtrBitmap;  // Center ship segment 
		}
					
		if (left == 'W' && top == 'W' && right == 'W' && bottom == 'W') {
			rtnValue = ship_1_Bitmap;  // single segment ship 
		}
		
		return rtnValue;
	}
}
