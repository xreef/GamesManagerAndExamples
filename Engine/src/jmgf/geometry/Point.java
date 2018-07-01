package jmgf.geometry;

/**
 * Crea e gestisce i punti
 * 
 * @author AchilleTerzo
 *
 */
public class Point implements IPoint
{
	protected int x = 0, y = 0;
	public Point() {}
	public Point(int x, int y) { setX(x); setY(y);}
	public int x() 			{ return x; }
	public int y() 			{ return y; }
	public int x(int result){ return x + result; }
	public int y(int result){ return y + result; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setPosition(Point p) { this.x = p.x(); this.y = p.y(); }
}
