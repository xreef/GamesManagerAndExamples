// SoundsPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* SoundsPanel displays 4 images (a dog, cat, sheep, and chicken),
   and monitors mouse presses on its canvas. If a click is
   over an image, then playClip() is called in LoadersTests
   to play the corresponding clip.

   SoundsPanel uses ImagesLoader to load its 4 images, which
   is a bit of over-kill for such simple needs.
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;


public class SoundsPanel extends JPanel
{
  // clip image names
  private final static String[] names = {"dog", "cat", "sheep", "chicken"};

  // on-screen top-left coords for the images
  private final static int[] xCoords = {20, 210, 20, 210};
  private final static int[] yCoords = {25, 25, 170, 170};

  // location of image and sound info
  private final static String IMS_FILE = "imagesInfo.txt";

  private static final int PWIDTH = 350;     // size of this panel
  private static final int PHEIGHT = 350;


  private int numImages;
  private BufferedImage[] images;
  private Rectangle[] hotSpots;   // a click inside these rectangles triggers
                                  // the playing of a clip

  private LoadersTests topLevel;



  public SoundsPanel(LoadersTests sts)
  {
    topLevel = sts;

    setPreferredSize( new Dimension(PWIDTH, PHEIGHT) );

    initImages();

    addMouseListener( new MouseAdapter() {
      public void mousePressed( MouseEvent e)
      { selectImage( e.getX(), e.getY());  }
    } );
  } // end of SoundsPanel()


  private void initImages()
  // load and initialise the images, and build their 'hot-spots'
  {
    numImages = names.length;

    hotSpots = new Rectangle[numImages];
    images = new BufferedImage[numImages];

    ImagesLoader imsLoader = new ImagesLoader(IMS_FILE); 

    for (int i=0; i < numImages; i++) {
      images[i] = imsLoader.getImage(names[i]);
      hotSpots[i] = new Rectangle( xCoords[i], yCoords[i], 
                           images[i].getWidth(), images[i].getHeight());
        // use the images' dimensions to determine the size of the rectangles
    }
  }  // end of initImages()



  public void paintComponent(Graphics g)
  // show the images against a white background
  {
    super.paintComponent(g);
    g.setColor(Color.white);
    g.fillRect(0, 0, PWIDTH, PHEIGHT);   // white background

    // display the images
    for (int i=0; i < numImages; i++)
      g.drawImage(images[i], xCoords[i], yCoords[i], this);
  } // end of paintComponent()


  private void selectImage(int x, int y)
  /* Work out which image was clicked on (perhaps none),
     and request that its corresponding clip be played. */
  {
    for (int i=0; i < numImages; i++)
      if (hotSpots[i].contains(x,y)) {
        topLevel.playClip(names[i], i);     // play the image's clip
        break;
      }
  }  // end of selectImage()

} // end of SoundsPanel class
