package it.reef.ark.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Ark extends JFrame {

	private static final Log log = LogFactory.getLog(Ark.class);
	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	private ArkPanel contentPane;

	// Frame per second
	private static int DEFAULT_FPS = 35;
	public static int BALL_STEP=6;
	
	/**
	 * Launch the application.
	 */
	public static void main(final String args[]) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
					int fps = DEFAULT_FPS;
					log.debug("LOG LEVEL: DEBUG");
					log.info("LOG LEVEL: INFO");
					log.warn("LOG LEVEL: WARN");
					log.error("LOG LEVEL: ERROR");
				    if (args.length != 0)
				      fps = Integer.parseInt(args[0]);

				    long period = (long) 1000.0/fps;
				    
					// Ark frame = 
						new Ark(period*1000000L); // ms --> nanosecs 
					// frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

	/**
	 * Create the frame.
	 */
	public Ark(long period) {
		super("Ark");
		
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) 
			  { contentPane.resumeGame();  }

			  public void windowDeactivated(WindowEvent e) 
			  {  contentPane.pauseGame();  }


			  public void windowDeiconified(WindowEvent e) 
			  {  contentPane.resumeGame();  }

			  public void windowIconified(WindowEvent e) 
			  {  contentPane.pauseGame(); }


			  public void windowClosing(WindowEvent e)
			  {  contentPane.stopGame();  }


			  public void windowClosed(WindowEvent e) {}
			  public void windowOpened(WindowEvent e) {}

			  

		});
		
		Container c = getContentPane();    // default BorderLayout used
		contentPane = new ArkPanel(this,period);
	    c.add(contentPane, BorderLayout.CENTER);
	    c.add(pannelloControlli(), BorderLayout.SOUTH);
	    
	    pack();
	    setResizable(false);
	    
	    center();
	    
	    setVisible(true);
	    
	
	}
	 private void center()
	   {
	      Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
	      int       nX  = (int) (scr.getWidth()  - getWidth()  ) / 2;
	      int       nY  = (int) (scr.getHeight() - getHeight() ) / 2;

	      setLocation( nX, nY );
	   }
	
	
	private JTextField jtfMem;   // displays no.of boxes used
	private JTextField jtfTime;  // displays time spent in game
	private JTextField jtfFps;
	private JTextField jtfLevel;
	public JPanel pannelloControlli(){
	    JPanel ctrls = new JPanel();   // Pannello per le informazioni
	    ctrls.setLayout( new BoxLayout(ctrls, BoxLayout.X_AXIS));

	    jtfLevel = new JTextField("L: 1 S:    0 Vite: 5");
	    jtfLevel.setEditable(false);
	    ctrls.add(jtfLevel);
	    
	    jtfMem = new JTextField("MEM: 0");
	    jtfMem.setEditable(false);
	    ctrls.add(jtfMem);

	    jtfTime = new JTextField("Time: 0 secs");
	    jtfTime.setEditable(false);
	    ctrls.add(jtfTime);

	    jtfFps = new JTextField("Average FPS/UPS: 00.00, 00.00");
	    jtfFps.setEditable(false);
	    ctrls.add(jtfFps);
	    
	    return ctrls;

	}
	
	  public void setLevel(int l, int globalScore, int vite){
		  jtfLevel.setText("L: "+l+" S: "+globalScore+" Vite: "+vite);
	  }
	  
	  public void setFpsUps(String string) {
			jtfFps.setText(string);
	  }
	  
//	  public void setBoxNumber(int no)
//	  {  jtfBox.setText("Fruit: " + no);  }

	  public void setTimeSpent(long t)
	  {  jtfTime.setText("Time: " + t + " secs"); }
	
		public void setMemRam(String no) {
			jtfMem.setText(no);
		}
}
