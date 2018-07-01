import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class WormFinestraPrincipale extends JFrame implements WindowListener
{
  private static int DEFAULT_FPS = 10;

  // private WormPanel wp;        // where the worm is drawn
  private WormPannelloGioco wp;
  private JTextField jtfBox;   // displays no.of boxes used
  private JTextField jtfTime;  // displays time spent in game
  private JTextField jtfFps;

  private JTextField jtfLevel;

	public int pHeight;
	
	public int pWidth;
  
  public WormFinestraPrincipale(long period)
  { super("Worm");
    makeGUI(period);

    addWindowListener( this );

    
    pack();
    setResizable(false);
    setVisible(true);
  }  // end of WormChase() constructor


  private void makeGUI(long period)
  {
    Container c = getContentPane();    // default BorderLayout used

    JPanel ctrls = new JPanel();   // Pannello per le informazioni
    ctrls.setLayout( new BoxLayout(ctrls, BoxLayout.X_AXIS));

    jtfLevel = new JTextField("L: 1 S:    0");
    jtfLevel.setEditable(false);
    ctrls.add(jtfLevel);
    
    jtfBox = new JTextField("Fruit picked: 0");
    jtfBox.setEditable(false);
    ctrls.add(jtfBox);

    jtfTime = new JTextField("Time spent: 0 secs");
    jtfTime.setEditable(false);
    ctrls.add(jtfTime);

    jtfFps = new JTextField("Average FPS/UPS: 00.00, 00.00");
    jtfFps.setEditable(false);
    ctrls.add(jtfFps);
    
    c.add(ctrls, BorderLayout.SOUTH);
    
    calcSizes();
    wp = new WormPannelloGioco(this, period);
    c.add(wp, BorderLayout.CENTER);
    
    pack();
  }  // end of makeGUI()

  public void setLevel(int l, int globalScore){
	  jtfLevel.setText("L: "+l+" S: "+globalScore);
  }
  
  public void setFpsUps(String string) {
		jtfFps.setText(string);
  }
  
  public void setBoxNumber(int no)
  {  jtfBox.setText("Fruit picked: " + no);  }

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

    new WormFinestraPrincipale(period*1000000L);    // ms --> nanosecs 
  }

  
  private void calcSizes()
  {
    GraphicsConfiguration gc = getGraphicsConfiguration( );
    Rectangle screenRect = gc.getBounds( );  // screen dimensions

    Toolkit tk = Toolkit.getDefaultToolkit( );
    Insets desktopInsets = tk.getScreenInsets(gc);

    Insets frameInsets = getInsets( );     // only works after pack( )

    Dimension tfDim = jtfBox.getPreferredSize( );  // textfield size

    pWidth = screenRect.width
               - (desktopInsets.left + desktopInsets.right)
               - (frameInsets.left + frameInsets.right);

    pHeight = screenRect.height
                - (desktopInsets.top + desktopInsets.bottom)
                - (frameInsets.top + frameInsets.bottom)
                - tfDim.height;
  }

} // end of WormChase class