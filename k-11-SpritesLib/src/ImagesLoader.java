
// ImagesLoader.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th
/* The Imagesfile and images are stored in "Images/"
   (the IMAGE_DIR constant).

   ImagesFile Formats:

    o <fnm>                     // a single image file

    n <fnm*.ext> <number>       // a series of numbered image files, whose
                                // filenames use the numbers 0 - <number>-1

    s <fnm> <number>            // a strip file (fnm) containing a single row
                                // of <number> images

    g <name> <fnm> [ <fnm> ]*   // a group of files with different names;
                                // they are accessible via  
                                // <name> and position _or_ <fnm> prefix

    and blank lines and comment lines.

    The numbered image files (n) can be accessed by the <fnm> prefix
    and <number>. 

    The strip file images can be accessed by the <fnm>
    prefix and their position inside the file (which is 
    assumed to hold a single row of images).

    The images in group files can be accessed by the 'g' <name> and the
    <fnm> prefix of the particular file, or its position in the group.


    The images are stored as BufferedImage objects, so they will be 
    manipulated as 'managed' images by the JVM (when possible).
*/
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
// for ImageIcon

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm765f]
 */
public class ImagesLoader {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6eb3]
 */
    private static final String IMAGE_DIR = "Images/";

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ead]
 */
    private HashMap imagesMap;
/* The key is the filename prefix, the object (value) 
       is an ArrayList of BufferedImages */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6ea8]
 */
    private HashMap gNamesMap;
/* The key is the 'g' <name> string, the object is an
       ArrayList of filename prefixes for the group. This is used to 
       access a group image by its 'g' name and filename. */
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e9d]
 */
    private GraphicsConfiguration gc;
// begin by loading the images specified in fnm

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e94]
 * @param fnm 
 */
    public  ImagesLoader(String fnm) {        
 initLoader();
    loadImagesFile(fnm);
    } 
// end of ImagesLoader()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e8e]
 */
    public  ImagesLoader() {        
  initLoader();  
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e88]
 */
    private void initLoader() {        
    imagesMap = new HashMap();
    gNamesMap = new HashMap();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    } 
// end of initLoader()
/* Formats:
        o <fnm>                     // a single image
        n <fnm*.ext> <number>       // a numbered sequence of images
        s <fnm> <number>            // an images strip
        g <name> <fnm> [ <fnm> ]*   // a group of images 

     and blank lines and comment lines.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e81]
 * @param fnm 
 */
    private void loadImagesFile(String fnm) {        
 
    String imsFNm = IMAGE_DIR + fnm;
    System.out.println("Reading file: " + imsFNm);
    try {
      InputStream in = this.getClass().getResourceAsStream(imsFNm);
      BufferedReader br = new BufferedReader( new InputStreamReader(in));
      // BufferedReader br = new BufferedReader( new FileReader(imsFNm));
      String line;
      char ch;
      while((line = br.readLine()) != null) {
        if (line.length() == 0)  // blank line
          continue;
        if (line.startsWith("//"))   // comment
          continue;
        ch = Character.toLowerCase( line.charAt(0) );
        if (ch == 'o')  // a single image
          getFileNameImage(line);
        else if (ch == 'n')  // a numbered sequence of images
          getNumberedImages(line);
        else if (ch == 's')  // an images strip
          getStripImages(line);
        else if (ch == 'g')  // a group of images
          getGroupImages(line);
        else
          System.out.println("Do not recognize line: " + line);
      }
      br.close();
    } 
    catch (IOException e) 
    { System.out.println("Error reading file: " + imsFNm);
      System.exit(1);
    }
    } 
// end of loadImagesFile()
// --------- load a single image -------------------------------
/* format:
        o <fnm>
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e7a]
 * @param line 
 */
    private void getFileNameImage(String line) {        
 StringTokenizer tokens = new StringTokenizer(line);
    if (tokens.countTokens() != 2)
      System.out.println("Wrong no. of arguments for " + line);
    else {
      tokens.nextToken();    // skip command label
      System.out.print("o Line: ");
      loadSingleImage( tokens.nextToken() );
    }
    } 
