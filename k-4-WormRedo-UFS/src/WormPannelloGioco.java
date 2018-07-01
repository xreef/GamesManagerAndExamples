// WormPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The game's drawing surface. It shows:
     - the moving worm
     - the obstacles (blue boxes)
     - the current average FPS and UPS
*/

import it.reef.concrete.Fruit;
import it.reef.concrete.WallManager;
import it.reef.input.WormKeyAdapter;
import it.reef.people.Worm;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.DecimalFormat;


public class WormPannelloGioco extends JPanel implements Runnable
{
  private static int PWIDTH = 500;   // size of panel
  private static int PHEIGHT = 400; 

  private static int DOTSIZE = 10;
  
  private static long MAX_STATS_INTERVAL = 1000000000L;
  // private static long MAX_STATS_INTERVAL = 1000L;
    // record stats every 1 second (roughly)

  private static final int NO_DELAYS_PER_YIELD = 16;
  /* Number of frames with a delay of 0 ms before the animation thread yields
     to other running threads. */

  private static int MAX_FRAME_SKIPS = 5;   // was 2;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered

  private static int NUM_FPS = 10;
     // number of FPS values stored to get an average


  // used for gathering statistics
  private long statsInterval = 0L;    // in ns
  private long prevStatsTime;   
  private long totalElapsedTime = 0L;
  private long gameStartTime;
  private int timeSpentInGame = 0;    // in seconds

  private long frameCount = 0;
  private double fpsStore[];
  private long statsCount = 0;
  private double averageFPS = 0.0;

  private long framesSkipped = 0L;
  private long totalFramesSkipped = 0L;
  private double upsStore[];
  private double averageUPS = 0.0;


  private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
  private DecimalFormat timedf = new DecimalFormat("0.####");  // 4 dp


  private Thread animator;           // the thread that performs the animation
  private boolean running = false;   // used to stop the animation thread
  private boolean isPaused = false;

  private boolean isLevelComplete=false;
  
  private long period;                // period between drawing in _nanosecs_
  private int numLevel=20;

  private WormFinestraPrincipale  wcFP;
//  private Worm fred;       // the worm
//  private Obstacles obs;   // the obstacles


  // used at game termination
  private boolean gameOver = false;
  private int score = 0;
  private int globalScore=0;
  private Font font;
  private FontMetrics metrics;

  private int schema = 0;
  
  // off screen rendering
  private Graphics dbg; 
  private Image dbImage = null;


  Worm vermicello;
  WallManager wallManager;
  Fruit fruit;
  WormKeyAdapter kavermicello;
  
  private Rectangle pauseArea, quitArea;  // globals
private boolean isOverPauseButton;
private boolean isOverQuitButton;
private boolean finishedOff;
  
  public WormPannelloGioco(WormFinestraPrincipale wc, long period)
  {
    wcFP = wc;
    this.period = period;

    PWIDTH = wcFP.pWidth;
    PHEIGHT = wcFP.pHeight;
    
    DOTSIZE = Math.min(Math.round(PWIDTH/50), (PHEIGHT/40));
    
    setBackground(Color.white);
    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

    setFocusable(true);
    requestFocus();    // the JPanel now has focus, so receives key events
	readyForTermination();

		wallManager = new WallManager(DOTSIZE,schema++);
	
		fruit = new Fruit(PWIDTH, PHEIGHT, DOTSIZE, wallManager);
		
		vermicello = new Worm(PWIDTH, PHEIGHT, DOTSIZE, wallManager, fruit);
	
	  kavermicello = new WormKeyAdapter(vermicello);
	  
	  addKeyListener(kavermicello);
	  
	  
	 // vermicello.setKeyAdapterVermicello(kavermicello);
	  
	    addKeyListener(new KeyAdapter(){

			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()=='p'){
					if (isPaused()){
						resumeGame();
					}else{
						pauseGame();
					}
				}
			}
	    	
	    });
	    
