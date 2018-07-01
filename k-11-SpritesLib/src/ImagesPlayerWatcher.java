
// ImagesPlayerWatcher.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* When an ImagesPlayer gets to the end of a sequence, it can
   call sequenceEnded() in a listener.
*/
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7662]
 */
public interface ImagesPlayerWatcher {
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f56]
 * @param imageName 
 */
    public void sequenceEnded(String imageName);
}


