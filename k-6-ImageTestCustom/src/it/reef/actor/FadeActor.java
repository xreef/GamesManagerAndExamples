package it.reef.actor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import it.reef.engine.effect.ImageSFX;
import it.reef.interf.Updatable;

public class FadeActor extends ImmagineStatica implements Updatable {

	ImageSFX isfx = new ImageSFX();

	private float alphaSet = 1.0f;
	
	public FadeActor(int x, int y, BufferedImage bi, ImageObserver io) {
		super(x, y, bi, io);
		// TODO Auto-generated constructor stub
	}

	public float getAlphaSet() {
		return alphaSet;
	}

	public void setAlphaSet(float alphaSet) {
		this.alphaSet = alphaSet;
	}

	public void update() {
		
	}

	@Override
	public void draw(Graphics g) {
		if (bi == null) {
			g.setColor(Color.yellow);
			g.fillRect(x, y, 20, 20);
			g.setColor(Color.black);
			g.drawString("??", x + 10, y + 10);
		} else{
			isfx.drawFadedImage((Graphics2D)g, bi, x, y, alphaSet);
		}
			
	}

}
