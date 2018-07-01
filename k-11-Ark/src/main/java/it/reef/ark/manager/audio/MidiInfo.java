package it.reef.ark.manager.audio;

// MidiInfo.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Hold a single midi sequence, and allow it to be played,
 stopped, paused, resumed, and made to loop.

 Looping is controlled by MidisLoader by calling tryLooping().

 MidisLoader passes a reference to its sequencer to each
 MidiInfo object, so that it can play its sequence.
 */

import it.reef.ark.resources.audio.music.Musics;

import java.io.*;
import javax.sound.midi.*;

public class MidiInfo {
	private String name, filename;
	private Sequence seq = null;
	private Sequencer sequencer; // passed in from MidisLoader
	private boolean isLooping = false;

	private Class classForStream;
	
	public MidiInfo(String nm, String path, String fnm, Sequencer sqr) {
		name = nm;
		filename = path + fnm;
		sequencer = sqr;
		loadMidi();
	} // end of MidiInfo()

	public MidiInfo(String nm, Class cfs, String fnm, Sequencer sqr) {
		name = nm;
		filename = fnm;
		sequencer = sqr;
		this.classForStream=cfs;
		loadMidi();
	} // end of MidiInfo()
	
	private void loadMidi()
	// load the Midi sequence
	{
		try {
			if (classForStream!=null){
				seq = MidiSystem.getSequence(classForStream.getResourceAsStream(filename));
			}else{
				seq = MidiSystem.getSequence(getClass().getResource(filename));
			}
			
		} catch (InvalidMidiDataException e) {
			System.out.println("Unreadable/unsupported midi file: " + filename);
		} catch (IOException e) {
			System.out.println("Could not read: " + filename);
		} catch (Exception e) {
			System.out.println("Problem with " + filename);
		}
	} // end of loadMidi()

	public void play(boolean toLoop) {
		if ((sequencer != null) && (seq != null)) {
			try {
				sequencer.setSequence(seq); // load MIDI sequence into the
											// sequencer
				sequencer.setTickPosition(0); // reset to the start
				isLooping = toLoop;
				sequencer.start(); // play it
			} catch (InvalidMidiDataException e) {
				System.out.println("Corrupted/invalid midi file: " + filename);
			}
		}
	} // end of play()

	public void stop()
	/*
	 * Stop the sequence. We want this to trigger an 'end-of-track' meta
	 * message, so we stop the track by winding it to its end. The meta message
	 * will be sent to meta() in MidisLoader, where the sequencer was created.
	 */
	{
		if ((sequencer != null) && (seq != null)) {
			isLooping = false;
			if (!sequencer.isRunning()) // the sequence may be paused
				sequencer.start();
			sequencer.setTickPosition(sequencer.getTickLength());
			// move to the end of the sequence to trigger an end-of-track msg
		}
	} // end of stop()

	public void pause()
	// pause the sequence by stopping the sequencer
	{
		if ((sequencer != null) && (seq != null)) {
			if (sequencer.isRunning())
				sequencer.stop();
		}
	}

	public void resume() {
		if ((sequencer != null) && (seq != null))
			sequencer.start();
	}

	public boolean tryLooping()
	/*
	 * Loop the music if it's been set to be loopable, and report whether
	 * looping has occurred. Called by MidisLoader from meta() when it has
	 * received an 'end-of-track' meta message.
	 * 
	 * In other words, the sequence is not set in 'looping mode' (which is
	 * possible with new methods in J2SE 1.5), but instead is made to play
	 * repeatedly by the MidisLoader.
	 */
	{
		if ((sequencer != null) && (seq != null)) {
			if (sequencer.isRunning())
				sequencer.stop();
			sequencer.setTickPosition(0);
			if (isLooping) { // play it again
				sequencer.start();
				return true;
			}
		}
		return false;
	} // end of tryLooping()

	// -------------- other access methods -------------------

	public String getName() {
		return name;
	}
	
	private int volumePercentage=100;
	private int originalVolumeValues[];

	public void setVolumePercentage(int volPerc){
		Track[] tracks = seq.getTracks();
		if (originalVolumeValues==null){
			originalVolumeValues = new int[tracks.length];
			for (int i=0;i<tracks.length;i++)
				originalVolumeValues[i]= getVolumeTrack(i); //mc[i].getController(VOLUME_CONTROLLER);
		}
		
		for (int i=0;i<tracks.length;i++){
			int vol = 127*volPerc/100;
			this.setMidiVolume(tracks[i], tracks[i].size(), vol);
		}
		

		this.volumePercentage=volPerc;
	}
	
	public void increaseVolume(){
		if (this.volumePercentage+10<=100){
			this.volumePercentage+=10;
			this.setVolumePercentage(this.volumePercentage);
		}else{
			this.setVolumePercentage(100);
		}
	}
	public void decreaseVolume(){
		if (this.volumePercentage-10>=0){
			this.volumePercentage-=10;
			this.setVolumePercentage(this.volumePercentage);
		}else{
			this.setVolumePercentage(0);
		}
	}

	private int getVolumeTrack(int idt){
		Track tracks[] = seq.getTracks( );
	      MidiEvent event;
	      MidiMessage message;
	      ShortMessage sMessage, newShort;

	      for (int i=0; i < tracks[idt].size(); i++) {
	    	 
	    	  Track track = tracks[idt];
	        event = track.get(i);          // get the event
	        message = event.getMessage( );  // get its MIDI message
	        long tick = event.getTick( );   // get its tick
	        if (message instanceof ShortMessage) {
	          sMessage = (ShortMessage) message;

	          // check if the message is a NOTE_ON
	          if (sMessage.getCommand( ) == ShortMessage.NOTE_ON) {
	        	  return sMessage.getData2( );
	          }
	        }
	      }
		return 0;
	}

    private void setMidiVolume(Track track, int size, int vol)
    {
      MidiEvent event;
      MidiMessage message;
      ShortMessage sMessage, newShort;

      for (int i=0; i < size; i++) {
        event = track.get(i);          // get the event
        message = event.getMessage( );  // get its MIDI message
        long tick = event.getTick( );   // get its tick
        if (message instanceof ShortMessage) {
          sMessage = (ShortMessage) message;

          // check if the message is a NOTE_ON
          if (sMessage.getCommand( ) == ShortMessage.NOTE_ON) {
        	  
             // int curVol = sMessage.getData2( );
             int newVol = (vol > 127) ? 127 : vol;
             newShort = new ShortMessage( );
             try {
               newShort.setMessage(ShortMessage.NOTE_ON,
                                    sMessage.getChannel( ),
                                    sMessage.getData1( ), newVol);
               track.remove(event);
               track.add( new MidiEvent(newShort,tick) );
             }
             catch ( InvalidMidiDataException e)
             {  System.out.println("Invalid data");  }
          }
        }
      }
    }  // end of doubleVolume( )
} // end of MidiInfo class
