package it.reef.ark.manager.audio;

// ClipInfo.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Load a clip, which can be played, stopped, resumed, looped. 

 An object implementing the SoundsWatcher interface 
 can be notified when the clip loops or stops.

 Changes 9th August 2004
 - closed the input stream in loadClip()

 Changes 15th September 2004
 - bug: if the supplied WAV file is less than a second long then 
 no sound is played. See the bug report at 
 http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5085008
 and SoundPlayer/PlayClipBF.java

 Solution: make all sounds > 1 sec.
 added clip.setFramePosition(0) to update()

 added checkDuration() and DecimalFormat df
 */

import it.reef.ark.element.action.SoundsWatcher;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClipInfo implements LineListener {

	private static final Log log = LogFactory.getLog(ClipInfo.class);
	
	private Class classForStream;

	private String name, filename;
	private Clip clip = null;
	private boolean isLooping = false;
	private SoundsWatcher watcher = null;
	private DecimalFormat df;

	FloatControl volumeAudioControl;
	BooleanControl muteControl;
	
	public ClipInfo(String nm, String path, String fnm) {
		name = nm;
		filename = path + fnm;
		df = new DecimalFormat("0.#"); // 1 dp

		loadClip(filename);
	} // end of ClipInfo()
	
	public ClipInfo(String nm, Class classForSream, String fnm) {
		name = nm;
		filename = fnm;
		this.classForStream=classForSream;
		df = new DecimalFormat("0.#"); // 1 dp

		loadClip(filename);
	} // end of ClipInfo()

	private void loadClip(String fnm) {
		try {
			AudioInputStream stream = null;
			// link an audio stream to the sound clip's file
			if (classForStream!=null){
				stream = AudioSystem.getAudioInputStream(classForStream.getResourceAsStream(fnm));
			}else{
				stream = AudioSystem.getAudioInputStream(getClass().getResource(fnm));
			}
			
			AudioFormat format = stream.getFormat();

			// convert ULAW/ALAW formats to PCM format
			if ((format.getEncoding() == AudioFormat.Encoding.ULAW)
					|| (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat newFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits() * 2, format.getChannels(),
						format.getFrameSize() * 2, format.getFrameRate(), true); // big
																					// endian
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

			clip.open(stream); // open the sound file as a clip
			stream.close(); // we're done with the input stream

			checkDuration();
			
			volumeAudioControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
			
			this.maxAudio = volumeAudioControl.getMaximum();
			this.minAudio = volumeAudioControl.getMinimum();
		} // end of try block

		catch (UnsupportedAudioFileException audioException) {
			log.fatal("Unsupported audio file: " + fnm,audioException);
			System.exit(1);
		} catch (LineUnavailableException noLineException) {
			log.fatal("No audio line available for : " + fnm,noLineException);
			System.exit(1);
		} catch (IOException ioException) {
			log.fatal("Could not read: " + fnm,ioException);
			System.exit(1);
		} catch (Exception e) {
			log.fatal("Problem with " + fnm,e);
			System.exit(1);
		}
	} // end of loadClip()

	private void checkDuration() {
		// duration (in secs) of the clip
		double duration = clip.getMicrosecondLength() / 1000000.0; // new
		if (duration <= 1.0) {
			System.out.println("WARNING. Duration <= 1 sec : "
					+ df.format(duration) + " secs");
			System.out.println("         The clip in " + filename
					+ " may not play in J2SE 1.5 -- make it longer");
		} else
			System.out.println(filename + ": Duration: " + df.format(duration)
					+ " secs");
	} // end of checkDuration()

	public void update(LineEvent lineEvent)
	/*
	 * Called when the clip's line detects open, close, start, or stop events.
	 * The watcher (if one exists) is notified.
	 */
	{
		// when clip is stopped / reaches its end
		if (lineEvent.getType() == LineEvent.Type.STOP) {
			// System.out.println("update() STOP for " + name);
			clip.stop();
			clip.setFramePosition(0); // NEW
			if (!isLooping) { // it isn't looping
				if (watcher != null)
					watcher.atSequenceEnd(name, SoundsWatcher.STOPPED);
			} else { // else play it again
				clip.start();
				if (watcher != null)
					watcher.atSequenceEnd(name, SoundsWatcher.REPLAYED);
			}
		}else if (lineEvent.getType() == LineEvent.Type.START) {
			if (watcher != null)
				watcher.atSequenceEnd(name, SoundsWatcher.STARTED);
		}
		log.debug("Update clip "+name);
	} // end of update()

	public void close() {
		if (clip != null) {
			clip.stop();
			clip.close();
		}
	}

	public void play(boolean toLoop) {
		if (clip != null) {
			isLooping = toLoop;
			clip.start(); // start playing
		}
	}

	public void stop()
	// stop and reset clip to its start
	{
		if (clip != null) {
			isLooping = false;
			clip.stop();
			clip.setFramePosition(0);
		}
	}

	public void pause()
	// stop the clip at its current playing position
	{
		if (clip != null)
			clip.stop();
	}

	public void resume() {
		if (clip != null)
			clip.start();
	}   
	
	private int volumePercentage = 0;
	private float maxAudio;
	private float minAudio;
	public void setVolumePercentage(int volumePercentageToSet){
		if (volumePercentageToSet==0){
			this.volumeAudioControl.setValue(0f);
		}else if (volumePercentageToSet>0){
			this.volumeAudioControl.setValue(
					this.maxAudio*volumePercentageToSet/100
			);
		}else if (volumePercentageToSet<0){
			this.volumeAudioControl.setValue(
				-1*minAudio*volumePercentageToSet/100	
			);
		}
		this.volumePercentage=volumePercentageToSet;
	}
	public int getVolumePercentage(){
		return volumePercentage;
	}

	public void setWatcher(SoundsWatcher sw) {
		watcher = sw;
	}

	// -------------- other access methods -------------------
	public String getName() {
		return name;
	}
} // end of ClipInfo class
