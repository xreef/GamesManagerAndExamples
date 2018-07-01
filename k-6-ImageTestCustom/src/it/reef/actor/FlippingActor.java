package it.reef.actor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import it.reef.engine.effect.ImageSFX;
import it.reef.interf.Updatable;

public class FlippingActor extends ImmagineStatica implements Updatable {

	ImageSFX isfx = new ImageSFX();
	
	BufferedImage imageToRender;
	
	private int flipType = ImageSFX.VERTICAL_FLIP;
	private int currentFlip=-1;
	
	
	public FlippingActor(int x, int y, BufferedImage bi, ImageObserver io) {
		super(x, y, bi, io);
		// TODO Auto-generated constructor stub
	}

	
	
	public int getFlipType() {
		return flipType;
	}



	public void setFlipType(int flipType) {
		this.flipType = flipType;
	}



	public void update() {
		if (currentFlip!=flipType){
			imageToRender = isfx.flipImage(bi, flipType);
			currentFlip=flipType;
		}
	}



	@Override
	public void draw(Graphics g) {
		if (imageToRender == null) {
	         g.setColor(Color.yellow);
	         g.fillRect(x, y, 20, 20);
	         g.setColor(Color.black);
	         g.drawString("??", x+10, y+10);
	       }
	       else
	         g.drawImage(imageToRender, x, y, iobs);
	}
	
	

}
