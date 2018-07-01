package it.reef.ark.main;

import it.reef.ark.element.action.BallWatcher;
import it.reef.ark.element.action.FruitWatcher;
import it.reef.ark.element.action.KeyProcessable;
import it.reef.ark.element.actor.BallsManager;
import it.reef.ark.element.actor.MagicFruitsManager;
import it.reef.ark.element.actor.Worm;
import it.reef.ark.element.actor.input.PlayerAdapter;
import it.reef.ark.element.concrete.BrickManager;
import it.reef.ark.manager.audio.ClipsLoader;
import it.reef.ark.manager.audio.MidisLoader;
import it.reef.ark.manager.image.ImageLoader;
import it.reef.ark.manager.image.ImageSFX;
import it.reef.ark.resources.audio.effect.Effects;
import it.reef.ark.resources.audio.music.Musics;
import it.reef.ark.resources.images.Images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ArkPanel extends JPanel implements Runnable, BallWatcher, FruitWatcher {

	private static final Log log = LogFactory.getLog(ArkPanel.class);

	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	private DecimalFormat timedf = new DecimalFormat("0.####"); // 4 dp
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Ark arkWindows;

	ImageSFX isfx = new ImageSFX();

	private long period; // period between drawing in nanosecs

	private int BRICK_PER_FRUIT=3;
	private int BRICK_SPEEDUP=8;
	
	private static final int NUM_WIDTH_CELLS=15;
	private static final int NUM_HEIGHT_CELLS=30;
	
	private int AREA_BRICK_WIDTH = NUM_WIDTH_CELLS*30; // 
	private int AREA_BRICK_HEIGHT = NUM_HEIGHT_CELLS*15;

	private int AREA_INFO = NUM_WIDTH_CELLS*12;
	private int AREA_BORDER = 10;

	private int AREA_WIDTH = AREA_BRICK_WIDTH + AREA_BORDER + AREA_BORDER
			+ AREA_INFO; // dimension
	private int AREA_HEIGHT = AREA_BRICK_HEIGHT + AREA_BORDER;

	private Rectangle2D AREA_ATTIVA = new Rectangle(AREA_BORDER, AREA_BORDER,
			AREA_BRICK_WIDTH, AREA_BRICK_HEIGHT);

	private int CUR_SCHEMA=1;
	private int TOTAL_SCHEMA=10;
	
	
	/**
	 * Create the panel.
	 */
	public ArkPanel(Ark a, long period) {
		this.period = period;
		this.arkWindows = a;

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();

		accelMemory = gd.getAvailableAcceleratedMemory(); // in bytes
		log.info("Initial Acc. Mem.: "
				+ df.format(((double) accelMemory) / (1024 * 1024)) + " MB");

		setBackground(Color.white);
		setPreferredSize(new Dimension(AREA_WIDTH, AREA_HEIGHT));

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		readyForTermination();

		gameInit();

	}

	boolean paused;
	boolean gameOver;
	boolean lifeLost;

	List<it.reef.ark.element.action.Updatable> actorUpdatable = new ArrayList<it.reef.ark.element.action.Updatable>();
	List<it.reef.ark.element.action.Drawable> actorDrawable = new ArrayList<it.reef.ark.element.action.Drawable>();

	private BrickManager brickManager;
	private BallsManager ballManager;
	private MagicFruitsManager magicFruitManager;
	
	PlayerAdapter playerAdapter;
	private Worm worm;

	ImageLoader il;
	private MidisLoader ml;
	private ClipsLoader cl;
	private void gameInit() {
		Startup swindow = new Startup();
		
		this.setEnabled(false);
		swindow.setVisible(true);
		
      Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
      int       nX  = (int) (scr.getWidth()  - swindow.getWidth()  ) / 2;
      int       nY  = (int) (scr.getHeight() - swindow.getHeight() ) / 2;

      swindow.setLocation( nX, nY );
		
		il = new ImageLoader(Images.class, "images.cfg",swindow);
		cl = new ClipsLoader(Effects.class, "sound.cfg",swindow);
		cl.setVolumePercentage(50);
		
		ml = new MidisLoader(Musics.class, "midi.cfg",swindow);
		
		swindow.setVisible(false);
		this.setEnabled(true);
				
		pauseGame();
		
		// ml.setVolumePercentage(15);		
		ml.play("animaniacs", true);
		// ml.setVolumePercentage(15);


		int BRICK_WIDTH = AREA_BRICK_WIDTH / NUM_WIDTH_CELLS;
		int BRICK_HEIGHT = AREA_BRICK_HEIGHT / NUM_HEIGHT_CELLS;

		il.resizeImage("brickB", BRICK_WIDTH, BRICK_HEIGHT, isfx);
		il.resizeImage("brickF", BRICK_WIDTH, BRICK_HEIGHT, isfx);
		il.resizeImage("brickG", BRICK_WIDTH, BRICK_HEIGHT, isfx);
		il.resizeImage("brickR", BRICK_WIDTH, BRICK_HEIGHT, isfx);

		il.resizeImage("bordo", AREA_BRICK_WIDTH + AREA_BORDER + AREA_BORDER,
				AREA_BRICK_HEIGHT + AREA_BORDER, isfx);

		il.resizeImage("arancia", BRICK_WIDTH, BRICK_WIDTH, isfx);
		il.resizeImage("mela", BRICK_WIDTH, BRICK_WIDTH, isfx);
		il.resizeImage("fragola", BRICK_WIDTH, BRICK_WIDTH, isfx);
		il.resizeImage("limone", BRICK_WIDTH, BRICK_WIDTH, isfx);
		il.resizeImage("banana", BRICK_WIDTH, BRICK_WIDTH, isfx);

		il.resizeImage("worm", (int) (BRICK_WIDTH * 2.5), BRICK_WIDTH, isfx);
		il.resizeImage("wormlong", (int) (BRICK_WIDTH * 5), BRICK_WIDTH, isfx);
		il.resizeImage("wormsmall", (int) (BRICK_WIDTH * 1.5), BRICK_WIDTH, isfx);

		il.resizeImage("gomitolo", (BRICK_HEIGHT), BRICK_HEIGHT, isfx);
		il.createRotatingSprite("gomitolo", 6, isfx);
		il.createRotatingSprite("arancia", 6, isfx);
		il.createRotatingSprite("mela", 6, isfx);
		il.createRotatingSprite("fragola", 6, isfx);
		il.createRotatingSprite("limone", 6, isfx);
		il.createRotatingSprite("banana", 6, isfx);
		

		startLevel(1);
		
		
		// initialise timing elements
		fpsStore = new double[NUM_FPS];
		upsStore = new double[NUM_FPS];
		for (int i = 0; i < NUM_FPS; i++) {
			fpsStore[i] = 0.0;
			upsStore[i] = 0.0;
		}

		this.paused = false;
		this.gameOver = false;
		this.lifeLost = false;
		this.levelComplete = false;
		
		addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'p' || e.getID()==KeyEvent.VK_ENTER) {
					if (isPaused()) {
						resumeGame();
					} else {
						pauseGame();
					}
				}
			}

		});
	}

	
	
	private void startLevel(int level){
		actorUpdatable = new ArrayList<it.reef.ark.element.action.Updatable>();
		actorDrawable = new ArrayList<it.reef.ark.element.action.Drawable>();
		
		int BRICK_HEIGHT = AREA_BRICK_HEIGHT / 30;
		
		
		
		brickManager = new BrickManager(level, il, this,AREA_ATTIVA, NUM_WIDTH_CELLS, NUM_HEIGHT_CELLS);
		actorDrawable.add(brickManager);

		int verticalWormPosition = (AREA_BORDER
				+ (AREA_BRICK_HEIGHT - (AREA_BRICK_HEIGHT / 15)))-6;
		int horizontalWormPosition = AREA_BORDER + (AREA_BRICK_WIDTH / 2);
		worm = new Worm(horizontalWormPosition, verticalWormPosition,
				(int) AREA_ATTIVA.getMinX(), (int) AREA_ATTIVA.getMaxX(),
				il.getImages("worm"), il.getImages("wormsmall"),
				il.getImages("wormlong"), this, period, 0.7, true, isfx, cl);
		playerAdapter = new it.reef.ark.element.actor.input.PlayerAdapter((KeyProcessable) worm);
		addKeyListener(playerAdapter);
		actorUpdatable.add(worm);
		actorDrawable.add(worm);

		ballManager = new BallsManager(AREA_ATTIVA,horizontalWormPosition+(worm.getWidth()/2), 
				(int)worm.getMyForm().getMinY()-(il.getImages("gomitolo").get(0).getHeight()), 
				il.getImages("gomitolo"), this, period, 1, true, cl,
				brickManager, worm, this);
		actorUpdatable.add(ballManager);
		actorDrawable.add(ballManager);

		magicFruitManager = new MagicFruitsManager(il, this, period, worm, ballManager, AREA_ATTIVA, this, cl);
		actorUpdatable.add(magicFruitManager);
		actorDrawable.add(magicFruitManager);

		this.pauseGame();
		this.levelStartTime = System.nanoTime();
	
	}
	
	
	
	private void gameUpdate() {
		if (!paused && !gameOver) {
			if (lifeLost) lifeLost=false;
			if (levelComplete) levelComplete=false;
			playerAdapter.processKeyEventList();
			log.debug("Fine playerAdapter inizio actorUpdate");
			for (int i = 0; i < actorUpdatable.size(); i++) {
				log.debug("Inizio actorUpdate per l'elemento: "+ actorUpdatable.get(i));
				actorUpdatable.get(i).update();
				log.debug("Fine actorUpdate per l'elemento: "+i);
			}
			
			log.debug("Fine actorUpdate");
		}
	}

	// off screen rendering
	private Graphics dbg;
	private Image dbImage = null;

	private void gameRender() {

		if (dbImage == null) {
			dbImage = createImage(AREA_WIDTH, AREA_HEIGHT);
			if (dbImage == null) {
				log.error("dbImage is null");
				return;
			} else
				dbg = dbImage.getGraphics();
		}

		// clear the background
		dbg.setColor(Color.white);
		dbg.fillRect(0, 0, AREA_WIDTH, AREA_HEIGHT);

		for (int i = 0; i < actorDrawable.size(); i++) {
			actorDrawable.get(i).draw(dbg);
		}

		dbg.drawImage(il.getImage("bordo"), 0, 0, this);

		drawData(dbg);
		
		drawLostLife(dbg);
		drawLevelComplete(dbg);
		drawGameOver(dbg);
		
		arkWindows.setFpsUps("Average FPS/UPS: " + df.format(averageFPS) + ", "
				+ df.format(averageUPS));
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
			log.error("Graphics context error.", e);
		}
	} // end of paintScreen()

	public void addNotify()
	// wait for the JPanel to be added to the JFrame before starting
	{
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}

	private Thread animator; // the thread that performs the animation

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
		if (!this.gameOver){
			this.ml.resume();
			paused = false;
		}
	}

	public void pauseGame()
	// called when the JFrame is deactivated / iconified
	{
		this.ml.pause();
		paused = true;
	}

	public void stopGame()
	// called when the JFrame is closing
	{
		running = false;
	}

	public boolean isPaused() {
		return paused;
	}

	// ----------------------------------------------

	private boolean running = false; // Running game animation control

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

	private long framesSkipped = 0L;

	// number of F skipped

	public void run() {
		try {
			// Variable control of UPS and FPS
			long beforeTime, afterTime, timeDiff, sleepTime;
			long overSleepTime = 0L;
			int noDelays = 0;
			long excess = 0L;

			gameStartTime = System.nanoTime();
			levelStartTime = System.nanoTime();
			prevStatsTime = gameStartTime;
			beforeTime = gameStartTime;

			running = true;

			while (running) {
				// log.debug("Inizio ciclo");
				gameUpdate();
				gameRender();
				paintScreen();
				// log.debug("Fine fasi gioco");
				
				afterTime = System.nanoTime();
				timeDiff = afterTime - beforeTime;
				sleepTime = (period - timeDiff) - overSleepTime;

				if (sleepTime > 0) { // some time left in this cycle
					try {
						Thread.sleep(sleepTime / 1000000L); // nano -> ms
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
				} else { // sleepTime <= 0; the frame took longer than the
							// period
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
					log.debug("Skip render");
				}
				framesSkipped += skips;

				storeStats();
			}

			printStats();
			System.exit(0); // so window disappears
		} catch (Exception e) {
			log.error("Errore nel ciclo while", e);
			e.printStackTrace();
		}
	} // end of run()

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

	// used for gathering statistics
	private long statsInterval = 0L; // in ns
	private long prevStatsTime;
	private long totalElapsedTime = 0L;
	private long gameStartTime;
	private long levelStartTime;
	private int timeSpentInGame = 0; // in seconds
	private int timeSpentInLevel = 0; // in seconds

	private long frameCount = 0;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFPS = 0.0;

	private long totalFramesSkipped = 0L;
	private double upsStore[];
	private double averageUPS = 0.0;

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
			timeSpentInLevel = (int) ((timeNow - levelStartTime) / 1000000000L); // ns
																				// secs
			arkWindows.setTimeSpent(timeSpentInGame);
			reportAccelMemory();

			long realElapsedTime = timeNow - prevStatsTime; // time since last
															// stats collection
			totalElapsedTime += realElapsedTime;

			double timingError = ((double) (realElapsedTime - statsInterval) / statsInterval) * 100.0;

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

			log.info(timedf.format((double) statsInterval / 1000000000L) + " "
					+ timedf.format((double) realElapsedTime / 1000000000L)
					+ "s " + df.format(timingError) + "% " + frameCount + "c "
					+ framesSkipped + "/" + totalFramesSkipped + " skip; "
					+ df.format(actualFPS) + " " + df.format(averageFPS)
					+ " afps; " + df.format(actualUPS) + " "
					+ df.format(averageUPS) + " aups");

			framesSkipped = 0;
			prevStatsTime = timeNow;
			statsInterval = 0L; // reset
		}
	} // end of storeStats()

	private void printStats() {
		log.info("Frame Count/Loss: " + frameCount + " / " + totalFramesSkipped);
		log.info("Average FPS: " + df.format(averageFPS));
		log.info("Average UPS: " + df.format(averageUPS));
		log.info("Time Spent: " + timeSpentInGame + " secs");
		// log.info("Boxes used: " + obs.getNumObstacles());
	} // end of printStats()

	int accelMemory;
	GraphicsDevice gd;

	private void reportAccelMemory()
	// report any change in the amount of accelerated memory
	{

		int mem = gd.getAvailableAcceleratedMemory(); // in bytes
		int memChange = mem - accelMemory;

		if (memChange != 0)
			this.arkWindows.setMemRam(df.format(((double) accelMemory)
					/ (1024 * 1024))
					+ " MB; Res: "
					+ df.format(((double) memChange) / 1024)
					+ " K");

		log.info("Acc. Mem: "
				+ df.format(((double) accelMemory) / (1024 * 1024))
				+ " MB; Res: " + df.format(((double) memChange) / 1024) + " K");
		accelMemory = mem;
	}

		  // private int BALLS_NUMBER = 1;
		  private int LIFES_NUMBER = 3;
