package it.reef.managers.audio;

import it.reef.managers.audio.interfaces.SoundsWatcher;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm67f5]
 */
public class ClipInfo implements LineListener {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6612]
 */
    private static final String SOUND_DIR = "Sounds/";

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm660c]
 */
    private String name;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6607]
 */
    private String filename;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65ff]
 */
    private Clip clip = null;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65f7]
 */
    private boolean isLooping = false;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65ee]
 */
    private SoundsWatcher watcher = null;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65e4]
 */
    private DecimalFormat df;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65da]
 * @param nm 
 * @param fnm 
 */
    public  ClipInfo(String nm, String fnm) {        
 name = nm;
    filename = SOUND_DIR + fnm;
    df = new DecimalFormat("0.#");  // 1 dp
    loadClip(filename);
    } 
// end of ClipInfo()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65d2]
 * @param fnm 
 */
    private void loadClip(String fnm) {        
    try {
      // link an audio stream to the sound clip's file
      AudioInputStream stream = AudioSystem.getAudioInputStream(
                          getClass().getResource(fnm) );
      AudioFormat format = stream.getFormat();
      // convert ULAW/ALAW formats to PCM format
      if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
           (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
        AudioFormat newFormat = 
           new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                format.getSampleRate(),
                                format.getSampleSizeInBits()*2,
                                format.getChannels(),
                                format.getFrameSize()*2,
                                format.getFrameRate(), true);  // big endian
        // update stream and format details
        stream = AudioSystem.getAudioInputStream(newFormat, stream);
        System.out.println("Converted Audio format: " + newFormat);
        format = newFormat;
      }
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      // make sure sound system supports data line
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Unsupported Clip File: " + fnm);
        return;
      }
      // get clip line resource
      clip = (Clip) AudioSystem.getLine(info);
      // listen to clip for events
      clip.addLineListener(this);
      clip.open(stream);    // open the sound file as a clip
      stream.close(); // we're done with the input stream
     
      checkDuration();
    } // end of try block
    catch (UnsupportedAudioFileException audioException) {
      System.out.println("Unsupported audio file: " + fnm);
    }
    catch (LineUnavailableException noLineException) {
      System.out.println("No audio line available for : " + fnm);
    }
    catch (IOException ioException) {
      System.out.println("Could not read: " + fnm);
    }
    catch (Exception e) {
      System.out.println("Problem with " + fnm);
    }
    } 
// end of loadClip()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65cc]
 */
    private void checkDuration() {        
    // duration (in secs) of the clip
	double duration = clip.getMicrosecondLength()/ 1000000.0;  // new
    if (duration <=  1.0) {
      System.out.println("WARNING. Duration <= 1 sec : " + df.format(duration) + " secs");
      System.out.println("         The clip in " + filename + 
                         " may not play in J2SE 1.5 -- make it longer");
    }
    else
      System.out.println(filename + ": Duration: " + df.format(duration) + " secs");
    } 
// end of checkDuration()
/* Called when the clip's line detects open, close, start, or
     stop events. The watcher (if one exists) is notified.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65c5]
 * @param lineEvent 
 */
    public void update(LineEvent lineEvent) {        
    // when clip is stopped / reaches its end
    if (lineEvent.getType() == LineEvent.Type.STOP) {
      // System.out.println("update() STOP for " + name);
      clip.stop();
      clip.setFramePosition(0);  // NEW
      if (!isLooping) {  // it isn't looping
        if (watcher != null)
          watcher.atSequenceEnd(name, SoundsWatcher.STOPPED);
      }
      else {      // else play it again
        clip.start();
        if (watcher != null)
          watcher.atSequenceEnd(name, SoundsWatcher.REPLAYED);
      }
    }
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65bf]
 */
    public void close() {        
 if (clip != null) {
      clip.stop();
      clip.close();
    }
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65b8]
 * @param toLoop 
 */
    public void play(boolean toLoop) {        
 if (clip != null) {
      isLooping = toLoop;
      clip.start(); // start playing
    }
    } 
// stop and reset clip to its start

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65b2]
 */
    public void stop() {        
 if (clip != null) {
      isLooping = false;
      clip.stop();
      clip.setFramePosition(0);
    }
    } 
// stop the clip at its current playing position

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65ac]
 */
    public void pause() {        
 if (clip != null)
      clip.stop();
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm65a6]
 */
    public void resume() {        
 if (clip != null)
      clip.start();
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm659f]
 * @param sw 
 */
    public void setWatcher(SoundsWatcher sw) {        
  watcher = sw;  
    } 
// -------------- other access methods -------------------

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6599]
 * @return 
 */
    public String getName() {        
  return name;  
    } 
 }