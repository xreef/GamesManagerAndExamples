package appletImage;
import image.img.Resource;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Random;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class ShowImageOptimizedBuffered extends JApplet
{
  private GraphicsConfiguration gc;
  private BufferedImage im;

  public void init( )
  {
    // get this device's graphics configuration
    GraphicsEnvironment ge =
         GraphicsEnvironment.getLocalGraphicsEnvironment( );
    gc = ge.getDefaultScreenDevice( ).getDefaultConfiguration( );

    im = loadImage("ball.gif");
  } // end of init( )


   public BufferedImage loadImage(String fnm)
   /* Load the image from <fnm>, returning it as a BufferedImage
      which is compatible with the graphics device being used.
      Uses ImageIO. */
   {
     try {
       BufferedImage im = ImageIO.read(Resource.getBall());

       int transparency = im.getColorModel( ).getTransparency( );
       BufferedImage copy =  gc.createCompatibleImage(
                                 im.getWidth( ),im.getHeight( ),transparency );
       // create a graphics context
       Graphics2D g2d = copy.createGraphics( );


       // copy image
       g2d.drawImage(im,0,0,null);
       g2d.dispose( );
       return copy;
     }
     catch(IOException e) {
       System.out.println("Load Image error for " + fnm + ":\n" + e);
       return null;
     }
  } // end of loadImage( )


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


} // end of ShowImage class