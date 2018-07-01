package mouse.keyproc;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
    
public class Animator implements KeyProcessable
{
    public Animator(Rectangle bounds)
    {
        this.bounds = bounds;
        keyState = new boolean[256];
    
        createHotSpot();
    
        speedX = 4;
        speedY = 4;
    }
    
    
    public void createHotSpot()
    {
        Random rand = new Random();
        
        int diameter = 100+rand.nextInt(200);
        Color col = new Color(rand.nextInt(Integer.MAX_VALUE));
    
        int xPos = (bounds.width-diameter)/2; // center x
        int yPos = (bounds.height-diameter)/2; // center y
    
        hotSpot = new HotSpot(new Point(xPos, yPos), diameter, col);
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
        hotSpot.bounds.x-=speedX;
        if(hotSpot.bounds.x<0)
            hotSpot.bounds.x = 0;
    }
        
    public void moveRight()
    {
        hotSpot.bounds.x+=speedX;
        if(hotSpot.bounds.x+hotSpot.bounds.width > bounds.width)
            hotSpot.bounds.x = bounds.width-hotSpot.bounds.width;
    
    }
    
    
    public void moveUp()
    {
        hotSpot.bounds.y-=speedY;
        if(hotSpot.bounds.y<0)
            hotSpot.bounds.y = 0;
    }
    
    
    public void moveDown()
    {
        hotSpot.bounds.y+=speedY;
        if(hotSpot.bounds.y+hotSpot.bounds.height > bounds.height)
            hotSpot.bounds.y = bounds.height-hotSpot.bounds.height;
    }
    
    
    public void render(Graphics g)
    {
        g.translate(bounds.x, bounds.y);
    
        g.setColor(Color.blue);
        g.fillRect(0, 0, bounds.width, bounds.height);
    
        hotSpot.render(g);
    
        g.translate(-bounds.x, -bounds.y);
    }
    
    
    public void handleKeyEvent(KeyEvent e)
    {
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
    
    public int speedX;
    public int speedY;
    
    public HotSpot hotSpot;    
    public Rectangle bounds;
    
    public boolean[] keyState;
}