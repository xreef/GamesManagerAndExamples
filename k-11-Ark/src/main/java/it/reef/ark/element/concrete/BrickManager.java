package it.reef.ark.element.concrete;

import it.reef.ark.element.action.Drawable;
import it.reef.ark.manager.image.ImageLoader;
import it.reef.ark.resources.level.Level;

import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BrickManager implements Drawable{
	private ImageLoader il;
	private ImageObserver io;
	private BufferedImage[] imagesType;
	
	private int WIDTH_CELL;
	private int HEIGHT_CELL;
	
	private int NUM_WIDTH_CELLS;
	private int NUM_HEIGHT_CELLS;
	
	private int MARGIN_LEFT = 10;
	private int MARGIN_TOP = 10;
	
	private int matrixElement[][]=new int[NUM_WIDTH_CELLS][NUM_HEIGHT_CELLS];
	private int cleanMatrixElement[][]=new int[][]{
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
							{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
	};
	
	// private int dotSize = 10; 
	
	private java.util.List<Brick> listBrick = new ArrayList<Brick>();
	
	public BrickManager(int schema,  ImageLoader il, ImageObserver io, Rectangle2D areaAttiva, int widthCells, int heightCells){
		
		
		this.MARGIN_LEFT=(int)areaAttiva.getMinX();
		this.MARGIN_TOP=(int)areaAttiva.getMinY();
		
		this.NUM_WIDTH_CELLS=widthCells;
		this.NUM_HEIGHT_CELLS=heightCells;
		
		this.WIDTH_CELL=(int)areaAttiva.getWidth()/NUM_WIDTH_CELLS;
		this.HEIGHT_CELL=(int)areaAttiva.getHeight()/NUM_HEIGHT_CELLS;
		
//		this.NUM_WIDTH_CELLS = 15;
//		this.NUM_HEIGHT_CELLS = 30;
		
//		this.AREA_WIDTH=(int)areaAttiva.getWidth();
//		this.AREA_HEIGHT=(int)areaAttiva.getHeight();
		this.il=il;
		this.io=io;
		this.matrixElement=cleanMatrixElement;
		
		imagesType = new BufferedImage[]{il.getImage("brickG"),il.getImage("brickG"),il.getImage("brickB"),il.getImage("brickR"),il.getImage("brickF")};
		
		this.solidBrickNumber=0;
		
		try{
			InputStream is = Level.getLevel(schema);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    String line = null;
	    	int cont = -1;
	        while ((line = reader.readLine()) != null && cont < NUM_HEIGHT_CELLS ) {
	        	cont++;
	        	char[] lchar = line.toCharArray();
	            for (int i=0;(i<lchar.length && i<NUM_WIDTH_CELLS );i++){
	            	char a = lchar[i];
	            	if (a!=' ') {
	            		matrixElement[cont][i]=Integer.parseInt(a+"");
	            	}
	            }
	        }
		}catch(Exception e){
			// this.matrixElement=cleanMatrixElement;
			e.printStackTrace();
		}
        
		createListOfWallFromMatrix(matrixElement);
	}
	
	public static int BRICK_SOLID=1;
	public static int BRICK_NORMAL=2;
	public static int BRICK_HARD=3;
	
	public int solidBrickNumber = 0;
	
	private void createListOfWallFromMatrix(int[][] m){
		for (int col=0;col<m[1].length;col++){
			for (int row=0;row<m.length;row++){
				int x = MARGIN_TOP+(col*WIDTH_CELL);
				int y = MARGIN_LEFT+(row*HEIGHT_CELL);
				if (m[row][col]==BRICK_SOLID || m[row][col]==BRICK_NORMAL || m[row][col]==BRICK_HARD){
					if (m[row][col]==BRICK_SOLID) solidBrickNumber++;
					Brick b = new Brick(x, y, m[row][col], imagesType, io, 1, 1, false);
					listBrick.add(b);
				}
			}
		}
	}

	public void draw(Graphics g) {
		for(int i=0;i<listBrick.size();i++){
			listBrick.get(i).draw(g);
		}
	}

	public java.util.List<Brick> getListBrick() {
		return listBrick;
	}
	
	public boolean detectWallCollision(Rectangle2D newPt){
		List<Brick> lw = this.getListBrick();
		for (int i=0;i<lw.size();i++){
			if (lw.get(i).intersect(newPt)) return true;
		}
		return false;
	}
	public boolean detectWallCollision(Ellipse2D newPt){
		List<Brick> lw = this.getListBrick();
		for (int i=0;i<lw.size();i++){
			if (lw.get(i).intersect(newPt)) return true;
		}
		return false;
	}
	public boolean isLevelComplete(){
		return listBrick.size()==solidBrickNumber;
	}
	public int remainingBrick()
	{
		return listBrick.size()-solidBrickNumber;
	}
}
