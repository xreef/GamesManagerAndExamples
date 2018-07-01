package it.reef.simplesound;

import it.reef.manage.AudioLineThread;
import it.reef.manage.AudioManager;
import it.reef.manage.SpeedDic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Control.Type;
import javax.swing.BoxLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.SliderUI;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class MainSound {

	private JComboBox trackComboBox;
	private JTextField sampleRateTextMax;
	private JTextField sampleRateTextMin;
	private JTextField masterGaTextMax;
	private JTextField masterGaTextMin;
	private JTextField panTextMax;
	private JTextField panTextMin;
	private JTextField sampleRateText;
	private JTextField masterGatext;
	private JTextField pantext;
	private JTextField nomeTextField;
	private JTextField sizeText;
	private JTextField tipoStream;

	private JList list;
	private DefaultListModel model;

	private JFrame frame;

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainSound window = new MainSound();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application
	 */
	public MainSound() {
		createContents();
	}

	JPanel panel;

	private static final int CLIP = 0;
	private static final int LINE = 1;
	private static final int MIDI = 2;

	int tipo = -1;

	Clip clip;

	JLabel labelDuration;
	
	AudioLineThread al;

	/**
	 * Initialize the contents of the frame
	 */
	private void createContents() {
		frame = new JFrame();
		frame.getContentPane().setLayout(new GridLayout(1, 0));
		frame.setBounds(100, 100, 500, 375);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setPreferredSize(new Dimension(100, 0));
		splitPane.setMinimumSize(new Dimension(100, 0));
		splitPane.setMaximumSize(new Dimension(100, 0));
		frame.getContentPane().add(splitPane);

		panel = new JPanel();
		panel.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC },
				new RowSpec[] { FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("fill:default:grow(1.0)"),
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("default"),
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC }));
		panel.setSize(112, 349);
		splitPane.setLeftComponent(panel);

		final JButton selectFileButton = new JButton();
		selectFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				File f = getFileToRead();
				if (f != null) {

					setFileToShow(f, true);
				}
			}
		});
		selectFileButton.setText("Select file");
		panel.add(selectFileButton, new CellConstraints(1, 1,
				CellConstraints.FILL, CellConstraints.FILL));

		model = new DefaultListModel();
		list = new JList(model);
		panel.add(list, new CellConstraints(1, 3, CellConstraints.FILL,
				CellConstraints.FILL));

		labelDuration = new JLabel();
		labelDuration.setText("Duration: none");
		panel.add(labelDuration, new CellConstraints(1, 9,
				CellConstraints.FILL, CellConstraints.DEFAULT));

		final JButton loadSelectedFileButton = new JButton();
		loadSelectedFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (list.getSelectedIndex() != -1) {
					setFileToShow(l.get(new Integer(list.getSelectedIndex())),
							false);
				}
			}
		});
		loadSelectedFileButton.setText("Load Selected file");
		panel.add(loadSelectedFileButton, new CellConstraints(1, 5));

		final JButton buttonRemove = new JButton();
		buttonRemove.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (list.getSelectedIndex() != -1) {
					l.remove(new Integer(list.getSelectedIndex()));
					model.remove(list.getSelectedIndex());
				}
			}
		});
		buttonRemove.setText("Remove Element");
		panel.add(buttonRemove, new CellConstraints(1, 7));

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)")}));
		splitPane.setRightComponent(panel_1);

		final JLabel tipoLabel = new JLabel();
		tipoLabel.setText("Tipo");
		panel_1.add(tipoLabel, new CellConstraints(1, 3));

		tipoStream = new JTextField();
		tipoStream.setEditable(false);
		panel_1.add(tipoStream, new CellConstraints(3, 3));

		final JButton playBtn = new JButton();
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (tipo==LINE){
					playStreamLine();
				}else if (tipo==CLIP){
					playClip(false);
				}else if (tipo==MIDI){
					playMidi();
				}
			}
		});
		playBtn.setText("Play");
		panel_1.add(playBtn, new CellConstraints(5, 1));

		final JLabel sizeLabel = new JLabel();
		sizeLabel.setText("Size");
		panel_1.add(sizeLabel, new CellConstraints(1, 5));

		sizeText = new JTextField();
		sizeText.setEditable(false);
		panel_1.add(sizeText, new CellConstraints(3, 5));

		final JLabel nomeLabel = new JLabel();
		nomeLabel.setText("Nome");
		panel_1.add(nomeLabel, new CellConstraints());

		nomeTextField = new JTextField();
		nomeTextField.setEditable(false);
		panel_1.add(nomeTextField, new CellConstraints(3, 1));

		final JButton loopButton = new JButton();
		loopButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (tipo==CLIP){
					playClip(true);
				}else if (tipo==LINE){
					playStreamLine();
				}else if (tipo==MIDI){
					playMidi();
				}
			}
		});
		loopButton.setText("Loop");
		panel_1.add(loopButton, new CellConstraints(5, 3));

		final JButton buttonStop = new JButton();
		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (tipo==CLIP && clip!=null && clip.isRunning()){
					stopClip();
				}else if (tipo==LINE && al!=null && al.isAlive()){
					stopStreamLine();
				}else if (tipo==MIDI && seq!=null && sequencer!=null && sequencer.isRunning()){
					stopMidi();
				}
			}
		});
		buttonStop.setText("Stop");
		panel_1.add(buttonStop, new CellConstraints(5, 5));

		// 'Clip looping' title
	    Border blackline = BorderFactory.createLineBorder(Color.black);
	    TitledBorder loopTitle = BorderFactory.createTitledBorder(
	                              blackline, "Control");

		panel_2 = new JPanel();
		panel_2.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC}));
		// panel_1.add(panel_2, new CellConstraints(1, 9, 5, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
		panel_2.setBorder(loopTitle);

		final JLabel muteLabel = new JLabel();
		muteLabel.setText("Mute");
		panel_2.add(muteLabel, new CellConstraints());

		final JLabel panLabel = new JLabel();
		panLabel.setText("Pan");
		panel_2.add(panLabel, new CellConstraints(1, 5));

		pantext = new JTextField();
		pantext.setEditable(false);
		panel_2.add(pantext, new CellConstraints(3, 5));

		final JLabel masterGainLabel = new JLabel();
		masterGainLabel.setText("Master Gain");
		panel_2.add(masterGainLabel, new CellConstraints(1, 9));

		masterGatext = new JTextField();
		masterGatext.setEditable(false);
		panel_2.add(masterGatext, new CellConstraints(3, 9));

		final JLabel sampleRateLabel = new JLabel();
		sampleRateLabel.setText("Sample Rate");
		panel_2.add(sampleRateLabel, new CellConstraints(1, 13));

		sampleRateText = new JTextField();
		sampleRateText.setEditable(false);
		panel_2.add(sampleRateText, new CellConstraints(3, 13));

		muteCheckBox = new JCheckBox();
		muteCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				BooleanControl muteControl = (BooleanControl) clip.getControl( BooleanControl.Type.MUTE );
				muteControl.setValue((((JCheckBox)e.getSource()).isSelected()));
			}
		});
		muteCheckBox.setEnabled(false);
		panel_2.add(muteCheckBox, new CellConstraints(3, 1));

		panTextMin = new JTextField();
		panTextMin.setEditable(false);
		panel_2.add(panTextMin, new CellConstraints(5, 5));

		panTextMax = new JTextField();
		panTextMax.setEditable(false);
		panel_2.add(panTextMax, new CellConstraints(7, 5));

		masterGaTextMin = new JTextField();
		masterGaTextMin.setEditable(false);
		panel_2.add(masterGaTextMin, new CellConstraints(5, 9));

		masterGaTextMax = new JTextField();
		masterGaTextMax.setEditable(false);
		panel_2.add(masterGaTextMax, new CellConstraints(7, 9));

		sampleRateTextMin = new JTextField();
		sampleRateTextMin.setEditable(false);
		panel_2.add(sampleRateTextMin, new CellConstraints(5, 13));

		sampleRateTextMax = new JTextField();
		sampleRateTextMax.setEditable(false);
		panel_2.add(sampleRateTextMax, new CellConstraints(7, 13));

		final JLabel curValLabel = new JLabel();
		curValLabel.setText("Cur Val");
		panel_2.add(curValLabel, new CellConstraints(3, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));

		final JLabel minValLabel = new JLabel();
		minValLabel.setText("Min Val");
		panel_2.add(minValLabel, new CellConstraints(5, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));

		final JLabel maxValLabel = new JLabel();
		maxValLabel.setText("Max Val");
		panel_2.add(maxValLabel, new CellConstraints(7, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));

		masterGaSlider = new JSlider();
		masterGaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if (!startupSlider){
					masterGatext.setText(String.valueOf((float)(masterGaSlider.getValue())/10));
					FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					fc.setValue(Float.parseFloat(masterGatext.getText()));
				}
			}
		});
		panel_2.add(masterGaSlider, new CellConstraints(3, 11, 5, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		panSlider = new JSlider();
		panSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if (!startupSlider){
				pantext.setText(String.valueOf((float)(panSlider.getValue())/10));
				FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.PAN);
				fc.setValue(Float.parseFloat(pantext.getText()));
				}
			}
		});

		panel_2.add(panSlider, new CellConstraints(3, 7, 5, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		sampleRateSlider = new JSlider();
		sampleRateSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if (!startupSlider){
				sampleRateText.setText(String.valueOf((float)(sampleRateSlider.getValue())/10));
				FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
				fc.setValue(Float.parseFloat(sampleRateText.getText()));
				}
			}
		});
		panel_2.add(sampleRateSlider, new CellConstraints(3, 15, 5, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		
		masterGaSlider.setEnabled(false);
    	panSlider.setEnabled(false);
    	sampleRateSlider.setEnabled(false);

		final JTabbedPane tabbedPane = new JTabbedPane();
		panel_1.add(tabbedPane, new CellConstraints(1, 7, 5, 3, CellConstraints.FILL, CellConstraints.FILL));

		
		tabbedPane.addTab("WAV", null, panel_2, null);


		
		final JPanel panel_3 = new JPanel();
		panel_3.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default")}));
		panel_3.setBorder(loopTitle);
		tabbedPane.addTab("Midi", null, panel_3, null);

		final JLabel volumeLabel = new JLabel();
		volumeLabel.setText("Volume Precalculation");
		panel_3.add(volumeLabel, new CellConstraints());

		midiVolumeSlider = new JSlider();
		midiVolumeSlider.setEnabled(false);
		midiVolumeSlider.setMaximum(0);
		midiVolumeSlider.setMaximum(127);
		midiVolumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				int selectedIndex = trackComboBox.getSelectedIndex(); 

				stopMidi( );
				
				setVolumeSeq(seq, midiVolumeSlider.getValue(), selectedIndex);
			}
		});
		panel_3.add(midiVolumeSlider, new CellConstraints(3, 3));

		
		trackComboBox = new JComboBox();
		trackComboBox.setEnabled(false);
		trackComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				JComboBox cb = (JComboBox)e.getSource(); 
		        int selectedIndex = cb.getSelectedIndex(); 
		        if (selectedIndex>0){
		        	midiVolumeSlider.setValue(getVolumeTrack(selectedIndex));
		        	midiVolumeSlider.setEnabled(true);
		        }
			}
		});
		panel_3.add(trackComboBox, new CellConstraints(3, 1));

		final JLabel volumeLabel_1 = new JLabel();
		volumeLabel_1.setText("Fade");
		panel_3.add(volumeLabel_1, new CellConstraints(1, 5));

		fadeMidiSliderReal = new JSlider();
		fadeMidiSliderReal.setEnabled(false);
		fadeMidiSliderReal.setValue(0);
		fadeMidiSliderReal.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if (channels!=null) setVolume(fadeMidiSliderReal.getValue());
			}
		});
		fadeMidiSliderReal.setMaximum(127);
		fadeMidiSliderReal.setMinimum(0);
		panel_3.add(fadeMidiSliderReal, new CellConstraints(3, 5));

		final JLabel panLabel_1 = new JLabel();
		panLabel_1.setText("Pan");
		panel_3.add(panLabel_1, new CellConstraints(1, 7));

		panMidiSlider = new JSlider();
		panMidiSlider.setEnabled(false);
		panMidiSlider.setValue(0);
		panMidiSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if (channels!=null) setPan(panMidiSlider.getValue());
			}
		});
		panMidiSlider.setMaximum(127);
		panMidiSlider.setMinimum(0);
		panel_3.add(panMidiSlider, new CellConstraints(3, 7));

		final JLabel speedLabel = new JLabel();
		speedLabel.setText("Speed");
		panel_3.add(speedLabel, new CellConstraints(1, 9));

		speedMidiSlider = new JSlider();
		speedMidiSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				if (sequencer!=null) {
					float sfactor = 0.5f;
					if (speedMidiSlider.getValue()!=0){
						sfactor=speedMidiSlider.getValue();
					}

					sequencer.setTempoFactor(sfactor);
				}
			}
		});
		
		SpeedDic s = new SpeedDic();

		speedMidiSlider.setLabelTable(s);
		speedMidiSlider.setPaintLabels(true);
		speedMidiSlider.setValue(1);
		speedMidiSlider.setMaximum(4);
		speedMidiSlider.setSnapToTicks(true);
		speedMidiSlider.setEnabled(false);
		panel_3.add(speedMidiSlider, new CellConstraints(3, 9));

		final JLabel muteAllLabel = new JLabel();
		muteAllLabel.setText("Mute all");
		panel_3.add(muteAllLabel, new CellConstraints(1, 11));

		midiMute = new JCheckBox();
		midiMute.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				Track tracks[] = seq.getTracks( );
				for (int i=0;i<tracks.length;i++){
					sequencer.setTrackMute(i, (((JCheckBox)e.getSource()).isSelected()));
				}
