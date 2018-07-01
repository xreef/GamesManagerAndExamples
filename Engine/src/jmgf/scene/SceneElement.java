package jmgf.scene;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import jmgf.geometry.Point;

/**
 * Crea gli elementi del gioco
 * 
 * @author AchilleTerzo
 *
 */
public abstract class SceneElement extends ScenePrimitive
{
	public boolean callingUpdate = false;
	public boolean detectingCollision = true;
	
	public SceneElement()
	{ 
		super();
		initialize();
	}
	public SceneElement(String name) { super(name); }
	
	protected Image _image = null;
	public final Image getImage() { return _image; }
	public final void setImage(Image image) { _image = image; }

	protected Layer element = null;
	public final Layer getLayer() { return element; }
	public void setLayer(Layer l) { element = l; }

	public void setPosition(Point p)
	{
		super.setPosition(p);
		element.setPosition(p.x(), p.y());
	}
	
	public void setDimension(Point p)
	{ 
		super.setDimension(p); 
		if (getLayer().getClass() == Sprite.class) ((Sprite)element).setImage(getImage(), area.width(), area.height());
	} 
	
	public void move(Point p)
	{
		element.move(p.x(), p.y());
		super.move(p);
	}

	public Enumeration getCollision(Scene scene)
	{
		Enumeration en = scene.getSceneElements();
		Hashtable temp = new Hashtable();
		while(en.hasMoreElements())
		{
			SceneElement el = (SceneElement) en.nextElement();
			if (el.detectingCollision && this != el && this.collidesWith(el)) temp.put(el, el.getClass().getName()); 
		}
		return temp.elements();
	}
	
	public boolean collidesWith(SceneNode node)
	{
		Enumeration en = node.getChildren();
		while(en.hasMoreElements())
		{
			Object o = (Object) en.nextElement();
			if(o.getClass() == SceneNode.class) if (this.collidesWith((SceneNode)o)) return true;
			else if (this.collidesWith((SceneElement)o)) return true;
		}
		return false;
	}
	
	public boolean collidesWith(SceneElement element) { return collidesWith(element, true); }
	public boolean collidesWith(SceneElement element, boolean pixelCollision)
	{ 
		if (! element.detectingCollision) return false;
		return collidesWith(element.getLayer(), pixelCollision);
	}
	
	public boolean collidesWith(Layer layer) { return collidesWith(layer, true); }
	public boolean collidesWith(Layer layer, boolean pixelCollision)
	{
		if (element.getClass() == TiledLayer.class) return false;
		if (layer.getClass() == Sprite.class) 
			return ((Sprite)element).collidesWith((Sprite)layer, pixelCollision); 
		else 
			return ((Sprite)element).collidesWith((TiledLayer)layer, pixelCollision); 
	}
	
	public void setVisible(boolean v) { element.setVisible(v); }
	
	protected Image createImage(String string)
	{
		Image image;
		try { image = Image.createImage(string); }
		catch(Exception e) { throw(new RuntimeException("Generic.<init>-->failed to load, caught " + e.getClass() + ": " + e.getMessage())); }
		return image;
	}
	
	public abstract int getId();
	
	public ScenePrimitive clone() throws InstantiationException, IllegalAccessException
	{
		SceneElement clone = (SceneElement) super.clone();
		clone.setImage(this.getImage());
		clone._name = this.getName();
		clone.setLayer(this.getLayer());
		clone.callingUpdate = this.callingUpdate;
		clone.setVisible(this.getLayer().isVisible());
		return clone;
	}
}
