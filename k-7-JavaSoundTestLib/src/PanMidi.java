
// PanMidi.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Play a Midi sequence, allowing its pan settings to be adjusted
   while it is being played. In this example, the pan is constantly
   switched from left speaker full on/no right speaker to right
   speaker full on/no left speaker.

   The pan settings are manipulated by calls to the channel controls
   of the synthesizer, in particular to control 10 (PAN_CONTROLLER), 
   to control the pan setting of a channel. 

   An alternative should be to use the balance control (BALANCE_CONTROLLER), 
   but it didn't work on my test machines.

   The pan manipulation is done by a separate PanChanger thread,
   set up in main(). A call to PanMidi's startPanChanger() starts
   the pan settings changing.

   FadeMidi (and PanMidi) are extended versions of PlayMidi.

   Change 6th August 2004
     - added synthesizer.open() to initSequencer()
*/

import java.io.*;
import javax.sound.midi.*;
import java.text.DecimalFormat;


public class PanMidi implements MetaEventListener 
{
  // private static final int BALANCE_CONTROLLER = 8;   // not working (?)
  private static final int PAN_CONTROLLER = 10;

  // midi meta event constant used to signal the end of a track 
  private static final int END_OF_TRACK = 47;

  private final static String SOUND_DIR = "Sounds/";


  private Sequencer sequencer;
  private Synthesizer synthesizer;
  private Sequence seq;
  private String filename;
  private DecimalFormat df; 

  // holds the synthesizer's channels
  private MidiChannel[] channels;


  public PanMidi(String fnm) 
  {
    df = new DecimalFormat("0.#");  // 1 dp

    filename = SOUND_DIR + fnm;
    initSequencer();
    loadMidi(filename);
    play();

    /* No need for sleeping to keep the object alive, since
       the PanChanger thread refers to it. */
  }  // end of PanMidi()


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
        System.out.println("Linking the MIDI sequencer and synthesizer");
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();  // new
        Receiver synthReceiver = synthesizer.getReceiver();
        Transmitter seqTransmitter = sequencer.getTransmitter();
        seqTransmitter.setReceiver(synthReceiver);
      }
      else 
        synthesizer = (Synthesizer) sequencer;
    }
    catch (MidiUnavailableException e){
      System.out.println("No sequencer available");
      System.exit(0);
    }
  } // end of initSequencer()


  private void loadMidi(String fnm)
  { seq = null;
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
        sequencer.start();   // play it
        channels = synthesizer.getChannels();
        // showChannelPans();
      }
      catch (InvalidMidiDataException e) {
        System.out.println("Corrupted/invalid midi file: " + filename);
        System.exit(0);
      }
    }
  } // end of play()


  private void showChannelPans()
  // show the pan levels for all the synthesizer channels
  {
    System.out.println("Syntheziser Channels: " + channels.length);

    System.out.print("Pans: {");
    for (int i=0; i < channels.length; i++)
      System.out.print( channels[i].getController(PAN_CONTROLLER) + " ");
    System.out.println("}");
  }  // end of showChannelPans()


  public void meta(MetaMessage event) 
  /* Meta-events trigger this method. 
     The end-of-track meta-event signals that the sequence has finished
  */
  { if (event.getType() == END_OF_TRACK) {
      System.out.println("Exiting");
      close();
      System.exit(0);  // necessary in J2SE 1.4.2 and earlier
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

  
  // ------------------- Pan Changer methods ------------------


  public void startPanChanger(PanChanger pc)
  /* Start the volume changing thread running, and supply the
     duration of the MIDI sequence in ms. 
     The duration is needed to estimate how often to change the pan setting.
  */
  {  pc.startChanging( (int)(seq.getMicrosecondLength()/1000) );  }


  public int getMaxPan()
  // return the max value for all the pan controllers
  {  
    int maxPan = 0;
    int channelPan;
    for (int i=0; i < channels.length; i++) {
      channelPan = channels[i].getController(PAN_CONTROLLER);
      if (maxPan < channelPan)
        maxPan = channelPan;
    }
    return maxPan; 
 } // end of getMaxPan()


  public void setPan(int panVal)
  // set all the controller's pan levels to panVal
  {      
    for (int i=0; i < channels.length; i++)
      channels[i].controlChange(PAN_CONTROLLER, panVal);
  }

  // ----------------------------------------------

  public static void main(String[] args) 
  {
    if (args.length != 1) {
      System.out.println("Usage: java PanMidi <midi file>");
      System.exit(0);
    }
    // set up the player and the pan changer
    PanMidi player = new PanMidi(args[0]);
    PanChanger pc = new PanChanger(player);

    player.startPanChanger(pc);  // start pan manipulation
  } // end of main()

} // end of PanMidi class
