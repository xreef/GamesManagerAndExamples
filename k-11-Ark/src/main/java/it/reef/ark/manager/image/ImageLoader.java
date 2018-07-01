package it.reef.ark.manager.image;

import it.reef.ark.manager.LoaderWatcher;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageLoader {

	private static final Log log = LogFactory.getLog(ImageLoader.class);
	
	private Class<?> classForStream;
	
	private String IMAGE_PATH = "../resources/images";
	private String IMAGE_CFG = "images.cfg";

	private HashMap<String, ArrayList<BufferedImage>> immagini;
	private HashMap<String, ArrayList<String>> nomiImmagine;

	private GraphicsConfiguration gc;

	private LoaderWatcher ilw;

	public ImageLoader(LoaderWatcher ilw) {
		this.ilw=ilw;
		initLoader();
		loadImagesFile(IMAGE_CFG);
	}
	
	public ImageLoader(String filePath, String filecfg, LoaderWatcher ilw){
		this.ilw=ilw;
		initLoader();
		this.IMAGE_PATH=filePath;
		this.IMAGE_CFG=filecfg;
		loadImagesFile(IMAGE_CFG);
	}

	public ImageLoader(Class<?> cfs, String filecfg, LoaderWatcher ilw){
		this.ilw=ilw;
		initLoader();
		this.IMAGE_PATH="";
		this.IMAGE_CFG=filecfg;
		classForStream = cfs;
		loadImagesFile(IMAGE_CFG);
	}
	
	private void initLoader() {
		immagini = new HashMap<String, ArrayList<BufferedImage>>();
		nomiImmagine = new HashMap<String, ArrayList<String>>();
		
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
	} // end of initLoader()

	private int getImageNumber(String fnm){
		int imageNum = 0;
		
		String imsFNm = IMAGE_PATH + fnm;
		//System.out.println("Reading file: " + imsFNm);
		log.info("Reading file: " + imsFNm);
		try {
			InputStream in = null;
			if (classForStream!=null){
				in = classForStream.getResourceAsStream(fnm);
			}else{
				in = this.getClass().getResourceAsStream(imsFNm);
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			
			// BufferedReader br = new BufferedReader( new FileReader(imsFNm));

			String line;
			char ch;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) // blank line
					continue;
				if (line.startsWith("//")) // comment
					continue;

				ch = Character.toLowerCase(line.charAt(0));
				if (ch == 'o') // a single image
					imageNum++;
				else if (ch == 'n') // a numbered sequence of images
					imageNum++;
				else if (ch == 's') // an images strip
					imageNum++;
				else if (ch == 'm') // an images strip
					imageNum++;
				else if (ch == 'g') // a group of images
					imageNum++;
				else
					log.warn("Do not recognize line: " + line);
			}
			br.close();
		} catch (IOException e) {
			log.error("Error reading file: " + imsFNm);
			System.exit(1);
		}
		return imageNum;
	}
	
	private void loadImagesFile(String fnm)
	/*
	 * Formats: o <fnm> // a single image n <fnm.ext> <number> // a numbered
	 * sequence of images s <fnm> <number> // an images strip g <name> <fnm> [
	 * <fnm> ] // a group of images
	 * 
	 * and blank lines and comment lines.
	 */
	{
		if (this.ilw!=null) this.ilw.setElementiDaCaricare(getImageNumber(fnm), "Caricamento immagini");
		
		String imsFNm = IMAGE_PATH + fnm;
		//System.out.println("Reading file: " + imsFNm);
		log.info("Reading file: " + imsFNm);
		try {
			InputStream in = null;
			if (classForStream!=null){
				in = classForStream.getResourceAsStream(fnm);
			}else{
				in = this.getClass().getResourceAsStream(imsFNm);
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			
			// BufferedReader br = new BufferedReader( new FileReader(imsFNm));
			int immaginiCaricate=0;
			
			String line;
			char ch;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) // blank line
					continue;
				if (line.startsWith("//")) // comment
					continue;

				ch = Character.toLowerCase(line.charAt(0));
				if (ch == 'o') // a single image
					{
						getFileNameImage(line);
						if (this.ilw!=null) this.ilw.setElementiCaricati(immaginiCaricate++, "Immagine semplice");
					}
				else if (ch == 'n') // a numbered sequence of images
					{
						getNumberedImages(line);
						if (this.ilw!=null) this.ilw.setElementiCaricati(immaginiCaricate++,"Immagini in sequenza");
					}
				else if (ch == 's') // an images strip
					{
						getStripImages(line);
						if (this.ilw!=null) this.ilw.setElementiCaricati(immaginiCaricate++, "Immagine composta");
					}
				else if (ch == 'm') // an images strip
					{
						getMatrixStripImages(line);
						if (this.ilw!=null) this.ilw.setElementiCaricati(immaginiCaricate++, "Immagine composta");
					}
				else if (ch == 'g') // a group of images
					{
						getGroupImages(line);
						if (this.ilw!=null) this.ilw.setElementiCaricati(immaginiCaricate++, "Gruppo di immagini");
					}
				else
					log.warn("Do not recognize line: " + line);
			}
			br.close();
		} catch (IOException e) {
			log.error("Error reading file: " + imsFNm);
			System.exit(1);
		}
	} // end of loadImagesFile()

	// --------- load a single image -------------------------------

	private void getFileNameImage(String line)
	/*
	 * format: o <fnm>
	 */
	{
		StringTokenizer tokens = new StringTokenizer(line);

		if (tokens.countTokens() != 2)
			log.warn("Wrong no. of arguments for " + line);
		else {
			tokens.nextToken(); // skip command label
			System.out.print("o Line: ");
			loadSingleImage(tokens.nextToken());
		}
	} // end of getFileNameImage()

	public boolean loadSingleImage(String fnm)
	// can be called directly
	{
		String name = getPrefix(fnm);

		if (immagini.containsKey(name)) {
			log.error("Error: " + name + "already used");
			return false;
		}

		BufferedImage bi = loadImage(fnm);
		if (bi != null) {
			ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
			imsList.add(bi);
			immagini.put(name, imsList);
			System.out.println("  Stored " + name + "/" + fnm);
			return true;
		} else
			return false;
	} // end of loadSingleImage()

	private String getPrefix(String fnm)
	// extract name before '.' of filename
	{
		int posn;
		if ((posn = fnm.lastIndexOf(".")) == -1) {
			log.warn("No prefix found for filename: " + fnm);
			return fnm;
		} else
			return fnm.substring(0, posn);
	} // end of getPrefix()
	
	   public BufferedImage loadImage(String fnm) 
	   /* Load the image from <fnm>, returning it as a BufferedImage
	      which is compatible with the graphics device being used.
	      Uses ImageIO.
	   */
	   {
	     try {
	    	 BufferedImage im = null;
	    	 if (classForStream!=null){
	    		 im =  ImageIO.read(classForStream.getResourceAsStream(fnm) );
	    	 }else{
	    		 im =  ImageIO.read(getClass().getResourceAsStream(IMAGE_PATH + fnm) );
	    	 }
	       // An image returned from ImageIO in J2SE <= 1.4.2 is 
	       // _not_ a managed image, but is after copying!

	       int transparency = im.getColorModel().getTransparency();
	       BufferedImage copy =  gc.createCompatibleImage(
	                                im.getWidth(), im.getHeight(),
			                        transparency );
	       // create a graphics context
	       Graphics2D g2d = copy.createGraphics();
	       // g2d.setComposite(AlphaComposite.Src);

	       // reportTransparency(IMAGE_DIR + fnm, transparency);

	       // copy image
	       g2d.drawImage(im,0,0,null);
	       g2d.dispose();
	       return copy;
	     } 
	     catch(IOException e) {
	       log.error("Load Image error for " + IMAGE_PATH + "/" + fnm + ":\n" + e); 
	       return null;
	     }
	  } // end of loadImage() using ImageIO

	   
	// --------- load numbered images -------------------------------

	   private void getNumberedImages(String line)
	   /* format:
	         n <fnm*.ext> <number>
	   */
	   {
	     StringTokenizer tokens = new StringTokenizer(line);

	     if (tokens.countTokens() != 3)
	       log.warn("Wrong no. of arguments for " + line);
	     else {
	       tokens.nextToken();    // skip command label
	       System.out.print("n Line: ");

	       String fnm = tokens.nextToken();
	       int number = -1;
	       try {
	         number = Integer.parseInt( tokens.nextToken() );
	       }
	       catch(Exception e)
	       { log.error("Number is incorrect for " + line);  }

	       
	       // fnm now is <f*.ext>
	       loadNumImages(fnm, number);
	     }
	   }  // end of getNumberedImages()

	   public int loadNumImages(String fnm, int number)
	   /*  Can be called directly.
	       fnm is the filename argument in:
	            n <f*.ext> <number>
	   */
	   {
	     String prefix = null;
	     String postfix = null;
	     int starPosn = fnm.lastIndexOf("*");   // find the '*'
	     if (starPosn == -1) {
	       log.warn("No '*' in filename: " + fnm);
	       prefix = getPrefix(fnm);
	     }
	     else {   // treat the fnm as prefix + "*" + postfix
	       prefix = fnm.substring(0, starPosn);
	       postfix = fnm.substring(starPosn+1);
	     }

	     if (immagini.containsKey(prefix)) {
	       log.error( "Error: " + prefix + "already used");
	       return 0;
	     }

	     return loadNumImages(prefix, postfix, number);
	   }  // end of loadNumImages()

	   private int loadNumImages(String prefix, String postfix, int number)
	   /* Load a series of image files with the filename format
	             prefix + <i> + postfix
	      where i ranges from 0 to number-1
	   */
	   { 
	     String imFnm;
	     BufferedImage bi;
	     ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
	     int loadCount = 0;

	     if (number <= 0) {
	       log.error("Error: Number <= 0: " + number);
	       imFnm = prefix + postfix;
	       if ((bi = loadImage(imFnm)) != null) {
	         loadCount++;
	         imsList.add(bi);
	         log.info("  Stored " + prefix + "/" + imFnm);
	       }
	     }
	     else {   // load prefix + <i> + postfix, where i = 0 to <number-1>
	       System.out.print("  Adding " + prefix + "/" + prefix + "*" + postfix + "... ");
	       for(int i=0; i < number; i++) {
	         imFnm = prefix + i + postfix;
	         if ((bi = loadImage(imFnm)) != null) {
	           loadCount++;
	           imsList.add(bi);
	           System.out.print(i + " ");
	         }
	       }
	       System.out.println();
	     }
	     if (loadCount == 0)
	         log.warn("No images loaded for " + prefix);
	       else 
	         immagini.put(prefix, imsList);

	       return loadCount;
	     }  // end of loadNumImages()
	   
	   // ---------------------- matrix strip image
	   
	   private void getMatrixStripImages(String line)
	   /* format:
	         m <fnm> <number_width> <number_heigth>
	   */
	   {
	     StringTokenizer tokens = new StringTokenizer(line);

	     if (tokens.countTokens() != 4)
	       log.warn("Wrong no. of arguments for " + line);
	     else {
	       tokens.nextToken();    // skip command label
	       System.out.print("s Line: ");

	       String fnm = tokens.nextToken();
	       int number_width = -1;
	       try {
	         number_width = Integer.parseInt( tokens.nextToken() );
	       }
	       catch(Exception e)
	       { 
	    	   log.warn("Number width is incorrect for " + line);  
	       }

	       int number_heigth = -1;
	       try {
	    	   number_heigth = Integer.parseInt( tokens.nextToken() );
	       }
	       catch(Exception e)
	       { 
	    	   log.warn("Number width is incorrect for " + line);  
	       }
	       
	       loadMatrixStripImages(fnm, number_width, number_heigth);
	     }
	   }  // end of getStripImages()
	   
	   
	   public int loadMatrixStripImages(String fnm, int number_width, int number_heigth)
	   /* Can be called directly, to load a strip file, <fnm>,
	      holding <number_width> <number_heighth> images.
	   */
	   {
	     String name = getPrefix(fnm);
	     if (immagini.containsKey(name)) {
	       log.error( "Error: " + name + "already used");
	       return 0;
	     }
	     // load the images into an array
	     BufferedImage[][] strip = loadMatrixStripImageArray(fnm, number_width,number_heigth);
	     if (strip == null)
	       return 0;

	     ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
	     int loadCount = 0;
	     log.info("  Adding " + name + "/" + fnm + "... ");
	     for (int ih=0; ih < number_heigth; ih++ ){	
	    	 for (int iw=0; iw < number_width; iw++) {
	    	 
		       loadCount++;
		       imsList.add(strip[iw][ih]);
		       System.out.print(iw + " " + ih + " - ");
	    	 }
	     }
	     System.out.println();

	     if (loadCount == 0)
	       log.warn("No images loaded for " + name);
	     else 
	    	 immagini.put(name, imsList);

	     return loadCount;
	   }  // end of loadStripImages()
	   
	   public BufferedImage[][] loadMatrixStripImageArray(String fnm, int number_width, int number_heigth) 
	   /* Extract the individual images from the strip image file, <fnm>.
	      We assume the images are stored in a matrix, and that there
	      are <number_width> of columns and <number_heigth> of row. The images are returned as a matrix of
	      BufferedImages
	   */
	   {
	     if (number_width <= 0) {
	       log.warn("number_width <= 0; returning null");
	       return null;
	     }

	     if (number_heigth <= 0) {
		       log.warn("number_heigth <= 0; returning null");
		       return null;
		     }
	     
	     BufferedImage stripIm;
	     if ((stripIm = loadImage(fnm)) == null) {
	       log.warn("Returning null");
	       return null;
	     }

	     int imWidth = (int) stripIm.getWidth() / number_width;
	     int imHeight = (int)stripIm.getHeight() / number_heigth;
	     int transparency = stripIm.getColorModel().getTransparency();

	     BufferedImage[][] strip = new BufferedImage[number_width][number_heigth];
	     Graphics2D stripGC;

	     // each BufferedImage from the strip file is stored in strip[]
	     for (int iw=0; iw < number_width; iw++) {
	    	 for (int ih=0; ih < number_heigth; ih++){
	    	 
		       strip[iw][ih] =  gc.createCompatibleImage(imWidth, imHeight, transparency);
		        
		       // create a graphics context
		       stripGC = strip[iw][ih].createGraphics();
		       // stripGC.setComposite(AlphaComposite.Src);
	
		       // copy image
		       stripGC.drawImage(stripIm, 
		                   0,0, imWidth,imHeight,
		                   iw*imWidth,ih*imHeight, (iw*imWidth)+imWidth,(ih*imHeight)+imHeight,
		                   null);
		       stripGC.dispose();
	    	 }
	     } 
	     return strip;
	   } // end of loadStripImageArray()

	   
	   // --------- load image strip -------------------------------

	   private void getStripImages(String line)
	   /* format:
	         s <fnm> <number>
	   */
	   {
	     StringTokenizer tokens = new StringTokenizer(line);

	     if (tokens.countTokens() != 3)
	       log.warn("Wrong no. of arguments for " + line);
	     else {
	       tokens.nextToken();    // skip command label
	       System.out.print("s Line: ");

	       String fnm = tokens.nextToken();
	       int number = -1;
	       try {
	         number = Integer.parseInt( tokens.nextToken() );
	       }
	       catch(Exception e)
	       { log.warn("Number is incorrect for " + line);  }

	       loadStripImages(fnm, number);
	     }
	   }  // end of getStripImages()

	   public int loadStripImages(String fnm, int number)
	   /* Can be called directly, to load a strip file, <fnm>,
	      holding <number> images.
	   */
	   {
	     String name = getPrefix(fnm);
	     if (immagini.containsKey(name)) {
	       log.error( "Error: " + name + "already used");
	       return 0;
	     }
	     // load the images into an array
	     BufferedImage[] strip = loadStripImageArray(fnm, number);
	     if (strip == null)
	       return 0;

	     ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
	     int loadCount = 0;
	     System.out.print("  Adding " + name + "/" + fnm + "... ");
	     for (int i=0; i < strip.length; i++) {
	       loadCount++;
	       imsList.add(strip[i]);
	       System.out.print(i + " ");
	     }
	     System.out.println();

	     if (loadCount == 0)
	       log.warn("No images loaded for " + name);
	     else 
	    	 immagini.put(name, imsList);

	     return loadCount;
	   }  // end of loadStripImages()
	   
	   public BufferedImage[] loadStripImageArray(String fnm, int number) 
	   /* Extract the individual images from the strip image file, <fnm>.
	      We assume the images are stored in a single row, and that there
	      are <number> of them. The images are returned as an array of
	      BufferedImages
	   */
	   {
	     if (number <= 0) {
	       log.warn("number <= 0; returning null");
	       return null;
	     }

	     BufferedImage stripIm;
	     if ((stripIm = loadImage(fnm)) == null) {
	       log.warn("Returning null");
	       return null;
	     }

	     int imWidth = (int) stripIm.getWidth() / number;
	     int height = stripIm.getHeight();
	     int transparency = stripIm.getColorModel().getTransparency();

	     BufferedImage[] strip = new BufferedImage[number];
	     Graphics2D stripGC;

	     // each BufferedImage from the strip file is stored in strip[]
	     for (int i=0; i < number; i++) {
	       strip[i] =  gc.createCompatibleImage(imWidth, height, transparency);
	        
	       // create a graphics context
	       stripGC = strip[i].createGraphics();
	       // stripGC.setComposite(AlphaComposite.Src);

	       // copy image
	       stripGC.drawImage(stripIm, 
	                   0,0, imWidth,height,
	                   i*imWidth,0, (i*imWidth)+imWidth,height,
	                   null);
	       stripGC.dispose();
	     } 
	     return strip;
	   } // end of loadStripImageArray()

	   
	   private void getGroupImages(String line)
	   /* format:
	         g <name> <fnm>  [ <fnm> ]*
	   */
	   { StringTokenizer tokens = new StringTokenizer(line);

	     if (tokens.countTokens() < 3)
	       log.warn("Wrong no. of arguments for " + line);
	     else {
	       tokens.nextToken();    // skip command label
	       System.out.print("g Line: ");

	       String name = tokens.nextToken();

	       ArrayList<String> fnms = new ArrayList<String>();
	       fnms.add( tokens.nextToken() );  // read filenames
	       
	       while (tokens.hasMoreTokens()) fnms.add( tokens.nextToken() );

	       loadGroupImages(name, fnms);
	     }
	   }  // end of getGroupImages()

	   public int loadGroupImages(String name, ArrayList<String> fnms)
	   /* Can be called directly to load a group of images, whose
	      filenames are stored in the ArrayList <fnms>. They will
	      be stored under the 'g' name <name>.
	   */
	   {
	     if (immagini.containsKey(name)) {
	       log.error( "Error: " + name + "already used");
	       return 0;
	     }

	     if (fnms.size() == 0) {
	       log.error("List of filenames is empty");
	       return 0;
	     }

	     BufferedImage bi;
	     ArrayList<String> nms = new ArrayList<String>();
	     ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
	     String nm, fnm;
	     int loadCount = 0;

	     System.out.println("  Adding to " + name + "...");
	     System.out.print("  ");
	     for (int i=0; i < fnms.size(); i++) {    // load the files
	       fnm = (String) fnms.get(i);
	       nm = getPrefix(fnm);
	       if ((bi = loadImage(fnm)) != null) {
	         loadCount++;
	         imsList.add(bi);
	         nms.add( nm );
	         System.out.print(nm + "/" + fnm + " ");
	       }
	     }
	     System.out.println();

	     if (loadCount == 0)
	       log.warn("No images loaded for " + name);
	     else {
	       immagini.put(name, imsList);
	       nomiImmagine.put(name, nms);
	     }

	     return loadCount;
	   }  // end of loadGroupImages()
	   
	   
	   // ------------------ access methods -------------------

	   public BufferedImage getImage(String name)
	   /* Get the image associated with <name>. If there are
	      several images stored under that name, return the 
	      first one in the list.
	   */
	   {
	     ArrayList<BufferedImage> imsList =  immagini.get(name);
	     if (imsList == null) {
	       log.warn("No image(s) stored under " + name);  
	       return null;
	     }

	     // System.out.println("Returning image stored under " + name);  
	     return imsList.get(0);
	   }  // end of getImage() with name input;



	   public BufferedImage getImage(String name, int posn)
	   /* Get the image associated with <name> at position <posn>
	     in its list. If <posn> is < 0 then return the first image
	     in the list. If posn is bigger than the list's size, then
	     calculate its value modulo the size.
	   */
	   {
	     ArrayList<BufferedImage> imsList = immagini.get(name);
	     if (imsList == null) {
	       log.warn("No image(s) stored under " + name);  
	       return null;
	     }

	     int size = imsList.size();
	     if (posn < 0) {
	       // System.out.println("No " + name + " image at position " + posn +
	       //                      "; return position 0"); 
	       return imsList.get(0);   // return first image
	     }
	     else if (posn >= size) {
	       // System.out.println("No " + name + " image at position " + posn); 
	       int newPosn = posn % size;   // modulo
	       // System.out.println("Return image at position " + newPosn); 
	       return imsList.get(newPosn);
	     }

	     // System.out.println("Returning " + name + " image at position " + posn);  
	     return imsList.get(posn);
	   }  // end of getImage() with posn input;



	   public BufferedImage getImage(String name, String fnmPrefix)
	   /* Get the image associated with the group <name> and filename
	      prefix <fnmPrefix>. 
	   */
	   {
	     ArrayList<BufferedImage> imsList = immagini.get(name);
	     if (imsList == null) {
	       log.warn("No image(s) stored under " + name);  
	       return null;
	     }

	     int posn = getGroupPosition(name, fnmPrefix);
	     if (posn < 0) {
	       // System.out.println("Returning image at position 0"); 
	       return imsList.get(0);   // return first image
	     }

	     // System.out.println("Returning " + name + 
	     //                        " image with pair name " + fnmPrefix);  
	     return imsList.get(posn);
	   }  // end of getImage() with fnmPrefix input;



	   private int getGroupPosition(String name, String fnmPrefix)
	   /* Search the hashmap entry for <name>, looking for <fnmPrefix>.
	      Return its position in the list, or -1.
	   */
	   {
	     ArrayList<String> groupNames = nomiImmagine.get(name);
	     if (groupNames == null) {
	       System.out.println("No group names for " + name);  
	       return -1;
	     }

	     String nm;
	     for (int i=0; i < groupNames.size(); i++) {
	       nm = (String) groupNames.get(i);
	       if (nm.equals(fnmPrefix))
	         return i;          // the posn of <fnmPrefix> in the list of names
	     }

	     log.warn("No " + fnmPrefix + 
	                   " group name found for " + name);  
	     return -1;
	   }  // end of getGroupPosition()



	   public ArrayList<BufferedImage> getImages(String name)
	   // return all the BufferedImages for the given name
	   {
	     ArrayList<BufferedImage> imsList = immagini.get(name);
	     if (imsList == null) {
	       log.warn("No image(s) stored under " + name);  
	       return null;
	     }

	     log.info("Returning all images stored under " + name);  
	     return imsList;
	   }  // end of getImages();


	   public boolean isLoaded(String name)
	   // is <name> a key in the imagesMap hashMap?
	   {
	     ArrayList<BufferedImage> imsList = immagini.get(name);
	     if (imsList == null)
	       return false;
	     return true;
	   }  // end of isLoaded()


	   public int numImages(String name)
	   // how many images are stored under <name>?
	   {
	     ArrayList<BufferedImage> imsList = immagini.get(name);
	     if (imsList == null) {
	       log.warn("No image(s) stored under " + name);  
	       return 0;
	     }
	     return imsList.size();
	   } // end of numImages()


	   public void resizeImage(String name, int width,int height, ImageSFX isfx){
		   ArrayList<BufferedImage> al = immagini.get(name);
		   log.info("Resize image: "+name);
		   for (int i=0;i<al.size();i++){
			   al.set(i, isfx.resizeImage(al.get(i), width, height));
		   }
	   }
	   
	   public void createRotatingSprite(String name, int numImage, ImageSFX isfx){
		   ArrayList<BufferedImage> al = immagini.get(name);
		   BufferedImage bi = al.get(0);
		   immagini.put(name, isfx.rotateImageToArray(bi, numImage));
	   }
}
