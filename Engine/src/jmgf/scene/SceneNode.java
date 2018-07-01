package jmgf.scene;

import java.util.Enumeration;
import java.util.Hashtable;

import jmgf.geometry.Point;

/**
 * Nodo degli elementi
 * 
 * @author Vito Ciullo
 *
 */
public class SceneNode extends ScenePrimitive
{
	private Hashtable children = new Hashtable();
	
	public SceneNode(String name) { super(name); }
	public SceneNode(ScenePrimitive element) 
	{ 
		super();
		attachChild(element, element.getName());
	}
	public SceneNode(String name, ScenePrimitive element) 
	{ 
		super(name);
		attachChild(element, element.getName());
	}

	public void attachChild(ScenePrimitive element) { children.put(element, element.getName()); }
	public void attachChild(ScenePrimitive element, String name) { children.put(element, name);	}

	public void deteachChild(ScenePrimitive element) { children.remove(element); }

	public Enumeration getChildren() { return children.elements(); }
		
	public void move(Point p)
	{
		//super.move(new );
	}
	
	public void initialize()
	{
		Enumeration e = children.keys();
		while(e.hasMoreElements()) ((ScenePrimitive)e.nextElement()).initialize();
	}

	public void update()
	{
		Enumeration e =  children.keys();
		while(e.hasMoreElements()) ((ScenePrimitive)e.nextElement()).update();
	}

}
