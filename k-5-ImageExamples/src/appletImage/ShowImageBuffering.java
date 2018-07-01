package appletImage;
import image.img.Resource;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Random;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class ShowImageBuffering extends JApplet
{
  private BufferedImage im;

  public void init( )
  { try {
      im =ImageIO.read( Resource.getBall() );
    }
    catch(IOException e) {
      System.out.println("Load Image error:");
    }
  } // end of init( )

  public void paint(Graphics g)
  {  
	  long timeInNanoStart = System.nanoTime();
	  Random r = new Random();
	  for (int i=0;i<100;i++){
		  g.drawImage(im, r.nextInt(50), r.nextInt(50), this);
	  }
	  long timeInNanoFinish = System.nanoTime();
	  
	  g.drawString("Time in milli: "+Math.round((timeInNanoFinish-timeInNanoStart)/100000), 10, 100);
  }
}