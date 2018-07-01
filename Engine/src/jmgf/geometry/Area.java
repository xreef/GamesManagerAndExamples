package jmgf.geometry;

/**
 * Crea e definisce un'area
 * 
 * @author AchilleTerzo
 *
 */
public class Area extends Point
{
	private int width=0, height=0;
	public Area() { super(); }
	public Area(Area area) { super(area.x(), area.y()); this.width = area.width(); this.height = area.height(); }
	public Area(int width, int height) { super(); this.width = width; this.height = height; }
	public Area(int x, int y, int width, int height) { super(x, y); this.width = width; this.height = height; }
	public int width() { return width; }
	public int height() { return height; }
	public int width(int result) { return width + result; }
	public int height(int result) { return height + result; }
	public void setWidth(int width) { this.width = width; }
	public void setHeight(int height) { this.height = height; }
	public void setDimension(int width, int height) { setDimension(new Point(width, height)); }
	public void setDimension(Point p) { this.width = p.x(); this.height = p.y(); }
}