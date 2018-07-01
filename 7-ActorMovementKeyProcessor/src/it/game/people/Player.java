package it.game.people;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

import it.game.keyproc.KeyProcessable;
import it.game.people.interf.Updater;

public class Player extends Actor implements KeyProcessable {
	
	public Player(){
		keyState = new boolean[256];
		 speedX = 4;
	     speedY = 4;
	}
	 
	public boolean collidesWith(Updater other) {
		// TODO Auto-generated method stub
		return false;
	}

	public void handleCollisions() {
		// TODO Auto-generated method stub
		
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

	public void draw() {
		// TODO Auto-generated method stub
		
	}

	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	public void logDebugString(String s) {
		// TODO Auto-generated method stub
		
	}

	public void logString(String s) {
		// TODO Auto-generated method stub
		
	}

	public boolean[] keyState;
	public int speedX=5;
    public int speedY=5;

	
    public void handleKeyEvent(LinkedList<KeyEvent> keyEventList)
    {
    	KeyEvent event;
    	   
        while(keyEventList.size()>0)
        {
            synchronized(keyEventList)
            {
                event = (KeyEvent)keyEventList.removeFirst();
                switch(event.getID())
                {
                    case KeyEvent.KEY_PRESSED:
                        keyState[event.getKeyCode()] = true;
                        break;
                    case KeyEvent.KEY_RELEASED:
                        keyState[event.getKeyCode()] = false;
                        break;
                }
            }
            
        }
        
        
    }
    
    public void animate()
    {
        if(keyState[KeyEvent.VK_LEFT] &&
            !keyState[KeyEvent.VK_RIGHT])
            moveLeft();
        else if(keyState[KeyEvent.VK_RIGHT] &&
            !keyState[KeyEvent.VK_LEFT])
            moveRight();
    
        if(keyState[KeyEvent.VK_UP] && !keyState[KeyEvent.VK_DOWN])
            moveUp();
        else if(keyState[KeyEvent.VK_DOWN] &&
            !keyState[KeyEvent.VK_UP])
            moveDown();
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


}
