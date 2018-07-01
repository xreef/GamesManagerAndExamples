package it.reef.manage;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class AudioManager {
	public static AudioFormat getAudioFormat(File f) {
		AudioInputStream stream = null;
		try {
			stream = AudioSystem.getAudioInputStream(f);
		} catch (UnsupportedAudioFileException e) {
			System.out.println("Unsupported audio file: " + f.getName());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("Could not read: " + f.getName());
			e.printStackTrace();
			return null;
		}

		AudioFormat format = stream.getFormat();

		return format;
	}

	public static AudioFormat convertULAWALAWtoPCM(AudioFormat format,
			AudioInputStream stream) throws Exception {
		// convert ULAW/ALAW formats to PCM format
		if ((format.getEncoding() == AudioFormat.Encoding.ULAW)
				|| (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
			AudioFormat newFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(),
					format.getSampleSizeInBits() * 2, format.getChannels(),
					format.getFrameSize() * 2, format.getFrameRate(), true); // big
			// endian
			// update stream and format details
			stream = AudioSystem.getAudioInputStream(newFormat, stream);
			System.out.println("Converted Audio format: " + newFormat);
			// format = newFormat;
			return newFormat;
		}
		return format;
	}

	public static Clip getClip(DataLine.Info info, AudioInputStream stream)
			throws LineUnavailableException, IOException {
		final Clip clip = (Clip) AudioSystem.getLine(info);

		// listen to clip for events
		clip.addLineListener(new LineListener() {
			public void update(LineEvent lineEvent)
			// called when the clip's line detects open, close, start,
			// stop events
			{
				// has the clip has reached its end?
				if (lineEvent.getType() == LineEvent.Type.STOP) {
					System.out.println("Exiting...");
					clip.stop();
					clip.setFramePosition(0);

					// lineEvent.getLine().close();
					// System.exit(0);
				}
			} // end of update()
		});

		clip.open(stream); // open the sound file as a clip
		stream.close(); // we're done with the input stream // new

		clip.setFramePosition(0);

		return clip;
	}

	public static Clip getClip(File f) throws UnsupportedAudioFileException,
			IOException, LineUnavailableException, Exception {
		// link an audio stream to the sound clip's file
		AudioInputStream stream = AudioSystem.getAudioInputStream(f);
		AudioFormat format = stream.getFormat();

		format = AudioManager.convertULAWALAWtoPCM(format, stream);

		DataLine.Info info = new DataLine.Info(Clip.class, format);

		// make sure sound system supports data line
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Unsupported Clip File: " + format);
		} else {

			Clip clip = AudioManager.getClip(info, stream);

			return clip;
		}
		return null;
	}

	/**
	 * Crea l'Output da dare in pasto al JVM Mixer
	 * 
	 * @return
	 * @throws LineUnavailableException
	 *             , Exception
	 */
	public static SourceDataLine createOutput(AudioFormat format,
			DataLine.Info info) throws LineUnavailableException, Exception
	// set up the SourceDataLine going to the JVM's mixer
	{
		SourceDataLine line;

		// get a line of the required format
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format);

		return line;
	} // end of createOutput()

	public static void playLine(SourceDataLine line, AudioInputStream stream)
	/*
	 * Read the sound file in chunks of bytes into buffer, and pass them on
	 * through the SourceDataLine
	 */
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
					offset += line.write(buffer, offset, numRead - offset);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		// wait until all data is played, then close the line
		// System.out.println("drained start");
		line.drain();
		// System.out.println("drained end");
		line.stop();
		line.close();
	} // end of play()

	private static final int END_OF_TRACK = 47;

	private static Sequencer obtainSequencer()
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
	
	public static Sequencer initSequencer() throws MidiUnavailableException {
		

			Sequencer sequencer = obtainSequencer();
				// MidiSystem.getSequencer();

			if (sequencer == null) {
				System.out.println("Cannot get a sequencer");
				System.exit(0);
			}

			sequencer.open();
			
			
			sequencer.addMetaEventListener(new AudioManager().new MetaEventPersonal(sequencer));
			Synthesizer synthesizer;
			// maybe the sequencer is not the same as the synthesizer
			// so link sequencer --> synth (this is required in J2SE 5.0)
			if (!(sequencer instanceof Synthesizer)) {
				System.out.println("Linking the sequencer to a synthesizer");
				synthesizer = MidiSystem.getSynthesizer();
				synthesizer.open();
				Receiver synthReceiver = synthesizer.getReceiver();
				Transmitter seqTransmitter = sequencer.getTransmitter();
				seqTransmitter.setReceiver(synthReceiver);
			} else
				synthesizer = (Synthesizer) sequencer;
			// I don't use the synthesizer in this simple code,
			// so storing it as a global isn't really necessary
			return sequencer;

	} // end of initSequencer( )

	private void close(Sequencer sequencer)
	// Close down the sequencer and synthesizer
	{
		if (sequencer != null) {
			if (sequencer.isRunning())
				sequencer.stop();

			// sequencer.removeMetaEventListener(MetaEventPersonal);
			sequencer.close();

		}
	} // end of close()

	class MetaEventPersonal implements MetaEventListener {
		Sequencer sequencer;

		public MetaEventPersonal(Sequencer sequencer) {
			this.sequencer = sequencer;
		}

		public void meta(MetaMessage event) {
			/*
			 * Meta-events trigger this method. The end-of-track meta-event
			 * signals that the sequence has finished
			 */
			{
				if (event.getType() == END_OF_TRACK) {
					System.out.println("Exiting...");
					close(sequencer);
					// System.exit(0); // necessary in J2SE 1.4.2 and earlier
				}
			} // end of meta()

		}

	}
	

}
