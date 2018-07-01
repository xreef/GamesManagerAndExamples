package it.reef.legend.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Legend extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -939796014830683480L;

	private static final Log log = LogFactory.getLog(Legend.class);
	
	// Frame per second
	private static int DEFAULT_FPS = 35;

	private LegendPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		log.debug("LOG LEVEL: DEBUG");
		log.info("LOG LEVEL: INFO");
		log.warn("LOG LEVEL: WARN");
		log.error("LOG LEVEL: ERROR");

		int fps = DEFAULT_FPS;
		long periodo = (long) 1000.0/fps;
		// Genero il frame
		new Legend(periodo*1000000L); // Periodo passato in nanosecondi
	}

	/**
	 * Create the frame.
	 */
	public Legend(long periodo) {
		super();
		
		addWindowListener(new WindowAdapter() {
//			public void windowActivated(WindowEvent e) 
//			  { contentPane.resumeGame();  }
//			  public void windowDeactivated(WindowEvent e) 
//			  {  contentPane.pauseGame();  }
//			  public void windowDeiconified(WindowEvent e) 
//			  {  contentPane.resumeGame();  }
//			  public void windowIconified(WindowEvent e) 
//			  {  contentPane.pauseGame(); }
//			  public void windowClosing(WindowEvent e)
//			  {  contentPane.stopGame();  }
//			  public void windowClosed(WindowEvent e) {}
//			  public void windowOpened(WindowEvent e) {}
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new LegendPanel(this, periodo);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
	    pack();
	    setResizable(false);
	    center();
	    setVisible(true);
	}

	private void center() {
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		int nX = (int) (scr.getWidth() - getWidth()) / 2;
		int nY = (int) (scr.getHeight() - getHeight()) / 2;
		setLocation(nX, nY);
	}
	
	private JTextField jtfMem; // displays no.of boxes used
	private JTextField jtfTime; // displays time spent in game
	private JTextField jtfFps;
	private JTextField jtfLevel;

	public JPanel pannelloControlli() {
		JPanel ctrls = new JPanel(); // Pannello per le informazioni
		ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

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

	public void setLevel(int l, int globalScore, int vite) {
		jtfLevel.setText("L: " + l + " S: " + globalScore + " Vite: " + vite);
	}

	public void setFpsUps(String string) {
		jtfFps.setText(string);
	}

	// public void setBoxNumber(int no)
	// { jtfBox.setText("Fruit: " + no); }

	public void setTimeSpent(long t) {
		jtfTime.setText("Time: " + t + " secs");
	}

	public void setMemRam(String no) {
		jtfMem.setText(no);
	}

}
