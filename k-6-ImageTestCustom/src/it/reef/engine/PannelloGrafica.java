package it.reef.engine;

// WormPanel.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The game's drawing surface. It shows:
 - the moving worm
 - the obstacles (blue boxes)
 - the current average FPS and UPS
 */

import it.reef.actor.BlurActor;
import it.reef.actor.FadeActor;
import it.reef.actor.FlippingActor;
import it.reef.actor.ImmagineDinamicaSprite;
import it.reef.actor.ImmagineStatica;
import it.reef.actor.MegamanXSize;
import it.reef.actor.RotateActor;
import it.reef.engine.effect.ImageSFX;
import it.reef.image.loader.ImageLoader;
import it.reef.image.loader.img.Images;
import it.reef.interf.Cyclical;
import it.reef.interf.Drawable;
import it.reef.interf.Updatable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PannelloGrafica extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private static final int PWIDTH = 700; // size of panel
	private static final int PHEIGHT = 500;

	// private static final int DOTSIZE = 10;

	private static long MAX_STATS_INTERVAL = 1000000000L;
	// private static long MAX_STATS_INTERVAL = 1000L;
	// record stats every 1 second (roughly)

	private static final int NO_DELAYS_PER_YIELD = 16;
	/*
	 * Number of frames with a delay of 0 ms before the animation thread yields
	 * to other running threads.
	 */

	private static int MAX_FRAME_SKIPS = 5; // was 2;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered

	private static int NUM_FPS = 10;
	// number of FPS values stored to get an average

	// used for gathering statistics
	private long statsInterval = 0L; // in ns
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	private int timeSpentInGame = 0; // in seconds

	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	private long framesSkipped = 0L;
	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	// private DecimalFormat timedf = new DecimalFormat("0.####"); // 4 dp

	private Thread animator; // the thread that performs the animation
	private boolean running = false; // used to stop the animation thread
	private boolean isPaused = false;

	private long period; // period between drawing in _nanosecs_

	private EngineFinestraPrincipale wcFP;
	// private Worm fred; // the worm
	// private Obstacles obs; // the obstacles

	// used at game termination
	private boolean gameOver = false;
	private int globalScore = 0;
	private Font font;
	private FontMetrics metrics;

	// off screen rendering
	private Graphics dbg;
	private Image dbImage = null;

	
	public ImageSFX imageSFX= new ImageSFX();
	
	List<Updatable> actorUpdatable= new ArrayList<Updatable>();
	List<Drawable> actorDrawable= new ArrayList<Drawable>();
	List<Cyclical> actorResumable= new ArrayList<Cyclical>();
	
	
	FlippingActor flippingActor;
	FadeActor fadeActor;
	
	public PannelloGrafica(EngineFinestraPrincipale wc, long period) {
	     // switch on translucency acceleration in Windows

	    
		wcFP = wc;
		this.period = period;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();

        accelMemory = gd.getAvailableAcceleratedMemory();  // in bytes
        System.out.println("Initial Acc. Mem.: " + 
                       df.format( ((double)accelMemory)/(1024*1024) ) + " MB" );
        
		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		readyForTermination();

		ImageLoader il = new ImageLoader(Images.class, "image.cfg", null);
		
		
		// INSERT ALL ACTOR AND CONCRETE HERE
		ImmagineStatica forestLightFront;
		
		ImmagineStatica brickFloatLeft;
		ImmagineStatica brickFloatMiddle;
		ImmagineStatica brickFloatRight;
		
		ImmagineStatica immagineStatica;
		ImmagineDinamicaSprite immagineSprite;
		ImmagineDinamicaSprite immagineSpriteNumbered;
		ImmagineDinamicaSprite immagineSpriteNumberedAnimated;
		ImmagineDinamicaSprite immagineSpriteMegamaN;
		
		ImmagineDinamicaSprite immagineSpriteArcher;
		ImmagineDinamicaSprite immagineSpriteArcherDied;
		ImmagineDinamicaSprite immagineSpriteArcherJump;
		ImmagineDinamicaSprite immagineSpriteArcherArc;
		
		ImmagineDinamicaSprite immagineSpriteArcherArrow;

		MegamanXSize immagineSpriteMegamaNEffect;
		
		
		
		// INIZIALIZE ACTOR CONCRETE
		// :TODO Inizialize Actor Concrete Listener
		immagineStatica = new ImmagineStatica(100, 100, il.getImage("explosion", 4), this);
		actorDrawable.add(immagineStatica);
		
		forestLightFront  = new ImmagineStatica(-120, 155, il.getImage("forestLightFront"), this);
		actorDrawable.add(forestLightFront);
		
		brickFloatLeft = new ImmagineStatica(10, 195, il.getImage("brick-floating-left"), this);
		actorDrawable.add(brickFloatLeft);
		brickFloatMiddle = new ImmagineStatica(58, 195, il.getImage("brick-floating-middle"), this);
		actorDrawable.add(brickFloatMiddle);
		brickFloatRight = new ImmagineStatica(106, 195, il.getImage("brick-floating-right"), this);
		actorDrawable.add(brickFloatRight);
		
		immagineSprite = new ImmagineDinamicaSprite(100, 200, il.getImages("explosion"),this, period, 2.0, false);
		actorUpdatable.add(immagineSprite);
		actorDrawable.add(immagineSprite);
		actorResumable.add(immagineSprite);
		
		immagineSpriteNumbered = new ImmagineDinamicaSprite(200, 100, il.getImages("explosionok"),this, period, 1.5, false);
		actorUpdatable.add(immagineSpriteNumbered);
		actorDrawable.add(immagineSpriteNumbered);
		actorResumable.add(immagineSpriteNumbered);
		
		immagineSpriteNumberedAnimated= new ImmagineDinamicaSprite(200, 200, il.getImages("explodeAnimated"),this, period, 1.5, false);
		actorUpdatable.add(immagineSpriteNumberedAnimated);
		actorDrawable.add(immagineSpriteNumberedAnimated);
		actorResumable.add(immagineSpriteNumberedAnimated);
		
		immagineSpriteMegamaN= new ImmagineDinamicaSprite(300, 100, il.getImages("MegaManXCorsa"),this, period, 0.8, true);
		actorUpdatable.add(immagineSpriteMegamaN);
		actorDrawable.add(immagineSpriteMegamaN);
		// actorResumable.add(immagineSpriteMegamaN);
		
		immagineSpriteArcher = new ImmagineDinamicaSprite(10, 100, il.getImages("grandelf"),this, period, 0.8, true);
		actorUpdatable.add(immagineSpriteArcher);
		actorDrawable.add(immagineSpriteArcher);

		immagineSpriteArcherArc = new ImmagineDinamicaSprite(10, 200, il.getImages("grandelf-arc"),this, period, 0.8, true);
		actorUpdatable.add(immagineSpriteArcherArc);
		actorDrawable.add(immagineSpriteArcherArc);

		immagineSpriteArcherJump = new ImmagineDinamicaSprite(10, 300, il.getImages("grandelf-jump"),this, period, 0.8, true);
		actorUpdatable.add(immagineSpriteArcherJump);
		actorDrawable.add(immagineSpriteArcherJump);

		
		immagineSpriteArcherArrow = new ImmagineDinamicaSprite(100, 280, il.getImages("grandelf-arrow"),this, period, 0.8, true);
		actorUpdatable.add(immagineSpriteArcherArrow);
		actorDrawable.add(immagineSpriteArcherArrow);

		immagineSpriteArcherDied = new ImmagineDinamicaSprite(10, 400, il.getImages("grandelf-died"),this, period, 0.8, false);
		actorUpdatable.add(immagineSpriteArcherDied);
		actorDrawable.add(immagineSpriteArcherDied);
		actorResumable.add(immagineSpriteArcherDied);
		
		immagineSpriteMegamaNEffect = new MegamanXSize(300, 300, il.getImages("MegaManXCorsa"),this, period, 0.8, true);
		immagineSpriteMegamaNEffect.setResizeFactor(1.2);
		actorUpdatable.add(immagineSpriteMegamaNEffect);
		actorDrawable.add(immagineSpriteMegamaNEffect);
		// actorResumable.add(immagineSpriteMegamaNEffect);
		
		flippingActor =  new FlippingActor(400, 100, il.getImage("cheese"), this);
		actorUpdatable.add(flippingActor);
		actorDrawable.add(flippingActor);
		// actorResumable.add(flippingActor);
		
		fadeActor = new FadeActor(400, 200, il.getImage("cheese"), this);
		actorUpdatable.add(fadeActor);
		actorDrawable.add(fadeActor);
		// actorResumable.add(fadeActor);

		RotateActor rota = new RotateActor(400, 300, il.getImage("atomic"),this, period, 1.8, 360, false);
		actorUpdatable.add(rota);
		actorDrawable.add(rota);
		actorResumable.add(rota);
		

		BlurActor ba =  new BlurActor(500,100,il.getImage("balls"),this, period, 4.0, 20, false);
		actorUpdatable.add(ba);
		actorDrawable.add(ba);
		actorResumable.add(ba);
		
		ImmagineDinamicaSprite barra= new ImmagineDinamicaSprite(300, 400, il.getImages("barra"),this, period, 0.4, false);
		actorUpdatable.add(barra);
		actorDrawable.add(barra);
		actorResumable.add(barra);
		
		ImmagineDinamicaSprite barrao= new ImmagineDinamicaSprite(100, 400, il.getImages("barrao"),this, period, 0.4, false);
		actorUpdatable.add(barrao);
		actorDrawable.add(barrao);
		actorResumable.add(barrao);
		
		addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'r') {
					for (int i=0;i<actorResumable.size();i++){
						actorResumable.get(i).resume();
					}
					
					flippingActor.setFlipType((flippingActor.getFlipType()+1)%3);
					
					if (fadeActor.getAlphaSet()>=1){
						fadeActor.setAlphaSet(0f);
					}else{
						fadeActor.setAlphaSet(fadeActor.getAlphaSet()+0.1f);
					}
				}
				
				if (e.getKeyChar() == 'p') {
					if (isPaused()) {
						resumeGame();
					} else {
						pauseGame();
					}
				}
			}

		});

		// addMouseListener( new MouseAdapter() {
		// public void mousePressed(MouseEvent e)
		// { testPress(e.getX(), e.getY()); }
		// });

		// set up message font
		font = new Font("SansSerif", Font.BOLD, 12);
		metrics = this.getFontMetrics(font);

		// initialise timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}
	} // end of Constructor()

	private void readyForTermination() {
		addKeyListener(new KeyAdapter() {
			// listen for esc, q, end, ctrl-c on the canvas to
			// allow a convenient exit from the full screen configuration
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if ((keyCode == KeyEvent.VK_ESCAPE)
						|| (keyCode == KeyEvent.VK_Q)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					running = false;
				}
			}
		});
	} // end of readyForTermination()

	public void addNotify()
	// wait for the JPanel to be added to the JFrame before starting
	{
		super.addNotify(); // creates the peer
		startGame(); // start the thread
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
	{
		isPaused = false;
	}

	public void pauseGame()
	// called when the JFrame is deactivated / iconified
	{
		isPaused = true;
	}

	public void stopGame()
	// called when the JFrame is closing
	{
		running = false;
	}

	public boolean isPaused() {
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

		while (running) {
			gameUpdate();
			gameRender();
			paintScreen();

			// OTHER CONTROL HERE
			// :TODO All Control Here

			gameOverControl();
			// fruit.lc=true && !isPaused;

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			/*
			 * If frame animation is taking too long, update the game state
			 * without rendering it, to get the updates/sec nearer to the
			 * required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate(); // update state but don't render
				skips++;
			}
			framesSkipped += skips;

			storeStats();
		}

		printStats();
		System.exit(0); // so window disappears
	} // end of run()

	private void gameOverControl() {
		// if (actor.dead() && !gameOver) {
		// gameOver=true;
		// }
	}

	private void gameUpdate() {
		if (!isPaused && !gameOver) {
			// INSERT HERE GAME UPDATE
			// :TODO Game Update
			for (int i=0;i<actorUpdatable.size();i++){
				actorUpdatable.get(i).update();
			}
			
			
		
		}

	} // end of gameUpdate()

	private void gameRender() {
		if (dbImage == null) {
			dbImage = createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} else
				dbg = dbImage.getGraphics();
		}

		// clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);

		// dbg.setColor(Color.blue);
		// dbg.setFont(font);
		//
		// // report frame count & average FPS and UPS at top left
		// // dbg.drawString("Frame Count " + frameCount, 10, 25);
		// dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " +
		// df.format(averageUPS), 20, 25); // was (10,55)
		wcFP.setFpsUps("Average FPS/UPS: " + df.format(averageFPS) + ", "
				+ df.format(averageUPS));
		dbg.setColor(Color.black);

		// draw game elements
		// :TODO Draw elements here
		for (int i = 0; i < actorDrawable.size(); i++) {
			actorDrawable.get(i).draw(dbg);
		}
		
		
		if (gameOver) {
			gameOverMessage(dbg);
		} else {
			if (isPaused) {
				gamePauseMessage(dbg);
			}
		}

	} // end of gameRender()

	private void gameOverMessage(Graphics g)
	// center the game-over message in the panel
	{
		String msg = "Game Over. Your Score: " + globalScore;
		int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
		int y = (PHEIGHT - metrics.getHeight()) / 2;
		g.setColor(Color.red);
		g.setFont(font);
		g.drawString(msg, x, y);
	} // end of gameOverMessage()

	private void gamePauseMessage(Graphics g)
	// center the game-over message in the panel
	{
		String msg = "Press >p< to resume.";
		int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
		int y = (PHEIGHT - metrics.getHeight()) / 2;
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
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	} // end of paintScreen()

	private void storeStats()
	/*
	 * The statistics: - the summed periods for all the iterations in this
	 * interval (period is the amount of time a single frame iteration should
	 * take), the actual elapsed time in this interval, the error between these
	 * two numbers;
	 * 
	 * - the total frame count, which is the total number of calls to run();
	 * 
	 * - the frames skipped in this interval, the total number of frames
	 * skipped. A frame skip is a game update without a corresponding render;
	 * 
	 * - the FPS (frames/sec) and UPS (updates/sec) for this interval, the
	 * average FPS & UPS over the last NUM_FPSs intervals.
	 * 
	 * The data is collected every MAX_STATS_INTERVAL (1 sec).
	 */
	{
		frameCount++;
		statsInterval += period;

		if (statsInterval >= MAX_STATS_INTERVAL) { // record stats every
													// MAX_STATS_INTERVAL
			long timeNow = System.nanoTime();
			timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L); // ns
																				// --
																				// >
																				// secs
			wcFP.setTimeSpent(timeSpentInGame);

			long realElapsedTime = timeNow - prevStatsTime; // time since last
															// stats collection
			totalElapsedTime += realElapsedTime;

			// double timingError =
			// ((double)(realElapsedTime - statsInterval) / statsInterval) *
			// 100.0;

			totalFramesSkipped += framesSkipped;

			double actualFPS = 0; // calculate the latest FPS and UPS
			double actualUPS = 0;
			if (totalElapsedTime > 0) {
				actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
				actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
			}

			// store the latest FPS and UPS
			fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
			upsStore[(int) statsCount % NUM_FPS] = actualUPS;
			statsCount = statsCount + 1;

			double totalFPS = 0.0; // total the stored FPSs and UPSs
			double totalUPS = 0.0;
			for (int i = 0; i < NUM_FPS; i++) {
				totalFPS += fpsStore[i];
				totalUPS += upsStore[i];
			}

			if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
				averageFPS = totalFPS / statsCount;
				averageUPS = totalUPS / statsCount;
			} else {
				averageFPS = totalFPS / NUM_FPS;
				averageUPS = totalUPS / NUM_FPS;
			}
			/*
			 * System.out.println(timedf.format( (double)
			 * statsInterval/1000000000L) + " " + timedf.format((double)
			 * realElapsedTime/1000000000L) + "s " + df.format(timingError) +
			 * "% " + frameCount + "c " + framesSkipped + "/" +
			 * totalFramesSkipped + " skip; " + df.format(actualFPS) + " " +
			 * df.format(averageFPS) + " afps; " + df.format(actualUPS) + " " +
			 * df.format(averageUPS) + " aups" );
			 */
			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	} // end of storeStats()

	private void printStats() {
		System.out.println("Frame Count/Loss: " + frameCount + " / "
				+ totalFramesSkipped);
		System.out.println("Average FPS: " + df.format(averageFPS));
		System.out.println("Average UPS: " + df.format(averageUPS));
		System.out.println("Time Spent: " + timeSpentInGame + " secs");
		// System.out.println("Boxes used: " + obs.getNumObstacles());
	} // end of printStats()

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	int accelMemory;
	GraphicsDevice gd;
    private void reportAccelMemory( )
    // report any change in the amount of accelerated memory
    {
        
        
      int mem = gd.getAvailableAcceleratedMemory( );   // in bytes
      int memChange = mem - accelMemory;

      if (memChange != 0)
    	  this.wcFP.setMemRam(df.format( ((double)accelMemory)/(1024*1024) ) +
                              " MB; Res: " +
                  df.format( ((double)memChange)/1024 ) + " K");
    	  
        System.out.println("Acc. Mem: " +
                  df.format( ((double)accelMemory)/(1024*1024) ) +
                              " MB; Res: " +
                  df.format( ((double)memChange)/1024 ) + " K");
      accelMemory = mem;
    }
	
} // end of class
