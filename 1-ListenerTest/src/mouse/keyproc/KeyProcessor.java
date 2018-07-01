package mouse.keyproc;
import java.awt.event.*;
import java.util.*;
    
public class KeyProcessor
{
    public KeyProcessor(KeyProcessable handler)
    {
        keyEventList = new LinkedList<KeyEvent>();
        this.handler = handler;
    }
    
    public void addEvent(KeyEvent event)
    {   
        synchronized(keyEventList)
        {
            keyEventList.add(event);
        }
    }
  
    public void processKeyEventList()
    {
        KeyEvent event;
   
        while(keyEventList.size()>0)
        {
            synchronized(keyEventList)
            {
                event = (KeyEvent)keyEventList.removeFirst();
            }
            handler.handleKeyEvent(event);
        }
    }
    
    private LinkedList<KeyEvent> keyEventList;
    private KeyProcessable handler;
}