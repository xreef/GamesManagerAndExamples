package image;

import image.img.Resource;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JApplet;

public class ShowImageBuffered
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage im;

  public ShowImageBuffered() {
	   try {
		      im =ImageIO.read( Resource.getBall() );
		    }
		    catch(IOException e) {
		      System.out.println("Load Image error:");
		    }
}



  public void paint(Graphics g, ImageObserver io)
  {  
	  Random r = new Random();
	  g.drawImage(im, r.nextInt(255), r.nextInt(200)+50, io); 
  }
}