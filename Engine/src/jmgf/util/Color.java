package jmgf.util;

/**
 * Imposta i colori
 * 
 * @author Vito Ciullo
 *
 */
public class Color 
{
	public static final int BLACK 			= 0x000000;
	public static final int DARK_GRAY 		= 0x444444;
	public static final int GRAY 			= 0x888888;
	public static final int LIGHT_GRAY		= 0xBBBBBB;
	public static final int WHITE 			= 0xFFFFFF;
	public static final int YELLOW			= 0xFFFF00;
	public static final int ORANGE 			= 0xFF8800;
	public static final int LIGHT_RED		= 0xFF8888;
	public static final int RED 			= 0xFF0000;
	public static final int DARK_RED 		= 0x440000;
	public static final int LIGHT_GREEN		= 0x88FFAA;
	public static final int GREEN			= 0x00FF00;
	public static final int DARK_GREEN 		= 0x004400;
	public static final int LIGHT_BLUE		= 0x4488FF;
	public static final int BLUE			= 0x0000FF;
	public static final int DARK_BLUE 		= 0x000044;
	public static final int LIGHT_PURPLE	= 0xEE88FF;
	public static final int PURPLE			= 0xFF00FF;
	public static final int DARK_PURPLE		= 0x440044;
	
	public static int parse(String hex) { return Integer.parseInt(hex.substring(hex.indexOf("#")+1), 16); }
}
