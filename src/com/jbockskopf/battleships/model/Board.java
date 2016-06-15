package com.jbockskopf.battleships.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class Board {

	private String boardName;
	private int boardSize;
	private ArrayList<Ship> ships;
	private ArrayList<Hint> hints;
	
	public Board() {
		ships = new ArrayList<Ship>();
		hints = new ArrayList<Hint>();
	}
	
	public Board(InputStream stream) 
	{
		this();
		
		BufferedReader rdr = null;
		try {
			rdr = new BufferedReader(new InputStreamReader(stream));
			
		    // Set instance variables for board name and board size.
			this.boardName = rdr.readLine();
			this.boardSize = Integer.parseInt(rdr.readLine());
			
			String line = rdr.readLine();
			while (line != null) {
			    // For each ship found, create a new Ship object.
				String parts[] = line.split(":");
				
				boolean isShip = true;
				int shipType = 0;
				try {
					// Ship rows begin with a number. If the conversion fails,
					// it is because the row begins with an 'H' (hint).
					shipType = Integer.parseInt(parts[0]);
				} catch (NumberFormatException nfe) {
					isShip = false;
				}
				
				// Divide everything to the right of the colon by commas 
				String[] coords = parts[1].split(",");
				
				if (isShip) {
					// Populate each ship with a coordinate array
					int startX, startY, endX, endY;
					startX = Integer.parseInt(coords[0]);
					startY = Integer.parseInt(coords[1]);
					if (shipType == 1) { // only 1 set of coordinates, so it's both start and end.
						endX = Integer.parseInt(coords[0]);
						endY = Integer.parseInt(coords[1]);
					} else {
						endX = Integer.parseInt(coords[2]);
						endY = Integer.parseInt(coords[3]);
					}
					
					Ship ship = new Ship(startX, startY, endX, endY);
					ships.add(ship);
				} else {
					// It's hint row. There may be several coordinate pairs.
					// Now divide everything after the colon at the commas
					for (int j=0; j < coords.length; j = j + 2) {
						// Add a hint for each pair of values
						int posX = Integer.parseInt(coords[j]);
						int posY = Integer.parseInt(coords[j+1]);
						Hint hint = new Hint(posX, posY);
						hints.add(hint);
					}
				}
				
				// Read another line from the file
				line = rdr.readLine();
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} finally {
			try {
				if (rdr != null) 
					rdr.close();
			} catch (IOException ioe) {
				
			}
		}
	}
	
	public String getName() 
	{
		return boardName;
	}
	
	public int getSize()
	{
		return boardSize;
	}
	
	public int getShipCount() 
	{
		return ships.size();
	}
	
	public Ship getShipAtIndex(int idx)
	{
		return ships.get(idx);
	}
	
	public int getHintCount() 
	{
		return hints.size();
	}
	
	public Hint getHintAtIndex(int idx)
	{
		return hints.get(idx);
	}
}
