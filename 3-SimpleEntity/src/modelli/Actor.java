package modelli;
import interfaceBase.Drawable;
import interfaceBase.Loggable;
import interfaceBase.Updater;

class Actor implements Updater, Drawable, Loggable {
	static final int ALIVE = 1;
	static final int DEAD = 2;
	private int state;
	private int x;
	private int y;
	private int height;
	private int width;

	// default constructor.
	Actor() {
	}

	// accessor functions for updating the entity
	public void setState(int curr_state) {
		state = curr_state;
	}

	public int getState() {
		return state;
	}

	public void setX(int curr_x) {
		x = curr_x;
	}

	public int getX() {
		return x;
	}

	public void setY(int curr_y) {
		y = curr_y;
	}

	public int getY() {
		return y;
	}

	public void setHeight(int curr_height) {
		height = curr_height;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int curr_width) {
		width = curr_width;
	}

	public int getWidth() {
		return width;
	}

	// update ourselves properly
	public void update() {
		// Update
	}

	// can we collide with you?
	public boolean collidesWith(Updater other) {
		boolean bool = true;
		return bool;
	}

	// If we can collide, then lets do so if needed.
	public void handleCollisions() {
	}

	// can I be seen this frame?
	public boolean isVisible() {
		boolean bool = true;
		return bool;
	}

	// Local definition of drawing routines as needed.
	public void draw() {
	}

	public void logDebugString(String s) {
		// TODO Auto-generated method stub
		
	}

	public void logString(String s) {
		// TODO Auto-generated method stub
		
	}
}
