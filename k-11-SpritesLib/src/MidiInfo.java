
// MidiInfo.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* Hold a single midi sequence, and allow it to be played,
   stopped, paused, resumed, and made to loop.

   Looping is controlled by MidisLoader by calling tryLooping().

   MidisLoader passes a reference to its sequencer to each
   MidiInfo object, so that it can play its sequence.
*/
import java.io.*;
import javax.sound.midi.*;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm765e]
 */
public class MidiInfo {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6dba]
 */
    private static final String SOUND_DIR = "Sounds/";

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6db4]
 */
    private String name;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6daf]
 */
    private String filename;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6da4]
 */
    private Sequence seq = null;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d9a]
 */
    private Sequencer sequencer;
// passed in from MidisLoader

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d93]
 */
    private boolean isLooping = false;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d89]
 * @param nm 
 * @param fnm 
 * @param sqr 
 */
    public  MidiInfo(String nm, String fnm, Sequencer sqr) {        
    name = nm;
    filename = SOUND_DIR + fnm;
    sequencer = sqr;
    loadMidi();
    } 
// end of MidiInfo()
// load the Midi sequence

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d83]
 */
    private void loadMidi() {        
 
    try {
      seq = MidiSystem.getSequence( getClass().getResource(filename) );
    }
    catch (InvalidMidiDataException e) {
      System.out.println("Unreadable/unsupported midi file: " + filename);
    }
    catch (IOException e) {
      System.out.println("Could not read: " + filename);
    }
    catch (Exception e) {
      System.out.println("Problem with " + filename);
    }
    } 
// end of loadMidi()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d7c]
 * @param toLoop 
 */
    public void play(boolean toLoop) {        
 if ((sequencer != null) && (seq != null)) {
      try {
        sequencer.setSequence(seq);   // load MIDI sequence into the sequencer
        sequencer.setTickPosition(0); // reset to the start
        isLooping = toLoop;
        sequencer.start();            // play it
      }
      catch (InvalidMidiDataException e) {
        System.out.println("Corrupted/invalid midi file: " + filename);
      }
    }
    } 
/* Stop the sequence. We want this to trigger an 'end-of-track'
     meta message, so we stop the track by winding it to its end.
     The meta message will be sent to meta() in MidisLoader, where
     the sequencer was created.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d76]
 */
    public void stop() {        
 if ((sequencer != null) && (seq != null)) {
      isLooping = false;
      if (!sequencer.isRunning())   // the sequence may be paused
        sequencer.start();
      sequencer.setTickPosition( sequencer.getTickLength() );  
         // move to the end of the sequence to trigger an end-of-track msg
    }
    } 
// pause the sequence by stopping the sequencer

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d70]
 */
    public void pause() {        
 if ((sequencer != null) && (seq != null)) {
      if (sequencer.isRunning())
        sequencer.stop();
    }
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d6a]
 */
    public void resume() {        
 if ((sequencer != null) && (seq != null))
      sequencer.start();
    } 
/* Loop the music if it's been set to be loopable,
     and report whether looping has occurred.
     Called by MidisLoader from meta() when it has received 
     an 'end-of-track' meta message.
 
     In other words, the sequence is not set in 'looping mode'
     (which is possible with new methods in J2SE 1.5), but instead
     is made to play repeatedly by the MidisLoader.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d64]
 * @return 
 */
    public boolean tryLooping() {        
 if ((sequencer != null) && (seq != null)) {
      if (sequencer.isRunning())
        sequencer.stop();
      sequencer.setTickPosition(0);
      if (isLooping) {    // play it again
        sequencer.start();
        return true;
      }
    }
    return false;
    } 
// end of tryLooping()
// -------------- other access methods -------------------

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6d5e]
 * @return 
 */
    public String getName() {        
  return name;  
    } 
 }
// end of MidiInfo class
