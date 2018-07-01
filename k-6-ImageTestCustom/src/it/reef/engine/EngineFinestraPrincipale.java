package it.reef.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EngineFinestraPrincipale extends JFrame implements WindowListener {
	private static int DEFAULT_FPS = 100;

	// private WormPanel wp; // where the worm is drawn
	private PannelloGrafica wp;
	private JTextField jtfMem; // displays no.of boxes used
	private JTextField jtfTime; // displays time spent in game
	private JTextField jtfFps;

	private JTextField jtfLevel;

	public EngineFinestraPrincipale(long period) {
		super("Worm");
		makeGUI(period);

		addWindowListener(this);

		pack();
		setResizable(false);
		setVisible(true);
	} // end of WormChase() constructor

	private void makeGUI(long period) {
		Container c = getContentPane(); // default BorderLayout used

		wp = new PannelloGrafica(this, period);
		c.add(wp, BorderLayout.CENTER);

		JPanel ctrls = new JPanel(); // Pannello per le informazioni
		ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

		jtfLevel = new JTextField("L: 1 S:    0 Vite: 5");
		jtfLevel.setEditable(false);
		ctrls.add(jtfLevel);

		jtfMem = new JTextField("Fruit: 0");
		jtfMem.setEditable(false);
		ctrls.add(jtfMem);

		jtfTime = new JTextField("Time: 0 secs");
		jtfTime.setEditable(false);
		ctrls.add(jtfTime);

		jtfFps = new JTextField("Average FPS/UPS: 00.00, 00.00");
		jtfFps.setEditable(false);
		ctrls.add(jtfFps);

		c.add(ctrls, BorderLayout.SOUTH);
	} // end of makeGUI()

	public void setLevel(int l, int globalScore, int vite) {
		jtfLevel.setText("L: " + l + " S: " + globalScore + " Vite: " + vite);
	}

	public void setFpsUps(String string) {
		jtfFps.setText(string);
	}

	public void setMemRam(String no) {
		jtfMem.setText(no);
	}

	public void setTimeSpent(long t) {
		jtfTime.setText("Time: " + t + " secs");
	}

	// ----------------- window listener methods -------------

	public void windowActivated(WindowEvent e) {
		wp.resumeGame();
	}

	public void windowDeactivated(WindowEvent e) {
		wp.pauseGame();
	}

	public void windowDeiconified(WindowEvent e) {
		wp.resumeGame();
	}

	public void windowIconified(WindowEvent e) {
		wp.pauseGame();
	}

	public void windowClosing(WindowEvent e) {
		wp.stopGame();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	// ----------------------------------------------------

	public static void main(String args[]) {
	    System.setProperty("sun.java2d.translaccel", "true"); 
	    System.setProperty("sun.java2d.ddforcevram", "true"); 
	    
	       // switch on hardware acceleration if using OpenGL with pbuffers
	    System.setProperty("sun.java2d.opengl", "true");
	    
		int fps = DEFAULT_FPS;
		if (args.length != 0)
			fps = Integer.parseInt(args[0]);

		long period = (long) 1000.0 / fps;
		System.out.println("fps: " + fps + "; period: " + period + " ms");

		new EngineFinestraPrincipale(period * 1000000L); // ms --> nanosecs
	}

} // end of WormChase class