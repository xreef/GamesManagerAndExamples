
package it.reef.managers.audio;
import it.reef.managers.audio.interfaces.SoundsWatcher;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequencer;
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
 * @poseidon-object-id [Im62d1359fm126e672b042mm67f7]
 */
public class MidisLoader implements MetaEventListener {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6638]
 */
    private static final int END_OF_TRACK = 47;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm662f]
 */
    private static final String SOUND_DIR = "Sounds/";
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6626]
 */
    private Sequencer sequencer;
public void meta(MetaMessage meta) {
	// TODO Auto-generated method stub
	
}
 }
// end of ClipInfo class
