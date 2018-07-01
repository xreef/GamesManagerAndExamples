package it.game.people;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import it.game.people.interf.Drawable;
import it.game.people.interf.KeyProcessable;
import it.game.people.interf.Loggable;
import it.game.people.interf.Updater;

abstract class Actor implements Updater, Drawable, Loggable, KeyProcessable {
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

	public void draw(Window w){
		w.getBufferStrategy().getDrawGraphics().drawImage(this.getImage(),this.getX(),this.getY(),w);
	}

	/**
	  * Read in the image to using GraphicsConfiguration 
	  *createCompatibleImage(width,height,transparency)method
	 * @return 
	  */
	private BufferedImage loadImage5(Window w)
	{
		BufferedImage bufferedImage=null;
		try {
			URL imageURL = getClass().getResource(this.getImageUrl());
			if (imageURL == null) {
				System.out.println("Unable to locate resource      return");
			}

			bufferedImage = ImageIO.read(imageURL);
			
		} catch (IOException x) {
			x.printStackTrace();
		}
		
		BufferedImage image = w.getGraphicsConfiguration().createCompatibleImage(
			    bufferedImage.getWidth(),
			    bufferedImage.getHeight(),
			    Transparency.BITMASK);
		  image.getGraphics().drawImage(bufferedImage,0,0,w);
		return image;
	}
	
	public void loadImage(Window w){
		this.setImage(loadImage5(w));
	}

}
