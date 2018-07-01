
// Sprite.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* A Sprite has a position, velocity (in terms of steps),
   an image, and can be deactivated.

   The sprite's image is managed with an ImagesLoader object,
   and an ImagesPlayer object for looping.

   The images stored until the image 'name' can be looped
   through by calling loopImage(), which uses an
   ImagesPlayer object.

*/
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7663]
 */
public class Sprite {
// default step sizes (how far to move in each update)

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm704e]
 */
    private static final int XSTEP = 5;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7048]
 */
    private static final int YSTEP = 5;
// default dimensions when there is no image

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7042]
 */
    private static final int SIZE = 12;
// image-related
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7039]
 */
    private ImagesLoader imsLoader;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7032]
 */
    private String imageName;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm702a]
 */
    private BufferedImage image;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7023]
 */
    private int width;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm701e]
 */
    private int height;
// image dimensions
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7016]
 */
    private ImagesPlayer player;
// for playing a loop of images

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm700f]
 */
    private boolean isLooping;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm700a]
 */
    private int pWidth;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7005]
 */
    private int pHeight;
// panel dimensions

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7000]
 */
    private boolean isActive = true;
// a sprite is updated and drawn only when it is active
// protected vars

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ffa]
 */
    protected int locx;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ff5]
 */
    protected int locy;
// location of sprite

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ff0]
 */
    protected int dx;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6feb]
 */
    protected int dy;
// amount to move for each update

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fdf]
 * @param x 
 * @param y 
 * @param w 
 * @param h 
 * @param imsLd 
 * @param name 
 */
    public  Sprite(int x, int y, int w, int h, ImagesLoader imsLd, String name) {        
    locx = x; locy = y;
    pWidth = w; pHeight = h;
    dx = XSTEP; dy = YSTEP;
    imsLoader = imsLd;
    setImage(name);    // the sprite's default image is 'name'
    } 
// end of Sprite()
// assign the name image to the sprite

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fd8]
 * @param name 
 */
    public void setImage(String name) {        
    imageName = name;
    image = imsLoader.getImage(imageName);
    if (image == null) {    // no image of that name was found
      System.out.println("No sprite image for " + imageName);
      width = SIZE;
      height = SIZE;
    }
    else {
      width = image.getWidth();
      height = image.getHeight();
    }
    // no image loop playing 
    player = null;
    isLooping = false;
    } 
// end of setImage()
/* Switch on loop playing. The total time for the loop is
     seqDuration secs. The update interval (from the enclosing
     panel) is animPeriod ms. */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fd0]
 * @param animPeriod 
 * @param seqDuration 
 */
    public void loopImage(int animPeriod, double seqDuration) {        
    if (imsLoader.numImages(imageName) > 1) {
      player = null;   // to encourage garbage collection of previous player
      player = new ImagesPlayer(imageName, animPeriod, seqDuration,
                                       true, imsLoader);
      isLooping = true;
    }
    else
      System.out.println(imageName + " is not a sequence of images");
    } 
// end of loopImage()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fca]
 */
    public void stopLooping() {        
    if (isLooping) {
      player.stop();
      isLooping = false;
    }
    } 
// of the sprite's image

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fc4]
 * @return 
 */
    public int getWidth() {        
  return width;  
    } 
// of the sprite's image

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fbe]
 * @return 
 */
    public int getHeight() {        
  return height;  
    } 
// of the enclosing panel

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fb8]
 * @return 
 */
    public int getPWidth() {        
  return pWidth;  
    } 
// of the enclosing panel

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fb2]
 * @return 
 */
    public int getPHeight() {        
  return pHeight;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fac]
 * @return 
 */
    public boolean isActive() {        
  return isActive;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6fa5]
 * @param a 
 */
    public void setActive(boolean a) {        
  isActive = a;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f9d]
 * @param x 
 * @param y 
 */
    public void setPosition(int x, int y) {        
  locx = x; locy = y;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f95]
 * @param xDist 
 * @param yDist 
 */
    public void translate(int xDist, int yDist) {        
  locx += xDist;  locy += yDist;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f8f]
 * @return 
 */
    public int getXPosn() {        
  return locx;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f89]
 * @return 
 */
    public int getYPosn() {        
  return locy;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f81]
 * @param dx 
 * @param dy 
 */
    public void setStep(int dx, int dy) {        
  this.dx = dx; this.dy = dy; 
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f7b]
 * @return 
 */
    public int getXStep() {        
  return dx;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f75]
 * @return 
 */
    public int getYStep() {        
  return dy;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f6c]
 * @return 
 */
    public Rectangle getMyRectangle() {        
  return  new Rectangle(locx, locy, width, height);  
    } 
// move the sprite

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f66]
 */
    public void updateSprite() {        
    if (isActive()) {
      locx += dx;
      locy += dy;
      if (isLooping)
        player.updateTick();  // update the player
    }
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f5f]
 * @param g 
 */
    public void drawSprite(Graphics g) {        
    if (isActive()) {
      if (image == null) {   // the sprite has no image
        g.setColor(Color.yellow);   // draw a yellow circle instead
        g.fillOval(locx, locy, SIZE, SIZE);
        g.setColor(Color.black);
      }
      else {
        if (isLooping)
          image = player.getCurrentImage();
        g.drawImage(image, locx, locy, null);
      }
    }
    } 
 }
// end of Sprite class
