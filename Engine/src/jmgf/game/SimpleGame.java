package jmgf.game;

import javax.microedition.lcdui.Graphics;

import jmgf.engine.GameManager;
import jmgf.launcher.Launcher;
import jmgf.util.Color;

/**
 * Implementazione del motore di gioco
 * 
 * @author AchilleTerzo
 *
 */
public abstract class SimpleGame extends Game
{
	public SimpleGame(Launcher m) { super(m); }
	private static int millisPerTick;
	private static int FRAMES_PER_TICK = 20;
	private static double startTime, interval, estimated, fps, frequency = 0;
	private long finalTime, totalTime;
	public synchronized void run()
	{
		isPaused = false;
		isStopped = false;
		millisPerTick = 1000 / FRAMES_PER_TICK;
		myDisplay.setCurrent(GameManager.getManager());
		while(isRunning) 
		{
			if(isStopped) break;
			startTime = System.currentTimeMillis();
			GameManager.getManager().initialize();
			update();
			GameManager.Graphics().setColor(Color.BLACK);
			fps = getEstimatedFPS();
			GameManager.Graphics().drawString("" + (int) fps, 1, 1, Graphics.TOP | Graphics.LEFT);
			GameManager.getManager().update();
			finalTime = System.currentTimeMillis();
			try
			{
				if (finalTime - startTime < (millisPerTick)) synchronized (this) { wait(millisPerTick - (long) (finalTime - startTime)); }
				else synchronized (this) { wait(2); }
			}
			catch (InterruptedException ex) { System.out.println("Shoite:- " + ex.getMessage()); }
			totalTime = System.currentTimeMillis();
			interval = totalTime - startTime;
			frequency = (interval == 0) ? frequency : 1000 / (totalTime - startTime);
			if(isPaused)
			{
				synchronized(this)
				{
				  try { wait(); }
				  catch(Exception e) { errorMsg(e); }
				}
			}
			
		}
	}
	
	public static double getEstimatedFPS() { return estimated = (frequency == 0) ? 0 : estimated * 0.9 + frequency * 0.1; }
}
