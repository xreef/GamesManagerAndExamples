
package it.reef.managers.audio.interfaces;
// SoundsWatcher.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* 
*/
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm67f3]
 */
public interface SoundsWatcher {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm652f]
 */
    public static final int STOPPED = 0;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6529]
 */
    public static final int REPLAYED = 1;
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6520]
 * @param filename 
 * @param status 
 */
    public void atSequenceEnd(String filename, int status);
}
// constants used by SoundsWatcher method, atSequenceEnd()


