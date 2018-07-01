package it.reef.ark.main;



import it.reef.ark.manager.LoaderWatcher;
import it.reef.ark.resources.images.Images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JLabel;

public class Startup extends JWindow implements LoaderWatcher{

	// public JWindow frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Startup window = new Startup();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Startup() {
		initialize();
	}
	JProgressBar progressBar;
	private JLabel labelDesc;
	
	private String title;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		int WIDTH = 300;
		int HEIGHT = 239;
		
	      Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
	      int       nX  = (int) (scr.getWidth()  - (WIDTH)  ) / 2;
	      int       nY  = (int) (scr.getHeight() - (HEIGHT) ) / 2;


		this.setBounds(nX, nY, WIDTH, HEIGHT);
		
		progressBar = new JProgressBar();
		this.getContentPane().add(progressBar, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		this.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		ImageIcon image   = new ImageIcon(); 
		try {
			Image i = ImageIO.read(Images.class.getResourceAsStream( "bruco-splash.jpg" ));
			image.setImage(i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		labelDesc = new JLabel("");
		labelDesc.setBounds(12, 210, 276, 15);
		panel.add(labelDesc);
		
		JLabel labelImage = new JLabel(image);
		labelImage.setBounds(0, 0, 300, 225);
		panel.add(labelImage);
	}

	public void setElementiCaricati(int numImage, String desc) {
		progressBar.setValue(numImage);
		labelDesc.setText(title+": "+desc);
	}

	public void setElementiDaCaricare(int numImage, String title) {
		progressBar=new JProgressBar();
		progressBar.setForeground(Color.RED);
		progressBar.setMaximum(numImage);
		progressBar.setMinimum(0);
		progressBar.setIndeterminate(true);
		progressBar.setEnabled(true);
		progressBar.setValue(0);
		this.title=title;
	}
}
