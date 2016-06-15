package com.jbockskopf.battleships.model;

import java.util.Date;


public class Game {

	private int boardSize;
	private String boardName;
	private Date gameStartDate;
	private Board board;
	public int boardGrid[][];
	public char playerChoices[][];
	
	public Game() 
	{
	}
	
	public Game(Board board)
	{
		this();

		this.board = board;
		
	    // Create an in-memory matrix of the game board 
		this.boardSize = board.getSize();
		this.boardName = board.getName();
		this.boardGrid = new int[boardSize][boardSize];
		
		// Populate all array elements with water.
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				boardGrid[i][j] = 0; // 0 = water
			}
		}
		
	    // Read the board's ship collection and add ship segments (1,2,3, or 4) to the matrix.
		for (int i = 0; i < board.getShipCount(); i++) {
			Ship ship = board.getShipAtIndex(i);
			int shipSize = ship.getLength();

			// Add the ship's coordinates to the matrix 
			for (int j = 0; j < shipSize; j++) {
				
				// Get the coordinates for this segment. 
				// Index 0 = x,index 1 = y.
				int[] coords = ship.getCoordsAtIndex(j);
				
				// Use the ship size as each position in the array, 
				// e.g. 2 2 or 4 4 4 4.
				boardGrid[coords[0]][coords[1]] = shipSize;
			}
		}
		
	    // Initialize a second matrix to hold the player's choices.
		this.playerChoices = new char[boardSize][boardSize];
		initPlayerChoices();
		
		// Start the game timer
		this.gameStartDate = new Date();
	}
	
	public int getBoardSize()
	{
		return this.boardSize;
	}
	
	public String getBoardName()
	{
		return this.boardName;
	}
	
	public void initPlayerChoices()
	{
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				playerChoices[i][j] = 'U'; // U = unknown
			}
		}
		
		// Add the hints to the player's choice matrix.
		for (int i = 0; i < board.getHintCount(); i++) {
			Hint hint = board.getHintAtIndex(i);
			int[] coords = hint.getCoords();

			// Check what is in the board matrix at these coordinates
			// (either water or a ship segment).
			// Lower case letters indicate unchangeable hint squares.
			if (boardGrid[coords[0]][coords[1]] == 0) {
				// it's water
				playerChoices[coords[0]][coords[1]] = 'w';
			} else {
				// it's a ship
				playerChoices[coords[0]][coords[1]] = 's';
			}
		}
	}
	
	public boolean isVictory()
	{
		boolean rtnValue = true;
		
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				
				// Look for a mismatch between the player choices and the game board
				// Per instructions, consider only ship squares for victory. User need
				// not fill in all the water squares. 
				if (boardGrid[i][j] > 0) { // Master grid shows a ship part
					if (Character.toUpperCase(playerChoices[i][j]) != 'S')
						// Players choice shows something other than a ship.
						rtnValue = false;
				}
			}
		}
		
		return rtnValue;
	}
	
	public void revealAll()
	{
		// Fill the player choices matrix with all the correct values
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (boardGrid[i][j] > 0) { 
					// Master grid shows a ship part
					playerChoices[i][j] = 'S';
				} else {
					// Master grid shows water
					playerChoices[i][j] = 'W';
				}
			}
		}
	}
	
	public long getGameStartTime()
	{
		return this.gameStartDate.getTime();
	}
}
