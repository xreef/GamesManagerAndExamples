package it.reef.managers.images.base;

import it.reef.managers.images.interfaces.ImageAccess;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LoopingImage implements ImageAccess {

	ArrayList<BufferedImage> imageSprite;

	private boolean isRepeating, imageUpdateIgnore;
	private long animationPeriod;
	// period used by animation loop (in ms)
	private long animationTotalTime;

	private int showPeriod;
	// period the current image is shown (in ms)
	private double seqDuration;

	// total duration of the entire image sequence (in secs)

	public LoopingImage(ArrayList<BufferedImage> is, long period,
			double duration, boolean repeat) {
		this.imageSprite = is;
		// this.width = imageSprite.get(0).getWidth();
		// this.heigth = imageSprite.get(0).getHeight();

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

		imagePointer = 0;
		imageUpdateIgnore = false;
		showPeriod = (int) (1000 * seqDuration / is.size());

	}

	public BufferedImage getCurrentImage() {
		return imageSprite.get(imagePointer);
	}

	public void updateTick() {
		imageTick();
	}

	int imagePointer = 0;

	private void imageTick() {
		if (!imageUpdateIgnore) {
			// update total animation time, modulo the animation sequence
			// duration
			animationTotalTime = (animationTotalTime + animationPeriod)
					% (long) (1000 * seqDuration);

			// calculate current displayable image position
			imagePointer = (int) (animationTotalTime / showPeriod); // in range
																	// 0 to
																	// num-1
			if (imagePointer >= imageSprite.size())
				imagePointer = imageSprite.size() - 1;

			if ((imagePointer == imageSprite.size() - 1) && (!isRepeating)) { // at
																				// end
																				// of
																				// sequence
				imageUpdateIgnore = true; // stop at this image
			}
		}
	}

	public boolean isCycleEnd() {
		return (!isRepeating && imageUpdateIgnore);
	}

	public void restartAt(int imPosn)
	/*
	 * Start showing the images again, starting with image number imPosn. This
	 * requires a resetting of the animation time as well.
	 */
	{
		if (imageSprite.size() != 0) {
			if ((imPosn < 0) || (imPosn > imageSprite.size() - 1)) {
				System.out.println("Out of range restart, starting at 0");
				imPosn = 0;
			}

			imagePointer = imPosn;
			// calculate a suitable animation time
			animationTotalTime = (long) imagePointer * showPeriod;
			imageUpdateIgnore = false;
		}
	} // end of restartAt()

	public void resume()
	// start at previous image position
	{
		if (imageSprite.size() != 0)
			animationTotalTime = 0L;
		imageUpdateIgnore = false;
	}

	public int getCurrentPointer() {
		return imagePointer;
	}

	public int getMaxPointer() {
		return imageSprite.size();
	}

}
