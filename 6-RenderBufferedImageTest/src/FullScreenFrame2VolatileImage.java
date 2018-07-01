import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.DisplayMode;
import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.Graphics;

import javax.imageio.ImageIO;

/**
 * @author Scott Shaver
 * 
 *         Adding the rendering loop.
 */
public class FullScreenFrame2VolatileImage extends FullScreenFrame1 implements
		Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is the color the back buffer will get cleared to each frame.
	 */
	private Color backgroundColor = Color.black;

	/**
	 * The number of milliseconds to sleep before drawing the next frame.
	 * Normally set to 5
	 */
	protected long renderLoopSpeed = 0;

	/**
	 * When set to true by the startRenderThread method the rendering thread
	 * will keep running. If set to false the render thread will stop executing.
	 */
	protected boolean doRender = false;

	/**
	 * If true the rendering thread will be paused.
	 */
	protected boolean pauseRender = false;

	protected boolean pauseOk = false;

	/**
	 * The handle to the rendering thread.
	 */
	protected Thread renderThread = null;

	/**
	 * Create a new full screen mode window with the specified title and video
	 * mode.
	 * 
	 * @param title
	 *            The window title.
	 * @param mode
	 *            The DisplayMode that we want to use.
	 * @throws java.awt.HeadlessException
	 */
	public FullScreenFrame2VolatileImage(String title, DisplayMode mode)
			throws HeadlessException {
		super(title, mode);
		addKeyListener(new KeyGrabPersonalizzato());
	}

	public boolean isPauseRender() {
		return pauseRender;
	}

	public void setPauseRender(boolean pauseRender) {
		this.pauseRender = pauseRender;
	}

	/**
	 * Create a windowed mode window with the specified title and video mode.
	 * 
	 * @param title
	 *            The window title.
	 * @param undecorated
	 *            If true then the window decorations will not be displayed.
	 * @throws java.awt.HeadlessException
	 */
	public FullScreenFrame2VolatileImage(String title, boolean undecorated)
			throws HeadlessException {
		super(title, undecorated);
		addKeyListener(new KeyGrabPersonalizzato());
	}

	/**
	 * Get the window on the screen and if we are running in full screen mode
	 * set the video mode.
	 * 
	 * @param numBuffers
	 *            The number of buffers to create including the front buffer
	 */
	public void initToScreen(int numBuffers) {
		super.initToScreen();

		// create a BufferStrategy with the specified number of buffers
		// the window must be on screen before this call is made
		createBufferStrategy(numBuffers);
	}

	/**
	 * Start the rendering thread if it is not currently running.
	 */
	public void startRenderThread() {
		if (doRender == false) {
			pauseRender = false;
			pauseOk = false;
			doRender = true;
			renderThread = new Thread(this, "Render Thread");
			renderThread.start();
		}
	}

	/**
	 * Stop the rendering thread.
	 */
	public void stopRenderThread() {
		doRender = false;
		pauseRender = false;
	}

	/**
	 * Remove the window from the screen, if we are in full screen mode then we
	 * need to reset the video mode.
	 */
	public void initFromScreen() {
		stopRenderThread();
		super.initFromScreen();
	}

	/**
	 * Pause or unpause the rendering thread.
	 * 
	 * @param b
	 *            True to pause the rendering thread, false to unpasue it.
	 */
	public void pauseRenderThread(boolean b) {
		pauseRender = b;
	}

	
	
	/**
	 * The entry point for the rendering thread.
	 */
	public void run() {
		loadImage();
		boolean contentsLost = false;
		try {
			// get the buffer strategy that we set up for this
			// window
			BufferStrategy bs = getBufferStrategy();

			// Stay in the render thread until it is stopped
			while (doRender) {
				// if the render thread has been paused then don't do anything
				if (!pauseRender || !pauseOk) {
					boolean swapFrames;
					if (pauseRender) {
						pauseOk = true;
						swapFrames = doRenderPausa();
					} else {
						swapFrames = doRender();
						pauseOk = false;
					}
					// perform the rendering for the new frame
					// and find out if our game logic thinks
					// we should show the new frame

					// Because the buffer strategy may have used volatile
					// images to speed things up we need to make sure that
					// the frame buffer we just rendered has not
					// been corrupted or lost
					if (bs.contentsLost())
						contentsLost = true;

					// If the buffer has been corrupted or lost then we
					// don't want to render the contents of them yet. We
					// will go through the loop again and redraw the frame.
					if (contentsLost) {
						if (bs.contentsRestored())
							contentsLost = false;
					} else if (swapFrames) {
						// Everything is okay with the buffers so go
						// ahead and swap the buffers to show the frame
						// we just rendered.
						bs.show();
					}
				}

				// now we sleep for a bit so we aren't taking the
				// entire CPU and to keep the frame rate where we want it.
				Thread.sleep(renderLoopSpeed);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	/**
	 * Call this method to make the screen redraw itself.
	 * 
	 * @return True if the frame buffer should be swapped, indicates the frame
	 *         is drawn completely
	 */
	public boolean doRender() {
		boolean shouldSwap = render(getBufferStrategy().getDrawGraphics());
		return shouldSwap;
	}

	public boolean doRenderPausa() {
		boolean shouldSwap = renderPausa(getBufferStrategy().getDrawGraphics());
		return shouldSwap;
	}

	public boolean renderPausa(Graphics g) {
		Random r = new Random();
		Color suprise = new Color(r.nextInt(255), r.nextInt(255), r
				.nextInt(255));

		g.setColor(suprise);
		g.fillOval(50, 50, 70, 70);
		return true;
	}

	protected long timeLastFrame = 0;
	protected int frameCount = 0;
	protected String framesPerSecondText = "0";
	protected int framesPerSecond = 0;
	protected Font fpsFont = new Font("Courier",Font.BOLD,12);

	
	/**
	 * returns the frames per second.
	 */
	public int getFramesPerSecond() {
		int fps = framesPerSecond;
		if(frameCount>0)
		{
			long curTime = System.currentTimeMillis();
			long val = curTime-timeLastFrame;
			if(val>=1000)
			{
				fps = (int)((frameCount*1000)/val);
				timeLastFrame = curTime;
				frameCount=0;
			}
		}
		return fps;
	}
    
	public void displayFramesPerSecond(Graphics g) {
		g.setColor(Color.white);
		g.setFont(fpsFont);
		int fps = getFramesPerSecond();
		if(framesPerSecond!=fps)
		{
			framesPerSecond = fps;
			framesPerSecondText = "FPS: "+fps;
		}
		g.drawString(framesPerSecondText,10,100);
	}
	
	// The resource location of the image to display
	private static final String imageResourceName = "/rock.png";
	// The handle to the buffered image we will load and display
	private VolatileImage image = null;
	private BufferedImage bufferedImage = null;

	// Read in the image,Put it in a VolatileImage object.
	private void loadImage() {
		try {
			URL imageURL = getClass().getResource(imageResourceName);
			if (imageURL == null) {
				System.out.println("Unable to locate the resource "
						+ imageResourceName);
				return;
			}
			bufferedImage = ImageIO.read(imageURL);
			image = getGraphicsConfiguration().createCompatibleVolatileImage(
					bufferedImage.getWidth(), bufferedImage.getHeight());
			image.getGraphics().drawImage(bufferedImage, 0, 0, this);
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	/**
	 * This method does the actual drawing you should override this method.
	 * Don't call this method directly, always call doRender() or let the
	 * rendering thread do the calls for you.
	 */
	public boolean render(Graphics g) {
		displayFramesPerSecond(g);
		frameCount++;
		
		Random rand = new Random();
		if (image != null) {

			int returnCode = image.validate(getGraphicsConfiguration());
			if (returnCode == VolatileImage.IMAGE_RESTORED) {
				// the contents need to be restored
				image.getGraphics().drawImage(bufferedImage, 0, 0, this);
			} else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
				// the old volatile image is incompatible
				image = getGraphicsConfiguration().createCompatibleVolatileImage(
															bufferedImage.getWidth(),
															bufferedImage.getHeight());
				image.getGraphics().drawImage(bufferedImage, 0, 0, this);
			}
			g.drawImage(image, rand.nextInt(getWidth()), rand.nextInt(getHeight()), this);
		}

		return true;
	}

	/**
	 * Set the color that the current buffer will be cleared to before drawing
	 * begins.
	 * 
	 * @param color
	 */
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}

	/**
	 * Sets the time, in milliseconds, between repaints when the render thread
	 * is running
	 * 
	 * @param speed
	 *            The milliseconds between repaints
	 */
	public void setRenderLoopSpeed(long speed) {
		renderLoopSpeed = speed;
	}

	/**
	 * Handle any special processing needed if the window is activated.
	 */
	protected void formWindowActivated(java.awt.event.WindowEvent evt) {
		pauseRenderThread(false);
	}

	/**
	 * Handle any special processing needed if the window is deactivated.
	 */
	protected void formWindowDeactivated(java.awt.event.WindowEvent evt) {
		pauseRenderThread(true);
	}

	/**
	 * Handle any special processing needed if the window is deiconified.
	 */
	protected void formWindowDeiconified(java.awt.event.WindowEvent evt) {
		pauseRenderThread(false); // this DOESN'T WORK
	}

	/**
	 * Handle any special processing needed if the window is iconified.
	 */
	protected void formWindowIconified(java.awt.event.WindowEvent evt) {
		pauseRenderThread(true);
	}

	/**
	 * Exit the program if the escape key is typed
	 */
	private void keyPressedHandler(java.awt.event.KeyEvent evt) {
		if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
			System.out.println("Exiting...");
			stopRenderThread();
			initFromScreen();
			System.exit(0);
		} else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_P) {
			if (this.pauseRender)
				pauseRenderThread(false);
			else
				pauseRenderThread(true);
		}
	}

	public class KeyGrabPersonalizzato extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == java.awt.event.KeyEvent.VK_P) {
				if (isPauseRender())
					pauseRenderThread(false);
				else
					pauseRenderThread(true);
			}
		}
	}

	public static void main(String[] args) {

		DisplayMode newMode = null;

		// see if the user wants to force windowed mode
		// even if full screen mode is available
		boolean forceWindowedMode = false;
		if (args.length >= 1)
			if (args[0].equalsIgnoreCase("windowed"))
				forceWindowedMode = true;

		// we need to make sure the system defualt display can
		// support full screen mode, if it can't we will run
		// in windowed mode
		boolean fullScreenMode = false;
		if (defaultScreenDevice.isFullScreenSupported()) {
			fullScreenMode = true;

			// dump a list of the available display modes to the console
			FullScreenFrame2VolatileImage.printDisplayModes();

			// try to get one of the modes we really want
			newMode = findRequestedMode();

			// if the mode doesn't exist then go into windowed mode
			// otherwise use full screen mode
			if (newMode == null)
				fullScreenMode = false;
		} else
			System.out.println("This system doesn't support full screen mode.");

		FullScreenFrame2VolatileImage myFrame = null;

		if (fullScreenMode && !forceWindowedMode)
			myFrame = new FullScreenFrame2VolatileImage(
					"FullScreenFrame2 Full Screen Mode", newMode);
		else {
			myFrame = new FullScreenFrame2VolatileImage(
					"FullScreenFrame2 Windowed Mode", false);
			myFrame.setBackground(Color.BLACK);
		}

		// put the window on screen using two buffers for the buffer strategy
		myFrame.initToScreen(2);
		// start the rendering thread
		myFrame.startRenderThread();
	}

}
