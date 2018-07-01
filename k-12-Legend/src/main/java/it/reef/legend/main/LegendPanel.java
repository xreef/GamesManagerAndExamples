package it.reef.legend.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import it.reef.legend.main.utils.Formatter;
import it.reef.legend.main.utils.Geometry;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LegendPanel extends JPanel implements Runnable, Geometry, Formatter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5843034550054303843L;
	private static final Log log = LogFactory.getLog(LegendPanel.class);
	
	private Legend legend;
	private long periodo;
	/**
	 * Create the panel.
	 * @param periodo 
	 * @param legend 
	 */
	public LegendPanel(Legend legend, long periodo) {
		this.periodo = periodo;
		this.legend = legend;
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();

		accelMemory = gd.getAvailableAcceleratedMemory(); // in bytes
		log.info("Initial Acc. Mem.: " + df.format(((double) accelMemory) / (1024 * 1024)) + " MB");

		setBackground(Color.white);
		setPreferredSize(new Dimension(AREA_WIDTH, AREA_HEIGHT));

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		readyForTermination();

		gameInit();

	}

	private void gameInit() {
		
	}
	private void gameUpdate(){
		
	}
	private void gameRender(){
		
	}
	private void paintScreen(){
		
	}

	// ----------------------- MEMORY ACCELERATION -------------------------
	int accelMemory;
	GraphicsDevice gd;

	private void reportAccelMemory()
	// report any change in the amount of accelerated memory
	{

		int mem = gd.getAvailableAcceleratedMemory(); // in bytes
		int memChange = mem - accelMemory;

		if (memChange != 0)
			this.legend.setMemRam(df.format(((double) accelMemory) / (1024 * 1024))
					+ " MB; Res: "
					+ df.format(((double) memChange) / 1024)
					+ " K");

		log.info("Acc. Mem: "
				+ df.format(((double) accelMemory) / (1024 * 1024))
				+ " MB; Res: " + df.format(((double) memChange) / 1024) + " K");
		accelMemory = mem;
	}

	// ------------------------------- THREAD --------------------------------------
	private boolean running = false; // Running game animation control
	// private static long MAX_STATS_INTERVAL = 1000L;
	// record stats every 1 second (roughly)
	private static long MAX_STATS_INTERVAL = 1000000000L;
	/*
	 * Number of frames with a delay of 0 ms before the animation thread yields
	 * to other running threads.
	 */
	private static final int NO_DELAYS_PER_YIELD = 16;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered
	private static int MAX_FRAME_SKIPS = 5; // was 2;
	// number of FPS values stored to get an average
	private static int NUM_FPS = 10;
	// number of F skipped
	private long framesSkipped = 0L;

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
				sleepTime = (this.periodo - timeDiff) - overSleepTime;

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
				while ((excess > this.periodo) && (skips < MAX_FRAME_SKIPS)) {
					excess -= this.periodo;
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
	}
	
	// --------------------------------- LOGGING ---------------------------------
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
		statsInterval += this.periodo;

		if (statsInterval >= MAX_STATS_INTERVAL) { // record stats every
													// MAX_STATS_INTERVAL
			long timeNow = System.nanoTime();
			timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L); // ns
			timeSpentInLevel = (int) ((timeNow - levelStartTime) / 1000000000L); // ns
																				// secs
			legend.setTimeSpent(timeSpentInGame);
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

	// -------------------------- KEY -------------------------
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

}
