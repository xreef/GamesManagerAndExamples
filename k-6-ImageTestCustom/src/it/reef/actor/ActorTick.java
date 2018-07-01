package it.reef.actor;

import it.reef.interf.Cyclical;
import it.reef.interf.Updatable;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 * 
 * Actor width synchronized stepper 
 * @author Renzo Mischianti
 *
 */
public class ActorTick extends Actor implements Updatable, Cyclical {

	protected ImageObserver io;
	
	// isRepeating: true if the cycle is forever
	// imageUpdateIgnore: true is the cycle is stopped
	private boolean isRepeating, imageUpdateIgnore;

	// period of update
	private long animationPeriod;
	// period passed by animation loop (in ms)
	private long animationTotalTime;
	// period from a tick to another tick (period the current image is shown (in
	// ms))
	private int showPeriod;
	// total duration of the entire image sequence (in secs)
	private double seqDuration;
	// step of cycle
	private int step;

	/**
	 * Prepare all variable for cycle
	 * 
	 * @param period
	 *            : ms. of frame
	 * @param duration
	 *            : total duration duration of cycle in sec.
	 * @param step
	 *            : total step (tick) of cycle
	 * @param repeat
	 *            : true repeat cycle forever
	 */
	protected void initImageTick(long period, double duration, int step,
			boolean repeat) {
		this.step = step;

		if (duration < 0.5) {
			System.out
					.println("Warning: minimum sequence duration is 0.5 sec.");
			seqDuration = 0.5;
		} else {
			seqDuration = duration;
		}

		animationPeriod = period / 1000000;

		isRepeating = repeat;
		animationTotalTime = 0L;

		tickStep = 0;
		imageUpdateIgnore = false;
		showPeriod = (int) (1000 * seqDuration / step);
	}

	// Position of cicle
	protected int tickStep = 0;
	private int lastTickStep = 0;

	/**
	 * Calculate the tickStep (position of cycle)
	 */
	protected void imageTick() {
		if (!imageUpdateIgnore) {
			// update total animation time, modulo the animation sequence duration
			animationTotalTime = (animationTotalTime + animationPeriod) % (long) (1000 * seqDuration);

			// calculate current displayable image position
			tickStep = (int) (animationTotalTime / showPeriod); // in range 0 to num-1
			if (tickStep >= step) tickStep = step - 1;

			if (((tickStep == step - 1) || tickStep < lastTickStep) && (!isRepeating)) { // at end of sequence
				imageUpdateIgnore = true; // stop at this image
			}
			lastTickStep = tickStep;
		}
	}

	/**
	 * Start cycle again, starting with step imPosn. This requires a resetting
	 * of the animation time as well.
	 */
	public void restartAt(int imPosn)

	{
		if (step != 0) {
			if ((imPosn < 0) || (imPosn > step - 1)) {
				System.out.println("Out of range restart, starting at 0");
				imPosn = 0;
			}

			tickStep = imPosn;
			// calculate a suitable animation time
			animationTotalTime = (long) tickStep * showPeriod;
			lastTickStep = 0;
			imageUpdateIgnore = false;
		}
	} // end of restartAt()

	/**
	 * Start cycle again, starting with step 0. This requires a resetting of the
	 * animation time as well.
	 */
	public void resume()
	// start at previous image position
	{
		if (step != 0)
			animationTotalTime = 0L;
		imageUpdateIgnore = false;
		lastTickStep = 0;
	}

	public void draw(Graphics g) {
		// TODO Auto-generated method stub

	}

	/**
	 * update() width imageTick() inside.
	 */
	public void update() {
		imageTick();
	}

}
