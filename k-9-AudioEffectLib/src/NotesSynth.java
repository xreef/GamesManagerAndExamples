
// NotesSynth.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* NotesSynth opens a stereo, signed PCM output line, using 16 bits per
   sample in little endian format. Four bytes will be used for each 
   sample/frame.

   sendNote() generates a note at a given frequency and amplitude.
   The note is actually a sine wave with that frequency and amplitude,
   stored as samples in a byte buffer. The buffer is emptied down
   the SourceDataLine in order to play the note.

   play() contains a simple example of how to use sendNote():
     - an increasing pitch sequence is generated, which repeats 9 times, 
       each time increasing a bit faster, with the volume decreasing

   sendNote() can be used to generate simple sounds at run time in
   a game without the need for loading/playing a clip.
*/

import java.io.*;
import javax.sound.sampled.*;


public class NotesSynth  
{
  private static int SAMPLE_RATE = 22050;       // number of samples /sec
  private static double MAX_AMPLITUDE = 32760;     // max loudness of a note
                 // actual max is 2^15-1, 32767, since we are using
                 // PCM signed 16 bit

  // frequence (pitch) range for the notes
  private static int MIN_FREQ = 250;
  private static int MAX_FREQ = 2000;
    /* Middle C (C4) has a frequency of 262 Hz, 
       doubling with each octave increase (C5, C6, ...),
       halving with each octave decrease (C3, C2 ...)
       See http://www.phys.unsw.edu.au/~jw/notes.html for a table.
    */


  private static AudioFormat format = null;
  private static SourceDataLine line = null;


  public static void main(String[] args) 
  { createOutput();
    play();
    System.exit(0);    // necessary for J2SE 1.4.2 or earlier
  } // end of main()


  private static void createOutput()
  // create a SourceDataLine for outputing the generated notes
  {
    /* Create a stereo, signed PCM output line, using 16 bits per sample in
       little endian format. Four bytes will be used for each sample/frame. */

    format =  new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                              SAMPLE_RATE, 16, 2, 4, SAMPLE_RATE, false);
   /*  SAMPLE_RATE   // samples/sec
       16            // sample size in bits, values can be -2^15 -- 2^15-1
       2             // no. of channels, stereo here
       4             // frame size in bytes (2 bytes/sample * 2 channels)
       SAMPLE_RATE   // same as frames/sec
       false         // little endian
    */

    System.out.println("Audio format: " + format);

    try {
      DataLine.Info info =
            new DataLine.Info(SourceDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line does not support: " + format);
        System.exit(0);
      }
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(format);
    }
    catch (Exception e)
    {  System.out.println( e.getMessage());  
       System.exit(0);
    }
  }  // end of createOutput()


  private static void play()
  // play notes through the SourceDataLine
  {
    // calculate a size for the byte buffer holding a note
    int maxSize = (int) Math.round(
                      (SAMPLE_RATE * format.getFrameSize())/ MIN_FREQ );   
        // the frame size is 4 bytes
    byte[] samples = new byte[maxSize];

    line.start();

    /* Generate an increasing pitch sequence, repeated 9 times, each
       time increasing a bit faster, and the volume decreasing */
    double volume;
    for (int step = 1; step < 10; step++)
      for (int freq = MIN_FREQ; freq < MAX_FREQ; freq += step) {
         volume = 1.0 - (step/10.0);
         sendNote(freq, volume, samples);
      }

    // wait until all data is played, then close the line
    line.drain();
    line.stop();
    line.close();
  }  // end of play()


  private static void sendNote(int freq, double volLevel, byte[] samples)
  /* We generate a single sine wave of the required amplitude and 
     frequency, equivalent to a single note.

     The pitch of the sound (how high the note is) depends on the 
     frequency of the wave. The higher the frequency, the higher the pitch. 

     Each note is 16 bit, little endian, stereo -- matching the format
  */
  { if ((volLevel < 0.0) || (volLevel > 1.0)) {
      System.out.println("Volume level should be between 0 and 1, using 0.9");
      volLevel = 0.9;
    }
    double amplitude = volLevel * MAX_AMPLITUDE;

    int numSamplesInWave = (int) Math.round( ((double) SAMPLE_RATE)/freq );
    int idx = 0;
    for (int i = 0; i < numSamplesInWave; i++) {
      double sine = Math.sin(((double) i / numSamplesInWave) * 2.0 * Math.PI);
      int sample = (int) (sine * amplitude);
      // left sample of stereo
      samples[idx + 0] = (byte) (sample & 0xFF);         // low byte
      samples[idx + 1] = (byte) ((sample >> 8) & 0xFF);  // high byte
      // right sample of stereo (identical to left)
      samples[idx + 2] = (byte) (sample & 0xFF);
      samples[idx + 3] = (byte) ((sample >> 8) & 0xFF);
      idx += 4;
    }

    // send out the samples (the single note)
    // line.write(samples, 0, idx);
    int offset = 0;
    while (offset < idx)
      offset += line.write(samples, offset, idx-offset);
  }  // end of sendNote()


} // end of NotesSynth class