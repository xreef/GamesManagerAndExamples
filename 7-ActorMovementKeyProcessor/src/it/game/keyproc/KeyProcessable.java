package it.game.keyproc;
import java.awt.event.*;
import java.util.LinkedList;
    
public interface KeyProcessable
{
    public void handleKeyEvent(LinkedList<KeyEvent> keyEventList);
}