//    addMouseListener( new MouseAdapter() {
//      public void mousePressed(MouseEvent e)
//      { testPress(e.getX(), e.getY()); }
//    });

	    

	  



    // in WormPanel( )
    // specify screen areas for the buttons
    pauseArea = new Rectangle(PWIDTH-100, PHEIGHT-45, 70, 15);
    quitArea  = new Rectangle(PWIDTH-100, PHEIGHT-20, 70, 15);

    addMouseMotionListener( new MouseMotionAdapter( ) {
        public void mouseMoved(MouseEvent e)
        { testMove(e.getX( ), e.getY( )); }
      });
	    
    // in the WormPanel constructor
    addMouseListener( new MouseAdapter( ) {
      public void mousePressed(MouseEvent e)
      { testPress(e.getX( ), e.getY( )); }
    });
    
    // set up message font
    font = new Font("SansSerif", Font.BOLD, 12);
    metrics = this.getFontMetrics(font);

    // initialise timing elements
    fpsStore = new double[NUM_FPS];
    upsStore = new double[NUM_FPS];
    for (int i=0; i < NUM_FPS; i++) {
      fpsStore[i] = 0.0;
      upsStore[i] = 0.0;
    }
  }  // end of WormPanel()

  private void testMove(int x, int y)
  // is (x,y) over the Pause or Quit button?
  {
    if (running) {   // stops problems with a rapid move
                     // after pressing Quit
      isOverPauseButton = pauseArea.contains(x,y) ? true : false;
      isOverQuitButton = quitArea.contains(x,y) ? true : false;
    }
  }




  private void testPress(int x, int y)
  {
  
    if (isOverPauseButton)
      isPaused = !isPaused;     // toggle pausing
  
    else if (isOverQuitButton)
      running = false;
    else {
      if (!isPaused && !gameOver) {

      }
    }
  }
  
  private void readyForTermination()
  {
	addKeyListener( new KeyAdapter() {
	// listen for esc, q, end, ctrl-c on the canvas to
	// allow a convenient exit from the full screen configuration
       public void keyPressed(KeyEvent e)
       { int keyCode = e.getKeyCode();
         if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
             (keyCode == KeyEvent.VK_END) ||
             ((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
           running = false;
         }
       }
     });
  }  // end of readyForTermination()

  private void finishOff( )
  { if (!finishedOff) {
      finishedOff = true;
      printStats( );
      System.exit(0);
    }
  }
  
  public void addNotify()
  // wait for the JPanel to be added to the JFrame before starting
  { 
	  super.addNotify();   // creates the peer
	  startGame();         // start the thread
  }


  private void startGame()
  // initialise and start the thread 
  { 
    if (animator == null || !running) {
      animator = new Thread(this);
	  animator.start();
    }
  } // end of startGame()
    

  // ------------- game life cycle methods ------------
  // called by the JFrame's window listener methods


  public void resumeGame()
  // called when the JFrame is activated / deiconified
  {  isPaused = false;  } 


  public void pauseGame()
  // called when the JFrame is deactivated / iconified
  { isPaused = true;   } 


  public void stopGame() 
  // called when the JFrame is closing
  {  running = false;   }

  public boolean isPaused(){
	  return isPaused;
  }
  // ----------------------------------------------


  public void run()
  /* The frames of the animation are drawn inside the while loop. */
  {
    long beforeTime, afterTime, timeDiff, sleepTime;
    long overSleepTime = 0L;
    int noDelays = 0;
    long excess = 0L;

    gameStartTime = System.nanoTime();
    prevStatsTime = gameStartTime;
    beforeTime = gameStartTime;

	running = true;

	while(running) {
	  gameUpdate();
      gameRender();
      paintScreen();
      
      gameCompleteControl();
      gameOverControl();
      // fruit.lc=true && !isPaused;
      
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
      framesSkipped += skips;

      storeStats();
	}

    printStats();
    System.exit(0);   // so window disappears
  } // end of run()


  private void gameOverControl() {
	if (vermicello.isDead() && !gameOver) {
		this.score=Math.round(fruit.getFruitCounter()*10 / ((timeSpentInGame/10)+1));
		this.globalScore+=this.score;
		setLevel(schema, globalScore);
		gameOver=true;
	}
	
  }

  private void gameCompleteControl(){
	  if (fruit.levelComplete()) {
		  this.score=Math.round(fruit.getFruitCounter()*10 / ((timeSpentInGame/10)+1));
		  this.globalScore+=this.score;
		  if (schema<=numLevel){
			  wallManager = new WallManager(DOTSIZE,schema++);
		  }else{
			  schema=0;
			  wallManager = new WallManager(DOTSIZE,schema++);
			  this.period+=4;
		  }
		  setLevel(schema, globalScore);
		  
		  fruit = new Fruit(PWIDTH, PHEIGHT, DOTSIZE, wallManager);
			
		  vermicello.reset(wallManager, fruit);

		  this.isPaused=true;
		  this.isLevelComplete=true;
		  
//		  WormKeyAdapter kavermicello = new WormKeyAdapter(vermicello);
//		  
//		  addKeyListener(kavermicello);
	  }
  }


private void gameUpdate() 
  { 
	  if (!isPaused && !gameOver){
		  // fred.move();
		  
		  vermicello.update();
		  fruit.update();
		  
		  setBoxNumber(fruit.getFruitCounter()-1);
	  }
	  
	  if (!isPaused && isLevelComplete){
		  	isLevelComplete=false;
		    gameStartTime = System.nanoTime();
		    prevStatsTime = gameStartTime;
		    // beforeTime = gameStartTime;
	  }
  }  // end of gameUpdate()


//  private void gameRender()
//  {
//    if (dbImage == null){
//      dbImage = createImage(PWIDTH, PHEIGHT);
//      if (dbImage == null) {
//        System.out.println("dbImage is null");
//        return;
//      }
//      else
//        dbg = dbImage.getGraphics();
//    }
//
//    // clear the background
//    dbg.setColor(Color.white);
//    dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
//
////	dbg.setColor(Color.blue);
////    dbg.setFont(font);
////
////    // report frame count & average FPS and UPS at top left
////	// dbg.drawString("Frame Count " + frameCount, 10, 25);
////	dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " +
////                                df.format(averageUPS), 20, 25);  // was (10,55)
//    wcFP.setFpsUps("Average FPS/UPS: " + df.format(averageFPS) + ", " +df.format(averageUPS));
//	dbg.setColor(Color.black);
//
//    // draw game elements: the obstacles and the worm
////    obs.draw(dbg);
////    fred.draw(dbg);
//
//	wallManager.draw(dbg);
//	vermicello.draw(dbg);
//	fruit.draw(dbg);
//	
//    if (gameOver){
//    	gameOverMessage(dbg);
//    }else{
//	    if (isLevelComplete) {
//	    	gameCompleteMessage(dbg);
//	    }else if (isPaused){
//	    	gamePauseMessage(dbg);
//	    }
//    }
//    
//    
//  }  // end of gameRender()

private void gameRender( )
{




  if (dbImage == null){
      dbImage = createImage(PWIDTH, PHEIGHT);
      if (dbImage == null) {
        System.out.println("dbImage is null");
        return;
      }
      else
        dbg = dbImage.getGraphics();
    }

    // clear the background
    dbg.setColor(Color.white);
    dbg.fillRect (0, 0, PWIDTH, PHEIGHT);

    dbg.setColor(Color.black);
    
    setFpsUps("Average FPS/UPS: " + df.format(averageFPS) + ", " +df.format(averageUPS));
    // report average FPS and UPS at top left
    dbg.drawString(fpsUpsText, 20, 25);

    // report time used and boxes used at bottom left

    dbg.drawString(levelText+"  "+boxNumberText+"  "+timeSpentText, 10, PHEIGHT-15);
    // dbg.drawString("Boxes used: " + boxesUsed, 260, pHeight-15);

    // draw the Pause and Quit "buttons"

    drawButtons(dbg);

    

    // draw game elements: the obstacles and the worm
//    obs.draw(dbg);
//    fred.draw(dbg);

	wallManager.draw(dbg);
	vermicello.draw(dbg);
	fruit.draw(dbg);
	
    if (gameOver){
    	gameOverMessage(dbg);
    }else{
	    if (isLevelComplete) {
	    	gameCompleteMessage(dbg);
	    }else if (isPaused){
	    	gamePauseMessage(dbg);
	    }
    }
}  // end of gameRender( )

private void drawButtons(Graphics g)
{
  g.setColor(Color.black);

  // draw the Pause "button"
  if (isOverPauseButton)
    g.setColor(Color.green);

  g.drawOval( pauseArea.x, pauseArea.y, pauseArea.width, pauseArea.height);
  if (isPaused)
    g.drawString("Paused", pauseArea.x, pauseArea.y+10);
  else
    g.drawString("Pause", pauseArea.x+5, pauseArea.y+10);

  if (isOverPauseButton)
    g.setColor(Color.black);

  // draw the Quit "button"
  if (isOverQuitButton)
    g.setColor(Color.green);

  g.drawOval(quitArea.x, quitArea.y, quitArea.width, quitArea.height);
  g.drawString("Quit", quitArea.x+15, quitArea.y+10);

  if (isOverQuitButton)
    g.setColor(Color.black);
}  // drawButtons( )

	private String levelText="L: 1 S:    0";
	private String boxNumberText = "Fruit picked: 0";
	private String fpsUpsText= "Average FPS/UPS: 00.00, 00.00";
	private String timeSpentText="Time spent: 0 secs";
	
	public void setLevel(int l, int globalScore){
		  levelText = ("L: "+l+" S: "+globalScore);
	}
	
	public void setFpsUps(String string) {
			fpsUpsText = (string);
	}
	
	public void setBoxNumber(int no)
	{  boxNumberText = ("Fruit picked: " + no);  }
	
	public void setTimeSpent(long t)
	{  timeSpentText = ("Time Spent: " + t + " secs"); }

  private void gameOverMessage(Graphics g)
  // center the game-over message in the panel
  {
    String msg = "Game Over. Your Score: " + globalScore;
	int x = (PWIDTH - metrics.stringWidth(msg))/2; 
	int y = (PHEIGHT - metrics.getHeight())/2;
	g.setColor(Color.red);
    g.setFont(font);
	g.drawString(msg, x, y);
  }  // end of gameOverMessage()

  private void gameCompleteMessage(Graphics g)
  // center the game-over message in the panel
  {
    String msg = "Level complete. Your Score this Level: " + score;
	int x = (PWIDTH - metrics.stringWidth(msg))/2; 
	int y = (PHEIGHT - metrics.getHeight())/2;
	g.setColor(Color.red);
    g.setFont(font);
	g.drawString(msg, x-8, y-8);
	
    msg = "Press >p< to start new level";
	g.setColor(Color.red);
    g.setFont(font);
	g.drawString(msg, x+8, y+8);
  } 
  
  private void gamePauseMessage(Graphics g)
  // center the game-over message in the panel
  {
	  String msg = "Press >p< to resume.";
		int x = (PWIDTH - metrics.stringWidth(msg))/2; 
		int y = (PHEIGHT - metrics.getHeight())/2;
		g.setColor(Color.red);
	    g.setFont(font);
		g.drawString(msg, x, y);
  } 
  
  private void paintScreen()
  // use active rendering to put the buffered image on-screen
  { 
    Graphics g;
    try {
      g = this.getGraphics();
      if ((g != null) && (dbImage != null))
        g.drawImage(dbImage, 0, 0, null);
      g.dispose();
    }
    catch (Exception e)
    { System.out.println("Graphics context error: " + e);  }
  } // end of paintScreen()


  private void storeStats()
  /* The statistics:
       - the summed periods for all the iterations in this interval
         (period is the amount of time a single frame iteration should take), 
         the actual elapsed time in this interval, 
         the error between these two numbers;

       - the total frame count, which is the total number of calls to run();

       - the frames skipped in this interval, the total number of frames
         skipped. A frame skip is a game update without a corresponding render;

       - the FPS (frames/sec) and UPS (updates/sec) for this interval, 
         the average FPS & UPS over the last NUM_FPSs intervals.

     The data is collected every MAX_STATS_INTERVAL  (1 sec).
  */
  { 
    frameCount++;
    statsInterval += period;

    if (statsInterval >= MAX_STATS_INTERVAL) {     // record stats every MAX_STATS_INTERVAL
      long timeNow = System.nanoTime();
      timeSpentInGame = (int) ((timeNow - gameStartTime)/1000000000L);  // ns --> secs
      setTimeSpent( timeSpentInGame );

      long realElapsedTime = timeNow - prevStatsTime;   // time since last stats collection
      totalElapsedTime += realElapsedTime;

      double timingError = 
         ((double)(realElapsedTime - statsInterval) / statsInterval) * 100.0;

      totalFramesSkipped += framesSkipped;

      double actualFPS = 0;     // calculate the latest FPS and UPS
      double actualUPS = 0;
      if (totalElapsedTime > 0) {
        actualFPS = (((double)frameCount / totalElapsedTime) * 1000000000L);
        actualUPS = (((double)(frameCount + totalFramesSkipped) / totalElapsedTime) 
                                                             * 1000000000L);
      }

      // store the latest FPS and UPS
      fpsStore[ (int)statsCount%NUM_FPS ] = actualFPS;
      upsStore[ (int)statsCount%NUM_FPS ] = actualUPS;
      statsCount = statsCount+1;

      double totalFPS = 0.0;     // total the stored FPSs and UPSs
      double totalUPS = 0.0;
      for (int i=0; i < NUM_FPS; i++) {
        totalFPS += fpsStore[i];
        totalUPS += upsStore[i];
      }

      if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
        averageFPS = totalFPS/statsCount;
        averageUPS = totalUPS/statsCount;
      }
      else {
        averageFPS = totalFPS/NUM_FPS;
        averageUPS = totalUPS/NUM_FPS;
      }
/*
      System.out.println(timedf.format( (double) statsInterval/1000000000L) + " " + 
                    timedf.format((double) realElapsedTime/1000000000L) + "s " + 
			        df.format(timingError) + "% " + 
                    frameCount + "c " +
                    framesSkipped + "/" + totalFramesSkipped + " skip; " +
                    df.format(actualFPS) + " " + df.format(averageFPS) + " afps; " + 
                    df.format(actualUPS) + " " + df.format(averageUPS) + " aups" );
*/
      framesSkipped = 0;
      prevStatsTime = timeNow;
      statsInterval = 0L;   // reset
    }
  }  // end of storeStats()


  private void printStats()
  {
    System.out.println("Frame Count/Loss: " + frameCount + " / " + totalFramesSkipped);
	System.out.println("Average FPS: " + df.format(averageFPS));
	System.out.println("Average UPS: " + df.format(averageUPS));
    System.out.println("Time Spent: " + timeSpentInGame + " secs");
    // System.out.println("Boxes used: " + obs.getNumObstacles());
  }  // end of printStats()



public long getPeriod() {
	return period;
}



public void setPeriod(long period) {
	this.period = period;
}

  
  
}  // end of WormPanel class
