package it.reef.manage;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public class AudioLineThread extends Thread {
	SourceDataLine line;
	AudioInputStream stream;
	
	public AudioLineThread(SourceDataLine line, AudioInputStream stream){
		this.line=line;
		this.stream=stream;
	}
	
	public void run() {
		
			if (line != null) {
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
		

	}
	
	
}
