
// BugRunner.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* A user can move a bat to hit a ball back up the screen.
   Each hit of a ball increments a 'rebounds' counter. Each
   missed ball decrements it.

   A 'new' ball is sent downwards from the top when the previous 
   ball has left the screen.

   When the number of rebounds reaches a prescribed maximum,
   or drops to 0, then the game is over, and a score is 
   reported.
  
   -----
   Pausing/Resuming/Quiting are controlled via the frame's window
   listener methods.

   Active rendering is used to update the JPanel. See WormP for
   another example, with additional statistics generation.

   Using Java 3D's timer: J3DTimer.getValue()
     *  nanosecs rather than millisecs for the period

   The MidisLoader, ClipsLoader, ImagesLoader, and ImagesPlayer
   classes are used for music, sounds, images, and animation.

   The bat and the ball are subclasses of a Sprite class.
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7666]
 */
public class BugRunner extends JFrame implements WindowListener {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm724b]
 */
    private static int DEFAULT_FPS = 40;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7242]
 */
    private BugPanel bp;
// where the game is drawn
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7238]
 */
    private MidisLoader midisLoader;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm722e]
 * @param period 
 */
    public  BugRunner(long period) {        
 super("BugRunner");
    // load the background MIDI sequence
    midisLoader = new MidisLoader();
    midisLoader.load("br", "blade_runner.mid");
    midisLoader.play("br", true);   // repeatedly play it
    Container c = getContentPane();    // default BorderLayout used
    bp = new BugPanel(this, period);
    c.add(bp, "Center");
    addWindowListener( this );
    pack();
    setResizable(false);
    setVisible(true);
    } 
// end of BugRunner() constructor
// ----------------- window listener methods -------------

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7224]
 * @param e 
 */
    public void windowActivated(WindowEvent e) {        
 bp.resumeGame();  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm721d]
 * @param e 
 */
    public void windowDeactivated(WindowEvent e) {        
 bp.pauseGame();  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7216]
 * @param e 
 */
    public void windowDeiconified(WindowEvent e) {        
  bp.resumeGame();  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm720f]
 * @param e 
 */
    public void windowIconified(WindowEvent e) {        
  bp.pauseGame(); 
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7208]
 * @param e 
 */
    public void windowClosing(WindowEvent e) {        
  bp.stopGame();  
     midisLoader.close();  // not really required
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7201]
 * @param e 
 */
    public void windowClosed(WindowEvent e) {        
        // your code here
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71fa]
 * @param e 
 */
    public void windowOpened(WindowEvent e) {        
        // your code here
    } 
// ----------------------------------------------------

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71f3]
 * @param args 
 */
    public static void main(String[] args) {        
 
    long period = (long)  1000.0/DEFAULT_FPS;
    // System.out.println("fps: " + DEFAULT_FPS + "; period: " + period + " ms");
    new BugRunner(period*1000000L);    // ms --> nanosecs 
    } 
 }
// end of BugRunner class
