import sun.misc.Perf;

//import com.sun.j3d.utils.timer.J3DTimer;

public class TimerRes
{
  public static void main(String args[])
  { // j3dTimeResolution( );
	nanoTimeResolution();
    sysTimeResolution( );
    perfTimeResolution( );
  }

//  private static void j3dTimeResolution( )
//  { 
//	  System.out.println("Java 3D Timer Resolution: " +
//      J3DTimer.getResolution( ) + " nsecs");
//  }

  private static void nanoTimeResolution( )
  {
    long total, count1, count2;

    count1 = System.nanoTime( );
    count2 = System.nanoTime( );
    while(count1 == count2)
      count2 = System.nanoTime( );
    total = (count2 - count1);

    count1 = System.nanoTime( );
    count2 = System.nanoTime( );
    while(count1 == count2)
      count2 = System.nanoTime( );
    total += (count2 - count1);

    count1 = System.nanoTime( );
    count2 = System.nanoTime( );
    while(count1 == count2)
      count2 = System.nanoTime( );
    total += (count2 - count1);

    count1 = System.nanoTime( );
    count2 = System.nanoTime( );
    while(count1 == count2)
      count2 = System.nanoTime( );
    total += (count2 - count1);

    System.out.println("Nano Time resolution: " + total/4 + " ns");
  } // end of nanoTimeResolution( )
  
  private static void sysTimeResolution( )
  {
    long total, count1, count2;

    count1 = System.currentTimeMillis( );
    count2 = System.currentTimeMillis( );
    while(count1 == count2)
      count2 = System.currentTimeMillis( );
    total = 1000L * (count2 - count1);

    count1 = System.currentTimeMillis( );
    count2 = System.currentTimeMillis( );
    while(count1 == count2)
      count2 = System.currentTimeMillis( );
    total += 1000L * (count2 - count1);

    count1 = System.currentTimeMillis( );
    count2 = System.currentTimeMillis( );
    while(count1 == count2)
      count2 = System.currentTimeMillis( );
    total += 1000L * (count2 - count1);

    count1 = System.currentTimeMillis( );
    count2 = System.currentTimeMillis( );
    while(count1 == count2)
      count2 = System.currentTimeMillis( );
    total += 1000L * (count2 - count1);

    System.out.println("System Time resolution: " +
                            total/4 + " microsecs");
  } // end of sysTimeResolution( )

  public class StopWatch
  {
    private Perf hiResTimer;
    private long freq;
    private long startTime;

    public StopWatch( )
    { hiResTimer = Perf.getPerf( );
      freq = hiResTimer.highResFrequency( );
    }

    public void start( )
    {  startTime = hiResTimer.highResCounter( ); }


    public long stop( )
    //  return the elapsed time in nanoseconds
    {  return (hiResTimer.highResCounter( ) -
                             startTime)*1000000000L/freq;  }


    public long getResolution( )
    // return counter resolution in nanoseconds
    {
      long diff, count1, count2;

      count1 = hiResTimer.highResCounter( );
      count2 = hiResTimer.highResCounter( );
      while(count1 == count2)
        count2 = hiResTimer.highResCounter( );
      diff = (count2 - count1);

      count1 = hiResTimer.highResCounter( );
      count2 = hiResTimer.highResCounter( );
      while(count1 == count2)
        count2 = hiResTimer.highResCounter( );
      diff += (count2 - count1);

      count1 = hiResTimer.highResCounter( );
      count2 = hiResTimer.highResCounter( );
      while(count1 == count2)
        count2 = hiResTimer.highResCounter( );
      diff += (count2 - count1);

      count1 = hiResTimer.highResCounter( );
      count2 = hiResTimer.highResCounter( );
      while(count1 == count2)
        count2 = hiResTimer.highResCounter( );
      diff += (count2 - count1);

      return (diff*1000000000L)/(4*freq);
    } // end of getResolution( )

  } // end of StopWatch class

  private static void perfTimeResolution( )
  {
	TimerRes ti = new TimerRes();
    StopWatch sw = ti.new StopWatch( );
    System.out.println("Perf Resolution: " +
                    sw.getResolution( ) + " nsecs");

    sw.start( );
    long time = sw.stop( );
    System.out.println("Perf Time " + time  + " nsecs");
  }

} // end of TimerRes class
