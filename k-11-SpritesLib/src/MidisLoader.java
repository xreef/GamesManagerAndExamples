
// MidisLoader.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* MidisLoader  stores a collection of MidiInfo objects
   in a HashMap whose keys are their names. 

   The name and filename for a midi sequence is obtained from a sounds
   information file which is loaded when MidisLoader is created.
   The file is assumed to be in Sounds/.

   MidisLoader allows a specified midi sequence to be played, stopped, 
   resumed, looped. A SoundsWatcher can be attached to the sequencer
   (not a sequence).

   MidisLoader differs from ClipsLoader in containing a Sequencer
   object for playing a sequence. Consequently, only a single 
   sequence can be played at a time, while many clips can be 
   playing together.

   A reference to the sequencer is passed to each MidiInfo object,
   which are responsible for playing, stopping, resuming and looping
   their sequences.
*/
import java.io.*;
import java.util.*;
import javax.sound.midi.*;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7668]
 */
public class MidisLoader implements MetaEventListener {
// midi meta-event constant used to signal the end of a track

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7319]
 */
    private static final int END_OF_TRACK = 47;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7313]
 */
    private static final String SOUND_DIR = "Sounds/";
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7307]
 */
    private Sequencer sequencer;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72fd]
 */
    private HashMap midisMap;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72f5]
 */
    private MidiInfo currentMidi = null;
// reference to currently playing MidiInfo object
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72eb]
 */
    private SoundsWatcher watcher = null;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72e3]
 */
    public  MidisLoader() {        
midisMap = new HashMap();
    initSequencer();
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72dc]
 * @param soundsFnm 
 */
    public  MidisLoader(String soundsFnm) {        
 midisMap = new HashMap();
    initSequencer();
    loadSoundsFile(soundsFnm);
    } 
/* Set up the MIDI sequencer, and the sequencer's meta-event
     listener. No synthesizer is used here. */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72d6]
 */
    private void initSequencer() {        
    try {
      sequencer = MidiSystem.getSequencer();
      if (sequencer == null) {
        System.out.println("Cannot get a sequencer");
        return;
      }
      sequencer.open();
      sequencer.addMetaEventListener(this);
      // maybe the sequencer is not the same as the synthesizer
      // so link sequencer --> synth (this is required in J2SE 1.5)
      if (!(sequencer instanceof Synthesizer)) {
        System.out.println("Linking the MIDI sequencer and synthesizer");
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        Receiver synthReceiver = synthesizer.getReceiver();
        Transmitter seqTransmitter = sequencer.getTransmitter();
        seqTransmitter.setReceiver(synthReceiver);
      }
    }
    catch (MidiUnavailableException e){
      System.out.println("No sequencer available");
      sequencer = null;
    }
    } 
// end of initSequencer()
/* The format of the input lines are:
        <name> <fnm>                     // a single sound file
     and blank lines and comment lines.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72cf]
 * @param soundsFnm 
 */
    private void loadSoundsFile(String soundsFnm) {        
    String sndsFNm = SOUND_DIR + soundsFnm;
    System.out.println("Reading file: " + sndsFNm);
    try {
      InputStream in = this.getClass().getResourceAsStream(sndsFNm);
      BufferedReader br = new BufferedReader( new InputStreamReader(in));
      // BufferedReader br = new BufferedReader( new FileReader(sndsFNm));
      StringTokenizer tokens;
      String line, name, fnm;
      while((line = br.readLine()) != null) {
        if (line.length() == 0)  // blank line
          continue;
        if (line.startsWith("//"))   // comment
          continue;
        tokens = new StringTokenizer(line);
        if (tokens.countTokens() != 2)
          System.out.println("Wrong no. of arguments for " + line);
        else {
          name = tokens.nextToken();
          fnm = tokens.nextToken();
          load(name, fnm);
        }
      }
      br.close();
    } 
    catch (IOException e) 
    { System.out.println("Error reading file: " + sndsFNm);
      System.exit(1);
    }
    } 
// end of loadSoundsFile()
/* Close down the sequencer (and any playing sequence).
     This is different from close() in ClipsLoader in
     that it is dealing with the sequencer primarily, rather
     than a particular midi sequence. 
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72c9]
 */
    public void close() {        
 stop();    // stop the playing sequence
    if (sequencer != null) {
      if (sequencer.isRunning())
        sequencer.stop();
       
      sequencer.removeMetaEventListener(this);
      sequencer.close();
      sequencer = null;
    }
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72c2]
 * @param sw 
 */
    public void setWatcher(SoundsWatcher sw) {        
  watcher = sw;  
    } 
// ----------- manipulate a particular midi sequence --------
// create a MidiInfo object, and store it under name

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72ba]
 * @param name 
 * @param fnm 
 */
    public void load(String name, String fnm) {        
    if (midisMap.containsKey(name))
      System.out.println( "Error: " + name + "already stored");
    else if (sequencer == null)
      System.out.println( "No sequencer for: " + name);
    else {
      midisMap.put(name, new MidiInfo(name, fnm, sequencer) );
      System.out.println("-- " + name + "/" + fnm);
    }
    } 
// end of load()
// play (perhaps loop) the sequence

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72b1]
 * @param name 
 * @param toLoop 
 */
    public void play(String name, boolean toLoop) {        
     MidiInfo mi = (MidiInfo) midisMap.get(name);
     if (mi == null)
       System.out.println( "Error: " + name + "not stored");
     else {
       if (currentMidi != null)
         System.out.println("Sorry, " + currentMidi.getName() +
                              " already playing");
       else {
        currentMidi = mi;   // store a reference to playing midi
        mi.play(toLoop);
       }
     }
    } 
// end of play()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72ab]
 */
    public void stop() {        
 if (currentMidi != null)
      currentMidi.stop();    // triggers an 'end-of-track' meta event
    else                     // which causes meta() to be called here
      System.out.println("No music playing");
    } 
// end of stop()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm72a5]
 */
    public void pause() {        
 if (currentMidi != null)
      currentMidi.pause();
    else
      System.out.println( "No music to pause");
    } 
// end of pause()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm729f]
 */
    public void resume() {        
 if (currentMidi != null)
      currentMidi.resume();
    else
      System.out.println("No music to resume");
    } 
// end of resume()
// ---------------------------------------------------
/* Called when a meta event occurs during sequence playing. 
     The code only deals with an end-of-track event, which can be 
     triggered by the MidisInfo object when a sequence reaches its end 
     _or_ is stopped. However, a sequence at its end may be looping,
     and so this is checked by calling tryLooping() in MidiInfo.

     If there is a watcher, it is notified of the status.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7295]
 * @param meta 
 */
    public void meta(MetaMessage meta) {        
    if (meta.getType() == END_OF_TRACK) {
      String name = currentMidi.getName();
      // System.out.println("  END_OF_TRACK for " + name);
      boolean hasLooped = currentMidi.tryLooping();  // music still looping?
      if (!hasLooped)   // no it's finished
        currentMidi = null;
      if (watcher != null) {   // tell the watcher
        if (hasLooped)      // the music is playing again
          watcher.atSequenceEnd(name, SoundsWatcher.REPLAYED);
        else   // the music has finished
          watcher.atSequenceEnd(name, SoundsWatcher.STOPPED);
      }
    }
    } 
 }
// end of MidisLoader class
