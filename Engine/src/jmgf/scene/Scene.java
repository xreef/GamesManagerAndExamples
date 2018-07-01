package jmgf.scene;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.rms.RecordStore;

import org.json.me.JSONArray;
import org.json.me.JSONException;

import jmgf.engine.GameManager;
import jmgf.engine.SceneManager;
import jmgf.engine.GameManager.Screen;
import jmgf.game.Game;
import jmgf.geometry.Area;

/**
 * Gestisce gli elementi del gioco
 * 
 * @author AchilleTerzo
 *
 */
public class Scene extends LayerManager implements ISerializable
{
	private SceneElement _player = null;
	public void setPlayer(SceneElement player) { _player = player; }
	
	protected String _name;
	public String getName() { return _name; }
	public Scene(String name) { _name = name; }
	
	public SceneElement getPlayer() { return _player; }
	private Hashtable sceneElements = new Hashtable();
	
	public static final String STORE = "GameInfo";
	
	private JSONArray a1, a2;
	
	public Enumeration getByType(Class classType)
	{
		Hashtable temp = new Hashtable();
		Enumeration enumeration =  sceneElements.keys();
		while(enumeration.hasMoreElements())
		{	
			SceneElement el = (SceneElement) enumeration.nextElement();
			if (el.getClass() == classType) temp.put(el, el);
		}
		return (temp.size() > 0) ? temp.elements() : null;
	}
		
	public Enumeration getSceneElements() {	return sceneElements.keys(); }
	
	public void append(SceneElement el)
	{ 
		sceneElements.put(el, el.getClass().getName());
		super.append(el.getLayer());
	}
	
	public void remove(SceneElement el)
	{
		sceneElements.remove(el);
		super.remove(el.getLayer());
	}
	
	public void initialize() {}
	
	public void update()
	{
		Enumeration en = sceneElements.keys();
		while(en.hasMoreElements())
		{	
			Object o = en.nextElement();
			if (o != null)
			{
				SceneElement el = (SceneElement) o;
				if (el.callingUpdate) el.update();
			}
		}
	}
	
	public void paint() { paint(new Area(Screen.left(), Screen.top(), Screen.width(), Screen.height())); }
	public void paint(Area area)
	{
		setViewWindow(area.x(), area.y(), area.width(), area.height());
		paint(GameManager.Graphics(), Screen.left(), Screen.top());
	}
	
	public void stop() { SceneManager.isPaused = true; }
	public void play() { SceneManager.isPaused = false; }

	/**
	 * Permette di serializzare una istanza di Scene
	 */
	public void serialize()
	{
		String s1 = "name: ";
		String s2 = "ID: ";
		String s3 = "width: ";
		String s4 = "height: ";
		String s5 = "x: ";
		String s6 = "y: ";
		String s7 = "visibility: ";
		
		a1 = new JSONArray();
		a2 = new JSONArray();
		Enumeration en = sceneElements.keys();
		int i = 0;
		while(en.hasMoreElements())
		{
			SceneElement el = (SceneElement) en.nextElement();
			try
			{
				a1.put(i, s1);
				i ++;
			    a1.put(i, el.getName());
			    a2.put(i, el.getName());
				i ++;
				a1.put(i, s2);
				i ++;
				a1.put(i, el.getId());
				a2.put(i, el.getId());
				i ++;
				a1.put(i, s3);
				i ++;
				a1.put(i, el.getArea().width());
				a2.put(i, el.getArea().width());
				i ++;
				a1.put(i, s4);
				i ++;
				a1.put(i, el.getArea().height());
				a2.put(i, el.getArea().height());
				i ++;
				a1.put(i, s5);
				i ++;
				a1.put(i, el.getArea().x());
				a2.put(i, el.getArea().x());
				i ++;
				a1.put(i, s6);
				i ++;
				a1.put(i, el.getArea().y());
				a2.put(i, el.getArea().y());
				i ++;
				a1.put(i, s7);
				i ++;
				a1.put(i, el.getLayer().isVisible());
				a2.put(i, el.getLayer().isVisible());
				i ++;
			}
			catch (JSONException e) { Game.errorMsg(e);	}
		}
		
		unserialize();
		
		//System.out.println(a1.toString());
	}
	
	public SceneElement[] unserialize()
	{
		try { encodeInfo();	}
		catch (Exception e) { Game.errorMsg(e); }
		return null;
	}
	
	private void encodeInfo() throws Exception
	  {
	    RecordStore store = null;
	    try
	    {
	    	byte[] data = a1.toString().getBytes();
	    	store = RecordStore.openRecordStore(STORE, true);
	    	int numRecords = store.getNumRecords();
	    	if(numRecords > 0) store.setRecord(1, data, 0, data.length);
	    	else store.addRecord(data, 0, data.length);
	    }
	    catch(Exception e) { throw(e); }
	    finally {
	      try { if(store != null) store.closeRecordStore(); }
	      catch(Exception e) { e.printStackTrace(); }
	    }
	  }
}
