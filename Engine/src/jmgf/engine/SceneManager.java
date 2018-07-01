package jmgf.engine;

import java.util.Enumeration;
import java.util.Hashtable;

import jmgf.engine.GameManager.Screen;
import jmgf.geometry.Area;
import jmgf.scene.Scene;
import jmgf.scene.SceneElement;

/**
 * Gestisce gli elementi del gioco
 * 
 * @author AchilleTerzo
 *
 */
public class SceneManager
{
	private static Hashtable scenes = new Hashtable();
	
	public static boolean isPaused = false;
	
	private static Scene currentScene = null;
	public static Scene getCurrentScene() { return currentScene; }
	
	public static Enumeration getScenes() { return scenes.keys(); }
	public static void add(Scene scene) { scenes.put(scene.getName(), scene); }
	public static void remove(SceneElement el) { scenes.remove(el); }
	public static Scene getScene(String name) { return (Scene)scenes.get(name); }
	public static void setActiveScene(String name)
	{
		Enumeration e = scenes.keys();
		while(e.hasMoreElements())
		{	
			String scene = (String)e.nextElement();
			if (scene.equalsIgnoreCase(name))
			{ 
				currentScene = (Scene)scenes.get(scene);
				ControllerManager.resetKey();
			}
		}
	}
	public static void setActiveScene(Scene scene) 
	{ 
		if (!scenes.contains(scene)) add(scene);
		currentScene = scene;
	}
	
	public static void render() { render(new Area(Screen.left(), Screen.top(), Screen.width(), Screen.height())); }
	public static void render(Area area)
	{ 
		if ((currentScene != null) && ! isPaused)
		{
			currentScene.update();
			currentScene.paint(area);
		}
	}
}
