
// PlaceClip.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Load an audio file as a clip, and play it once.
   Its termination is detected by update() being called. 

   The command line can also include voluem and pan settings:
        java PlaceClip <clip file> [ <volume> [<pan>] ]

   Volume must be between 0.0f and 1.0f (loudest), or 
   NO_VOL_CHANGE (-1.0f).

   Pan must be between -1.0f and 1.0f (left to right speakers), 
   or NO_PAN_CHANGE (0.0f).

   In J2SE 1.5, the BALANCE control is available, which works like PAN
   (FloatControl.Type.BALANCE). PAN is still around, but may not work
   for stereo signals.

   PlaceClip is an extended version of PlayClip. The changes are
   in the extra methods for reading the volume and pan settings
   from the command line, and the methods for adjusting the
   clip controls: setVolume() and setPan(). showControls() lists
   the available controls for a clip (which depends on the
   clip and mixer).

   Changes 6th August 2004
     - measure time of clip using getMicrosecondLength() in loadClip()
     - closed the input stream in loadClip()

   Changes 16th September 2004
     - bug: if the supplied WAV file is less than a second long then 
            no sound is played. See the bug report at 
            http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5085008

     Solution: edit the sound to be > 1 sec in length
        We call checkDuration() from loadClip() to check if 
        the clip duration is <= 1 sec

*/

import java.io.*;
import javax.sound.sampled.*;
import java.text.DecimalFormat;



public class PlaceClip implements LineListener
{
  private final static String SOUND_DIR = "Sounds/";

  private final static float NO_VOL_CHANGE = -1.0f;
  private final static float NO_PAN_CHANGE = 0.0f;

  private Clip clip = null;
  private AudioFormat format;
  private DecimalFormat df; 

  private float volume, pan;   // settings supplied on the command line



  public PlaceClip(String[] args)
  {
    df = new DecimalFormat("0.#");  // 1 dp

    getSettings(args);   // get the volume and pan settings
                         // from the command line
    loadClip(SOUND_DIR + args[0]);

    // clip control methods 
    showControls();
    setVolume(volume);
    setPan(pan);

    play();

    // wait for the sound to finish playing; guess at 10 mins!
    try { 
      Thread.sleep(600000);   // 10 mins in ms
    } 
    catch(InterruptedException e) 
    { System.out.println("Sleep Interrupted"); }
  } // end of PlaceClip()


  private void getSettings(String[] args)
  /* The format for the command line is:
        java PlaceClip <clip file> [ <volume> [<pan>] ]

     so we have to do some work to extract the volume and pan
     settings.
  */
  {
    if (args.length == 1) {
      volume = NO_VOL_CHANGE;
      pan = NO_PAN_CHANGE;
      System.out.println("No volume or pan settings supplied");
    }
    else if (args.length == 2) {
      getVolumeSetting(args[1]);
      pan = NO_PAN_CHANGE;
      System.out.println("No pan setting supplied");
    }
    else if (args.length == 3) {
      getVolumeSetting(args[1]);
      getPanSetting(args[2]);
    }
    else {
      System.out.println("Usage: java PlaceClip <clip file> [ <volume> [<pan>] ]");
      System.exit(0);
    }
  }  // end of getSettings()


  private void getVolumeSetting(String volStr)
  {
    try {
      volume = Float.parseFloat(volStr);
    }
    catch (NumberFormatException ex){ 
      System.out.println("Incorrect volume format"); 
      volume = NO_VOL_CHANGE;
    }
    if (volume == NO_VOL_CHANGE)
      System.out.println("No volume change"); 
    else if ((volume >= 0.0f) && (volume <= 1.0f))
      System.out.println("Volume setting: " + volume); 
    else {
      System.out.println("Volume out of range (0-1.0f); volume not being changed"); 
      volume = NO_VOL_CHANGE;
    }
  }  // end of getVolumeSetting()


  private void getPanSetting(String panStr)
  {
    try {
      pan = Float.parseFloat(panStr);
    }
    catch (NumberFormatException ex){ 
      System.out.println("Incorrect pan format"); 
      pan = NO_PAN_CHANGE;
    }
    if (pan == NO_PAN_CHANGE)
      System.out.println("No pan change"); 
    else if ((pan >= -1.0f) && (pan <= 1.0f))
      System.out.println("Pan setting: " + pan); 
    else {
      System.out.println("Pan out of range (-1.0f - 1.0f); pan not being changed"); 
      pan = NO_PAN_CHANGE;
    }
  }  // end of getPanSetting()


