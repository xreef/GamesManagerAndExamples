
// BufferedPlayer.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Read the sound file as chunks of bytes into a buffer
   via an AudioInputStream, then pass them on to the 
   SourceDataLine. 

   This approach does not require all of the sound file to
   be in memory at the same time.

   I've coded this in a C-style, as a series of static methods
   and static globals. But the approach works inside classes/objects
   as well.

   Changes 16th September
     bug: will not play short WAV files, in similar way to PlayClip.java
     - added checkDuration() to report on length of sound file
 */


import java.io.*;
import javax.sound.sampled.*;


public class BufferedPlayer  
{
  private static AudioInputStream stream;
  private static AudioFormat format = null;
  private static SourceDataLine line = null;


  public static void main(String[] args) 
  {
	  args=new String[]{"spacemusic.au"};
    if (args.length != 1) {
      System.out.println("Usage: java BufferedPlayer <clip file>");
      System.exit(0);
    }

    createInput("/home/reef/progetti/workspace-games-test/k-7-JavaSoundTestLib/src/Sounds/" + args[0]);
    createOutput();

    int numBytes = (int)(stream.getFrameLength() * format.getFrameSize());
         // use getFrameLength() from the stream, since the format 
         // version may return -1 (WAV file formats always return -1)
    System.out.println("Size in bytes: " + numBytes);

    checkDuration();
    play();

    System.exit(0);   // necessary in J2SE 1.4.2 and earlier
  } // end of main()


  private static void checkDuration()
  {
    long milliseconds =  (long)((stream.getFrameLength() * 1000) / 
                                stream.getFormat().getFrameRate());
    double duration = milliseconds / 1000.0;
    if (duration <= 1.0) {
      System.out.println("WARNING. Duration <= 1 sec : " + duration + " secs");
      System.out.println("         The sample may not play in J2SE 1.5 -- make it longer");
    }
    else
      System.out.println("Duration: " + duration + " secs");
  }  // end of checkDuration()


  private static void createInput(String fnm)
  // Set up the audio input stream from the sound file
  {
    try {
      // link an audio stream to the sampled sound's file
      stream = AudioSystem.getAudioInputStream( new File(fnm) );
      format = stream.getFormat();
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
    }
    catch (UnsupportedAudioFileException e) 
    {  System.out.println( e.getMessage());  
       System.exit(0);
    }
    catch (IOException e) 
    {  System.out.println( e.getMessage());  
       System.exit(0);
    }
  }  // end of createInput()


  private static void createOutput()
  // set up the SourceDataLine going to the JVM's mixer
  {
    try {
      // gather information for line creation
      DataLine.Info info =
            new DataLine.Info(SourceDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line does not support: " + format);
        System.exit(0);
      }
      // get a line of the required format
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(format); 
    }
    catch (Exception e)
    {  System.out.println( e.getMessage());  
       System.exit(0);
    }
  }  // end of createOutput()


  private static void play()
  /* Read  the sound file in chunks of bytes into buffer, and
     pass them on through the SourceDataLine */
  {
    int numRead = 0;
    byte[] buffer = new byte[line.getBufferSize()];

    line.start();
    // read and play chunks of the audio
    try {
      int offset;
      while ((numRead = stream.read(buffer, 0, buffer.length)) >= 0) {
        // System.out.println("read: " + numRead);
        offset = 0;
        while (offset < numRead)
          offset += line.write(buffer, offset, numRead-offset);
      }
    }
    catch (IOException e) 
    {  System.out.println( e.getMessage()); }

    // wait until all data is played, then close the line
    // System.out.println("drained start");
    line.drain();
    // System.out.println("drained end");
    line.stop();
    line.close();
  }  // end of play()

  
} // end of BufferedPlayer class