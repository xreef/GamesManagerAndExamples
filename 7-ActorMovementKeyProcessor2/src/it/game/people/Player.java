package it.game.people;

import it.game.people.interf.KeyProcessable;
import it.game.people.interf.Updater;

import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Player extends Actor implements KeyProcessable {
	
	private static final String imagePlayer = "/rock.png";
	
	public BufferedImage image;

	public boolean[] keyState;
	public int speedX=1;
    public int speedY=1;
	
	public Player(){
		keyState = new boolean[256];
		speedX = 5;
	    speedY = 5;
	}
	 
	public boolean collidesWith(Updater other) {
		// TODO Auto-generated method stub
		return false;
	}

	public void handleCollisions() {
		// TODO Auto-generated method stub
		
	}

	public void update() {
		 if(keyState[KeyEvent.VK_LEFT] && !keyState[KeyEvent.VK_RIGHT])
		            moveLeft();
		 else if(keyState[KeyEvent.VK_RIGHT] && !keyState[KeyEvent.VK_LEFT])
		            moveRight();
		    
		 if(keyState[KeyEvent.VK_UP] && !keyState[KeyEvent.VK_DOWN])
		            moveUp();
		 else if(keyState[KeyEvent.VK_DOWN] && !keyState[KeyEvent.VK_UP])
		            moveDown();
	}

	public boolean isVisible() {
		return true;
	}

	public void logDebugString(String s) {
		// TODO Auto-generated method stub
		
	}

	public void logString(String s) {
		// TODO Auto-generated method stub
		
	}
    
    public void moveLeft()
    {
    	int x = this.getX();
        x-=speedX;
        if(x<0) x = 0;
        this.setX(x);
    }
        
    public void moveRight()
    {
    	int x = this.getX();
    	
        x+=speedX;
//        if(x+hotSpot.bounds.width > bounds.width)
//            x = bounds.width-hotSpot.bounds.width;
        this.setX(x);
    }
    
    
    public void moveUp()
    {
    	int y = this.getY();
        y-=speedY;
        if(y<0)
            y = 0;
        this.setY(y);
    }
    
    
    public void moveDown()
    {
    	int y = this.getY();
    	y+=speedY;
//        if(y+hotSpot.bounds.height > bounds.height)
//            y = bounds.height-hotSpot.bounds.height;
    	this.setY(y);
    }

    
    
    
    
	public void handleKeyEvent(KeyEvent e) {
		switch(e.getID())
        {
            case KeyEvent.KEY_PRESSED:
                keyState[e.getKeyCode()] = true;
                break;
            case KeyEvent.KEY_RELEASED:
                keyState[e.getKeyCode()] = false;
                break;
        }
	}

	public String getImageUrl() {
		return imagePlayer;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
