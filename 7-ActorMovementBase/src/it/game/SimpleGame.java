package it.game;

import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.net.URL;

import it.game.base.FullScreenFrame3;
import it.game.manager.ActorManager;
import it.game.people.Player;

public class SimpleGame extends FullScreenFrame3 {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int GAME_INIT          = 1;
	static final int GAME_MAIN          = 2;
	static final int GAME_SHUTDOWN      = 3;
	static final int GAME_MENU          = 4;
	
	public int gamestate = 4;
	
	ActorManager am = null;
	
	public SimpleGame(String title, DisplayMode mode) throws HeadlessException {
		super(title, mode);
		am = new ActorManager();
		setState(GAME_INIT);
	}

	//Create a windowed mode window with the specified title and video mode.
	public SimpleGame(String title, boolean undecorated) throws HeadlessException
	{
		super(title,undecorated);
		
    	am = new ActorManager();
		setState(GAME_INIT);
	}
	
	public void setState(int state)
	{
		gamestate = state;
		update();
	}
	
	/*This function will process the current game state and call the
	*underlying functions until the execution is terminated by input or
	*breaking the game state.
	*/
	public void update()
	{
	  switch(gamestate)
	  {
	    case GAME_INIT:
	         gameInit();
	      break;
	     case GAME_MAIN:
	       run();
	       break;
	     case GAME_SHUTDOWN:
	       gameShutdown();
	       break;
	     case GAME_MENU:
	       // displayGameMenu();
	       break;
	     default:
	       System.out.println("Could not ID Gamestate");
	   }
	}
	
	class PlayerAdapter extends KeyAdapter
	{
		private Player player;

		public PlayerAdapter(Player p) {
			player = p;
		}

		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				player.setY(player.getY() - 5);
				break;
			case KeyEvent.VK_DOWN:
				player.setY(player.getY() + 5);
				break;
			case KeyEvent.VK_LEFT:
				player.setX(player.getX() - 5);
				break;
			case KeyEvent.VK_RIGHT:
				player.setX(player.getX() + 5);
				break;
			default:
				break;
			}
		}
	}
	
	// The resource location of the image to display 
	private static final String imagePlayer = "/rock.png";
	
	private void gameInit() {
		URL imageURL = getClass().getResource(imagePlayer);
		  if(imageURL==null){
			  System.out.println("Unable to locate resource      return");
		  }
		// loadBufferedImage(imageURL);
		loadImage5(imageURL);
		initToScreen(2);
		
		// start the rendering thread
		startRenderThread();
		am.createEntity("it.game.people.Player",1);
		  //add some input to update the player position on screen.
		addKeyListener(
				new PlayerAdapter((Player)am.actorList.getFirst())
		);
		setState(GAME_MAIN);
		
	}
	
	public void gameShutdown()
	{    
	  System.out.println("The game is shutting down.");
	  am.clearEntities();
	  stopRenderThread();
	  initFromScreen();
	  System.exit(0); 
	} 

	
	public boolean render(Graphics g) 
	{
	    super.render(g);
	    Player a;    
	    if(image!=null)
	    {
	      for (int i = 0; i<am.actorList.size(); i++) 
	        {
	              a = (Player)am.actorList.get(i);
	              g.drawImage(image,a.getX(),a.getY(),this);
	           }
	    }
	    return true;
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
					boolean swapFrames = doRender();
					
					// perform the rendering for the new frame
					// and find out if our game logic thinks
					// we should show the new frame
					
					
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
	
	//Can be left as is.
    public static void main(String[] args)
    {
		DisplayMode newMode = null;
		
		// see if the user wants to force windowed mode even if full screen mode is available
		boolean forceWindowedMode = false;
		if(args.length >= 1)
			if(args[0].equalsIgnoreCase("windowed"))
				forceWindowedMode = true;
		
		// we need to make sure the system defualt display can support full screen mode, if it can't 
		//we will run in windowed mode
		boolean fullScreenMode = false;
		if(defaultScreenDevice.isFullScreenSupported())
		{
			fullScreenMode = true;
				
			// try to get one of the modes we really want
			newMode = findRequestedMode();
			
			// if the mode doesn't exist then go into windowed mode or use full screen mode
			if(newMode==null)
				fullScreenMode = false;
		}
		else
			System.out.println("This system doesn't support full screen mode.");
			
		SimpleGame myFrame = null;
			
		if(fullScreenMode && !forceWindowedMode)
			myFrame = new SimpleGame("SimpleGame Full Screen Mode", newMode);
		else
			myFrame = new SimpleGame("SimpleGame Windowed Mode", false);
			
		
	}
    
	/**
	 * Exit the program if the escape key is typed 
	 */
	private void keyPressedHandler(java.awt.event.KeyEvent evt) {
		if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ESCAPE)
		{
			System.out.println("Exiting...");
			setState(GAME_SHUTDOWN);
			update();
			
			System.exit(0);
		}
	}
}