// end of getFileNameImage()
// can be called directly

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e73]
 * @return 
 * @param fnm 
 */
    public boolean loadSingleImage(String fnm) {        
    String name = getPrefix(fnm);
    if (imagesMap.containsKey(name)) {
      System.out.println( "Error: " + name + "already used");
      return false;
    }
    BufferedImage bi = loadImage(fnm);
    if (bi != null) {
      ArrayList imsList = new ArrayList();
      imsList.add(bi);
      imagesMap.put(name, imsList);
      System.out.println("  Stored " + name + "/" + fnm);
      return true;
    }
    else
      return false;
    } 
// end of loadSingleImage()
// extract name before '.' of filename

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e6c]
 * @return 
 * @param fnm 
 */
    private String getPrefix(String fnm) {        
    int posn;
    if ((posn = fnm.lastIndexOf(".")) == -1) {
      System.out.println("No prefix found for filename: " + fnm);
      return fnm;
    }
    else
      return fnm.substring(0, posn);
    } 
// end of getPrefix()
// --------- load numbered images -------------------------------
/* format:
        n <fnm*.ext> <number>
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e65]
 * @param line 
 */
    private void getNumberedImages(String line) {        
    StringTokenizer tokens = new StringTokenizer(line);
    if (tokens.countTokens() != 3)
      System.out.println("Wrong no. of arguments for " + line);
    else {
      tokens.nextToken();    // skip command label
      System.out.print("n Line: ");
      String fnm = tokens.nextToken();
      int number = -1;
      try {
        number = Integer.parseInt( tokens.nextToken() );
      }
      catch(Exception e)
      { System.out.println("Number is incorrect for " + line);  }
      loadNumImages(fnm, number);
    }
    } 
// end of getNumberedImages()
/*  Can be called directly.
      fnm is the filename argument in:
           n <f*.ext> <number>
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e5d]
 * @return 
 * @param fnm 
 * @param number 
 */
    public int loadNumImages(String fnm, int number) {        
    String prefix = null;
    String postfix = null;
    int starPosn = fnm.lastIndexOf("*");   // find the '*'
    if (starPosn == -1) {
      System.out.println("No '*' in filename: " + fnm);
      prefix = getPrefix(fnm);
    }
    else {   // treat the fnm as prefix + "*" + postfix
      prefix = fnm.substring(0, starPosn);
      postfix = fnm.substring(starPosn+1);
    }
    if (imagesMap.containsKey(prefix)) {
      System.out.println( "Error: " + prefix + "already used");
      return 0;
    }
    return loadNumImages(prefix, postfix, number);
    } 
