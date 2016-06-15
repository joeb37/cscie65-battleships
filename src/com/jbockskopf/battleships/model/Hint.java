package com.jbockskopf.battleships.model;

public class Hint {
	private int[] coords;
	
	public Hint() {
		coords = new int[2];
	}
	
	public Hint(int x, int y)
	{
		this();
		coords[0] = x;
		coords[1] = y;
	}

	public int[] getCoords()
	{
		return coords;
	}
}
