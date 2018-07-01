package jmgf.scene;

import jmgf.geometry.Area;
import jmgf.geometry.Point;

/**
 * Primitiva degli elementi della scena
 * 
 * @author Vito Ciullo
 *
 */
public abstract class ScenePrimitive implements IScenePrimitive
{
	protected Area area = new Area();
	protected String _name = "";
	public String getName() { return _name; }
	public ScenePrimitive() { _name = this.getClass().getName(); }
	public ScenePrimitive(String name) { _name = name; }

	public Point getPosition() { return new Point(area.x(), area.y()); }
	public Area getArea() { return area; }
	public void setArea(Area a) { this.area = a; }
	
	public void setDimension(int width, int height) { setDimension(new Point(width, height)); }
	public void setDimension(Point p) { area.setDimension(p); } 
	
	public void setPosition(int x, int y) { setPosition( new Point(x, y) ); }
	public void setPosition(Point p) { area.setPosition(p); }
	
	public void move(int x, int y) { this.move(new Point(x, y)); } 
	public void move(Point p)
	{
		this.setPosition( new Point(this.getPosition().x() + p.x(), this.getPosition().y() + p.y()) );
	}

	public ScenePrimitive clone() throws InstantiationException, IllegalAccessException 
	{
		ScenePrimitive clone = (ScenePrimitive) this.getClass().newInstance();
		clone.setArea(new Area(this.area));
		return clone;
	}

	public String serialize() throws InstantiationException, IllegalAccessException 
	{
		
		return "";
	}
}
