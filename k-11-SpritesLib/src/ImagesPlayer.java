
// ImagesPlayer.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* ImagesPLayer is aimed at displaying the sequence of images
   making up a 'n', 's', or 'g' image file, as loaded by
   ImagesLoader.

   The ImagesPlayer constructor is supplied with the
   intended duration for showing the entire sequence
   (seqDuration). This is used to calculate showPeriod,
   the amount of time each image should be shown before
   the next image is displayed.

   The animation period (animPeriod) input argument states
   how often the ImagesPlayer's updateTick() method will be
   called. The intention is that updateTick() will be called periodically
   from the update() method in the top-level animation framework.

   The current animation time is calculated when updateTick()
   is called, and used to calculate imPosition, imPosition
   specifies which image should be returned when getCurrentImage() 
   is called.

   The ImagesPlayer can be set to cycle, stop, resume, or restart
   at a given image position.

   When the sequence finishes, a callback, sequenceEnded(), can
   be invoked in a specified object implementing the 
   ImagesPlayerWatcher interface.

*/
import java.awt.image.*;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7660]
 */
public class ImagesPlayer {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f39]
 */
    private String imName;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f34]
 */
    private boolean isRepeating;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f2f]
 */
    private boolean ticksIgnored;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f27]
 */
    private ImagesLoader imsLoader;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f20]
 */
    private int animPeriod;
// period used by animation loop (in ms)

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f1b]
 */
    private long animTotalTime;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f16]
 */
    private int showPeriod;
// period the current image is shown (in ms)

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f11]
 */
    private double seqDuration;
// total duration of the entire image sequence (in secs)

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f0c]
 */
    private int numImages;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f07]
 */
    private int imPosition;
// position of current displayable image
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6eff]
 */
    private ImagesPlayerWatcher watcher = null;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ef2]
 * @param nm 
 * @param ap 
 * @param d 
 * @param isr 
 * @param il 
 */
    public  ImagesPlayer(String nm, int ap, double d, boolean isr, ImagesLoader il) {        
    imName = nm;
    animPeriod = ap; 
    seqDuration = d;
    isRepeating = isr;
    imsLoader = il;
    animTotalTime = 0L;
    if (seqDuration <  0.5) {
      System.out.println("Warning: minimum sequence duration is 0.5 sec.");
      seqDuration =  0.5;
    }
    if (!imsLoader.isLoaded(imName)) {
      System.out.println(imName + " is not known by the ImagesLoader");
      numImages = 0;
      imPosition = -1;
      ticksIgnored = true;
    }
    else {
      numImages = imsLoader.numImages(imName);
      imPosition = 0;
      ticksIgnored = false;
      showPeriod = (int) (1000 * seqDuration / numImages);
    }
    } 
// end of ImagesPlayer()
/* We assume that this method is called every animPeriod ms */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6eec]
 */
    public void updateTick() {        
    if (!ticksIgnored) {
      // update total animation time, modulo the animation sequence duration
      animTotalTime = (animTotalTime + animPeriod) % (long)(1000 * seqDuration);
      // calculate current displayable image position
      imPosition = (int) (animTotalTime / showPeriod);   // in range 0 to num-1
      if ((imPosition == numImages-1) && (!isRepeating)) {  // at end of sequence
        ticksIgnored = true;   // stop at this image
        if (watcher != null)
          watcher.sequenceEnded(imName);   // call callback
      }
    }
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ee6]
 * @return 
 */
    public BufferedImage getCurrentImage() {        
 if (numImages != 0)
      return imsLoader.getImage(imName, imPosition); 
    else
      return null; 
    } 
// end of getCurrentImage()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ee0]
 * @return 
 */
    public int getCurrentPosition() {        
  return imPosition;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ed9]
 * @param w 
 */
    public void setWatcher(ImagesPlayerWatcher w) {        
  watcher = w;  
    } 
/* updateTick() calls will no longer update the
     total animation time or imPosition. */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ed3]
 */
    public void stop() {        
  ticksIgnored = true;  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ecd]
 * @return 
 */
    public boolean isStopped() {        
  return ticksIgnored;  
    } 
// are we at the last image and not cycling through them?

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ec7]
 * @return 
 */
    public boolean atSequenceEnd() {        
  return ((imPosition == numImages-1) && (!isRepeating));  
    } 
/* Start showing the images again, starting with image number
     imPosn. This requires a resetting of the animation time as 
     well. */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ec0]
 * @param imPosn 
 */
    public void restartAt(int imPosn) {        
    if (numImages != 0) {
      if ((imPosn < 0) || (imPosn > numImages-1)) {
        System.out.println("Out of range restart, starting at 0");
        imPosn = 0;
      }
      imPosition = imPosn;
      // calculate a suitable animation time
      animTotalTime = (long) imPosition * showPeriod;
      ticksIgnored = false;
    }
    } 
// start at previous image position

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6eba]
 */
    public void resume() {        
 
    if (numImages != 0)
      ticksIgnored = false;
    } 
 }
// end of ImagesPlayer class
