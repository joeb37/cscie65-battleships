package com.jbockskopf.battleships.model;


import java.util.ArrayList;


public class Ship {
	private ArrayList<int[]> coords;
	
	public Ship () {
		coords = new ArrayList<int[]>();
	}
	
	public Ship (int startX, int startY, int endX, int endY)
	{
		this();
		
		for (int i = startX; i <= endX; i++) {
			for (int j = startY; j <= endY; j++) {
				int[] c = new int[2];
				c[0] = i;
				c[1] = j;
				coords.add(c);
			}
		}
	}
	
	public int getLength()
	{
		return coords.size();
	}
	
	public int[] getCoordsAtIndex(int idx)
	{
		return coords.get(idx);
	}
}
