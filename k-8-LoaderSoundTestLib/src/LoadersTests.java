// LoadersTests.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* LoadersTests demonstrates the use of MidisLoader and ClipsLoader.
   Midi sequences and clips can be played, paused, resumed, stopped,
   and looped. 

   Multiple clips can be played at once, due to the way that Clip
   objects are executed. Only a single midi sequence can play at
   a time since MidisLoader only creates a single Sequencer.

   Many clips can be played while a midi sequence is playing.

   The theme of this example is the farm. Try playing the 'Old McDonald'
   sequence, and set the dog, sheep, etc clips to loop at the same time.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;



public class LoadersTests extends JFrame 
				implements ActionListener, ItemListener, SoundsWatcher
{
  // short names for the midi sequences
  private final static String[] shortSongNames =
           {"farmer", "mary", "mcdonald", "baa"};

  // long names, used in the combo box display
  private final static String[] songNames = 
           {"The Farmer in the Dell", "Mary had a Little Lamb",
            "Old McDonald had a Farm", "Bach Sheep"};

  // clip image names (used to label the checkboxes)
  private final static String[] names = {"dog", "cat", "sheep", "chicken"};

  // the clip and midi sound information files, located in Sounds/
  private final static String SNDS_FILE = "clipsInfo.txt";
  private final static String MIDIS_FILE = "midisInfo.txt";

  // for colouring the "Loop" button upon activaion
  private final static Color grayGreen = new Color(88, 110, 84);
  private final static Color lightGreen = new Color(90, 120, 20);

  // GUI controls
  private JButton playJbut, pauseJbut, stopJbut, loopJbut;
  private JCheckBox dogJck, catJck, sheepJck, chickenJck;
  private JComboBox namesJcb;

  // clip loop flags, stored in the order:
  //               "dog", "cat", "sheep", "chicken"
  private boolean[] clipLoops = {false, false, false, false};

  private boolean isPauseButton;   // for midi songs

  private Color background;  // stores the "Loop" button's background colour

  private ClipsLoader clipsLoader;
  private MidisLoader midisLoader;


  public LoadersTests()
  {
    super( "Sounds Tests" );
    Container c = getContentPane();
    c.setLayout( new BorderLayout() );   

    SoundsPanel sp = new SoundsPanel(this);   // the images canvas
    c.add( sp, BorderLayout.CENTER);
    initGUI(c);                        // the rest of the controls

    // initialise the loaders
    clipsLoader = new ClipsLoader(SNDS_FILE); 
    clipsLoader.setWatcher("dog", this);     // watch the dog clip

    midisLoader = new MidisLoader(MIDIS_FILE);
    midisLoader.setWatcher(this);            // watch midi playing

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        midisLoader.close();   // to shut down the sequencer
        System.exit(0);
      }
    });

    pack();
    setResizable(false);    // fixed size display
    centerFrame();
    setVisible(true);
  } // end of LoadersTests()


  private void centerFrame()
  // Place this JFrame in the center of the screen 
  {
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension sd = tk.getScreenSize();
    Dimension fd = getSize();
    setLocation(sd.width/2 - fd.width/2, sd.height/2 - fd.height/2);
  }  // end of centerFrame()


  private void initGUI(Container c)
  /*  The right hand side is a series of 4 checkboxes for looping
      the clips. 

      The left hand side is a series of buttons for
      playing, pausing/resuming, stopping, looping the chosen
      midi sequence. A list of the sequences is given in a combo box.
  */
  {
    // the right-hand side of the control panel

    // checkboxes for playing clips in loops
    JPanel p1 = new JPanel();
    p1.setLayout( new GridLayout(2,2));

    dogJck = new JCheckBox(names[0]);    // for the dog image
    dogJck.addItemListener(this);
    p1.add(dogJck);

    catJck = new JCheckBox(names[1]);    // cat
    catJck.addItemListener(this);
    p1.add(catJck);

    sheepJck = new JCheckBox(names[2]);   // sheep
    sheepJck.addItemListener(this);
    p1.add(sheepJck);

    chickenJck = new JCheckBox(names[3]); // chicken
    chickenJck.addItemListener(this); 
    p1.add(chickenJck);

    // 'Clip looping' title
    Border blackline = BorderFactory.createLineBorder(Color.black);
    TitledBorder loopTitle = BorderFactory.createTitledBorder(
                              blackline, "Clip Looping");

    
    // bring all the parts of the right-hand side together
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout( new BoxLayout(rightPanel, BoxLayout.Y_AXIS) );
    rightPanel.add( p1);
    rightPanel.setBorder(loopTitle); 


    // -----------------------------------
    // the left hand side of the control panel
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout( new BoxLayout(leftPanel, BoxLayout.Y_AXIS) );

    // first row of midi buttons
    JPanel p2 = new JPanel();
    p2.setLayout( new BoxLayout(p2, BoxLayout.X_AXIS) );

    // midi control buttons: "Play", "Pause/Resume", "Stop"
    playJbut = new JButton("Play");
    playJbut.addActionListener(this);
    p2.add(playJbut);

    pauseJbut = new JButton("Resume");  // this text helps sets the max dimensions
    Dimension d = pauseJbut.getMaximumSize();
    pauseJbut.setPreferredSize( d );   // fix the button's dimensions
    pauseJbut.setMinimumSize( d );
    pauseJbut.setMaximumSize( d );
    pauseJbut.setText("Pause");     // change the text
    pauseJbut.setEnabled(false);    // disabled at start
    pauseJbut.addActionListener(this);
    p2.add(pauseJbut);
    isPauseButton = true;

    stopJbut = new JButton("Stop");
    stopJbut.addActionListener(this);
    p2.add(stopJbut);
    stopJbut.setEnabled(false);    // disabled at start

    leftPanel.add(p2);   // the first row of midi buttons


    // midi looping button which appears on the second row of buttons
    loopJbut = new JButton("Loop");
    loopJbut.addActionListener(this);

    JPanel p3 = new JPanel();   // used to make the loop button left justified
    p3.setLayout( new FlowLayout(FlowLayout.LEFT));
    p3.add(loopJbut);
    leftPanel.add(p3);

    // song names combo box
    namesJcb = new JComboBox(songNames);
    leftPanel.add(namesJcb);

    // ------------------------------------
    // combine the left and right hand sides of the control panel
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout( new BoxLayout(controlPanel, BoxLayout.X_AXIS) );
    controlPanel.add(leftPanel);
    controlPanel.add(rightPanel);
   
    c.add( controlPanel, BorderLayout.SOUTH);
  } // end of initGUI()



  public void actionPerformed(ActionEvent e)
  /* Triggered by a "Play", "Loop", "Pause/Resume", "Stop" button press.
     The relevant method in MidisLoader is called.
     A lot of effort is spent on disabling/enabling the right buttons.
  */
  {
    // which song is currently selected?
    String songName = shortSongNames[ namesJcb.getSelectedIndex() ];

    if (e.getSource() == playJbut) {    // "Play" pressed
      midisLoader.play(songName, false);    // play the sequence, no looping
      playJbut.setEnabled(false);
      loopJbut.setEnabled(false);
      pauseJbut.setEnabled(true);
      stopJbut.setEnabled(true); 
    }
    else if (e.getSource() == loopJbut) {    // "Loop" pressed
      midisLoader.play(songName, true);   // play the sequence, with looping
      playJbut.setEnabled(false);
      background = loopJbut.getBackground();
      loopJbut.setBackground(lightGreen);  // highlight the looping button 
      loopJbut.setEnabled(false);
      pauseJbut.setEnabled(true);
      stopJbut.setEnabled(true); 
    }
    else if (e.getSource() == pauseJbut) {    // "Pause/Resume" pressed
      if (isPauseButton) {   
        midisLoader.pause();    // pause the sequence
        pauseJbut.setText("Resume");      // Pause --> Resume
        stopJbut.setEnabled(false); 
     }
      else {
        midisLoader.resume();  // resume the sequence
        pauseJbut.setText("Pause");      // Resume --> Pause
        stopJbut.setEnabled(true); 
      }
      isPauseButton = !isPauseButton;
    }
    else if (e.getSource() == stopJbut) {  // "Stop" pressed
      midisLoader.stop();   // stop the sequence
      playJbut.setEnabled(true);
      loopJbut.setEnabled(true);
      loopJbut.setBackground(background); // syop highlighting the loop button
      pauseJbut.setEnabled(false);
      stopJbut.setEnabled(false); 
    }
    else
      System.out.println("Action unknown");
  }  // end of actionPerformed()



  public void itemStateChanged(ItemEvent e)
  /* Triggered by selecting/deselecting a clip looping checkbox.
     The correct ClipsLoader method is called.
  */
  {  // which checkbox was selected?
    String name = ((JCheckBox)e.getItem()).getText();
    boolean isSelected = (e.getStateChange() == e.SELECTED) ? true : false ;
    // System.out.println("Clicked " + name + ": " + isSelected);

    boolean switched = false;
    for (int i=0; i < names.length; i++)
      if (names[i].equals(name)) {
        clipLoops[i] = !clipLoops[i];   // update the clip loop flags
        switched = true;
        break;
      }
    if (!switched)
      System.out.println("Item unknown");
    else {
      if (!isSelected)   // the user just switched off looping for name
        clipsLoader.stop(name);    // so stop the playing of name's clip
    }
  }  // end of itemStateChanged()


  public void atSequenceEnd(String name, int status)
  // can be called by the ClipsLoader and MidisLoader
  {
    if (status == SoundsWatcher.STOPPED)
      System.out.println(name + " stopped");
    else if (status == SoundsWatcher.REPLAYED)
      System.out.println(name + " replayed");
    else
      System.out.println(name + " status code: " + status);
  }  // end of atSequenceEnd()


  // -------------------- sound manipulation ------------


  public void playClip(String name, int i)
  // called from SoundsPanel to play a given clip (looping or not)
  {  clipsLoader.play(name, clipLoops[i]);  }


// ------------------------------------------------------

   public static void main( String args[] )
   { new LoadersTests();  } 

} // end of LoadersTests class