//			    boolean muted = sequencer.getTrackMute(4);
//			    if (!muted)
//			      // muting failed
				
			}
		});
		
		panel_3.add(midiMute, new CellConstraints(3, 11));
	}
	JPanel panel_2;
	JCheckBox muteCheckBox;
	JSlider sampleRateSlider, panSlider, masterGaSlider, midiVolumeSlider;
	JSlider fadeMidiSliderReal;
	JSlider panMidiSlider;
	JSlider speedMidiSlider;
	JCheckBox midiMute;
	
	public File getFileToRead() {
		JFileChooser chooser = new JFileChooser();
		// Note: source for ExampleFileFilter can be found in FileChooserDemo,
		// under the demo/jfc directory in the Java 2 SDK, Standard Edition.
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("wav");
		filter.addExtension("au");
		filter.addExtension("mid");
		filter.setDescription("WAV, AU & MID Audio File");
		chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(panel);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			System.out.println("You chose to open this file: "
					+ chooser.getSelectedFile().getName());
			return file;
			// //This is where a real application would open the file.
			// log.append("Opening: " + file.getName() + "." + newline);
		} else {
			System.out.println("Non approvata scelta file.");
			return null;
		}
	}

	private DecimalFormat df;

	private void checkDuration(Clip clip) {
		// duration (in secs) of the clip
		double duration = clip.getMicrosecondLength() / 1000000.0; // new
		if (duration <= 1.0) {
			System.out.println("WARNING. Duration <= 1 sec : "
					+ df.format(duration) + " secs");
			System.out
					.println("         The clip may not play in J2SE 1.5 -- make it longer");
		} else {
			df = new DecimalFormat("0.#"); // 1 dp
			System.out.println("Duration: " + df.format(duration) + " secs");
			labelDuration.setText("Duration: " + df.format(duration) + " secs");
		}

	} // end of checkDuration()

	private void checkDuration(AudioInputStream stream) {
		long milliseconds = (long) ((stream.getFrameLength() * 1000) / stream
				.getFormat().getFrameRate());
		double duration = milliseconds / 1000.0;
		if (duration <= 1.0) {
			System.out.println("WARNING. Duration <= 1 sec : " + duration
					+ " secs");
			System.out
					.println("         The sample may not play in J2SE 1.5 -- make it longer");
		} else {
			df = new DecimalFormat("0.#"); // 1 dp
			System.out.println("Duration: " + duration + " secs");
			labelDuration.setText("Duration: " + df.format(duration) + " secs");
		}

	} // end of checkDuration()

	private void checkDuration(Sequence seq) {
		df = new DecimalFormat("0.#"); // 1 dp
		double duration = ((double) seq.getMicrosecondLength( )) / 1000000;
        System.out.println("Duration: " + df.format(duration)+" secs");
        labelDuration.setText("Duration: " + df.format(duration) + " secs");
	}
	
	int counter = 0;
	HashMap<Integer, File> l = new HashMap<Integer, File>();

	Sequencer sequencer;
	// Synthesizer synthesizer;
	Sequence seq;
	
	public void setFileToShow(File f, boolean addToList) {
		
		if (f != null) {
			resetControls();
			try {
				long length = f.length();
				double lkb = length / 1000;
				df = new DecimalFormat("0.00");
				sizeText.setText("Size: " + df.format(lkb) + "KByte");

				nomeTextField.setText(f.getName());

				if (f.getName().indexOf(".mid")>=0){
		
					if (sequencer!=null && sequencer.isOpen()){
						sequencer.close();
						seq=null;
					}
					
					
					sequencer =	AudioManager.initSequencer();
					
					seq = MidiSystem.getSequence( f );
					
					tipoStream.setText("MIDI");
					
					checkDuration(seq);
					tipo=MIDI;
					if (addToList) {
						l.put(new Integer(counter), f);
						model.add(counter, f.getName());
						counter++;
					}
					clip=null;
					resetControls();
					loadTrack();
					
				}else{
					trackComboBox.setEnabled(false);
					midiVolumeSlider.setEnabled(false);
					trackComboBox.removeAll();
					midiVolumeSlider.setValue(0);
					if (sequencer!=null && sequencer.isOpen()){
						sequencer.close();
						seq=null;
					}
					
					// link an audio stream to the sound clip's file
					AudioInputStream streamLoc = AudioSystem.getAudioInputStream(f);
					AudioFormat format = streamLoc.getFormat();
	
					tipoStream.setText(format.toString());
	
					
					format = AudioManager.convertULAWALAWtoPCM(format, streamLoc);
					
	
					if (length > 1000000) {
						DataLine.Info info = new DataLine.Info(
								SourceDataLine.class, format);
						if (!AudioSystem.isLineSupported(info)) {
							System.out.println("Line does not support: " + format);
						} else {
							SourceDataLine lineLoc = AudioManager.createOutput(format, info);
	
							clip = null;
	
							tipo=LINE;
							
							al=new AudioLineThread( lineLoc,streamLoc);
							
							
							
							checkDuration(streamLoc);
						}
	
					} else {
						DataLine.Info info = new DataLine.Info(Clip.class, format);
	
						// make sure sound system supports data line
						if (!AudioSystem.isLineSupported(info)) {
							System.out.println("Unsupported Clip File: "
									+ f.getAbsolutePath());
						} else {
							if (addToList) {
								l.put(new Integer(counter), f);
								model.add(counter, f.getName());
								counter++;
							}
							clip = AudioManager.getClip(info, streamLoc);
	
							tipo=CLIP;
							
							checkDuration(clip);
							showControls( );
						}
					}
				}

			} catch (UnsupportedAudioFileException audioException) {
				System.out.println("Unsupported audio file: " + f.getName());
				// System.exit(0);
			} catch (LineUnavailableException noLineException) {
				System.out.println("No audio line available for : "
						+ f.getName());
				// System.exit(0);
			} catch (IOException ioException) {
				System.out.println("Could not read: " + f.getName());
				// System.exit(0);
			} catch (InvalidMidiDataException e) {
				System.out.println("Unsupported midi file: " + f.getName());
			} catch (MidiUnavailableException e) {
				System.out.println("No midi sequencer available");
			}
			catch (Exception ex) {
				System.out.println("Problem with " + f.getName());
				// System.exit(0);
			}

		}
	}

	private void resetControls(){
    	muteCheckBox.setEnabled(false);
    	masterGaSlider.setEnabled(false);
    	panSlider.setEnabled(false);
    	sampleRateSlider.setEnabled(false);
    	pantext.setEditable(false);
    	masterGatext.setEditable(false);
    	sampleRateText.setEditable(false);
    	pantext.setText("");
    	masterGatext.setText("");
    	sampleRateText.setText("");
    	panTextMin.setText("");
    	panTextMax.setText("");
    	masterGaTextMin.setText("");
    	masterGaTextMax.setText("");
    	sampleRateTextMin.setText("");
    	sampleRateTextMax.setText("");
    	trackComboBox.setEnabled(false);
		midiVolumeSlider.setEnabled(false);
		trackComboBox.removeAll();
		midiVolumeSlider.setValue(0);
		fadeMidiSliderReal.setEnabled(false);
		panMidiSlider.setEnabled(false);
		speedMidiSlider.setEnabled(false);
		midiMute.setEnabled(false);
	}
	boolean startupSlider=false;
	private void showControls( )
    { 
		
		if (clip != null) {
    	// Control.Type

			startupSlider=true;
        Control cntls[] = clip.getControls( );
        for(int i=0; i<cntls.length; i++){
          Control.Type ct = cntls[i].getType();
          if (ct.equals(FloatControl.Type.MASTER_GAIN)){
//        	  masterGatext.setEditable(true);
        	  FloatControl fc = (FloatControl) cntls[i];
        	  masterGatext.setText(String.valueOf(fc.getValue()));
        	  masterGaTextMin.setText(String.valueOf(fc.getMinimum()));
        	  masterGaTextMax.setText(String.valueOf(fc.getMaximum()));
        	  masterGaSlider.setEnabled(true);
        	  masterGaSlider.setMinimum(Math.round(fc.getMinimum()*10));
        	  masterGaSlider.setMaximum(Math.round(fc.getMaximum()*10));
        	  masterGaSlider.setValue(Math.round(fc.getValue()*10));
          }else if (ct.equals(FloatControl.Type.PAN)){
//        	  pantext.setEditable(true);
        	  FloatControl fc = (FloatControl) cntls[i];
        	  pantext.setText(String.valueOf(fc.getValue()));
        	  panTextMin.setText(String.valueOf(fc.getMinimum()));
        	  panTextMax.setText(String.valueOf(fc.getMaximum()));
        	  panSlider.setEnabled(true);
        	  panSlider.setMinimum(Math.round(fc.getMinimum()*10));
        	  panSlider.setMaximum(Math.round(fc.getMaximum()*10));
        	  panSlider.setValue(Math.round(fc.getValue()*10));
          }else if (ct.equals(FloatControl.Type.SAMPLE_RATE)){
//        	  sampleRateText.setEditable(true);
        	  FloatControl fc = (FloatControl) cntls[i];
        	  sampleRateText.setText(String.valueOf(fc.getValue()));
        	  sampleRateTextMin.setText(String.valueOf(fc.getMinimum()));
        	  sampleRateTextMax.setText(String.valueOf(fc.getMaximum()));
        	  sampleRateSlider.setEnabled(true);
        	  sampleRateSlider.setMinimum(Math.round(fc.getMinimum()*10));
        	  sampleRateSlider.setMaximum(Math.round(fc.getMaximum()*10));
        	  sampleRateSlider.setValue(Math.round(fc.getValue()*10));
          }else if (ct.equals(BooleanControl.Type.MUTE)){
        	  muteCheckBox.setSelected(false);
        	  muteCheckBox.setEnabled(true);
          }
          System.out.println( i + ".  " + cntls[i].toString( ) );
        }
        startupSlider=false;
      }
    }
	
	private void playClip(boolean loop) {
		if (clip != null) {
			System.out.println("Playing...");
			if (loop)
				clip.loop(10);
			else
				clip.start(); // start playing
		}
	}

	private void stopClip() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}

	private void playStreamLine()
	/*
	 * Read the sound file in chunks of bytes into buffer, and pass them on
	 * through the SourceDataLine
	 */
	{
		
		// al = new AudioLineThread(line,stream);
		al.run();
//		if (line != null) {
//			int numRead = 0;
//			byte[] buffer = new byte[line.getBufferSize()];
//
//			line.start();
//			// read and play chunks of the audio
//			try {
//				int offset;
//				while ((numRead = stream.read(buffer, 0, buffer.length)) >= 0) {
//					// System.out.println("read: " + numRead);
//					offset = 0;
//					while (offset < numRead)
//						offset += line.write(buffer, offset, numRead - offset);
//				}
//			} catch (IOException e) {
//				System.out.println(e.getMessage());
//			}
//
//			// wait until all data is played, then close the line
//			// System.out.println("drained start");
//			line.drain();
//			// System.out.println("drained end");
//			line.stop();
//			line.close();
//		} // end of play()
	}
	
	private void stopStreamLine(){
		
		if (al.isAlive())
		{
			al=null;
		}
	}
	
	private void playMidi( )
    { if ((sequencer != null) && (seq != null)) {
        try {
          sequencer.setSequence(seq);  // load MIDI into sequencer
          sequencer.start( );   // start playing it
        }
        catch (InvalidMidiDataException e) {
          System.out.println("Corrupted/invalid midi file: ");

        }
      }
    }
	private void stopMidi( )
    { if ((sequencer != null) && (seq != null)) {
        
          sequencer.stop();   // start playing it
        
      }
    }
	

	private MidiChannel[] channels; 
	
	private void loadTrack() {
		Synthesizer synthesizer = null;
//		try {
//			synthesizer = MidiSystem.getSynthesizer();
//		} catch (MidiUnavailableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
		 if (!(sequencer instanceof Synthesizer)) {
		        System.out.println("Linking the MIDI sequencer and synthesizer");
		        synthesizer = MidiSystem.getSynthesizer();
			    synthesizer.open();  // new
		        Receiver synthReceiver = synthesizer.getReceiver();
		        Transmitter seqTransmitter = sequencer.getTransmitter();
		        seqTransmitter.setReceiver(synthReceiver);
		      }
		      else 
		        synthesizer = (Synthesizer) sequencer;
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instrument[] inst = synthesizer.getAvailableInstruments();
		channels = synthesizer.getChannels();
		fadeMidiSliderReal.setValue(getMaxVolume());
		panMidiSlider.setValue(getMaxPan());
		fadeMidiSliderReal.setEnabled(true);
		panMidiSlider.setEnabled(true);
		speedMidiSlider.setEnabled(true);
		speedMidiSlider.setValue(1);
		midiMute.setEnabled(true);
		
		trackComboBox.removeAll();
		trackComboBox.setEnabled(false);
		Track tracks[] = seq.getTracks(); // get all the tracks
		for (int i = 0; i < tracks.length; i++) // iterate through them
		{
			Track t = tracks[i];
			String instrum = "";
			for (int ev = 0; ev < t.size(); ev++) {
				MidiEvent me = t.get(ev);

				MidiMessage mm = me.getMessage();

				int status = mm.getStatus();

				if ((status & 0xF0) == ShortMessage.NOTE_ON) {
					ShortMessage smsg = (ShortMessage) mm;
					int channel = smsg.getChannel();
					instrum += inst[channel].getName();
					break;
				}

			}

			trackComboBox.addItem(instrum);
			trackComboBox.setEnabled(true);
		}

	}
	private static final int VOLUME_CONTROLLER = 7;
	
	public int getMaxVolume()
	  // return the max level for all the volume controllers
	  {  
	    int maxVol = 0;
	    int channelVol;
	    for (int i=0; i < channels.length; i++) {
	      channelVol = channels[i].getController(VOLUME_CONTROLLER);
	      if (maxVol < channelVol)
	        maxVol = channelVol;
	    }
	    return maxVol; 
	 } // end of getVolume()
	
	public void setVolume(int vol)
	  // set all the controller's volume levels to vol
	  {      
	    for (int i=0; i < channels.length; i++)
	      channels[i].controlChange(VOLUME_CONTROLLER, vol);

	    // showChannelVolumes();
	  }
    	 
	
	
	
    // global constants
    // private static final int BALANCE_CONTROLLER = 8; //not working?
    private static final int PAN_CONTROLLER = 10;

    public int getMaxPan( )
    // return the max value for all the pan controllers
    { int maxPan = 0;
      int channelPan;
      for (int i=0; i < channels.length; i++) {
        channelPan = channels[i].getController(PAN_CONTROLLER);
        if (maxPan < channelPan)
          maxPan = channelPan;
      }
      return maxPan;
    }

    public void setPan(int panVal)
    // set all the controller's pan levels to panVal
    { for (int i=0; i < channels.length; i++)
        channels[i].controlChange(PAN_CONTROLLER, panVal);
    }
	
	
	
	
	
	
	
	
	private int getVolumeTrack(int idt){
		Track tracks[] = seq.getTracks( );
	      MidiEvent event;
	      MidiMessage message;
	      ShortMessage sMessage, newShort;

	      for (int i=0; i < tracks[idt].size(); i++) {
	    	 
	    	  Track track = tracks[idt];
	        event = track.get(i);          // get the event
	        message = event.getMessage( );  // get its MIDI message
	        long tick = event.getTick( );   // get its tick
	        if (message instanceof ShortMessage) {
	          sMessage = (ShortMessage) message;

	          // check if the message is a NOTE_ON
	          if (sMessage.getCommand( ) == ShortMessage.NOTE_ON) {
	        	  return sMessage.getData2( );
	          }
	        }
	      }
		return 0;
	}
	
	private void setVolumeSeq(Sequence seq, int vol, int track)
	{
		Track tracks[] = seq.getTracks( );
		setMidiVolume(tracks[track], tracks[track].size( ), vol);
	}
	private void setVolumeSeq(Sequence seq, int vol)
    { Track tracks[] = seq.getTracks( );     // get all the tracks
      for(int i=0; i < tracks.length; i++)  // iterate through them
        setMidiVolume(tracks[i], tracks[i].size( ), vol);
    }
    private void setMidiVolume(Track track, int size, int vol)
    {
      MidiEvent event;
      MidiMessage message;
      ShortMessage sMessage, newShort;

      for (int i=0; i < size; i++) {
        event = track.get(i);          // get the event
        message = event.getMessage( );  // get its MIDI message
        long tick = event.getTick( );   // get its tick
        if (message instanceof ShortMessage) {
          sMessage = (ShortMessage) message;

          // check if the message is a NOTE_ON
          if (sMessage.getCommand( ) == ShortMessage.NOTE_ON) {
        	  
             // int curVol = sMessage.getData2( );
             int newVol = (vol > 127) ? 127 : vol;
             newShort = new ShortMessage( );
             try {
               newShort.setMessage(ShortMessage.NOTE_ON,
                                    sMessage.getChannel( ),
                                    sMessage.getData1( ), newVol);
               track.remove(event);
               track.add( new MidiEvent(newShort,tick) );
             }
             catch ( InvalidMidiDataException e)
             {  System.out.println("Invalid data");  }
          }
        }
      }
    }  // end of doubleVolume( )
}
