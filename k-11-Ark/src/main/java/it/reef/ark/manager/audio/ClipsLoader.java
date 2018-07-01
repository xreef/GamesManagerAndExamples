package it.reef.ark.manager.audio;

// ClipsLoader.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* ClipsLoader  stores a collection of ClipInfo objects
 in a HashMap whose keys are their names. 

 The name and filename for a clip is obtained from a sounds
 information file which is loaded when ClipsLoader is created.
 The information file is assumed to be in Sounds/.

 ClipsLoader allows a specified clip to be played, stopped, 
 resumed, looped. A SoundsWatcher can be attached to a clip.
 All of this functionality is handled in the ClipInfo object; 
 ClipsLoader simply redirects the method call to the right ClipInfo.

 It is possible for many clips to play at the same time, since
 each ClipInfo object is responsible for playing its clip.
 */

import it.reef.ark.element.action.SoundsWatcher;
import it.reef.ark.manager.LoaderWatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClipsLoader {
	
	private static final Log log = LogFactory.getLog(ClipsLoader.class);
	
	private Class classForStream;
	
	private String SOUND_DIR = "Sounds/";
	private String FILE_CONF = "sound.cfg";

	private HashMap<String, ClipInfo> clipsMap;

	private LoaderWatcher clw;
	
	/*
	 * The key is the clip 'name', the object (value) is a ClipInfo object
	 */

	public ClipsLoader(String path, String soundsFnm,LoaderWatcher clw) {
		this.clw=clw;
		clipsMap = new HashMap<String, ClipInfo>();
		SOUND_DIR = path;
		FILE_CONF = soundsFnm;
		loadSoundsFile();
	}

	public ClipsLoader(LoaderWatcher clw) {
		this.clw=clw;
		clipsMap = new HashMap<String, ClipInfo>();
	}


	public ClipsLoader(Class cfs, String soundsFnm,LoaderWatcher clw) {
		this.clw=clw;
		clipsMap = new HashMap<String, ClipInfo>();
		SOUND_DIR = "";
		FILE_CONF = soundsFnm;
		this.classForStream = cfs;
		loadSoundsFile();
	}
	
	

	private int getClipNumber(){
		int cl=0;
		String sndsFNm = SOUND_DIR + FILE_CONF;
		System.out.println("Reading file: " + sndsFNm);
		try {
			InputStream in = null;
			if (classForStream!=null){
				in = classForStream.getResourceAsStream(FILE_CONF);
			}else{
				in = this.getClass().getResourceAsStream(sndsFNm);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// BufferedReader br = new BufferedReader( new FileReader(sndsFNm));
			StringTokenizer tokens;
			String line, name, fnm;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) // blank line
					continue;
				if (line.startsWith("//")) // comment
					continue;

				tokens = new StringTokenizer(line);
				if (tokens.countTokens() != 2)
					log.warn("Wrong no. of arguments for " + line);
				else {
					name = tokens.nextToken();
					fnm = tokens.nextToken();
					cl++;
				}
			}
			br.close();
		} catch (IOException e) {
			log.fatal("Error reading file: " + sndsFNm);
			System.exit(1);
		}
		return cl;
	}
	
	private void loadSoundsFile()
	/*
	 * The file format are lines of: <name> <filename> // a single sound file
	 * and blank lines and comment lines.
	 */
	{
		int clCaric = 0;
		if (this.clw!=null) this.clw.setElementiDaCaricare(this.getClipNumber(),"Caricamento Clip");
		String sndsFNm = SOUND_DIR + FILE_CONF;
		System.out.println("Reading file: " + sndsFNm);
		try {
			InputStream in = null;
			if (classForStream!=null){
				in = classForStream.getResourceAsStream(FILE_CONF);
			}else{
				in = this.getClass().getResourceAsStream(sndsFNm);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// BufferedReader br = new BufferedReader( new FileReader(sndsFNm));
			StringTokenizer tokens;
			String line, name, fnm;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) // blank line
					continue;
				if (line.startsWith("//")) // comment
					continue;

				tokens = new StringTokenizer(line);
				if (tokens.countTokens() != 2)
					log.warn("Wrong no. of arguments for " + line);
				else {
					name = tokens.nextToken();
					fnm = tokens.nextToken();
					load(name, SOUND_DIR, fnm);
					if (this.clw!=null) this.clw.setElementiCaricati(clCaric++, name);
				}
			}
			br.close();
		} catch (IOException e) {
			log.fatal("Error reading file: " + sndsFNm);
			System.exit(1);
		}
	} // end of loadSoundsFile()

	// ----------- manipulate a particular clip --------

	public void load(String name, String path, String fnm)
	// create a ClipInfo object for name and store it
	{
		if (clipsMap.containsKey(name))
			log.error("Error: " + name + "already stored");
		else {
			if (classForStream!=null){
				clipsMap.put(name, new ClipInfo(name, classForStream, fnm));
			}else{
				clipsMap.put(name, new ClipInfo(name, path, fnm));
			}
			
			System.out.println("-- " + name + "/" + fnm);
		}
	} // end of load()

	public void load(String name, String fnm)
	// create a ClipInfo object for name and store it
	{
		load(name, SOUND_DIR, fnm);
	} // end of load()
	
	public void close(String name)
	// close the specified clip
	{
		ClipInfo ci = (ClipInfo) clipsMap.get(name);
		if (ci == null)
			log.error("Error: " + name + "not stored");
		else
			ci.close();
	} // end of close()

	public void play(String name, boolean toLoop)
	// play (perhaps loop) the specified clip
	{
		ClipInfo ci = clipsMap.get(name);
		if (ci == null)
			log.error("Error: " + name + "not stored");
		else {
			ci.play(toLoop);
			log.debug("Play clip "+name+". ");
		}
			
	} // end of play()

	public void stop(String name)
	// stop the clip, resetting it to the beginning
	{
		ClipInfo ci = clipsMap.get(name);
		if (ci == null)
			log.error("Error: " + name + "not stored");
		else
			ci.stop();
	} // end of stop()

	public void pause(String name) {
		ClipInfo ci = clipsMap.get(name);
		if (ci == null)
			log.error("Error: " + name + "not stored");
		else
			ci.pause();
	} // end of pause()

	public void resume(String name) {
		ClipInfo ci = clipsMap.get(name);
		if (ci == null)
			log.error("Error: " + name + "not stored");
		else
			ci.resume();
	} // end of resume()

	// -------------------------------------------------------

	public void setWatcher(String name, SoundsWatcher sw)
	/*
	 * Set up a watcher for the clip. It will be notified when the clip loops or
	 * stops.
	 */
	{
		ClipInfo ci =  clipsMap.get(name);
		if (ci == null)
			log.error("Error: " + name + "not stored");
		else
			ci.setWatcher(sw);
	} // end of setWatcher()

	private int volumePercentage = 0;
	public void increaseVolume(){
		this.volumePercentage+=10;
		if (this.volumePercentage<=100){
			for (ClipInfo ci:clipsMap.values()){
				ci.setVolumePercentage(volumePercentage);
			}
		}
	}
	public void decreaseVolume(){
		this.volumePercentage-=10;
		if (this.volumePercentage>=-100){
			for (ClipInfo ci:clipsMap.values()){
				ci.setVolumePercentage(volumePercentage);
			}
		}
	}
	public void setVolumePercentage(int volPerc){
		if (volPerc<=100 && volPerc>=-100){
			for (ClipInfo ci:clipsMap.values()){
				ci.setVolumePercentage(volPerc);
			}
		}
		this.volumePercentage=volPerc;
	}
	
} // end of ClipsLoader class
