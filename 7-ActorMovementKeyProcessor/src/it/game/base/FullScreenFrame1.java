
package it.game.base;


import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.DisplayMode;

/**
 * @author Scott Shaver
 *
 * The basics of full screen mode and windowed mode.
 */
public class FullScreenFrame1 extends Frame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// get all of the video mode information we need first
	protected static GraphicsEnvironment gfxEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	protected static GraphicsDevice[] screenDeviceList = gfxEnv.getScreenDevices();
	protected static GraphicsDevice defaultScreenDevice = gfxEnv.getDefaultScreenDevice();
	protected static DisplayMode[] displayModes = defaultScreenDevice.getDisplayModes();
	
	/**
	 * Width of the window in windowed mode.
	 */
	private static final int windowedWidth = 640;
	
	/**
	 * Height of the window in windowed mode.
	 */
	private static final int windowedHeight = 640;
	
	/**
	 * If true then we a running in windowed mode and not fullscreen
	 */	
	protected boolean windowedMode = false;
	
	/**
	 * When running in full screen mode this holds the video mode that the
	 * display has been set to.
	 */
	protected DisplayMode newDisplayMode = null;
	
	/**
	 * This is the list of modes that we will allow our game to run in.
	 * They are listed in order of preference from top to bottom. We will
	 * accept any of these resolutions at the specified color depth at
	 * any refresh rate but we have listed specific refresh rates as well.
	 */
	private static DisplayMode[] requestedDisplayModes = new DisplayMode[] 
	{
		/*new DisplayMode(1280, 1024,32, 60),
		new DisplayMode(1280, 1024,24, 60),
		new DisplayMode(1280, 1024,16, 60),*/
		
		new DisplayMode(1024, 768, 32, 75),
		new DisplayMode(1024, 768, 32, 72),
		new DisplayMode(1024, 768, 32, 70),
		new DisplayMode(1024, 768, 32, 60),
		new DisplayMode(1024, 768, 32, DisplayMode.REFRESH_RATE_UNKNOWN),
		
		new DisplayMode(1024, 768, 24, DisplayMode.REFRESH_RATE_UNKNOWN),
		
		new DisplayMode(1024, 768, 16, 75),
		new DisplayMode(1024, 768, 16, 72),
		new DisplayMode(1024, 768, 16, 70),
		new DisplayMode(1024, 768, 16, 60),
		new DisplayMode(1024, 768, 16, DisplayMode.REFRESH_RATE_UNKNOWN),
		
		new DisplayMode(800,  600, 32, 100),
		new DisplayMode(800,  600, 32, 85),
		new DisplayMode(800,  600, 32, 75),
		new DisplayMode(800,  600, 32, 72),
		new DisplayMode(800,  600, 32, 70),
		new DisplayMode(800,  600, 32, 60),
		new DisplayMode(800,  600, 32, DisplayMode.REFRESH_RATE_UNKNOWN),
		
		new DisplayMode(800,  600, 24, DisplayMode.REFRESH_RATE_UNKNOWN),
		
		new DisplayMode(800,  600, 16, 85),
		new DisplayMode(800,  600, 16, 75),
		new DisplayMode(800,  600, 16, 72),
		new DisplayMode(800,  600, 16, 70),
		new DisplayMode(800,  600, 16, 60),
		new DisplayMode(800,  600, 16, DisplayMode.REFRESH_RATE_UNKNOWN),

		new DisplayMode(640,  480, 32, 120),
		new DisplayMode(640,  480, 32, 100),
		new DisplayMode(640,  480, 32, 85),
		new DisplayMode(640,  480, 32, 75),
		new DisplayMode(640,  480, 32, 72),
		new DisplayMode(640,  480, 32, 70),
		new DisplayMode(640,  480, 32, 60),
		new DisplayMode(640,  480, 32, DisplayMode.REFRESH_RATE_UNKNOWN),
		
		new DisplayMode(640,  480, 24, DisplayMode.REFRESH_RATE_UNKNOWN),
		
		new DisplayMode(640,  480, 16, 100),
		new DisplayMode(640,  480, 16, 85),
		new DisplayMode(640,  480, 16, 75),
		new DisplayMode(640,  480, 16, 72),
		new DisplayMode(640,  480, 16, 70),
		new DisplayMode(640,  480, 16, 60),
		new DisplayMode(640,  480, 16, DisplayMode.REFRESH_RATE_UNKNOWN),
	};
	
    /**
     * Create a new full screen mode window with the specified title and video mode.
     * 
     * @param title The window title.
     * @param mode The DisplayMode that we want to use.
     * @throws java.awt.HeadlessException
     */
    public FullScreenFrame1(String title, DisplayMode mode) throws HeadlessException
    {
        super(title);
        
        System.out.println("Entering the following video mode:");
		printDisplayMode(mode);
		
        // this is the video mode we will run in
		newDisplayMode = mode;
		
		       
		// assume we don't want window decorations like the title bar in full screen mode
		setUndecorated(true);

		// make sure the windowing system doesn't send repaint events
		// to the window	
		setIgnoreRepaint(true);   

		// make sure we get the window events		
		setupWindowListener();   
		
		// make sure we get keyboard events 
		setupKeyListener();   
    }

	/**
	 * Create a windowed mode window with the specified title and video mode.
	 * 
	 * @param title The window title.
	 * @param undecorated If true then the window decorations will not be displayed.
	 * @throws java.awt.HeadlessException
	 */
	public FullScreenFrame1(String title, boolean undecorated) throws HeadlessException
	{
		super(title);
		
		System.out.println("Entering windowed mode.");
		
		// we are running in windowed mode
     	windowedMode = true;
     	
     	// don't allow the user to resize the window
		setResizable(false);
		
		// do we want window decorations like the title bar?
		setUndecorated(undecorated);
	
		// make sure the windowing system doesn't send repaint events
		// to the window	
		setIgnoreRepaint(true);   

		// make sure we get the window events		
		setupWindowListener();
		
		// make sure we get keyboard events 
		setupKeyListener();   
	}
	
	/**
	 * Get the window on the screen and if we are running in 
	 * full screen mode set the video mode.
	 */
	public void initToScreen() 
	{
		pack();
        
		if(!windowedMode)
		{
			// set this Frame as a full screen window 
			defaultScreenDevice.setFullScreenWindow(this);
			// change the video mode to the one we wanted
			defaultScreenDevice.setDisplayMode(newDisplayMode);
		}
		else
		{
			setSize(windowedWidth,windowedHeight);
			setVisible(true);
		}
	}

	/**
	 * Remove the window from the screen, if we are in full screen
	 * mode then we need to reset the video mode.
	 */
	public void initFromScreen() 
	{
		if(!windowedMode)
		{
		
			defaultScreenDevice.setFullScreenWindow(null);
		}
		else
			setVisible(false);
	}

	/**
	 * Prints a list of the available display modes for the default
	 * screen display device. 
	 */
	public static void printDisplayModes()
	{
		// loop through each of the available modes
		for(int mIndex=0; mIndex < displayModes.length; mIndex++)
			printDisplayMode(displayModes[mIndex]);
	}
	
	/**
	 * Print the display mode
	 */
	public static void printDisplayMode(DisplayMode mode)
	{
		System.out.println(
			"" + mode.getWidth()+
			"x" + mode.getHeight()+
			"x" + mode.getBitDepth()+
			"@" + mode.getRefreshRate());
	}
	
	/**
	 * Returns the best matching mode from our preferred list.
	 * If no match can be found null is returned.
	 */
	public static DisplayMode findRequestedMode()
	{
		DisplayMode best = null;

		// loop through each of our requested modes
		for(int rIndex=0; rIndex < requestedDisplayModes.length; rIndex++)
		{
			// loop through each of the available modes
			for(int mIndex=0; mIndex < displayModes.length; mIndex++)
			{
				if(displayModes[mIndex].getWidth() == requestedDisplayModes[rIndex].getWidth() &&
				   displayModes[mIndex].getHeight() == requestedDisplayModes[rIndex].getHeight() &&
				   displayModes[mIndex].getBitDepth() == requestedDisplayModes[rIndex].getBitDepth())
				{
					// We found resolution a match
					if(best==null)
					{
						// if the refresh rate was specified try to match that as well
						if(requestedDisplayModes[rIndex].getRefreshRate()!=DisplayMode.REFRESH_RATE_UNKNOWN)
						{
							if(displayModes[mIndex].getRefreshRate() >=
								requestedDisplayModes[rIndex].getRefreshRate())
							{
								best = displayModes[mIndex];
								return best;
							}
						}
						else
						{
							best = displayModes[mIndex];
							return best;
						}
					}
				}
			}
		}

		// no matching modes so we return null
		return best;
	}
	
	/** 
	 * We may need to handle different window events
	 * so we set up a window listener for this Frame.
	 */
	private void setupWindowListener() 
	{
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
			public void windowIconified(java.awt.event.WindowEvent evt) {
				formWindowIconified(evt);
			}
			public void windowDeiconified(java.awt.event.WindowEvent evt) {
				formWindowDeiconified(evt);
			}
			public void windowActivated(java.awt.event.WindowEvent evt) {
				formWindowActivated(evt);
			}
			public void windowDeactivated(java.awt.event.WindowEvent evt) {
				formWindowDeactivated(evt);
			}
		});
        
	}
	
	/** 
	 * Listen for keyboard events so we can exit the program with the escape key.
	 */
	private void setupKeyListener() 
	{
		addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {keyPressedHandler(evt);}
		});
	}
	
	/**
	 * Exit the program if the escape key is typed 
	 */
	private void keyPressedHandler(java.awt.event.KeyEvent evt) {
		if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ESCAPE)
		{
			System.out.println("Exiting...");
			initFromScreen();
			System.exit(0);
		}
	}
	
	/** 
	 * Handle any special processing needed if the window is activated. 
 	 */
	protected void formWindowActivated(java.awt.event.WindowEvent evt) 
	{
	}

	/** 
	 * Handle any special processing needed if the window is deactivated. 
	 */
	protected void formWindowDeactivated(java.awt.event.WindowEvent evt) 
	{
	}

	/** 
	 * Handle any special processing needed if the window is deiconified. 
	 */
	protected void formWindowDeiconified(java.awt.event.WindowEvent evt) 
	{
	}

	/** 
	 * Handle any special processing needed if the window is iconified. 
	 */
	protected void formWindowIconified(java.awt.event.WindowEvent evt) 
	{
	}

	/** 
	 * Handle any special processing needed if the window is closed. 
	 */
	protected void exitForm(java.awt.event.WindowEvent evt) 
	{
		// make sure we restore the video mode before exiting
		initFromScreen();
		
		// force the program to exit
		System.exit(0);
	}
}
