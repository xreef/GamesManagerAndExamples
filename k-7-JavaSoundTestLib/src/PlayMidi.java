
// PlayMidi.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Load a MIDI sequence and play it once. PlayMidi goes to sleep
   while the sequence is playing. It is woken up by a
   end-of-track meta-event triggering a call to its meta()
   method, which causes it to exit.

   Change 6th August 2004
     - added synthesizer.open() to initSequencer()

*/

import java.io.*;
import javax.sound.midi.*;
import java.text.DecimalFormat;


public class PlayMidi implements MetaEventListener 
{
  // midi meta-event constant used to signal the end of a track 
  private static final int END_OF_TRACK = 47;

  private final static String SOUND_DIR = "Sounds/";


  private Sequencer sequencer;
  private Synthesizer synthesizer;
  private Sequence seq = null;
  private String filename;

  private DecimalFormat df; 


  public PlayMidi(String fnm) 
  {
    df = new DecimalFormat("0.#");  // 1 dp

    filename = SOUND_DIR + fnm;
    initSequencer();
    loadMidi(filename);
    play();

    // wait for the sound to finish playing; guess at 10 mins!
    System.out.println("Waiting");
    try { 
      Thread.sleep(600000);   // 10 mins in ms
    } 
    catch(InterruptedException e) 
    { System.out.println("Sleep Interrupted"); }
  }  // end of PlayMidi()


  private void initSequencer() 
  /* Set up the MIDI sequencer, the sequencer's meta-event
     listener, and its synthesizer. */
  {
    try {
      sequencer = MidiSystem.getSequencer();

      if (sequencer == null) {
        System.out.println("Cannot get a sequencer");
        System.exit(0);
      }

      sequencer.open();
      sequencer.addMetaEventListener(this);

      // maybe the sequencer is not the same as the synthesizer
      // so link sequencer --> synth (this is required in J2SE 1.5)
      if (!(sequencer instanceof Synthesizer)) {
        System.out.println("Linking the sequencer to a synthesizer");
        synthesizer = MidiSystem.getSynthesizer();
	    synthesizer.open();  // new
        Receiver synthReceiver = synthesizer.getReceiver();
        Transmitter seqTransmitter = sequencer.getTransmitter();
        seqTransmitter.setReceiver(synthReceiver);  
      }
      else 
        synthesizer = (Synthesizer) sequencer;   
           // we don't use the synthesizer in this simple code, 
           // so storing it as a global isn't really necessary
    }
    catch (MidiUnavailableException e){
      System.out.println("No sequencer available");
      System.exit(0);
    }
  } // end of initSequencer()


  private void loadMidi(String fnm)
  { 
    try {
      seq = MidiSystem.getSequence( getClass().getResource(fnm) );
      double duration = ((double) seq.getMicrosecondLength()) / 1000000;
      System.out.println("Duration: " +  df.format(duration) + " secs");
    }
    catch (InvalidMidiDataException e) {
      System.out.println("Unreadable/unsupported midi file: " + fnm);
      System.exit(0);
    }
    catch (IOException e) {
      System.out.println("Could not read: " + fnm);
      System.exit(0);
    }
    catch (Exception e) {
      System.out.println("Problem with " + fnm);
      System.exit(0);
    }
  } // end of loadMidi()



  private void play()
  { 
    if ((sequencer != null) && (seq != null)) {
      try {
        sequencer.setSequence(seq);  // load MIDI into sequencer
        sequencer.start();   // start playing it
      }
      catch (InvalidMidiDataException e) {
        System.out.println("Corrupted/invalid midi file: " + filename);
        System.exit(0);
      }
    }
  } // end of play()



  public void meta(MetaMessage event) 
  /* Meta-events trigger this method. 
     The end-of-track meta-event signals that the sequence has finished
  */
  { if (event.getType() == END_OF_TRACK) {
      System.out.println("Exiting...");
      close();
      System.exit(0);    // necessary in J2SE 1.4.2 and earlier
    }
  } // end of meta()


  private void close()
  // Close down the sequencer and synthesizer
  {
    if (sequencer != null) {
      if (sequencer.isRunning())
        sequencer.stop();
       
      sequencer.removeMetaEventListener(this);
      sequencer.close();

      if (synthesizer != null)
        synthesizer.close();
    }
  }  // end of close()



  // ----------------------------------------------

  public static void main(String[] args) 
  {
    if (args.length != 1) {
      System.out.println("Usage: java PlayMidi <midi file>");
      System.exit(0);
    }
    new PlayMidi(args[0]);
    System.exit(0);    // required in J2SE 1.4.2. or earlier
  } // end of main()

} // end of PlayMidi class