//		  private boolean isVitaPersa=false;
//		  
//		  private void vitaPersa(){
//			  if (BALLS_NUMBER){
//				  this.score=0;
//				  this.globalScore+=this.score;
//				  // wallManager = new WallManager(DOTSIZE,schema);
//				  
//				  // wcFP.setLevel(schema, globalScore);
//				  
//				  fruit = new Fruit(PWIDTH, PHEIGHT, DOTSIZE, wallManager);
//					
//				  vermicello.reset(wallManager, fruit);
//
//				  wcFP.setLevel(schema, globalScore, vermicello.viteRestanti());
//				  
//				  this.paused=true;
//				  this.isVitaPersa=true;
//			  }
//		  }

		public void checkVita(boolean isDead) {
			if (isDead){
				ballManager.checkBallToRemove();
				if (ballManager.getNumBall()==0){
					if (LIFES_NUMBER==1)
					{
						this.gameOver = true;
						cl.play("bell", true);
					}else{
						LIFES_NUMBER--;
						lifeLost();
					}
					this.pauseGame();
				}
			}
		}
		
		private String[] musics = new String[]{"animaniacs","bugsbunny","hakuna","looney","mickey","oye","pink","scooby","simpson","tiny"};
		private String[][] levelSuccesful=new String[][]{{"miao","yodel"},{"smile-5","applauso"},{"prettiness","kiss"}};
		private String[][] levelUnsuccesful=new String[][]{{"cry-3","cry"},{"sweat","ohh"},{"surprise-4","punch"},{"vomit","vomiting"}};
		
		private int levelUnsuccesfulIndex=0;
		public void lifeLost(){
			this.lifeLost = true;
			ballManager.addBall(worm.getX(), worm.getY(),  (int)(Math.random()*-90)-45);
			this.levelUnsuccesfulIndex=(int)(Math.random()*(levelUnsuccesful.length));
			cl.play(levelUnsuccesful[this.levelUnsuccesfulIndex][1], false);
		}

		public void drawLostLife(Graphics g){
			if (this.lifeLost)
				g.drawImage(il.getImage(levelUnsuccesful[this.levelUnsuccesfulIndex][0]), (int)AREA_ATTIVA.getCenterX()-(il.getImage(levelUnsuccesful[this.levelUnsuccesfulIndex][0]).getWidth()/2), (int)AREA_ATTIVA.getCenterY()-(il.getImage(levelUnsuccesful[this.levelUnsuccesfulIndex][0]).getHeight()/2), this);
		}
		public void drawLevelComplete(Graphics g){
			if (this.levelComplete)
				g.drawImage(il.getImage(levelSuccesful[this.levelCompleteIndex][0]), (int)AREA_ATTIVA.getCenterX()-(il.getImage(levelSuccesful[this.levelCompleteIndex][0]).getWidth()/2), (int)AREA_ATTIVA.getCenterY()-(il.getImage(levelSuccesful[this.levelCompleteIndex][0]).getHeight()/2), this);
		}
		public void drawGameOver(Graphics g){
			if (this.gameOver){
				g.drawImage(il.getImage("rip"), (this.getWidth()/2)-(il.getImage("rip").getWidth()/2),  (this.getHeight()/2)-(il.getImage("rip").getHeight()/2), il.getImage("rip").getWidth(),il.getImage("rip").getHeight(), this);
				
				Font f = new Font("Verdana", Font.ITALIC, 24);
				g.setFont(f);
				g.drawString("Score: "+SCORE+"Px", (this.getWidth()/2)-(il.getImage("rip").getWidth()/2),  (this.getHeight()/2)-(il.getImage("rip").getHeight()));
			}
		}
		private boolean levelComplete=false;
		private int levelCompleteIndex=0;
		public void levelComplete(){
			
			levelComplete=true;
			CUR_SCHEMA++;
			if (CUR_SCHEMA>TOTAL_SCHEMA){
				CUR_SCHEMA=1;
			}
			this.startLevel(CUR_SCHEMA);
			if (CUR_SCHEMA==1) ballManager.addSpeed(20);
			addPointsToScore(10*CUR_SCHEMA);
			
			this.levelCompleteIndex=(int)(Math.random()*(levelSuccesful.length));
			cl.play(levelSuccesful[this.levelCompleteIndex][1], false);
			
//			ml.stop();
//			ml.play("", toLoop)
			
			this.paused=true;
			
			this.ml.stop();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.ml.play(musics[(int)(Math.random()*musics.length)], true);

			pauseGame();
		}
		public void checkFineLev(int remaningBrick) {
			  if (remaningBrick<=0) {
				  	levelComplete();
			  }

		}

		private int SCORE;
		public void addPointsToScore(int points) {
			this.SCORE+=points;
		}

		
		private int brickRemoved = 0;
		public void brickRemoved(int x, int y) {
			brickRemoved++;
			if ((int)(Math.random()*BRICK_PER_FRUIT)==0) {
				int tipoF = (int)(Math.random()*MagicFruitsManager.TIPI_FRUTTA);
				log.debug("Magic Fruit: "+tipoF);
				magicFruitManager.addFruit(x, y, tipoF);
				cl.play("fruitCretion", false);
			}
			if (brickRemoved%BRICK_SPEEDUP==0) {
				ballManager.addSpeed(20);
			}
		}

		public void addBall(int x, int y, int angle) {
			ballManager.addBall(x, y, angle);
			// BALLS_NUMBER++;
		}
		
		public void drawData(Graphics g){
			g.setColor(Color.BLACK);
			g.fillRect(AREA_WIDTH-AREA_INFO, 0, AREA_INFO, AREA_HEIGHT);
			
			int INTERNAL_BORDER = 4;
			
			g.drawImage(il.getImage("fruits-back"), AREA_WIDTH-AREA_INFO+INTERNAL_BORDER, INTERNAL_BORDER, AREA_INFO-(INTERNAL_BORDER*2), AREA_HEIGHT-(INTERNAL_BORDER*2), this);
			
			int startWidth= AREA_WIDTH-AREA_INFO+INTERNAL_BORDER+AREA_BORDER;
			int startHeight = AREA_BORDER+AREA_BORDER+INTERNAL_BORDER+AREA_BORDER;
			int width=AREA_INFO-(INTERNAL_BORDER*2+AREA_BORDER*2);
			int height=AREA_HEIGHT-(INTERNAL_BORDER*2+AREA_BORDER*2);
			
			g.setColor(Color.BLUE);
			Font f = new Font("Verdana", Font.BOLD+Font.ITALIC, 24);
			g.setFont(f);
			g.drawString("Brukonoid", startWidth,startHeight);
			
			int altezza = startHeight+ (24+2);
			
			Font fsIntestazione = new Font("Verdana", Font.BOLD, 12);
			Font fsDescrizione = new Font("Verdana", Font.PLAIN, 12);
			
			g.setColor(Color.DARK_GRAY);

			g.setFont(fsIntestazione);
			g.drawString("Level", startWidth,altezza);

			altezza+=12+2;
			g.setFont(fsDescrizione);
			g.drawString(CUR_SCHEMA+"", startWidth,altezza);

			
			g.setColor(Color.RED);
			altezza+=12+2;
			g.setFont(fsIntestazione);
			g.drawString("Vite", startWidth,altezza);
			
			altezza+=12+2;
			g.setFont(fsDescrizione);
			g.drawString(LIFES_NUMBER+"", startWidth,altezza);
		
			g.setColor(Color.CYAN);
			altezza+=12+2;
			g.setFont(fsIntestazione);
			g.drawString("Score", startWidth,altezza);
			
			altezza+=12+2;
			g.setFont(fsDescrizione);
			g.drawString(this.SCORE+"", startWidth,altezza);
			
			g.setColor(Color.MAGENTA);
			altezza+=12+2;
			g.setFont(fsIntestazione);
			g.drawString("Tempo trascorso", startWidth,altezza);
			
			altezza+=12+2;
			g.setFont(fsDescrizione);
			g.drawString(this.timeSpentInGame+"", startWidth,altezza);
			
			g.setColor(Color.PINK);
			altezza+=12+2;
			g.setFont(fsIntestazione);
			g.drawString("Tempo livello", startWidth,altezza);
			
			altezza+=12+2;
			g.setFont(fsDescrizione);
			g.drawString(this.timeSpentInLevel+"", startWidth,altezza);
		}

}
