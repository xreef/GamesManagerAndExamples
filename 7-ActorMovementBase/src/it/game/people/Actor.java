package it.game.people;
import it.game.people.interf.Drawable;
import it.game.people.interf.Loggable;
import it.game.people.interf.Updater;

abstract class Actor implements Updater, Drawable, Loggable {
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




}
