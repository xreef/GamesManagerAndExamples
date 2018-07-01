package jmgf.engine;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

import jmgf.game.Game;
import jmgf.geometry.*;
import jmgf.util.Color;

/**
 * Nucleo del motore grafico, ha una classe interna Screen per i parametri dello schermo
 * 
 * @author Vito Ciullo
 *
 */
public final class GameManager extends GameCanvas implements IManager
{
	private static GameManager _instance = new GameManager();
	public static GameManager getManager() { return _instance; }
	
	private static Graphics _graphics = _instance.getGraphics();
	public static Graphics Graphics() { return _graphics; }
	public static class Screen
	{
		private static Area screenArea = new Area(_instance.getWidth(), _instance.getHeight());
		public static int top() { return screenArea.x(); }
		public static int left() { return screenArea.y(); }
		public static int width() { return screenArea.width(); }
		public static int height() { return screenArea.height(); }
		public static int top(int result) { return screenArea.x() + result; }
		public static int left(int result) { return screenArea.y() + result; }
		public static int width(int result) { return screenArea.width() + result; }
		public static int height(int result) { return screenArea.height() + result; }
		public static void setPosition(Point position) { setPosition(position.x(), position.y());}
		public static void setPosition(int x, int y) {screenArea.setX(x); screenArea.setY(y);}
		public static int color = Color.parse("#000000");
		public static Image background = null;
	}
	
	private GameManager() { super(false); }
	
	public void initialize(Graphics graphics) {	_graphics = graphics; initialize(); }
	public void initialize()
	{
		_graphics.setColor(Screen.color);
		_graphics.fillRect(0, 0, Screen.width(), Screen.height());
		if (Screen.background != null) _graphics.drawRegion(Screen.background, 0, 0, Screen.background.getWidth(), Screen.background.getHeight(), Sprite.TRANS_NONE, 0, 0, Graphics.TOP | Graphics.LEFT);
		ControllerManager.getManager().update();
	}
	public void update() 	 { _instance.flushGraphics(); }
	
	public void startGame() {}
	
	public void destroyCanvas() { _instance = null; }
}