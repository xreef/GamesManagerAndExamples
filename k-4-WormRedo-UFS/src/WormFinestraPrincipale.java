import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class WormFinestraPrincipale extends JFrame // implements WindowListener
{
  private static int DEFAULT_FPS = 10;

  // private WormPanel wp;        // where the worm is drawn
  private WormPannelloGioco wp;
//  private JTextField jtfBox;   // displays no.of boxes used
//  private JTextField jtfTime;  // displays time spent in game
//  private JTextField jtfFps;

//  private JTextField jtfLevel;

	public int pHeight;
	
	public int pWidth;
  
  public WormFinestraPrincipale(long period)
  { super("Worm");
    makeGUI(period);

    // addWindowListener( this );

    
    pack();
    setResizable(false);
    setVisible(true);
  }  // end of WormChase() constructor


  private void makeGUI(long period)
  {
    Container c = getContentPane();    // default BorderLayout used

    JPanel ctrls = new JPanel();   // Pannello per le informazioni
    ctrls.setLayout( new BoxLayout(ctrls, BoxLayout.X_AXIS));

//    jtfLevel = new JTextField("L: 1 S:    0");
//    jtfLevel.setEditable(false);
//    ctrls.add(jtfLevel);
//    
//    jtfBox = new JTextField("Fruit picked: 0");
//    jtfBox.setEditable(false);
//    ctrls.add(jtfBox);
//
//    jtfTime = new JTextField("Time spent: 0 secs");
//    jtfTime.setEditable(false);
//    ctrls.add(jtfTime);
//
//    jtfFps = new JTextField("Average FPS/UPS: 00.00, 00.00");
//    jtfFps.setEditable(false);
//    ctrls.add(jtfFps);
//    
//    c.add(ctrls, BorderLayout.SOUTH);
    
    calcSizes();
    wp = new WormPannelloGioco(this, period);
    c.add(wp, BorderLayout.CENTER);
    setUndecorated(true);   // no borders or titlebar
    setIgnoreRepaint(true);  // turn off paint events since doing active rendering
    pack();
  }  // end of makeGUI()


  



  // ----------------------------------------------------

  public static void main(String args[])
  { 
    int fps = DEFAULT_FPS;
    if (args.length != 0)
      fps = Integer.parseInt(args[0]);

    long period = (long) 1000.0/fps;
    System.out.println("fps: " + fps + "; period: " + period + " ms");

    new WormFinestraPrincipale(period*1000000L);    // ms --> nanosecs 
  }

  
  private void calcSizes()
  {
	  Toolkit tk = Toolkit.getDefaultToolkit( );
	    Dimension scrDim = tk.getScreenSize( );
	    setPreferredSize(scrDim);   // set JPanel size

	    pWidth = scrDim.width;      // store dimensions for later
	    pHeight = scrDim.height;
  }

} // end of WormChase class