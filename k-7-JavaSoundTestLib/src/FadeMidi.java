
// FadeMidi.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Play a Midi sequence, allowing its volume to be adjusted
   while it is being played. In this example, the volume is
   gradually reduced to 0 during the course of the playing.

   The volume is manipulated by calls to the channel controls
   of the synthesizer, in particular to control 7 (VOLUME_CONTROLLER), 
   to control the volume setting of a channel.

   The volume manipulation is done by a separate VolChanger thread,
   set up in main(). A call to FadeMidi's startVolChanger() starts
   the volume changing.
  
   FadeMidi (and PanMidi) are extended versions of PlayMidi.

   Change 6th August 2004
     - added synthesizer.open() to initSequencer()

   Change 16th September 2004
     - got sequencer by explicitly requesting it in obtainSequencer()

*/

import java.io.*;
import javax.sound.midi.*;
import java.text.DecimalFormat;



public class FadeMidi implements MetaEventListener 
{
  // midi meta-event constant used to signal the end of a track 
  private static final int END_OF_TRACK = 47;

  private static final int VOLUME_CONTROLLER = 7;

  private final static String SOUND_DIR = "Sounds/";


  private Sequencer sequencer;
  private Synthesizer synthesizer;
  private Sequence seq;
  private String filename;

  private DecimalFormat df; 

  // holds the synthesizer's channels
  private MidiChannel[] channels;


  public FadeMidi(String fnm) 
  {
    df = new DecimalFormat("0.#");  // 1 dp

    filename = SOUND_DIR + fnm;
    initSequencer();
    loadMidi(filename);
    play();

    /* No need for sleeping to keep the object alive, since
       the VolChanger thread refers to it. */
  }  // end of FadeMidi()


  private void initSequencer() 
  /* Set up the MIDI sequencer, the sequencer's meta-event
     listener, and its synthesizer. */
  {
    try {
      sequencer = obtainSequencer();

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


  private Sequencer obtainSequencer()
  /* This method handles a bug in J2SE 1.5.0 which retrieves
     the sequencer with getSequencer() but does not allow
     its volume to be changed. */
  {
    // return MidiSystem.getSequencer();   
    // okay in J2SE 1.4.2, but not in J2SE 1.5.0

    MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();
    int seqPosn = -1;
    for(int i=0; i < mdi.length; i++) {
      System.out.println(mdi[i].getName());
      // if (mdi[i].getName().contains("Sequencer")) {
      if (mdi[i].getName().indexOf("Sequencer") != -1) {
        seqPosn = i;    // found the Sequencer
        System.out.println("  Found Sequencer");
      }
    }

    try {
      if (seqPosn != -1)
        return (Sequencer) MidiSystem.getMidiDevice( mdi[seqPosn] );
      else
        return null;
    }
    catch(MidiUnavailableException e)
    { return null; }
  }  // end of obtainSequencer()


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
        // showChannelVolumes();
      }
      catch (InvalidMidiDataException e) {
        System.out.println("Corrupted/invalid midi file: " + filename);
        System.exit(0);
      }
    }
  } // end of play()


  private void showChannelVolumes()
  // show the volume levels for all the synthesizer channels
  {
    System.out.println("Synthesizer Channels: " + channels.length);

    System.out.print("Volumes: {");
    for (int i=0; i < channels.length; i++)
      System.out.print( channels[i].getController(VOLUME_CONTROLLER) + " ");
    System.out.println("}");
  }  // end of showChannelVolumes()


  public void meta(MetaMessage event) 
  /* Meta-events trigger this method. 
     The end-of-track meta-event signals that the sequence has finished
  */
  { if (event.getType() == END_OF_TRACK) {
      System.out.println("Exiting");
      close();
      System.exit(0);   // necessary in J2SE 1.4.2 and earlier
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

  
  // ------------------- Volume Changer methods ------------------

  public void startVolChanger(VolChanger vc)
  /* Start the volume changing thread running, and supply the
     duration of the MIDI sequence in ms. 
     The duration is needed to estimate how often to change the volume.
  */
  {  vc.startChanging( (int)(seq.getMicrosecondLength()/1000) );  }


  public int getMaxVolume()
  // return the max level for all the volume controllers
  {  
    int maxVol = 0;
    int channelVol;
    for (int i=0; i < channels.length; i++) {
      channelVol = channels[i].getController(VOLUME_CONTROLLER);
      if (maxVol < channelVol)
        maxVol = channelVol;
    }
    return maxVol; 
 } // end of getVolume()


  public void setVolume(int vol)
  // set all the controller's volume levels to vol
  {      
    for (int i=0; i < channels.length; i++)
      channels[i].controlChange(VOLUME_CONTROLLER, vol);

    // showChannelVolumes();
  }

  // ----------------------------------------------

  public static void main(String[] args) 
  {
	  args=new String[]{"bsheep.mid"};
    if (args.length != 1) {
      System.out.println("Usage: java FadeMidi <midi file>");
      System.exit(0);
    }
    // set up the player and the volume changer
    FadeMidi player = new FadeMidi(args[0]);
    VolChanger vc = new VolChanger(player);

    player.startVolChanger(vc);  // start volume manipulation
  } // end of main()

} // end of FadeMidi class
