
// PanChanger.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* PanChanger is a pan seting changing thread, which operates on
   the synthesizer used by the MIDI player in PanMidi.

   It adjusts the pan settings every CYCLE_PERIOD ms until the time
   used reaches the known duration of the music.

   The pan adjustment is determined by the panVals[] array. A 0
   value means that only the left speaker is playing, 127 means
   only the right speaker, and 80 is both.

   The thread cycles through the panVals[] array until it finishes.
*/

public class PanChanger extends Thread
{
  // time to move left to right and back again
  private static int CYCLE_PERIOD = 4000;  // in ms
  
  // pan values used in a single cycle 
  // (make the array's length integer divisible into CYCLE_PERIOD)
  private int[] panVals = {0, 127};
  // or try
  // private int[] panVals = {0, 16, 32, 48, 64, 80, 96, 112, 127, 
  //                          112, 96, 80, 64, 48, 32, 16};

  private PanMidi player;
  private int duration;  // of the music in ms


  public PanChanger(PanMidi p)
  {  super("PanChanger");  
     player = p;
  }


  public void startChanging(int d)
  /* PanMidi calls this method, supplying the duration of
     its MIDI sequence in ms. */
  { duration = d;
    start();
  } // end of startChanging()


  public void run()
  /* Modify the pan setting, sleep a while, then repeat, until the
     time spent matches (or exceeds) the music's duration.
  */
  { /* Get the original pan setting, just for information. It
        is not used any further. */
     int pan = player.getMaxPan();
     System.out.println("Max Pan: " + pan);
  
     int panValsIdx = 0;
     int timeCount = 0;
     int delayPeriod = (int) (CYCLE_PERIOD / panVals.length);

     System.out.print("Panning");
     while(timeCount < duration){ 
       try {
         if (player != null)
           player.setPan( panVals[panValsIdx] );
         Thread.sleep(delayPeriod);    // delay
       }
       catch(InterruptedException e) {}
       System.out.print(".");
       panValsIdx = (panValsIdx+1) % panVals.length;  // cycle through the array
       timeCount += delayPeriod;
     } 
     System.out.println();
  } // end of run()

}  // end of PanChanger.java
