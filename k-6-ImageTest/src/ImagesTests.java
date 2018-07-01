
// ImagesTests.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Display a screen-full of images, exhibiting various animated
   effects.

   The images are loaded with the ImagesLoader object, so are 
   defined as 'o', 'n', 's' and 'g' images.

   The single images (the 'o' images)
   can have various special effects applied to them. This is done
   by the programmer _manually_ editing paintComponent(). Sorry,
   no fancy GUI in this program :)

   The special effects are applied over a period of several 'ticks'
   of the panel's timer, and then repeat. The SFX methods:

      * resizingImage():   the image grows;
      * flippingImage():   keep flipping the image horizontally and vertically;
      * fadingImage():     the image smoothly fades away to nothing;
      * rotatingImage():   spin the image in a clockwise direction;
      * blurringImage():   make the image increasingly more blurred;
      * reddenImage():     turn the image ever more red;
      * brighteningImage():  keep turning up the image's brightness;
      * negatingImage():   keep switching betwen the image and its negative;
      * mixedImage();      keep mixing up the colours of the image;
      * teleportImage():   make the image fade, pixels at a time;
      * zapImage():        change the image to a mass of yellow and red pixels;

   ------
   The 'n', 's', and 'g' images are animated by
   showing their component images one after another, in a cycle.
   The 'n' and 's' images use ImagesPlayer objects to do this.

   The 'g' example (the 'fighter) is animated via a counter and 
   the updateFighter() method in this class. 

   A Swing Timer is used to trigger the
   updates and redraws of the images every PERIOD ms.

   The 'cats', 'kaboom', 'cars', and 'fighter' images come from 
   the SpriteLib sprite library of images by Ari Feldman at
   http://www.arifeldman.com/games/spritelib.html

   The basn6a08.png and basn6a16.png images come from the PNG Suite 
   of Willem van Schaik at http://www.schaik.com/pngsuite/pngsuite.html
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.text.DecimalFormat;


public class ImagesTests extends JPanel
                           implements ActionListener,
                                      ImagesPlayerWatcher 
{
  private final static String IMS_FILE = "imsInfo.txt";
    /* The file holding the 'o', 'n', 's', and 'g' image information, 
       extracted with an ImagesLoader object. */

  private static final int PERIOD = 100;    // 0.1 secs
      /* A Swing timer is triggered every PERIOD ms to update
         and redraw the images. */

  private static final int PWIDTH = 850;     // size of this panel
  private static final int PHEIGHT = 400;


  private ImagesLoader imsLoader;   // the image loader
  private int counter;
  private boolean justStarted;
  private ImageSFXs imageSfx;    // the special effects class

  private GraphicsDevice gd;   // for reporting accl. memory usage
  private int accelMemory;   
  private DecimalFormat df;

  // hold the single 'o' images
  private BufferedImage atomic, balls, bee, cheese, eyeChart,
                        house, pumpkin, scooter,
                        fighter, ufo, owl, basn8, basn16;

  // for manipulating the 'n' and 's' images
  private ImagesPlayer numbersPlayer, figurePlayer, carsPlayer, 
                       catsPlayer, kaboomPlayer;

  // temporary BufferedImages used for the 'teleport' and 'zapping' effects
  private BufferedImage teleImage = null;
  private BufferedImage zapImage = null;


  public ImagesTests()
  {
    df = new DecimalFormat("0.0");  // 1 dp

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    gd = ge.getDefaultScreenDevice();

    accelMemory = gd.getAvailableAcceleratedMemory();  // in bytes
    System.out.println("Initial Acc. Mem.: " + 
                   df.format( ((double)accelMemory)/(1024*1024) ) + " MB" );

    setBackground(Color.white);
    setPreferredSize( new Dimension(PWIDTH, PHEIGHT) );

    // load and initialise the images
    imsLoader = new ImagesLoader(IMS_FILE); 
    imageSfx = new ImageSFXs();
    initImages();

    counter = 0;
    justStarted = true;

    new Timer(PERIOD, this).start();    // start the Swing timer
  } // end of ImagesTests()


  private void initImages()
  {
    // initialize the 'o' image variables
    atomic = imsLoader.getImage("atomic");
    balls = imsLoader.getImage("balls");
    bee = imsLoader.getImage("bee");
    cheese = imsLoader.getImage("cheese");
    eyeChart = imsLoader.getImage("eyeChart");
    house = imsLoader.getImage("house");
    pumpkin = imsLoader.getImage("pumpkin");
    scooter = imsLoader.getImage("scooter");
    ufo = imsLoader.getImage("ufo");
    owl = imsLoader.getImage("owl");
    basn8 = imsLoader.getImage("basn6a08");
    basn16 = imsLoader.getImage("basn6a16");

    /* Initialize ImagesPlayers for the 'n' and 's' images.
       The 'numbers' sequence is not cycled, the other are. 
    */
    numbersPlayer = 
         new ImagesPlayer("numbers", PERIOD, 1, false, imsLoader);
    numbersPlayer.setWatcher(this);     // report the sequence's end back here

    figurePlayer = 
         new ImagesPlayer("figure", PERIOD, 2, true, imsLoader);
    carsPlayer =
         new ImagesPlayer("cars", PERIOD, 1, true, imsLoader);
    catsPlayer =
         new ImagesPlayer("cats", PERIOD, 0.5, true, imsLoader);
    kaboomPlayer = 
         new ImagesPlayer("kaboom", PERIOD, 1.5, true, imsLoader);

    // the 'g' image, the fighter is set using a filename prefix
    fighter = imsLoader.getImage("fighter", "left");
  }  // end of initImages()


  public void sequenceEnded(String imageName)
  // called by ImagesPlayer when its images sequence has finished
  {  System.out.println( imageName + " sequence has ended");  }



  public void actionPerformed(ActionEvent e)
  // triggered by the timer: update, repaint
  { 
    if (justStarted)   // don't do updates the first time through
      justStarted = false;
    else
      imagesUpdate();

    repaint();  
  } // end of actionPerformed()



  private void imagesUpdate()
  { 
    // numbered images ('n' images); using ImagesPlayer
    numbersPlayer.updateTick();
    if (counter%30 == 0)     // restart the image sequence periodically
      numbersPlayer.restartAt(2);

    figurePlayer.updateTick();

    // strip images ('s' images); using ImagesPlayer
    carsPlayer.updateTick();
    catsPlayer.updateTick(); 
    kaboomPlayer.updateTick();

    // grouped images ('g' images)
    // The 'fighter' images are the only grouped images in this example.
    updateFighter();
  } // end of imagesUpdate()



  private void updateFighter()
  /* The info in Images/imsInfo.txt for 'fighter' is:
        g fighter  left.gif right.gif still.gif up.gif

     The counter is translated into a value which cycles
     through the four filename prefixes: left, right, still, up.

     The images are shown using their filename prefixes (although a
     positional approach could be used, which would allow an
     ImagesPlayer to be employed.
  */
  {
    int posn = counter % 4;  // number of fighter images; 
          // could use  imsLoader.numImages("fighter")
    switch(posn) {
      case 0:
        fighter = imsLoader.getImage("fighter", "left");
        break;
      case 1:
        fighter = imsLoader.getImage("fighter", "right");
        break;
      case 2:
        fighter = imsLoader.getImage("fighter", "still");
        break;
      case 3:
        fighter = imsLoader.getImage("fighter", "up");
        break;
      default:
        System.out.println("Unknown fighter group name");
        fighter = imsLoader.getImage("fighter", "left");
        break;
    }
  }  // end of updateFighter()



  public void paintComponent(Graphics g)
   /* The special effects are applied to the single images 
      inside paintComponent().

      The single images are:
         * atomic  : a GIF of an atom
         * balls   : a JPEG of a group of coloured balls
         * bee     : a GIF of a bee
         * cheese  : a GIF of a block of cheese
         * eyeChart : a GIF of a black and white eye chart
         * house   : a GIF of a house

         * pumpkin : an opaque PNG of a halloween cartoon pumpkin

         * scooter : a GIF of a scooter
         * ufo     : a GIF of a flying saucer

         * owl     : a transparent PNG of an owl
         * basn8   : a translucent PNG using a 8-bit alpha channel
         * basn16  : a translucent PNG using a 16-bit alpha channel
     
      All the GIFs have transparent elements, apart from eyeChart.
   */
   {
     super.paintComponent(g);
     Graphics2D g2d = (Graphics2D)g; 

     // use antialiasing
     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);

     // smoother (and slower) image transformations  (e.g. for resizing)
     g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                          RenderingHints.VALUE_INTERPOLATION_BILINEAR);

     // clear the background
     g2d.setColor(Color.blue);
     g2d.fillRect(0, 0, PWIDTH, PHEIGHT);

     // display current images
     // ------------------ single 'o' images ---------------------

     /* The programmer must manualy edit the code here in order to
        draw the 'o' images with different special effects. */

     // drawImage(g2d, atomic, 10, 25);
     rotatingImage(g2d, atomic, 10, 25);
     // imageSfx.drawBlurredImage(g2d, atomic, 10, 25);

     // drawImage(g2d, balls, 110, 25);
     // blurringImage(g2d, balls, 110, 25);
     // negatingImage(g2d, balls, 110, 25);
     mixedImage(g2d, balls, 110, 25);

     // drawImage(g2d, bee, 210, 25);
     //rotatingImage(g2d, bee, 210, 25);
     teleImage = teleportImage(g2d, bee, teleImage, 210, 25);

     // drawImage(g2d, cheese, 310, 25);
     flippingImage(g2d, cheese, 310, 25);
     // mixedImage(g2d, cheese, 310, 25);

     // drawImage(g2d, eyeChart, 410, 25);
     // reddenImage(g2d, eyeChart, 410, 25);
     blurringImage(g2d, eyeChart, 410, 25);
     // teleImage = teleportImage(g2d, eyeChart, teleImage, 410, 25);

     // drawImage(g2d, house, 540, 25);
     reddenImage(g2d, house, 540, 25);
     // imageSfx.drawRedderImage(g2d, house, 540, 25, 5.0f);

     // drawImage(g2d, pumpkin, 710, 25);
     // imageSfx.drawBrighterImage(g2d, pumpkin, 710, 25, 2.0f);
     // brighteningImage(g2d, pumpkin, 710, 25);
     zapImage = zapImage(g2d, pumpkin, zapImage, 710, 25);

     // drawImage(g2d, scooter, 10, 160);
     // flippingImage(g2d, scooter, 10, 160);
     // imageSfx.drawNegatedImage(g2d, scooter, 10, 160);
     // blurringImage(g2d, scooter, 10, 160);
     brighteningImage(g2d, scooter, 10, 160);

     // drawImage(g2d, ufo, 110, 140);
     fadingImage(g2d, ufo, 110, 140);
     // zapImage = zapImage(g2d, ufo, zapImage, 110, 140);

     // drawImage(g2d, owl, 450, 250);
     negatingImage(g2d, owl, 450, 250);
     // brighteningImage(g2d, owl, 450, 250);
     // resizingImage(g2d, owl, 450, 250);

     // drawImage(g2d, basn8, 650, 250);
     // resizingImage(g2d, basn8, 650, 250);
     mixedImage(g2d, basn8, 650, 250);

     // drawImage(g2d, basn16, 750, 250);
     resizingImage(g2d, basn16, 750, 250);
     // imageSfx.drawNegatedImage(g2d, basn16, 750, 250);


     /* The following lines do _not_ need to be edited. */

     // --------------- numbered images -------------------

     drawImage(g2d, numbersPlayer.getCurrentImage(), 280, 140);
     drawImage(g2d, figurePlayer.getCurrentImage(), 550, 140);

     // --------------- strip images ----------------------

     drawImage(g2d, catsPlayer.getCurrentImage(), 10, 235);
     drawImage(g2d, kaboomPlayer.getCurrentImage(), 150, 250);
     drawImage(g2d, carsPlayer.getCurrentImage(), 250, 250);

     // --------------- grouped images --------------------

     drawImage(g2d, fighter, 350, 250);

     // -----------
     reportAccelMemory();

     // increment the counter, modulo 100
     counter = (counter + 1)% 100;    // 0-99 is a large enough range
   } // end of paintComponent()



   private void drawImage(Graphics2D g2d, BufferedImage im, int x, int y)
   /* Draw the image, or a yellow box with ?? in it if there is no image. */
   { 
     if (im == null) {
       // System.out.println("Null image supplied");
       g2d.setColor(Color.yellow);
       g2d.fillRect(x, y, 20, 20);
       g2d.setColor(Color.black);
       g2d.drawString("??", x+10, y+10);
     }
     else
       g2d.drawImage(im, x, y, this);
   } // end of drawImage()



  private void reportAccelMemory()
  // report any change in the amount of accelerated memory
  {
    int mem = gd.getAvailableAcceleratedMemory();   // in bytes
    int memChange = mem - accelMemory;

    if (memChange != 0) 
      System.out.println(counter + ". Acc. Mem: " + 
                df.format( ((double)accelMemory)/(1024*1024) ) + " MB; Change: " +
                df.format( ((double)memChange)/1024 ) + " K");
    accelMemory = mem;
  }  // end of reportAcceleMemory()



  // ----------------- methods that use ImageSFXs class ---------------------

  /* Most of the methods here support animated effects: effects which
     gradually change over a series of timer ticks (i.e. animation frames). 

     The actual effect (e.g. fading) is obtained by calling the 
     relevant method in ImageSFXs. 

     The animation is 'hacked' together by examining the current 
     counter value (which is incremented on each 'tick' of the
     timer (modulo 100). The counter value is used in some way to
     vary the input arguments to the ImageSFXs method.
  */


  private void resizingImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Gradually resize the image, scaling it by the same amount along the
     x- and y- axes. The image is unaffected, since the operation 
     writes to the screen.

     The ImageSFXs method used is drawResizedImage() which uses 
     drawImage() rather than AffineTransforms, so is faster.
  */
  {
    double sizeChange = (counter%6)/2.0 + 0.5;    // gives 0.5 -- 3
    imageSfx.drawResizedImage(g2d, im, x, y, sizeChange, sizeChange);
  }  // end of resizingImage()



  private void flippingImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Flip the image horizontally, vertically or both (a double flip).
     The method cycles through these based on the counter value. 

     The ImageSFXs method used is getFlippedImage(). The flipping uses 
     drawImage() rather than AffineTransforms, so is faster.

     getFlippedImage() returns a new BufferedImage, but it is not saved
     globally in this example.
  */
  {
    BufferedImage flipIm = null;
    if (counter%4 == 0)
      flipIm = im;    // no flipping
    else if (counter%4 == 1)
      flipIm = imageSfx.getFlippedImage(im, ImageSFXs.HORIZONTAL_FLIP);
    else if (counter%4 == 2)
      flipIm = imageSfx.getFlippedImage(im, ImageSFXs.VERTICAL_FLIP);
    else 
      flipIm = imageSfx.getFlippedImage(im, ImageSFXs.DOUBLE_FLIP);

    drawImage(g2d, flipIm, x, y);
  }  // end of flippingImage()



  private void fadingImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Gradually fade out the image over the space of 25 frames, then
     start again. The fade is continuous across the entire image rather
     than pixelated as in teleportImage() (see below). The image
     is unaffected, since the operation writes to the screen.

     The ImageSFXs method used is drawFadedImage() which
     uses an AlphaComposite operation. When alpha == 0 the image will be
     invisible.
  */
  {
    float alpha = 1.0f - (((counter*4)%100)/100.0f);
    imageSfx.drawFadedImage(g2d, ufo, x, y, alpha);
  }  // end of fadingImage()



  private void rotatingImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Rotate the image in steps of 10 degrees in a clockwise direction,
     using the image's center as the center of rotation. A copy of the
     image is rotated.

     Image clipping may occur if the image doesn't have sufficient 
     transparent space around it. 

     The ImageSFXs method used is getRotatedImage() which
     uses a AffineTransform operation.
  */
  {
    int angle = (counter * 10) % 360;
    // System.out.println("Im width/height: " + im.getWidth() + ", " + 
    //                                          im.getHeight() );
    BufferedImage rotIm = imageSfx.getRotatedImage(im, angle);
    drawImage(g2d, rotIm, x, y);
  }  // end of rotatingImage()



  private void blurringImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Increasingly blur the image over a period of 8 frames,
     then start again with the original image. The image
     is unaffected, since the operation writes to the screen.

     Any alpha component is unaffected.

     The ImageSFXs method used is drawBlurredImage() which
     uses a ConvolveOp. The increased bluriness is achieved by making
     the ConvolveOp's matrix bigger. The size of the matrix is
     determined by the fadeSize value supplied here.
  */
  {
    int fadeSize = (counter%8)*2 + 1;    // gives 1,3,5,7,9,11,13,15
    if (fadeSize == 1)
      drawImage(g2d, im, x, y);     // start again with the original image
    else 
      imageSfx.drawBlurredImage(g2d, im, x, y, fadeSize);
  }  // end of blurringImage()



  private void reddenImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Increase the redness of the image over a period of 21 frames,
     then start again with the original colours. The image
     is unaffected, since the operation writes to the screen.

     As the redness increases, the amount of blue and green decreases.
     Any alpha component is unaffected.

     The ImageSFXs method used is drawRedderImage().
     There are two versions of it in ImageSFXs: one uses a LookupOp, 
     the other a RescaleOp. Currently the RescaleOp version is commented out.
     Functionally, there is no difference between them.
  */
  {
    float brightness = 1.0f + (((float) counter%21)/10.0f);    
             // gives values in the range 1.0-3.0, in steps of 0.1
    if (brightness == 1.0f)
      drawImage(g2d, im, x, y);   // start again with the original image
    else 
      imageSfx.drawRedderImage(g2d, im, x, y, (float) brightness);
  }  // end of reddenImage()



  private void brighteningImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Increase the brighness of the image over a period of 9 frames,
     then start again with the original colours. The image
     is unaffected, since the operation writes to the screen.

     Any alpha component is unaffected.

     The ImageSFXs method used is drawBrighterImage() which
     uses a RescaleOp.
  */
  { int brightness = counter%9;    // gives 0-8
    if (brightness == 0)
      drawImage(g2d, im, x, y);   // start again with the original image
    else 
      imageSfx.drawBrighterImage(g2d, im, x, y, (float) brightness);
  }  // end of brighteningImage()



  private void negatingImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Flip between the original image and its negative depending on
     the counter value.
     The ImageSFXs method used is drawNegatedImage().
  */
  { if (counter%10 < 5)   // show the negative
      imageSfx.drawNegatedImage(g2d, im, x, y);
    else  // show the original
      drawImage(g2d, im, x, y);
  }  // end of negatingImage()



  private void mixedImage(Graphics2D g2d, BufferedImage im, int x, int y)
  /* 
     Flip between the original image and mixed up coloured versions depending on
     the counter value.
     The ImageSFXs method used is drawMixedColouredImage().
  */
  { if (counter%10 < 5)   // mix it up 
      imageSfx.drawMixedColouredImage(g2d, im, x, y);
    else  // show the original for a while
      drawImage(g2d, im, x, y);
  }  // end of mixedImage()




  private BufferedImage teleportImage(Graphics2D g2d,
                 BufferedImage im, BufferedImage teleIm, int x, int y)
  /* 
     The 'teleport' effect is the gradual disappearance of the image,
     over the course of 7 frames (after which the effect repeats). 
     Individual pixels are made 0, which results in transparency.

     This pixelated effect should be compared with the smoother
     fading offered by fadingImage() (see above).

     The changes are applied to a copy of the image (stored in the global 
     teleImage). The copy is assigned an alpha channel if the
     original does not have one, to ensure that the image becomes
     transparent (rather than black).

     The ImageSFXs method used is eraseImageParts().
     Its second argument specifies that the affected pixels
     are located in the image's pixel array at 
     positions which are multiple of the supplied number.
  */
  {
    if (teleIm == null) {    // start the effect
      if (imageSfx.hasAlpha(im))
        teleIm = imageSfx.copyImage(im); 
      else   // no alpha channel
        teleIm = imageSfx.makeTransImage(im);  // give the copy an alpha channel
    }

    int eraseSteps = counter%7;    // range is 0 to 6
    switch(eraseSteps) {
      case 0:      // restart the effect
        if (imageSfx.hasAlpha(im))
          teleIm = imageSfx.copyImage(im); 
        else  // not transparent
          teleIm = imageSfx.makeTransImage(im); 
        break;
      case 1:
        imageSfx.eraseImageParts(teleIm, 11);  // every 11th pixel to go
        break;
      case 2:
        imageSfx.eraseImageParts(teleIm, 7);  // every 7th pixel
        break;
      case 3:
        imageSfx.eraseImageParts(teleIm, 5);  // 5th
        break;
      case 4:
        imageSfx.eraseImageParts(teleIm, 3);  // 3rd
        break;
      case 5:
        imageSfx.eraseImageParts(teleIm, 2);  // every 2nd pixel
        break;
      case 6:
        imageSfx.eraseImageParts(teleIm, 1);  
        break;               // every pixel to go, i.e. fully erased
      default:
        System.out.println("Unknown count for teleport");
        break;
    } // end switch

    drawImage(g2d, teleIm, x, y);
    return teleIm;
  }  // end of teleportImage()



  private BufferedImage zapImage(Graphics2D g2d, BufferedImage im, 
                              BufferedImage zapIm, int x, int y)
  /* 
     'zap' means the gradually changing of the image's visible parts to 
     a random mix of red and yellow pixels. The amount changed increases
     over the course of the effect (11 frames).

     The changes are applied to a copy of the image (stored in the global 
     zapImage). After 11 frames, the image is restored, and the effect 
     begins again.

     The amount of 'zapping' is controlled by the likelihood
     value which increases from 0 to 1.

     The method used in ImageSFXs is zapImageParts()
  */
  {  if ((zapIm == null) || (counter%11 == 0))
      zapIm = imageSfx.copyImage(im);    // restart the effect
    else {
      double likelihood = (counter%11)/10.0;    // produces range 0 to 1
      imageSfx.zapImageParts(zapIm, likelihood);
    }
    drawImage(g2d, zapIm, x, y);
    return zapIm;
  }  // end of zapImage()



   // --------------------------------------------

   public static void main(String args[])
   {
     // switch on translucency acceleration in Windows
    System.setProperty("sun.java2d.translaccel", "true"); 
    System.setProperty("sun.java2d.ddforcevram", "true"); 

     // switch on hardware acceleration if using OpenGL with pbuffers
     // System.setProperty("sun.java2d.opengl", "true"); 

     ImagesTests ttPanel = new ImagesTests();

     // create a JFrame to hold the test JPanel
     JFrame app = new JFrame("Image Tests");
     app.getContentPane().add(ttPanel, BorderLayout.CENTER);
     app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

     app.pack();
     app.setResizable(false);  
     app.setVisible(true);
  } // end of main()

} // end of ImagesTests class
