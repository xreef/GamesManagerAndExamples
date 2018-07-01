package jmgf.launcher;

import javax.microedition.midlet.MIDlet;

/**
 * La MIDlet del gioco
 * 
 * @author Vito Ciullo
 *
 */
public abstract class Launcher extends MIDlet
{
	public abstract void startApp();
	public abstract void pauseApp();
	public abstract void destroyApp(boolean b);
}
