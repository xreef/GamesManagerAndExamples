
// BallSprite.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* The ball drops from the top of the panel at varying speeds and
   angles of trajectory. It bounces off the walls.

   If the ball hits the bat, it will rebound and disappear off
   the top of the screen. If the bat misses it, then the ball will
   disappear off the bottom of the panel.

   After leaving the screen, the ball is placed back at the top
   of the panel, and used again.

   The number of 'returned' balls is incremented when the ball bounces
   off the bat (numRebounds is incremented). When the ball drops
   off the bottom, numRebounds is decremented.

   If numRebounds reaches MAX_BALLS_RETURNED, the game is over.
   If numRebounds reaches 0, the game is also over.

   The ball is represented by several different images,
   being changed at the start of the next ball drop.

   Various clips are played when the ball hits the walls and the bat.
*/
import java.awt.*;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7669]
 */
public class BallSprite extends Sprite {
// the ball's x- and y- step values are STEP +/- STEP_OFFSET

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm73a0]
 */
    private static final int STEP = 8;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm739a]
 */
    private static final int STEP_OFFSET = 2;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7390]
 */
    private static final String[] ballNames = {"rock1", "orangeRock", "computer", "ball"};
// images used for the balls
// reach this number of balls to end the game

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm738a]
 */
    private static final int MAX_BALLS_RETURNED = 16;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7384]
 */
    private int nameIndex;
// for choosing a ball name
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm737c]
 */
    private ClipsLoader clipsLoader;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm736f]
 */
    private BugPanel bp;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7365]
 */
    private BatSprite bat;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm735e]
 */
    private int numRebounds;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7352]
 * @param w 
 * @param h 
 * @param imsLd 
 * @param cl 
 * @param bp 
 * @param b 
 */
    public  BallSprite(int w, int h, ImagesLoader imsLd, ClipsLoader cl, BugPanel bp, BatSprite b) {        
 
    super( w/2, 0, w, h, imsLd, ballNames[0]);  
           // the ball is positioned in the middle at the top of the panel
    clipsLoader = cl;
    this.bp = bp;
    bat = b;
    nameIndex = 0;
    numRebounds = MAX_BALLS_RETURNED/2;  
        // the no. of returned balls starts half way to the maximum
    initPosition();
    } 
// initialise the ball's image, position and step values (speed)

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm734b]
 */
    private void initPosition() {        
 
    setImage( ballNames[nameIndex]);
    nameIndex = (nameIndex+1)%ballNames.length;
    setPosition( (int)(getPWidth() * Math.random()), 0);   
                                  // somewhere along the top
    int step = STEP + getRandRange(STEP_OFFSET);
    int xStep = ((Math.random() <  0.5) ? -step : step); // move left or right
    setStep(xStep, STEP + getRandRange(STEP_OFFSET));   // move down
    } 
// end of initPosition()
// random number generator between -x and x

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7344]
 * @return 
 * @param x 
 */
    private int getRandRange(int x) {        
   return ((int)(2 * x * Math.random())) - x;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm733e]
 */
    public void updateSprite() {        
    hasHitBat();
    goneOffScreen();
    hasHitWall();
    // System.out.println("ball (" + locx + ", " + locy + ")");
    super.updateSprite();
    } 
// end of updateSprite()
/* If the ball has hit the bat, make it bounce up.
     The y-step is reversed, and the ball is moved up a 
     little bit, so it no longer intersects the bat.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7338]
 */
    private void hasHitBat() {        
    Rectangle rect = getMyRectangle();
    if (rect.intersects( bat.getMyRectangle() )) {     // bat collision?
      clipsLoader.play("hitBat", false);
      Rectangle interRect = rect.intersection(bat.getMyRectangle());
      dy = -dy;       // reverse ball's y-step direction
      locy -= interRect.height;    // move the ball up
    }
    } 
/* Respond when the ball has gone off the top
     of the panel or the bottom. Adjust numRebounds and either
     finish the game or reuse the ball. */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7332]
 */
    private void goneOffScreen() {        
    if (((locy+getHeight()) <= 0) && (dy < 0)) { // off top and moving up
      numRebounds++;
      if (numRebounds == MAX_BALLS_RETURNED)
        bp.gameOver();    // finish
      else
        initPosition();   // start the ball in a new position
    }
    else if ((locy >= getPHeight()) && (dy > 0)) { // off bottom and moving down
      numRebounds--;
      if (numRebounds == 0)
        bp.gameOver();
      else
        initPosition();
    }
    } 
/* Respond when the ball hits a wall.
     Only change the ball's direction if the present direction 
     (dx/dy) is heading over the edge.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm732c]
 */
    private void hasHitWall() {        
    if ((locx <= 0) && (dx < 0)) {  // touching lhs and moving left
      clipsLoader.play("hitLeft", false);
      dx = -dx;   // move right
    }
    else if ((locx+getWidth() >= getPWidth()) && (dx > 0)) {   
		                           // touching rhs and moving right
      clipsLoader.play("hitRight", false);
      dx = -dx;   // move left
    }
    } 
// Called from BugPanel to report the returned balls

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7320]
 * @param g 
 * @param x 
 * @param y 
 */
    public void drawBallStats(Graphics g, int x, int y) {        
  g.drawString("Returns: " + numRebounds + "/" + MAX_BALLS_RETURNED, x, y);  
    } 
 }
// end of BallSprite class
