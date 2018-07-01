package it.game.base;

import java.awt.DisplayMode;
import java.awt.HeadlessException;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * @author Scott Shaver
 *
 * Adding a frames per second counter.
 */
public class FullScreenFrame3 extends FullScreenFrame2
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected long timeLastFrame = 0;
	protected int frameCount = 0;
	protected String framesPerSecondText = "0";
	protected int framesPerSecond = 0;
	protected Font fpsFont = new Font("Courier",Font.BOLD,12);

    /**
	 * Create a new full screen mode window with the specified title and video mode.
	 * 
     * @param title
     * @param mode
     * @throws HeadlessException
     */
    public FullScreenFrame3(String title, DisplayMode mode) throws HeadlessException
    {
        super(title, mode);
    }

    /**
	 * Create a windowed mode window with the specified title and video mode.
	 * 
     * @param title
     * @param undecorated
     * @throws HeadlessException
     */
    public FullScreenFrame3(String title, boolean undecorated) throws HeadlessException
    {
        super(title, undecorated);
    }
    
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

	/**
	 * This method does the actuall drawing you should override this method.
	 * Don't call this method directly, always call doRender() or let the rendering
	 * thread do the calls for you.
	 */
	public boolean render(Graphics g) 
	{
		super.render(g);
		displayFramesPerSecond(g);
		frameCount++;
		return true;
	}
	
	
	// The handle to the buffered image we will load and display
	private BufferedImage bufferedImage = null;
	public BufferedImage image = null;
	//Read in the image to display and put it in a BufferedImage    
	private void loadBufferedImage(URL imageURL)
	{
	try
	{
	   bufferedImage = ImageIO.read(imageURL);
	}
	catch(IOException x)
	 { 
	    x.printStackTrace();
	 }
	}
	
	/**
	  * Read in the image to using GraphicsConfiguration 
	  *createCompatibleImage(width,height,transparency)method
	  */
	public void loadImage5(URL imageURL)
	{
		loadBufferedImage(imageURL);
		image = getGraphicsConfiguration().createCompatibleImage(
	    bufferedImage.getWidth(),
	    bufferedImage.getHeight(),
	    Transparency.BITMASK);
		image.getGraphics().drawImage(bufferedImage,0,0,this);
	}
}
