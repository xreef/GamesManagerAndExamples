package it.reef.ark.element.actor.type;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class ImmagineStatica extends Actor  {

	BufferedImage bi;
	ImageObserver iobs;
	
	public ImmagineStatica(int x, int y, BufferedImage bi, ImageObserver io){
		this.bi=bi;
		this.width=bi.getWidth();
		this.heigth=bi.getHeight();
		this.iobs=io;
		this.x=x;
		this.y=y;
	}
	
	
	public void draw(Graphics g) {
		if (bi == null) {
	         g.setColor(Color.yellow);
	         g.fillRect(x, y, 20, 20);
	         g.setColor(Color.black);
	         g.drawString("??", x+10, y+10);
	       }
	       else
	         g.drawImage(bi, x, y, iobs);
	}

	

}
