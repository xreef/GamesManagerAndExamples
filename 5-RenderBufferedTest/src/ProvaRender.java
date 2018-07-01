import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class ProvaRender extends Thread {
	boolean doRender = true;
	boolean pauseRender = false;
	long renderLoopSpeed = 60;

	
	public void run() {
		boolean contentsLost = false;
		try {
			// get the buffer strategy that we set up
			BufferStrategy bs = getBufferStrategy();
			// Stay in the render thread until it is stopped
			while (doRender) {
				// if paused then don’t do anything
				if (!pauseRender) {
					// perform the rendering for the new frame
					doRender();
					// Because the buffer strategy may have used volatile
					// images to speed things up make sure that
					// the frame buffer just rendered has not
					// been corrupted or lost
					if (bs.contentsLost())
						contentsLost = true;
					// If the buffer has been corrupted or lost then we
					// don’t want to show the contents of it yet. We
					// will go through the loop again and redraw the frame.
					if (contentsLost) {
						if (bs.contentsRestored())
							contentsLost = false;
					} else {
						// Things are okay,swap the buffers to show the frame
						bs.show();
					}
				}

				// Sleep for a bit and keep the frame rate static.
				Thread.sleep(renderLoopSpeed);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public boolean doRender() {
		boolean shouldSwap = render(getBufferStrategy().getDrawGraphics());
		return shouldSwap;
	}

	private boolean render(Graphics drawGraphics) {
		// TODO Auto-generated method stub
		return false;
	}

	private BufferStrategy getBufferStrategy() {
		// TODO Auto-generated method stub
		return null;
	}
}
