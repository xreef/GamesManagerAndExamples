package it.reef.ark.element.actor.input;
import it.reef.ark.element.action.KeyProcessable;
import it.reef.ark.manager.image.ImageLoader;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
    
public class PlayerAdapter extends KeyAdapter {
	private KeyProcessable player;
	private LinkedList<KeyEvent> keyEventList;
	
	private static final Log log = LogFactory.getLog(PlayerAdapter.class);
	
	public PlayerAdapter(KeyProcessable p) {
		player = p;
		keyEventList = new LinkedList<KeyEvent>();
	}

    
	public void keyPressed(KeyEvent e) {
		synchronized(e)
        {
			if (keyEventList.size()==0 || keyEventList.getLast()!=e) keyEventList.add(e);
        }
	}
	public void keyReleased(KeyEvent e) {
		synchronized(e)
        {
			if (keyEventList.size()==0 || keyEventList.getLast()!=e) keyEventList.add(e);
        }
	}
  
    public void processKeyEventList()
    {
        KeyEvent event;
        
        // log.debug("Processo gli eventi da tastiera INIZIO. Size: "+keyEventList.size() );
        
        while(keyEventList.size()>0)
        {
            synchronized(keyEventList)
            {
                event = (KeyEvent)keyEventList.removeFirst();
            }
            player.handleKeyEvent(event);
        }
        
        // log.debug("Processo gli eventi da tastiera FINE" );
        
    }
}