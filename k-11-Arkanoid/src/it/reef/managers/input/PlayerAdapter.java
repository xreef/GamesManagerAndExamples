
package it.reef.managers.input;
import it.reef.managers.input.interfaces.KeyProcessable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm67f1]
 */
public interface KeyProcessable {
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm650b]
 * @param e 
 */
    public void handleKeyEvent(KeyEvent e);
}

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm67f2]
 */
public class PlayerAdapter extends KeyAdapter {
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6516]
 */
    private KeyProcessable player;
 }