  private void loadClip(String fnm)
  {
    try {
      // link an audio stream to the sound clip's file
      AudioInputStream stream = AudioSystem.getAudioInputStream(
                          getClass().getResource(fnm) );

      format = stream.getFormat();    // format is a global since it's used in setPan()
      System.out.println("Audio format: " + format);

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
        System.exit(0);
      }

      // get clip line resource
      clip = (Clip) AudioSystem.getLine(info);

      // listen to clip for events
      clip.addLineListener(this);

      clip.open(stream);    // open the sound file as a clip
      stream.close(); // we're done with the input stream  // new

      checkDuration();
    } // end of try block

    catch (UnsupportedAudioFileException audioException) {
      System.out.println("Unsupported audio file: " + fnm);
      System.exit(0);
    }
    catch (LineUnavailableException noLineException) {
      System.out.println("No audio line available for : " + fnm);
        System.exit(0);
    }
    catch (IOException ioException) {
      System.out.println("Could not read: " + fnm);
      System.exit(0);
    }
    catch (Exception e) {
      System.out.println("Problem with " + fnm);
      System.exit(0);
    }
  } // end of loadClip()


  private void checkDuration()
  {
    // duration (in secs) of the clip
	double duration = clip.getMicrosecondLength()/1000000.0;  // new
    if (duration <= 1.0) {
      System.out.println("WARNING. Duration <= 1 sec : " + df.format(duration) + " secs");
      System.out.println("         The clip may not play in J2SE 1.5 -- make it longer");
    }
    else
      System.out.println("Duration: " + df.format(duration) + " secs");
  }  // end of checkDuration()



  private void showControls()
  // display all the controls available for this clip
  {
    if (clip != null) {
      Control cntls[] = clip.getControls(); 
      for(int i=0; i<cntls.length; i++)
        System.out.println( i + ".  " + cntls[i].toString() );
    }
  }  // end of showControls()


  private void setVolume(float volume)
  /* Volume is between 0.0f and 1.0f (loudest), or NO_VOL_CHANGE (-1.0f)
     The gain is calculated using a linear scale, although it
     would be better to use a logarithmic scale since this is
     how gain is expressed in decibels.
  */
  {
    if ((clip != null) && (volume != NO_VOL_CHANGE)) {
      if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
        FloatControl gainControl =
           (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        System.out.println("Volume: " + volume + "; New gain: " + gain);
        gainControl.setValue(gain);
      }
      else
        System.out.println("No Volume controls available");
    }
  }  // end of setVolume()


  private void setPan(float pan)
  /* pan is between -1.0f and 1.0f (left to right speaker), 
     or NO_PAN_CHANGE (0.0f).

     If panning isn't available, we try using a balance control.

     The default mixer in J2SE 1.5 doesn't support panning, 
     but has a balance control. However, the control will only be 
     available if the input is in stereo.
  */
  {
    if ((clip == null) || (pan == NO_PAN_CHANGE))
      return;   // do nothing

    if (clip.isControlSupported(FloatControl.Type.PAN)) {
      FloatControl panControl =
         (FloatControl) clip.getControl(FloatControl.Type.PAN);
      // System.out.println("Pan range: " + pan);
      panControl.setValue(pan);
    }
    else if (clip.isControlSupported(FloatControl.Type.BALANCE)) {
      FloatControl balControl =
         (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
      // System.out.println("balance range: " + pan);
      balControl.setValue(pan);
    }
    else {
      System.out.println("No Pan or Balance controls available");
      if (format.getChannels() == 1)   // mono input
        System.out.println("Your audio file is mono; try converting it to stereo");
    }
  }  // end of setPan()



  private void play()
  { if (clip != null)
      clip.start();   // start playing
  }


  public void update(LineEvent lineEvent)
  // called when the clip's line detects open, close, start, stop events
  {
    // the clip has reaches its end 
    if (lineEvent.getType() == LineEvent.Type.STOP) {
      // System.out.println("Exiting...");
      clip.stop();
      lineEvent.getLine().close();
      System.exit(0);    // necessary in J2SE 1.4.2 and earlier
    }
  } // end of update()


  // --------------------------------------------------

  public static void main(String[] args)
  {  new PlaceClip(args);  
     System.exit(0);    // required in J2SE 1.4.2. or earlier
  }

} // end of PlaceClip.java
