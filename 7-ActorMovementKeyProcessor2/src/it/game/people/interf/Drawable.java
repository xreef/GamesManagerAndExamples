package it.game.people.interf;

import java.awt.Window;
import java.awt.image.BufferedImage;

public interface Drawable {
	
	public String getImageUrl();
	public boolean isVisible();
	public BufferedImage getImage();
	public void setImage(BufferedImage image);
	public int getX();
	public int getY();
	public void draw(Window w);
	public void loadImage(Window w);
}
