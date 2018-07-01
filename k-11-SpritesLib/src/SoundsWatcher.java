
// SoundsWatcher.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* 
*/
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm7661]
 */
public interface SoundsWatcher {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f4f]
 */
    public static final int STOPPED = 0;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f49]
 */
    public static final int REPLAYED = 1;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6f40]
 * @param filename 
 * @param status 
 */
    public void atSequenceEnd(String filename, int status);
}
// constants used by SoundsWatcher method, atSequenceEnd()


