
// BugPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* The game's drawing surface. Uses active rendering to a JPanel
   with the help of Java 3D's timer.
   See WormP for a version with statistics generation.
*/
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7665]
 */
public class BugPanel extends JPanel implements Runnable {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71ec]
 */
    private static final int PWIDTH = 500;
// size of panel

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71e6]
 */
    private static final int PHEIGHT = 400;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71e0]
 */
    private static final int NO_DELAYS_PER_YIELD = 16;
/* Number of frames with a delay of 0 ms before the animation thread yields
     to other running threads. */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71da]
 */
    private static int MAX_FRAME_SKIPS = 5;
// no. of frames that can be skipped in any one animation loop
// i.e the games state is updated but not rendered
// image and clip loader information files

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71d4]
 */
    private static final String IMS_INFO = "imsInfo.txt";

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71ce]
 */
    private static final String SNDS_FILE = "clipsInfo.txt";

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71c5]
 */
    private Thread animator;
// the thread that performs the animation

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71c0]
 */
    private volatile boolean running = false;
// used to stop the animation thread

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71b6]
 */
    private volatile boolean isPaused = false;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71af]
 */
    private long period;
// period between drawing in _nanosecs_
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7245]
 */
    private BugRunner bugTop;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm71a2]
 */
    private ClipsLoader clipsLoader;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7372]
 */
    private BallSprite ball;
// the sprites
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7193]
 */
    private BatSprite bat;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm718c]
 */
    private long gameStartTime;
// when the game started

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7187]
 */
    private int timeSpentInGame;
// used at game termination

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7182]
 */
    private volatile boolean gameOver = false;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm717b]
 */
    private int score = 0;
// for displaying messages
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm716f]
 */
    private Font msgsFont;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7162]
 */
    private FontMetrics metrics;
// off-screen rendering
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7158]
 */
    private Graphics dbg;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm714b]
 */
    private Image dbImage = null;
// holds the background image
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm713e]
 */
    private BufferedImage bgImage = null;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7134]
 * @param br 
 * @param period 
 */
    public  BugPanel(BugRunner br, long period) {        
    bugTop = br;
    this.period = period;
    setDoubleBuffered(false);
    setBackground(Color.black);
    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));
    setFocusable(true);
    requestFocus();    // the JPanel now has focus, so receives key events
	addKeyListener( new KeyAdapter() {
       public void keyPressed(KeyEvent e)
       { processKey(e);  }
     });
    // load the background image
    ImagesLoader imsLoader = new ImagesLoader(IMS_INFO); 
    bgImage = imsLoader.getImage("bladerunner");
    // initialise the clips loader
    clipsLoader = new ClipsLoader(SNDS_FILE); 
    // create game sprites
    bat = new BatSprite(PWIDTH, PHEIGHT, imsLoader, 
                                     (int)(period/1000000L) ); // in ms
    ball = new BallSprite(PWIDTH, PHEIGHT, imsLoader, clipsLoader, this, bat); 
    addMouseListener( new MouseAdapter() {
      public void mousePressed(MouseEvent e)
      { testPress(e.getX()); }  // handle mouse presses
    });
    // set up message font
    msgsFont = new Font("SansSerif", Font.BOLD, 24);
    metrics = this.getFontMetrics(msgsFont);
    } 
// end of BugPanel()
// handles termination and game-play keys

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm712a]
 * @param e 
 */
    private void processKey(KeyEvent e) {        
    int keyCode = e.getKeyCode();
    // termination keys
	// listen for esc, q, end, ctrl-c on the canvas to
	// allow a convenient exit from the full screen configuration
    if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
        (keyCode == KeyEvent.VK_END) ||
        ((keyCode == KeyEvent.VK_C) && e.isControlDown()) )
      running = false;
    
    // game-play keys
    if (!isPaused && !gameOver) {
      if (keyCode == KeyEvent.VK_LEFT)
        bat.moveLeft(); 
      else if (keyCode == KeyEvent.VK_RIGHT)
        bat.moveRight();
      else if (keyCode == KeyEvent.VK_DOWN)
        bat.stayStill();
    }
    } 
// called by BallSprite to signal that the game is over

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7124]
 */
    public void gameOver() {        
 
    int finalTime = 
          (int) ((System.nanoTime() - gameStartTime)/1000000000L);  // ns --> secs
    score = finalTime;   // could be more fancy!
    clipsLoader.play("gameOver", false);   // play a game over clip once
    gameOver = true;  
    } 
// end of gameOver()
// wait for the JPanel to be added to the JFrame before starting

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm711e]
 */
    public void addNotify() {        
 super.addNotify();   // creates the peer
    startGame();         // start the thread
    } 
