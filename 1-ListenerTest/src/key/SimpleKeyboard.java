package key;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
 
public class SimpleKeyboard extends JFrame implements Runnable
{
	public SimpleKeyboard()
	{
		super("Prova tasti.");
		addWindowListener(new WindowAdapter(){
		       public void windowClosing(WindowEvent we){
		         System.exit(0);
		       }
		     });
		init();
		run();
		
	}
	
    public void init()
    {
       getContentPane().setLayout(null);
       setSize(250,200);
       
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
       
       addKeyListener(new MyKeyAdapter());
       
       
       
       // addKeyListener(this);
       
    // don't allow the user to resize the window
		setResizable(false);
		
		// do we want window decorations like the title bar?
		setUndecorated(false);
	
		// make sure the windowing system doesn't send repaint events
		// to the window	
		setIgnoreRepaint(true);   
       
		setSize(windowedWidth,windowedHeight);
		setVisible(true);
		
		doRender = true;
		
       createBufferStrategy(2);
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
	
	/**
	 * Width of the window in windowed mode.
	 */
	private static final int windowedWidth = 250;
	
	/**
	 * Height of the window in windowed mode.
	 */
	private static final int windowedHeight = 200;
	
	/**
	 * If true then we a running in windowed mode and not fullscreen
	 */	
	protected boolean windowedMode = false;
	
	/**
	 * Remove the window from the screen, if we are in full screen
	 * mode then we need to reset the video mode.
	 */
	public void initFromScreen() 
	{
			setVisible(false);
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
    
    public void start()
    {
       lastKeyEvent = null;
       requestFocus();
    }
    
    public boolean render(Graphics g)
    {
       g.setColor(Color.black);
       g.fillRect(0,0,250,200);
       g.setColor(Color.white); 
       g.drawString("Example: Simple Keyboard",50,20);
       g.drawString("Press a key",90,165);
          
       if(lastKeyEvent!=null)
       { 
          g.drawString("Key description:",30,65);
          g.drawString(lastKeyEvent.getKeyText(lastKeyEvent
              .getKeyCode()),120,65);
             
          g.drawString("Key character:",30,80);
          g.drawString(String.valueOf(lastKeyEvent
              .getKeyChar()),120,80);
             
          g.drawString("Key code:",30,95);
          g.drawString(String.valueOf(lastKeyEvent
              .getKeyCode()),120,95);
             
          g.drawString("Is an Action key:",30,110);
          g.drawString(String.valueOf(lastKeyEvent
              .isActionKey()),120,110);
             
          g.drawString("Modifier keys:",30,125);
          g.drawString(lastKeyEvent.getKeyModifiersText
              (lastKeyEvent.getModifiers()),120,125);
       }
	return true;   
    }
    
    class MyKeyAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e) {
			lastKeyEvent = e;
		       repaint();
		}
	}
    
       
       // The methods keyReleased(..) and KeyTyped(..) inherited 
       // from KeyListener interface 
       // must be defined, but we can ignore them if we choose
    public void keyReleased(KeyEvent e) 
    { 
       // ignore
    }
    
    public void keyTyped(KeyEvent e)
    {
       // ignore
    }
    
    private KeyEvent lastKeyEvent;
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleKeyboard k = new SimpleKeyboard();
	}

	public void run() {
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
	
	public boolean doRender() 
	{
		boolean shouldSwap = render(getBufferStrategy().getDrawGraphics());
		return shouldSwap;
	}

}