// end of loadNumImages()
/* Load a series of image files with the filename format
            prefix + <i> + postfix
     where i ranges from 0 to number-1
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e54]
 * @return 
 * @param prefix 
 * @param postfix 
 * @param number 
 */
    private int loadNumImages(String prefix, String postfix, int number) {        
 
    String imFnm;
    BufferedImage bi;
    ArrayList imsList = new ArrayList();
    int loadCount = 0;
    if (number <= 0) {
      System.out.println("Error: Number <= 0: " + number);
      imFnm = prefix + postfix;
      if ((bi = loadImage(imFnm)) != null) {
        loadCount++;
        imsList.add(bi);
        System.out.println("  Stored " + prefix + "/" + imFnm);
      }
    }
    else {   // load prefix + <i> + postfix, where i = 0 to <number-1>
      System.out.print("  Adding " + prefix + "/" + 
                           prefix + "*" + postfix + "... ");
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
      System.out.println("No images loaded for " + prefix);
    else 
      imagesMap.put(prefix, imsList);
    return loadCount;
    } 
// end of loadNumImages()
// --------- load image strip -------------------------------
/* format:
        s <fnm> <number>
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e4d]
 * @param line 
 */
    private void getStripImages(String line) {        
    StringTokenizer tokens = new StringTokenizer(line);
    if (tokens.countTokens() != 3)
      System.out.println("Wrong no. of arguments for " + line);
    else {
      tokens.nextToken();    // skip command label
      System.out.print("s Line: ");
      String fnm = tokens.nextToken();
      int number = -1;
      try {
        number = Integer.parseInt( tokens.nextToken() );
      }
      catch(Exception e)
      { System.out.println("Number is incorrect for " + line);  }
      loadStripImages(fnm, number);
    }
    } 
// end of getStripImages()
/* Can be called directly, to load a strip file, <fnm>,
     holding <number> images.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e45]
 * @return 
 * @param fnm 
 * @param number 
 */
    public int loadStripImages(String fnm, int number) {        
    String name = getPrefix(fnm);
    if (imagesMap.containsKey(name)) {
      System.out.println( "Error: " + name + "already used");
      return 0;
    }
    // load the images into an array
    BufferedImage[] strip = loadStripImageArray(fnm, number);
    if (strip == null)
      return 0;
    ArrayList imsList = new ArrayList();
    int loadCount = 0;
    System.out.print("  Adding " + name + "/" + fnm + "... ");
    for (int i=0; i < strip.length; i++) {
      loadCount++;
      imsList.add(strip[i]);
      System.out.print(i + " ");
    }
    System.out.println();
    if (loadCount == 0)
      System.out.println("No images loaded for " + name);
    else 
      imagesMap.put(name, imsList);
    return loadCount;
    } 
// end of loadStripImages()
// ------ grouped filename seq. of images ---------
/* format:
        g <name> <fnm>  [ <fnm> ]*
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e3e]
 * @param line 
 */
    private void getGroupImages(String line) {        
 StringTokenizer tokens = new StringTokenizer(line);
    if (tokens.countTokens() < 3)
      System.out.println("Wrong no. of arguments for " + line);
    else {
      tokens.nextToken();    // skip command label
      System.out.print("g Line: ");
      String name = tokens.nextToken();
      ArrayList fnms = new ArrayList();
      fnms.add( tokens.nextToken() );  // read filenames
      while (tokens.hasMoreTokens())
        fnms.add( tokens.nextToken() );
      loadGroupImages(name, fnms);
    }
    } 
// end of getGroupImages()
/* Can be called directly to load a group of images, whose
     filenames are stored in the ArrayList <fnms>. They will
     be stored under the 'g' name <name>.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e33]
 * @return 
 * @param name 
 * @param fnms 
 */
    public int loadGroupImages(String name, ArrayList fnms) {        
    if (imagesMap.containsKey(name)) {
      System.out.println( "Error: " + name + "already used");
      return 0;
    }
    if (fnms.size() == 0) {
      System.out.println("List of filenames is empty");
      return 0;
    }
    BufferedImage bi;
    ArrayList nms = new ArrayList();
    ArrayList imsList = new ArrayList();
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
      System.out.println("No images loaded for " + name);
    else {
      imagesMap.put(name, imsList);
      gNamesMap.put(name, nms);
    }
    return loadCount;
    } 
// end of loadGroupImages()
// supply the group filenames in an array

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e2b]
 * @return 
 * @param name 
 * @param fnms 
 */
    public int loadGroupImages(String name, String[] fnms) {        
  
    ArrayList al = new ArrayList( Arrays.asList(fnms) );
    return loadGroupImages(name, al);  
    } 
// ------------------ access methods -------------------
/* Get the image associated with <name>. If there are
     several images stored under that name, return the 
     first one in the list.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e24]
 * @return 
 * @param name 
 */
    public BufferedImage getImage(String name) {        
    ArrayList imsList = (ArrayList) imagesMap.get(name);
    if (imsList == null) {
      System.out.println("No image(s) stored under " + name);  
      return null;
    }
    // System.out.println("Returning image stored under " + name);  
    return (BufferedImage) imsList.get(0);
    } 
