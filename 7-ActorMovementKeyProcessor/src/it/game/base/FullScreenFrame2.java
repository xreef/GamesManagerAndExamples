package it.game.base;

import java.awt.HeadlessException;
import java.awt.DisplayMode;
import java.awt.Color;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;

/**
 * @author Scott Shaver
 * 
 * Adding the rendering loop.
 */
public class FullScreenFrame2 extends FullScreenFrame1 implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is the color the back buffer will get cleared to
	 * each frame.  
	 */
	private Color backgroundColor = Color.black;
	
	/**
	 * The number of milliseconds to sleep before drawing the next frame.
	 */
	protected long renderLoopSpeed = 5;
	
	/**
	 * When set to true by the startRenderThread method the rendering thread
	 * will keep running. If set to false the render thread will stop executing.
	 */
	protected boolean doRender = false;
	
	/**
	 * If true the rendering thread will be paused.
	 */
	protected boolean pauseRender = false;

	/**
	 * The handle to the rendering thread.
	 */
	protected Thread renderThread = null;
	
	/**
	 * Create a new full screen mode window with the specified title and video mode.
	 * 
	 * @param title The window title.
	 * @param mode The DisplayMode that we want to use.
	 * @throws java.awt.HeadlessException
	 */
	public FullScreenFrame2(String title, DisplayMode mode) throws HeadlessException
	{
		super(title,mode);
	}

	/**
	 * Create a windowed mode window with the specified title and video mode.
	 * 
	 * @param title The window title.
	 * @param undecorated If true then the window decorations will not be displayed.
	 * @throws java.awt.HeadlessException
	 */
	public FullScreenFrame2(String title, boolean undecorated) throws HeadlessException
	{
		super(title,undecorated);
	}
	
	/**
	 * Get the window on the screen and if we are running in 
	 * full screen mode set the video mode.
	 * 
	 * @param numBuffers The number of buffers to create including the front buffer
	 */
	public void initToScreen(int numBuffers) 
	{
		super.initToScreen();

		// create a BufferStrategy with the specified number of buffers
		// the window must be on screen before this call is made
		createBufferStrategy(numBuffers);
	}
	
	/**
	 * Start the rendering thread if it is not currently running.
	 */
	public void startRenderThread() 
	{
		if(doRender==false)
		{
			pauseRender = false;
			doRender = true;
			renderThread = new Thread(this,"Render Thread");
			renderThread.start();
		}
	}
    
	/**
	 * Stop the rendering thread.
	 */
	public void stopRenderThread() 
	{
		doRender = false;
		pauseRender = false;
	}
	
	/**
	 * Remove the window from the screen, if we are in full screen
	 * mode then we need to reset the video mode.
	 */
	public void initFromScreen() 
	{
		stopRenderThread();
		super.initFromScreen();
	}
    
	/**
	 * Pause or unpause the rendering thread.
	 * 
	 * @param b True to pause the rendering thread, false to unpasue it.
	 */
	public void pauseRenderThread(boolean b) 
	{
		pauseRender = b;
	}
    
    /**
     * The entry point for the rendering thread.
     */
	public void run() 
	{
		boolean contentsLost = false;
		try
		{
			// get the buffer strategy that we set up for this
			// window
			BufferStrategy bs = getBufferStrategy();
					
			// Stay in the render thread until it is stopped
			while(doRender)
			{
				// if the render thread has been paused then don't do anything
				if(!pauseRender)
				{
					// perform the rendering for the new frame
					// and find out if our game logic thinks
					// we should show the new frame
					boolean swapFrames = doRender();
					
					// Because the buffer strategy may have used volatile
					// images to speed things up we need to make sure that
					// the frame buffer we just rendered has not
					// been corrupted or lost
					if(bs.contentsLost())
						contentsLost=true;
						
				    // If the buffer has been corrupted or lost then we
				    // don't want to render the contents of them yet. We 
				    // will go through the loop again and redraw the frame. 
					if(contentsLost)
					{
						if(bs.contentsRestored())
							contentsLost=false;
					}
					else if(swapFrames)
					{
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
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
	    
	/**
	 * Call this method to make the screen redraw itself.
	 * 
	 * @return True if the frame buffer should be swapped, indicates the frame is drawn completely
	 */
	public boolean doRender() 
	{
		boolean shouldSwap = render(getBufferStrategy().getDrawGraphics());
		return shouldSwap;
	}
	
	/**
	 * This method does the actuall drawing you should override this method.
	 * Don't call this method directly, always call doRender() or let the rendering
	 * thread do the calls for you.
	 */
	public boolean render(Graphics g) 
	{
		g.setColor(backgroundColor);
		g.fillRect(0,0,getWidth(),getHeight());
		return true;
	}

	/**
	 * Set the color that the current buffer will be cleared to before drawing begins.
	 * @param color
	 */
	public void setBackgroundColor(Color color)
	{
		backgroundColor = color;
	}

	/**
	 * Sets the time, in milliseconds, between repaints when the render thread is running
	 * @param speed The milliseconds between repaints
	 */
	public void setRenderLoopSpeed(long speed) 
	{
		renderLoopSpeed = speed;
	}
	
	/** 
	 * Handle any special processing needed if the window is activated. 
	 */
	protected void formWindowActivated(java.awt.event.WindowEvent evt) 
	{
		pauseRenderThread(false);
	}
	
	/** 
	 * Handle any special processing needed if the window is deactivated. 
	 */
	protected void formWindowDeactivated(java.awt.event.WindowEvent evt) 
	{
		pauseRenderThread(true);
	}

	/** 
	 * Handle any special processing needed if the window is deiconified. 
	 */
	protected void formWindowDeiconified(java.awt.event.WindowEvent evt) 
	{
		pauseRenderThread(false); // this DOESN'T WORK
	}

	/** 
	 * Handle any special processing needed if the window is iconified. 
	 */
	protected void formWindowIconified(java.awt.event.WindowEvent evt) 
	{
		pauseRenderThread(true);
	}
	

}
