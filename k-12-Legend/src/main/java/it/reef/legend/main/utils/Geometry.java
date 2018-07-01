package it.reef.legend.main.utils;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public interface Geometry {
	static final int NUM_WIDTH_CELLS=15;
	static final int NUM_HEIGHT_CELLS=30;
	
	int AREA_BRICK_WIDTH = NUM_WIDTH_CELLS*30; // 
	int AREA_BRICK_HEIGHT = NUM_HEIGHT_CELLS*15;

	int AREA_INFO = NUM_WIDTH_CELLS*12;
	int AREA_BORDER = 10;

	int AREA_WIDTH = AREA_BRICK_WIDTH + AREA_BORDER + AREA_BORDER + AREA_INFO; // dimension
	int AREA_HEIGHT = AREA_BRICK_HEIGHT + AREA_BORDER;

	Rectangle2D AREA_ATTIVA = new Rectangle(AREA_BORDER, AREA_BORDER, AREA_BRICK_WIDTH, AREA_BRICK_HEIGHT);

}
