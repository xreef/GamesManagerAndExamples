package it.reef.ark.element.actor.type;

import it.reef.ark.element.action.Cyclical;
import it.reef.ark.element.action.Updatable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class ImmagineDinamicaSprite extends Actor implements Cyclical, Updatable {

	
	ArrayList<BufferedImage> imageSprite;
	ImageObserver iobs;
	
	private boolean isRepeating, imageUpdateIgnore;
	private long animationPeriod;
    // period used by animation loop (in ms)
	private long animationTotalTime;
	
	private int showPeriod;     
    // period the current image is shown (in ms)
	private double seqDuration;   
    // total duration of the entire image sequence (in secs)
	
	int counter=1;
	
	
	
	public ImmagineDinamicaSprite(int x, int y, ArrayList<BufferedImage> is,
			ImageObserver io, long period, double duration, boolean repeat) {
		this.imageSprite = is;
		this.width = imageSprite.get(0).getWidth();
		this.heigth = imageSprite.get(0).getHeight();
		this.iobs = io;
		this.x = x;
		this.y = y;

		if (duration < 0.5) {
			System.out
					.println("Warning: minimum sequence duration is 0.5 sec.");
			seqDuration = 0.5;
		} else {
			seqDuration = duration;
		}

		animationPeriod = period/1000000;

		isRepeating = repeat;
		animationTotalTime = 0L;

		imagePointer = 0;
		imageUpdateIgnore = false;
		showPeriod = (int) (1000 * seqDuration / is.size());

	}
	
	public void changeImageSprite(ArrayList<BufferedImage> is){
		this.imageSprite = is;
		this.width = imageSprite.get(0).getWidth();
		this.heigth = imageSprite.get(0).getHeight();
		imagePointer = 0;
		// imageUpdateIgnore = false;
		// showPeriod = (int) (1000 * seqDuration / is.size());
	}
	
	public void draw(Graphics g) {
		if (imageSprite.get(imagePointer) == null) {
	         g.setColor(Color.yellow);
	         g.fillRect(x, y, 20, 20);
	         g.setColor(Color.black);
	         g.drawString("??", x+10, y+10);
	       }
	       else
	         g.drawImage(imageSprite.get(imagePointer), x, y, iobs);
	}

	public void applyEffect() {
		// TODO Auto-generated method stub
	}

	public void update() {
		imageTick();
	}

	int imagePointer=0;
	private void imageTick() {
		if (!imageUpdateIgnore) {
			// update total animation time, modulo the animation sequence
			// duration
			animationTotalTime = (animationTotalTime + animationPeriod) % (long) (1000 * seqDuration);

			// calculate current displayable image position
			imagePointer = (int) (animationTotalTime / showPeriod); // in range 0 to num-1
			if (imagePointer>=imageSprite.size()) imagePointer=imageSprite.size()-1;

			if ((imagePointer == imageSprite.size() - 1) && (!isRepeating)) { // at end of sequence
				imageUpdateIgnore = true; // stop at this image
			}
		}

		// if (imagePointer<imageSprite.size()-1){
		// imagePointer++;
		// }else{
		// imagePointer=0;
		// }
	}
	
	public void restartAt(int imPosn)
	  /* Start showing the images again, starting with image number
	     imPosn. This requires a resetting of the animation time as 
	     well. */
	  {
	    if (imageSprite.size() != 0) {
	      if ((imPosn < 0) || (imPosn > imageSprite.size()-1)) {
	        System.out.println("Out of range restart, starting at 0");
	        imPosn = 0;
	      }

	      imagePointer = imPosn;
	      // calculate a suitable animation time
	      animationTotalTime = (long) imagePointer * showPeriod;
	      imageUpdateIgnore = false;
	    }
	  }  // end of restartAt()


	  public void resume()
	  // start at previous image position
	  { 
	    if (imageSprite.size() != 0)
	    	animationTotalTime = 0L;
	    	imageUpdateIgnore = false;
	  }

	public ArrayList<BufferedImage> getImageSprite() {
		return imageSprite;
	}

	public void setImageSprite(ArrayList<BufferedImage> imageSprite) {
		this.imageSprite = imageSprite;
	} 
	  
	public void stopAnimation(){
		this.imageUpdateIgnore = true;
	}
	public void resumeAnimation(){
		this.imageUpdateIgnore = false;
	}
	 
	public boolean isSpriteActive(){
		return !imageUpdateIgnore;
	}
	public int getImagePointer(){
		return this.imagePointer;
	}
}

