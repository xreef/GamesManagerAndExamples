package it.reef.ark.element.action;

// SoundsWatcher.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* 
*/

public interface SoundsWatcher 
{
  // constants used by SoundsWatcher method, atSequenceEnd()
  public final static int STOPPED = 0;
  public final static int REPLAYED = 1;
  public final static int STARTED = 2;

  void atSequenceEnd(String filename, int status);
}