// end of getImage() with name input;
/* Get the image associated with <name> at position <posn>
    in its list. If <posn> is < 0 then return the first image
    in the list. If posn is bigger than the list's size, then
    calculate its value modulo the size.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e1c]
 * @return 
 * @param name 
 * @param posn 
 */
    public BufferedImage getImage(String name, int posn) {        
    ArrayList imsList = (ArrayList) imagesMap.get(name);
    if (imsList == null) {
      System.out.println("No image(s) stored under " + name);  
      return null;
    }
    int size = imsList.size();
    if (posn < 0) {
      // System.out.println("No " + name + " image at position " + posn +
      //                      "; return position 0"); 
      return (BufferedImage) imsList.get(0);   // return first image
    }
    else if (posn >= size) {
      // System.out.println("No " + name + " image at position " + posn); 
      int newPosn = posn % size;   // modulo
      // System.out.println("Return image at position " + newPosn); 
      return (BufferedImage) imsList.get(newPosn);
    }
    // System.out.println("Returning " + name + " image at position " + posn);  
    return (BufferedImage) imsList.get(posn);
    } 
// end of getImage() with posn input;
/* Get the image associated with the group <name> and filename
     prefix <fnmPrefix>. 
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e14]
 * @return 
 * @param name 
 * @param fnmPrefix 
 */
    public BufferedImage getImage(String name, String fnmPrefix) {        
    ArrayList imsList = (ArrayList) imagesMap.get(name);
    if (imsList == null) {
      System.out.println("No image(s) stored under " + name);  
      return null;
    }
    int posn = getGroupPosition(name, fnmPrefix);
    if (posn < 0) {
      // System.out.println("Returning image at position 0"); 
      return (BufferedImage) imsList.get(0);   // return first image
    }
    // System.out.println("Returning " + name + 
    //                        " image with pair name " + fnmPrefix);  
    return (BufferedImage) imsList.get(posn);
    } 
// end of getImage() with fnmPrefix input;
/* Search the hashmap entry for <name>, looking for <fnmPrefix>.
     Return its position in the list, or -1.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e0c]
 * @return 
 * @param name 
 * @param fnmPrefix 
 */
    private int getGroupPosition(String name, String fnmPrefix) {        
    ArrayList groupNames = (ArrayList) gNamesMap.get(name);
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
    System.out.println("No " + fnmPrefix + 
                  " group name found for " + name);  
    return -1;
    } 
// end of getGroupPosition()
// return all the BufferedImages for the given name

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6e05]
 * @return 
 * @param name 
 */
    public ArrayList getImages(String name) {        
    ArrayList imsList = (ArrayList) imagesMap.get(name);
    if (imsList == null) {
      System.out.println("No image(s) stored under " + name);  
      return null;
    }
    System.out.println("Returning all images stored under " + name);  
    return imsList;
    } 
// end of getImages();
// is <name> a key in the imagesMap hashMap?

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6dfe]
 * @return 
 * @param name 
 */
    public boolean isLoaded(String name) {        
    ArrayList imsList = (ArrayList) imagesMap.get(name);
    if (imsList == null)
      return false;
    return true;
    } 
// end of isLoaded()
// how many images are stored under <name>?

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6df7]
 * @return 
 * @param name 
 */
    public int numImages(String name) {        
    ArrayList imsList = (ArrayList) imagesMap.get(name);
    if (imsList == null) {
      System.out.println("No image(s) stored under " + name);  
      return 0;
    }
    return imsList.size();
    } 
// end of numImages()
// ------------------- Image Input ------------------
/* There are three versions of loadImage() here! They use:
           ImageIO   // the preferred approach
           ImageIcon
           Image
     We assume that the BufferedImage copy required an alpha
     channel in the latter two approaches.
  */
