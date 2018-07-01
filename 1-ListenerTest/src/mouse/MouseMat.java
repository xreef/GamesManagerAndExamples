package mouse;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
    
public class MouseMat extends JComponent implements MouseListener,
    MouseMotionListener
{
    public MouseMat(Rectangle bounds)
    {
       setBounds(bounds);
       setLayout(null);
       rect = new Rectangle(getWidth()/4, getHeight()/4,
           getWidth()/2, getHeight()/2);
    
       addMouseListener(this);
       addMouseMotionListener(this);
    }
    
    public void paintComponent(Graphics g)
    {
       Graphics2D g2D = (Graphics2D)g;
       g2D.setColor(Color.cyan);
       g2D.fillRect(0, 0, getWidth(), getHeight());
       if(mouseOver) g2D.setColor(Color.red);
       else g2D.setColor(Color.blue);
       g2D.fill(rect);
       g2D.setColor(Color.black);
       g2D.drawString("You can't lose me!!!!!",rect.x+40,rect.y+100);
       g2D.drawString("Example: Simple Mouse",120,20);
       
       
       g2D.drawString("Last recorded mouse position: ("+mousePoint.x+","+mousePoint.y+")",80,300);
       g2D.drawString("Last click count: "+clickCount,80,315);
       
       String lmStr = "Last mouse press event: ";
       if(lastMouseEvent!=null)
       {
          switch(lastMouseEvent.getID())
          {
             case MouseEvent.MOUSE_PRESSED:
                lmStr+="Mouse Pressed";
                break;
             case MouseEvent.MOUSE_RELEASED:
                lmStr+="Mouse Released";
                break;
             case MouseEvent.MOUSE_CLICKED:
                lmStr+="Mouse Clicked";
                break;
          }
       }
       g2D.drawString(lmStr,80,330);
       
       String lmmStr = "Last mouse motion event: ";
       if(lastMouseMotionEvent!=null)
       {
          switch(lastMouseMotionEvent.getID())
          {
             case MouseEvent.MOUSE_MOVED:
                lmmStr+="Mouse Moved";
                break;
             case MouseEvent.MOUSE_DRAGGED:
                lmmStr+="Mouse Dragged";
                break;
          }
       }
       g2D.drawString(lmmStr,80,345);
    }
    
    public void mousePressed(MouseEvent e)
    {
       if((e.getModifiers() & MouseEvent.BUTTON1_MASK)!=0)
       {
          rect.setLocation(e.getX()-(rect.width/2),e.getY()-
              (rect.height/2));
          lastMouseEvent = e;
          repaint();
       }
    }
    
    public void mouseReleased(MouseEvent e)
    {
       if((e.getModifiers() & MouseEvent.BUTTON1_MASK)!=0)
       {
          lastMouseEvent = e;
          repaint();
       }
    }
    
    public void mouseClicked(MouseEvent e)
    {
       if((e.getModifiers() & MouseEvent.BUTTON1_MASK)!=0)
       {
          clickCount = e.getClickCount();
          lastMouseEvent = e;
          repaint();
       }
    }
    
    public void mouseEntered(MouseEvent e)
    {
       mouseOver = true;
       repaint();
    }
    
    public void mouseExited(MouseEvent e)
    {
       mouseOver = false;
       repaint();
    }
    
    
    public void mouseMoved(MouseEvent e)
    {
       mousePoint.setLocation(e.getX(), e.getY());
       lastMouseMotionEvent = e;
       repaint();
    }
    
    public void mouseDragged(MouseEvent e)
    {
       mousePoint.setLocation(e.getX(), e.getY());
       lastMouseMotionEvent = e;
       repaint();
    }
    
    private int clickCount;
    private Rectangle rect;
    private Point mousePoint = new Point(0, 0);
    private MouseEvent lastMouseEvent;
    private MouseEvent lastMouseMotionEvent;
    private boolean mouseOver;
}
