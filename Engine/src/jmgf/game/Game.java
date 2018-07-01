package jmgf.game;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;

import jmgf.engine.GameManager;
import jmgf.launcher.Launcher;

/**
 * Nucleo dell'engine, gestisce eventuali errori prodotti nel gioco, visualizzandoli
 * a display
 * 
 * @author Vito Ciullo
 *
 */
public abstract class Game extends Thread implements IGame, Runnable, CommandListener
{
	protected static Launcher _launcher = null;
	public static Launcher getLauncher() { return _launcher; }
	protected final static boolean isRunning = true;
	protected static boolean isPaused = false;
	protected static boolean isStopped = false;
	public static Display myDisplay;
	
	public Game(Launcher m)
	{
		_launcher = m;
		myDisplay = Display.getDisplay(_launcher);
		myDisplay.setCurrent(GameManager.getManager());
		try { initialize(); }
		catch(Exception e)
		{ 
			e.printStackTrace();
			errorMsg(e);
		}
	}
	
	public synchronized void resume()
	{
		isPaused = false;
		this.notify();
	}
	public synchronized void pause() { isPaused = true; }
	public synchronized void stop()
	{
		isStopped = true;
		this.notify();
	}	

    public static void errorMsg(Exception e)
    {
		e.printStackTrace();
    	if(e.getMessage() == null) { errorMsg(e.toString()); }
    	else errorMsg(e.getClass().getName() + ":" + e.getMessage());
    }

    public static void errorMsg(String msg)
    {
    	Alert errorAlert = new Alert("error", msg, null, AlertType.ERROR);
    	//errorAlert.setCommandListener(this);
    	errorAlert.setTimeout(5000);
    	Display.getDisplay(_launcher).setCurrent(errorAlert);
    	System.out.println(msg);
    }
}
