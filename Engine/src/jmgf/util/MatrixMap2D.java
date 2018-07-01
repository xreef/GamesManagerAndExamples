package jmgf.util;

/**
 *  Matrice di interi int
 *  
 * @author Vito Ciullo  
 *
 */
public class MatrixMap2D
{
	private int rows, columns, count = 0;
	private int numObjects = 0;
	private int[] values;
	int[][] retSquare;
	byte[][] objectCoords;
	byte[] typeObjects;
	
	public MatrixMap2D(int[] values, int rows, int columns)
	{
		this.values = values;
		this.rows  = rows;
		this.columns = columns;
		for(int i = 0; i < values.length; i ++) if(values[i] > 1) 
			numObjects ++;
	}
	
	public int get(int row, int column) { return values[row * columns + column]; }
	
	public int getColumnCount() { return columns; }
	
	public int getRowsCount() { return rows; }
	
	public void createLevel(int[] values)
	{
		objectCoords = new byte[numObjects][2];
		typeObjects = new byte[numObjects];
		retSquare = new int[rows][columns];
		for(int i = 0; i < rows; i ++) for(int j = 0; j < columns; j ++)
		{
			if(get(i, j) > 1)
			{
				typeObjects[count] = (byte) get(i, j);
				objectCoords[count][0] = (byte) j;
				objectCoords[count][1] = (byte) i;
				values[j + columns * i] = 0;
				count ++;
			}
			retSquare[i][j] = values[j + columns * i];
		}
	}
	
	public int[][] getSquare()
	{
		createLevel(values);
		return retSquare;
	}
	public byte[] getTypeObjects() { return typeObjects; }
	public byte[][] getObjectCoords() { return objectCoords; }
}