// initialise and start the thread

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7118]
 */
    private void startGame() {        
 
    if (animator == null || !running) {
      animator = new Thread(this);
	  animator.start();
    }
    } 
// called when the JFrame is activated / deiconified

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7112]
 */
    public void resumeGame() {        
  isPaused = false;  
    } 
// called when the JFrame is deactivated / iconified

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm710c]
 */
    public void pauseGame() {        
 isPaused = true;   
    } 
// called when the JFrame is closing

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7106]
 */
    public void stopGame() {        
  running = false;   
    } 
// ----------------------------------------------
// use a mouse press to control the bat

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm70ff]
 * @param x 
 */
    private void testPress(int x) {        
 if (!isPaused && !gameOver)
      bat.mouseMove(x);
    } 
/* The frames of the animation are drawn inside the while loop. */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm70f9]
 */
    public void run() {        
    long beforeTime, afterTime, timeDiff, sleepTime;
    long overSleepTime = 0L;
    int noDelays = 0;
    long excess = 0L;
    gameStartTime = System.nanoTime();
    beforeTime = gameStartTime;
	running = true;
	while(running) {
	  gameUpdate();
      gameRender();
      paintScreen();
      afterTime = System.nanoTime();
      timeDiff = afterTime - beforeTime;
      sleepTime = (period - timeDiff) - overSleepTime;  
      if (sleepTime > 0) {   // some time left in this cycle
        try {
          Thread.sleep(sleepTime/1000000L);  // nano -> ms
        }
        catch(InterruptedException ex){}
        overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
      }
      else {    // sleepTime <= 0; the frame took longer than the period
        excess -= sleepTime;  // store excess time value
        overSleepTime = 0L;
        if (++noDelays >= NO_DELAYS_PER_YIELD) {
          Thread.yield();   // give another thread a chance to run
          noDelays = 0;
        }
      }
      beforeTime = System.nanoTime();
      /* If frame animation is taking too long, update the game state
         without rendering it, to get the updates/sec nearer to
         the required FPS. */
      int skips = 0;
      while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
        excess -= period;
	    gameUpdate();    // update state but don't render
        skips++;
      }
	}
    System.exit(0);   // so window disappears
    } 
// end of run()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm70f3]
 */
    private void gameUpdate() {        
 if (!isPaused && !gameOver) {
      ball.updateSprite();  
      bat.updateSprite();  
    }
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm70ed]
 */
    private void gameRender() {        
    if (dbImage == null){
      dbImage = createImage(PWIDTH, PHEIGHT);
      if (dbImage == null) {
        System.out.println("dbImage is null");
        return;
      }
      else
        dbg = dbImage.getGraphics();
    }
    // draw the background: use the image or a black colour
    if (bgImage == null) {
      dbg.setColor(Color.black);
      dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
    }
    else
      dbg.drawImage(bgImage, 0, 0, this);
    // draw game elements
    ball.drawSprite(dbg); 
    bat.drawSprite(dbg);
    reportStats(dbg);
    if (gameOver)
      gameOverMessage(dbg);
    } 
// Report the number of returned balls, and time spent playing

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm70e6]
 * @param g 
 */
    private void reportStats(Graphics g) {        
    if (!gameOver)    // stop incrementing the timer once the game is over
      timeSpentInGame = 
          (int) ((System.nanoTime() - gameStartTime)/1000000000L);  // ns --> secs
	g.setColor(Color.yellow);
    g.setFont(msgsFont);
    ball.drawBallStats(g, 15, 25);  // the ball sprite reports the ball stats
	g.drawString("Time: " + timeSpentInGame + " secs", 15, 50);
   
	g.setColor(Color.black);
    } 
// end of reportStats()
// center the game-over message in the panel

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm70df]
 * @param g 
 */
    private void gameOverMessage(Graphics g) {        
    String msg = "Game Over. Your score: " + score;
	int x = (PWIDTH - metrics.stringWidth(msg))/2; 
	int y = (PHEIGHT - metrics.getHeight())/2;
	g.setColor(Color.red);
    g.setFont(msgsFont);
	g.drawString(msg, x, y);
    } 
// end of gameOverMessage()
// use active rendering to put the buffered image on-screen

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm70d9]
 */
    private void paintScreen() {        
 
    Graphics g;
    try {
      g = this.getGraphics();
      if ((g != null) && (dbImage != null))
        g.drawImage(dbImage, 0, 0, null);
      // Sync the display on some systems.
      // (on Linux, this fixes event queue problems)
      Toolkit.getDefaultToolkit().sync();
      g.dispose();
    }
    catch (Exception e)
    { System.out.println("Graphics context error: " + e);  }
    } 
// end of paintScreen()
 }
// end of BugPanel class
