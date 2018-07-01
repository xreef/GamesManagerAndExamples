
// WormChase.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A worm moves around the screen and the user must
   click (press) on its head to complete the game.

   If the user misses the worm's head then a blue box
   will be added to the screen (if the worm's body was
   not clicked upon).

   A worm cannot move over a box, so the added obstacles
   *may* make it easier to catch the worm.

   A worm starts at 0 length and increases to a maximum
   length which it keeps from then on.

   A score is displayed on screen at the end, calculated
   from the number of boxes used and the time taken. Less
   boxes and less time mean a higher score.

   -------------
   
   The display includes two textfields for showing the current time
   and number of boxes. The average FPS/UPS values are drawn in
   the game's JPanel.

   Pausing/Resuming/Quiting are controlled via the frame's window
   listener methods.

   Uses active rendering to update the JPanel.

   Using Java 3D's timer: J3DTimer.getValue()
     *  nanosecs rather than millisecs for the period

   Average FPS / UPS
				20			50			80			100
   Win 98:    20/20       48/50       81/83       96/100
   Win 2000:  20/20       43/50       59/83       58/100    // slow machine
   Win XP:    20/20       50/50       83/83      100/100
*/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class TestImmagineBuffering extends JFrame implements WindowListener
{
  private static int DEFAULT_FPS = 100;

  private TestImmagineBufferingPanel wp;        // where the worm is drawn
  private JTextField jtfBox;   // displays no.of boxes used
  private JTextField jtfTime;  // displays time spent in game


  public TestImmagineBuffering(long period)
  { super("The Worm Chase");
    makeGUI(period);

    addWindowListener( this );
    pack();
    setResizable(false);
    setVisible(true);
  }  // end of WormChase() constructor


  private void makeGUI(long period)
  {
    Container c = getContentPane();    // default BorderLayout used

    wp = new TestImmagineBufferingPanel(this, period);
    c.add(wp, "Center");

    JPanel ctrls = new JPanel();   // a row of textfields
    ctrls.setLayout( new BoxLayout(ctrls, BoxLayout.X_AXIS));

    jtfBox = new JTextField("Boxes used: 0");
    jtfBox.setEditable(false);
    ctrls.add(jtfBox);

    jtfTime = new JTextField("Time Spent: 0 secs");
    jtfTime.setEditable(false);
    ctrls.add(jtfTime);

    c.add(ctrls, "South");
  }  // end of makeGUI()


  public void setBoxNumber(int no)
  {  jtfBox.setText("Boxes used: " + no);  }

  public void setTimeSpent(long t)
  {  jtfTime.setText("Time Spent: " + t + " secs"); }
  

  // ----------------- window listener methods -------------

  public void windowActivated(WindowEvent e) 
  { wp.resumeGame();  }

  public void windowDeactivated(WindowEvent e) 
  {  wp.pauseGame();  }


  public void windowDeiconified(WindowEvent e) 
  {  wp.resumeGame();  }

  public void windowIconified(WindowEvent e) 
  {  wp.pauseGame(); }


  public void windowClosing(WindowEvent e)
  {  wp.stopGame();  }


  public void windowClosed(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}

  // ----------------------------------------------------

  public static void main(String args[])
  { 
    int fps = DEFAULT_FPS;
    if (args.length != 0)
      fps = Integer.parseInt(args[0]);

    long period = (long) 1000.0/fps;
    System.out.println("fps: " + fps + "; period: " + period + " ms");

    new TestImmagineBuffering(period*1000000L);    // ms --> nanosecs 
  }

} // end of WormChase class


