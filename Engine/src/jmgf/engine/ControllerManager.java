package jmgf.engine;

import jmgf.controller.Directions;

/**
 * Gestisce i controlli della tastiera
 * 
 * @author Vito Ciullo
 *
 */
public class ControllerManager implements IManager
{
	private static ControllerManager _instance = new ControllerManager();
	public static ControllerManager getManager() { return _instance; }
	
	public ControllerManager() {}
	
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int UP = 4;
	public final static int DOWN = 8;
	public final static int ALL = LEFT | RIGHT | UP | DOWN;
	public final static int NONE = - 1;
	
	private int _filter = ALL;
	public void setDirectionsFilter(int filter) { _filter = filter;	}
	
	private static int keyPressed = 0;
	public static int getKeyPressed() { return keyPressed; }
	public static boolean isPressed(int key) { if ((keyPressed & key) != 0) return true; return false; }
	public static Directions Directions() {	return _instance.getDirections(); }
	
	public int getControllX()
	{
		if((keyPressed & GameManager.LEFT_PRESSED) != 0) return ((LEFT & _filter) != 0) ? Directions.LEFT : Directions.NONE;
		else if((keyPressed & GameManager.RIGHT_PRESSED) != 0) return ((RIGHT & _filter) != 0) ? Directions.RIGHT : Directions.NONE;
		else return Directions.NONE;
	}
	
	public int getControllY()
	{
		if((keyPressed & GameManager.UP_PRESSED) != 0) return ((UP & _filter) != 0) ? Directions.UP : Directions.NONE;
		else if((keyPressed & GameManager.DOWN_PRESSED) != 0) return ((DOWN & _filter) != 0) ? Directions.DOWN : Directions.NONE;
		else return Directions.NONE;
	}
	
	public Directions getDirections() { return new Directions(getControllX(), getControllY()); }

	public void initialize() {}
	public void update() { keyPressed = GameManager.getManager().getKeyStates(); }
	public static void resetKey() { keyPressed = 0; }
}