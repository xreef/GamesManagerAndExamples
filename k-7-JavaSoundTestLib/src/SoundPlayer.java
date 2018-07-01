
// SoundPlayer.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Offer a series of AudioClips in a pop-down list which
   can be played once, looped or stopped.

   It is possible to have multiple clips playing/looping
   at the same time. 

   A stop() button terminates all the currently playing 
   AudioClips.

   Based on the Java sound tutorial example, SoundApplication
*/

import javax.swing.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class SoundPlayer extends JFrame implements ActionListener
{
  private final static String SOUND_DIR = "Sounds/";

  private final static String[] soundFNms = 
           {"spacemusic.au", "tiger.aiff", "mcdonald.mid", "dog.wav"};

  private HashMap soundsMap;         // stores <fnm,AudioClip> pairs
  private ArrayList playingClips;    // list of playing AudioClips
        
  private JComboBox playListJcb;
  private JButton playButton, loopButton, stopButton;
  private JLabel statusLabel;
        


  public SoundPlayer() 
  {
    super( "Sound Application" );

    playingClips = new ArrayList();
    initGUI();
    loadSounds();

    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setSize( new Dimension(400,100) );
    setResizable(false);    // fixed size display
    setVisible(true);
  } // end of SoundPlayer()


  private void initGUI()
  /* Create a combox listing the AudioClips, and the "Play",
     "Loop", and "Stop" buttons. */
  {
    Container c = getContentPane();
    c.setLayout( new BorderLayout() );   


    playListJcb = new JComboBox(soundFNms);

    playButton = new JButton("Play");
    playButton.addActionListener(this);

    loopButton = new JButton("Loop");
    loopButton.addActionListener(this);

    stopButton = new JButton("Stop");
    stopButton.addActionListener(this);
        
    statusLabel = new JLabel("Click Play or Loop to play the selected sound file.");
                
    JPanel controlPanel = new JPanel();
    controlPanel.add(playListJcb);
    controlPanel.add(playButton);
    controlPanel.add(loopButton);
    controlPanel.add(stopButton);           
                
    JPanel statusPanel = new JPanel();
    statusPanel.add(statusLabel);
                
    c.add(controlPanel, BorderLayout.CENTER);
    c.add(statusPanel, BorderLayout.SOUTH);
  }  // end of initGUI()



  private void loadSounds()
  /* Load the AudioClips, storing them in a HashMap, soundsMap, whose
     key is the filename of the clip. */
  {
    soundsMap = new HashMap();
    for (int i=0; i < soundFNms.length; i++) {
      AudioClip clip = Applet.newAudioClip( 
                getClass().getResource(SOUND_DIR + soundFNms[i]) );
      if (clip == null)
        System.out.println("Problem loading " +  SOUND_DIR + soundFNms[i]);
      else 
        soundsMap.put(soundFNms[i], clip);
    }
  } // end of loadSounds()


  public void actionPerformed(ActionEvent event) 
  // respond to button presses
  {
    Object source = event.getSource();
    if (source == playButton)
      playMusic(false);    // false means not looping
    else if (source == loopButton)
      playMusic(true);     // start looping play
    else if (source == stopButton)
      stopMusic();
  } // end of actionPerformed()


  private void playMusic(boolean toLoop)
  /* Retrieve the relevant AudioClip, and start it playing or
     looping. Store a reference to the clip in the playingClips
     ArrayList since it is playing. */
  {
    String chosenFile = (String) playListJcb.getSelectedItem();

    // try to get the AudioClip.
    AudioClip audioClip = (AudioClip) soundsMap.get(chosenFile);
    if (audioClip == null) {
      statusLabel.setText("Sound " + chosenFile + " not loaded");
      return;
    }

    if (toLoop)
      audioClip.loop();
    else
      audioClip.play();     // play it once

    playingClips.add(audioClip);   // store a reference to the playing clip
    String times = (toLoop) ? " repeatedly" : " once";
    statusLabel.setText("Playing sound " + chosenFile + times);
  }  // end of playMusic()



  private void stopMusic()
  /* Stop all the music by calling stop() on all the references in
     playingClips. The issue is that some of the clips may have
     already finished playing by themselves, but there is no way
     to detect that when using the AudioClip class. */
  {
    if (playingClips.isEmpty())
      statusLabel.setText("Nothing to stop");
    else {
      AudioClip audioClip;
      for(int i=0; i < playingClips.size(); i++) {
        audioClip = (AudioClip) playingClips.get(i);
        audioClip.stop();   // may already have stopped, but calling
                            // stop() again does no harm
      }
      playingClips.clear();
      statusLabel.setText("Stopped all music");
    }
  }  // end of stopMusic()


  // -----------------------------------------

  public static void main(String args[]) 
  {  new SoundPlayer();  }

} // end of SoundPlayer class