/* Load the image from <fnm>, returning it as a BufferedImage
      which is compatible with the graphics device being used.
      Uses ImageIO.
   */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6df0]
 * @return 
 * @param fnm 
 */
    public BufferedImage loadImage(String fnm) {        
     try {
       BufferedImage im =  ImageIO.read( 
                      getClass().getResource(IMAGE_DIR + fnm) );
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
       System.out.println("Load Image error for " +
                     IMAGE_DIR + "/" + fnm + ":\n" + e); 
       return null;
     }
    } 
// end of loadImage() using ImageIO

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6de8]
 * @param fnm 
 * @param transparency 
 */
    private void reportTransparency(String fnm, int transparency) {        
    System.out.print(fnm + " transparency: ");
    switch(transparency) {
      case Transparency.OPAQUE:
        System.out.println("opaque");
        break;
      case Transparency.BITMASK:
        System.out.println("bitmask");
        break;
      case Transparency.TRANSLUCENT:
        System.out.println("translucent");
        break;
      default:
        System.out.println("unknown");
        break;
    } // end switch
    } 
// end of reportTransparency()
/* Load the image from <fnm>, returning it as a BufferedImage.
      Uses ImageIcon.
   */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6de1]
 * @return 
 * @param fnm 
 */
    private BufferedImage loadImage2(String fnm) {        
 ImageIcon imIcon = new ImageIcon(
                      getClass().getResource(IMAGE_DIR + fnm) );
    if (imIcon == null)
      return null;
    int width = imIcon.getIconWidth();
    int height = imIcon.getIconHeight();
    Image im = imIcon.getImage();
    return makeBIM(im, width, height);
    } 
// end of loadImage() using ImageIcon
// make a BufferedImage copy of im, assuming an alpha channel

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6dd8]
 * @return 
 * @param im 
 * @param width 
 * @param height 
 */
    private BufferedImage makeBIM(Image im, int width, int height) {        
    BufferedImage copy = new BufferedImage(width, height, 
                                        BufferedImage.TYPE_INT_ARGB);
    // create a graphics context
     Graphics2D g2d = copy.createGraphics();
    // g2d.setComposite(AlphaComposite.Src);
    // copy image
    g2d.drawImage(im,0,0,null);
    g2d.dispose();
    return copy;
    } 
// end of makeBIM()
/* Load the image from <fnm>, returning it as a BufferedImage.
     Use Image.
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6dd1]
 * @return 
 * @param fnm 
 */
    public BufferedImage loadImage3(String fnm) {        
 Image im = readImage(fnm);
    if (im == null)
      return null;
    int width = im.getWidth( null );
    int height = im.getHeight( null );
    return makeBIM(im, width, height);
    } 
// end of loadImage() using Image
// load the image, waiting for it to be fully downloaded

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6dca]
 * @return 
 * @param fnm 
 */
    private Image readImage(String fnm) {        
    Image image = Toolkit.getDefaultToolkit().getImage(
                     getClass().getResource(IMAGE_DIR + fnm) );
    MediaTracker imageTracker = new MediaTracker( new JPanel() );
    imageTracker.addImage(image, 0);
    try {
      imageTracker.waitForID(0);
    }
    catch (InterruptedException e) {
      return null;
    }
    if (imageTracker.isErrorID(0))
      return null;
    return image;
    } 
// end of readImage()
/* Extract the individual images from the strip image file, <fnm>.
     We assume the images are stored in a single row, and that there
     are <number> of them. The images are returned as an array of
     BufferedImages
  */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6dc1]
 * @return 
 * @param fnm 
 * @param number 
 */
    public BufferedImage[] loadStripImageArray(String fnm, int number) {        
    if (number <= 0) {
      System.out.println("number <= 0; returning null");
      return null;
    }
    BufferedImage stripIm;
    if ((stripIm = loadImage(fnm)) == null) {
      System.out.println("Returning null");
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
    } 
// end of loadStripImageArray()
 }
// end of ImagesLoader class
