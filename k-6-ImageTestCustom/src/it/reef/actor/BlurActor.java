package it.reef.actor;

import it.reef.engine.effect.ImageSFX;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.ImageObserver;
import java.awt.image.Kernel;

public class BlurActor extends ActorTick {
	private BufferedImage imageOriginal;

	private ConvolveOp blurOp;

	ImageSFX isfx = new ImageSFX();

	public BlurActor(int x, int y, BufferedImage imageOriginal, ImageObserver io,
			long period, double duration, int step, boolean repeat) {
		initImageTick(period, duration, step, repeat);
		this.imageOriginal = imageOriginal;

		this.io = io;
		
		this.x=x;
		this.y=y;
		this.width=imageOriginal.getWidth();
		this.heigth=imageOriginal.getHeight();
		
		// blur by convolving the image with a matrix
		float ninth = 1.0f / 9.0f;

		float[] blurKernel = 	{ // the 'hello world' of Image Ops :)
									ninth, ninth, ninth, 
									ninth, ninth, ninth, 
									ninth, ninth, ninth 
								};

		blurOp = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_ZERO_FILL, null);
	}

	public void update() {
		super.update();
		this.setEffect();
	}
	
	private void setEffect(){
		// blur by convolving the image with a matrix
		float ninth = 1.0f / (tickStep+1);

		
		float[] blurKernel = 	{ // the 'hello world' of Image Ops :)
									ninth, ninth, ninth, ninth, 
									ninth, ninth, ninth, ninth, 
									ninth, ninth, ninth, ninth,
									ninth, ninth, ninth, ninth,
								};
		
		blurOp = new ConvolveOp(new Kernel(4, 4, blurKernel), ConvolveOp.EDGE_NO_OP, null);
	}
	
	public void draw(Graphics g) {
		if (imageOriginal == null) {
	         g.setColor(Color.yellow);
	         g.fillRect(x, y, 20, 20);
	         g.setColor(Color.black);
	         g.drawString("??", x+10, y+10);
	       }
	       else
	    	   isfx.drawBlurredImage((Graphics2D)g, imageOriginal, x, y, blurOp);
	    	   
	         // g.drawImage(imageToDraw, x, y, io);
	}
}
