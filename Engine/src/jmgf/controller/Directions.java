package jmgf.controller;

import jmgf.geometry.Point;

/**
 * Gestisce le direzioni
 * 
 * @author Vito Ciullo
 *
 */
public class Directions extends Point
{
	public static int UP = -1;
	public static int LEFT = -1;
	public static int DOWN = 1;
	public static int RIGHT = 1;
	public static int NONE = 0;
	
	public static int parse(String dir)
	{
		if (dir.toLowerCase().equals("up")) return UP;
		if (dir.toLowerCase().equals("left")) return LEFT;
		if (dir.toLowerCase().equals("down")) return DOWN;
		if (dir.toLowerCase().equals("right")) return RIGHT;
		return NONE;
	}
	
	public Directions(int x, int y) { super(x, y); }
}
