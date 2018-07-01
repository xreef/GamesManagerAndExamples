package it.reef.legend.element.actor.type;

import it.reef.legend.element.action.Drawable;

public abstract class Actor implements Drawable {

	public int x;
	public int y;
	public int width;
	public int heigth;

	
	public Actor() {

	}
	
	public Actor(int x, int y, int width, int heigth) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.heigth=heigth;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

}
