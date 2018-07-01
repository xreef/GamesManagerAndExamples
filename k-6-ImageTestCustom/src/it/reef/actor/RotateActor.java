package it.reef.actor;

import it.reef.engine.effect.ImageSFX;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class RotateActor extends ActorTick {

	ImageObserver iobs;
	
	BufferedImage imageStart;
	
	BufferedImage imageToDraw;
	
	private int angle=1;
	

	
	private ImageSFX isfx;

	public RotateActor(int x, int y, BufferedImage bi, ImageObserver io, 
			long period, double duration, int step,
			boolean repeat) {
		this.imageStart = bi;
		this.width = bi.getWidth();
		this.heigth = bi.getHeight();
		this.iobs = io;
		this.x = x;
		this.y = y;
		
		initImageTick( period,  duration,  step, repeat);

		this.isfx=new ImageSFX();
	}




	
	public int getAngle() {
		return angle;
	}



	public void setAngle(int angle) {
		this.angle = angle;
	}



	public void update() {
		super.update();
		imageToDraw = isfx.rotateImage(imageStart, tickStep);
	}



	public void applyEffect() {
		// TODO Auto-generated method stub
		
	}
	
	public void draw(Graphics g) {
		if (imageToDraw == null) {
	         g.setColor(Color.yellow);
	         g.fillRect(x, y, 20, 20);
	         g.setColor(Color.black);
	         g.drawString("??", x+10, y+10);
	       }
	       else
	         g.drawImage(imageToDraw, x, y, iobs);

	}
	
	
	
//	private boolean isRepeating, imageUpdateIgnore;
//	private long animationPeriod;
//    // period used by animation loop (in ms)
//	private long animationTotalTime;
//	
//	private int showPeriod;     
//    // period the current image is shown (in ms)
//	private double seqDuration;   
//    // total duration of the entire image sequence (in secs)
//	
//	private int step;
//	
//	private void initImageTick(long period, double duration, int step,boolean repeat){
//		this.step=step;
//
//		if (duration < 0.5) {
//			System.out
//					.println("Warning: minimum sequence duration is 0.5 sec.");
//			seqDuration = 0.5;
//		} else {
//			seqDuration = duration;
//		}
//
//		animationPeriod = period/1000000;
//
//		isRepeating = repeat;
//		animationTotalTime = 0L;
//
//		tickStep = 0;
//		imageUpdateIgnore = false;
//		showPeriod = (int) (1000 * seqDuration / step);
//	}
//	
//	int tickStep=0;
//	int lastTickStep=0;
//	private void imageTick() {
//		if (!imageUpdateIgnore) {
//			// update total animation time, modulo the animation sequence
//			// duration
//			animationTotalTime = (animationTotalTime + animationPeriod) % (long) (1000 * seqDuration);
//
//			// calculate current displayable image position
//			tickStep = (int) (animationTotalTime / showPeriod); // in range 0 to num-1
//			if (tickStep>=step) tickStep=step-1;
//			
//			if (((tickStep == step - 1) || tickStep<lastTickStep) && (!isRepeating)) { // at end of sequence
//				imageUpdateIgnore = true; // stop at this image
//			}
//			
//			lastTickStep=tickStep;
//		}
//	}
//
//	public void restartAt(int imPosn)
//	  /* Start showing the images again, starting with image number
//	     imPosn. This requires a resetting of the animation time as 
//	     well. */
//	  {
//	    if (step != 0) {
//	      if ((imPosn < 0) || (imPosn > step-1)) {
//	        System.out.println("Out of range restart, starting at 0");
//	        imPosn = 0;
//	      }
//
//	      tickStep = imPosn;
//	      // calculate a suitable animation time
//	      animationTotalTime = (long) tickStep * showPeriod;
//	      lastTickStep=0;
//	      imageUpdateIgnore = false;
//	    }
//	  }  // end of restartAt()
//
//
//	  public void resume()
//	  // start at previous image position
//	  { 
//	    if (step != 0)
//	    	animationTotalTime = 0L;
//	    	imageUpdateIgnore = false;
//	    	lastTickStep=0;
//	  } 


	
	
}
