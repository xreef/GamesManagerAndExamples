
package it.reef.managers.images;
import it.reef.managers.images.interfaces.ImageAccess;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm67ef]
 */
public class LoopingImage implements ImageAccess {

	public BufferedImage getCurrentImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCurrentPointer() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxPointer() {
		// TODO Auto-generated method stub
		return 0;
	}
 }

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm67ee]
 */
public class ImageSFX {
/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64f1]
 */
    private GraphicsConfiguration gc;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64ea]
 */
    public static final int NONE = -1;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64e4]
 */
    public static final int CENTRE = 0;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64de]
 */
    public static final int UP = 1;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64d8]
 */
    public static final int RIGHT = 2;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64d2]
 */
    public static final int DOWN = 3;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64cc]
 */
    public static final int LEFT = 4;
// constants used to specify the type of image flipping required

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64c6]
 */
    public static final int VERTICAL_FLIP = 0;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64c0]
 */
    public static final int HORIZONTAL_FLIP = 1;

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64ba]
 */
    public static final int DOUBLE_FLIP = 2;
// flip horizontally and vertically

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64b3]
 */
    public  ImageSFX() {        
		// get the GraphicsConfiguration so images can be copied easily and
		// efficiently
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    } 
// draw a resized image by multiplying by widthChange and heightChange

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64a9]
 * @return 
 * @param im 
 * @param widthChange 
 * @param heightChange 
 */
    public BufferedImage resizeImage(BufferedImage im, double widthChange, double heightChange) {        
		if (im == null) {
			System.out.println("drawResizedImage: input image is null");
		}
		if (widthChange <= 0) {
			System.out.println("width change cannot <= 0");
			widthChange =  1.0;
		}
		if (heightChange <= 0) {
			System.out.println("height change cannot <= 0");
			heightChange =  1.0;
		}
		int destWidth = (int) (im.getWidth() * widthChange);
		int destHeight = (int) (im.getHeight() * heightChange);
		int transparency = im.getColorModel().getTransparency();
		BufferedImage copy = gc.createCompatibleImage(destWidth, destHeight,
				transparency);
		// create a graphics context
		Graphics2D g2d = copy.createGraphics();
		// g2d.setComposite(AlphaComposite.Src);
		// reportTransparency(IMAGE_DIR + fnm, transparency);
		// copy image
		g2d.drawImage(im, 0, 0, destWidth, destHeight, null);
		g2d.dispose();
		return copy;
    } 
// end of drawResizedImage()
/*
	 * Return a new image which has been flipped horizontally, vertically, or
	 * both.
	 */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64a1]
 * @return 
 * @param im 
 * @param flipKind 
 */
    public BufferedImage flipImage(BufferedImage im, int flipKind) {        
		if (im == null) {
			System.out.println("getFlippedImage: input image is null");
			return null;
		}
		int imWidth = im.getWidth();
		int imHeight = im.getHeight();
		int transparency = im.getColorModel().getTransparency();
		BufferedImage copy = gc.createCompatibleImage(imWidth, imHeight,
				transparency);
		Graphics2D g2d = copy.createGraphics();
		// g2d.setComposite(AlphaComposite.Src);
		// draw in the flipped image
		renderFlip(g2d, im, imWidth, imHeight, flipKind);
		g2d.dispose();
		return copy;
    } 
// end of getFlippedImage()
// the flipping is achieved by supplying suitable coords to drawImage()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6496]
 * @param g2d 
 * @param im 
 * @param imWidth 
 * @param imHeight 
 * @param flipKind 
 */
    private void renderFlip(Graphics2D g2d, BufferedImage im, int imWidth, int imHeight, int flipKind) {        
		if (flipKind == VERTICAL_FLIP)
			g2d.drawImage(im, imWidth, 0, 0, imHeight, 0, 0, imWidth, imHeight,
					null);
		else if (flipKind == HORIZONTAL_FLIP)
			g2d.drawImage(im, 0, imHeight, imWidth, 0, 0, 0, imWidth, imHeight,
					null);
		else
			// assume DOUBLE_FLIP
			g2d.drawImage(im, imWidth, imHeight, 0, 0, 0, 0, imWidth, imHeight,
					null);
    } 
// end of renderFlip()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm648c]
 * @return 
 * @param x 
 * @param iOriginale 
 * @param iModificata 
 * @param orizontalAlign 
 */
    public int getNewX(int x, BufferedImage iOriginale, BufferedImage iModificata, int orizontalAlign) {        
		int destX = x + iOriginale.getWidth() / 2 - iModificata.getWidth() / 2;
		if (orizontalAlign == ImageSFX.CENTRE) {
			destX = x + iOriginale.getWidth() / 2 - iModificata.getWidth() / 2;
		} else if (orizontalAlign == ImageSFX.LEFT) {
			destX = x;
		} else if (orizontalAlign == ImageSFX.RIGHT) {
			destX = x + iOriginale.getWidth() - iModificata.getWidth();
		}
		return destX;
    } 

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6482]
 * @return 
 * @param y 
 * @param iOriginale 
 * @param iModificata 
 * @param verticalAlign 
 */
    public int getNewY(int y, BufferedImage iOriginale, BufferedImage iModificata, int verticalAlign) {        
		int destY = y;
		if (verticalAlign == ImageSFX.CENTRE) {
			destY = y + iOriginale.getHeight() / 2 - iModificata.getHeight()
					/ 2;
		} else if (verticalAlign == ImageSFX.UP) {
			destY = y;
		} else if (verticalAlign == ImageSFX.DOWN) {
			destY = y + iOriginale.getHeight() - iModificata.getHeight();
		}
		return destY;
    } 
// --------------- alpha composite effect: fading -----------------
// --------------- alpha composite effect: fading -----------------
/*
	 * The degree of fading is specified with the alpha value. alpha == 1 means
	 * fully visible, 0 mean invisible.
	 */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6476]
 * @param g2d 
 * @param im 
 * @param x 
 * @param y 
 * @param alpha 
 */
    public void drawFadedImage(Graphics2D g2d, BufferedImage im, int x, int y, float alpha) {        
		if (im == null) {
			System.out.println("drawFadedImage: input image is null");
			return;
		}
		if (alpha < 0.0f) {
			System.out.println("Alpha must be >= 0.0f; setting to 0.0f");
			alpha = 0.0f;
		} else if (alpha > 1.0f) {
			System.out.println("Alpha must be <= 1.0f; setting to 1.0f");
			alpha = 1.0f;
		}
		Composite c = g2d.getComposite(); // backup the old composite
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
		g2d.drawImage(im, x, y, null);
		g2d.setComposite(c);
		// restore the old composite so it doesn't mess up future rendering
    } 
// end of drawFadedImage()

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm646e]
 * @return 
 * @param src 
 * @param angle 
 */
    public BufferedImage rotateImage(BufferedImage src, int angle) {        
		if (src == null) {
			System.out.println("getRotatedImage: input image is null");
			return null;
		}
		int transparency = src.getColorModel().getTransparency();
		BufferedImage dest = gc.createCompatibleImage(src.getWidth(), src
				.getHeight(), transparency);
		Graphics2D g2d = dest.createGraphics();
		AffineTransform origAT = g2d.getTransform(); // save original
		// rotate the coord. system of the dest. image around its center
		AffineTransform rot = new AffineTransform();
		rot.rotate(Math.toRadians(angle), src.getWidth() / 2,
				src.getHeight() / 2);
		g2d.transform(rot);
		g2d.drawImage(src, 0, 0, null); // copy in the image
		g2d.setTransform(origAT); // restore original transform
		g2d.dispose();
		return dest;
    } 
// blurring with a fixed convolution kernel

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6463]
 * @param g2d 
 * @param im 
 * @param x 
 * @param y 
 * @param blurOp 
 */
    public void drawBlurredImage(Graphics2D g2d, BufferedImage im, int x, int y, ConvolveOp blurOp) {        
		if (im == null) {
			System.out.println("getBlurredImage: input image is null");
			return;
		}
		g2d.drawImage(im, blurOp, x, y); // use predefined ConvolveOp
    } 
/*
	 * The size argument is used to specify a sizesize blur kernel, filled with
	 * 1/(sizesize) values.
	 * 
	 * Size should be odd so that the center cell of the kernel corresponds to
	 * the coordinate being blurred.
	 * 
	 * The larger the size value, the larger the kernel, and more blurry the
	 * resulting image.
	 * 
	 * An issue is the edge effects, which will produce a nasty black border or
	 * a border with no bluriness, depending on which ConvolveOp EDGE constant
	 * is used.
	 */

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6456]
 * @param g2d 
 * @param im 
 * @param x 
 * @param y 
 * @param blurMatrix 
 * @param size 
 */
    public void drawBlurredImage(Graphics2D g2d, BufferedImage im, int x, int y, float[] blurMatrix, int size) {        
		if (im == null) {
			System.out.println("getBlurredImage: input image is null");
			return;
		}
		int imWidth = im.getWidth();
		int imHeight = im.getHeight();
		int maxSize = (imWidth > imHeight) ? imWidth : imHeight;
		if ((maxSize % 2) == 0) // if even
			maxSize--; // make it odd
		if ((size % 2) == 0) { // if even
			size++; // make it odd
			System.out
					.println("Blur size must be odd; adding 1 to make size = "
							+ size);
		}
		if (size < 3) {
			System.out.println("Minimum blur size is 3");
			size = 3;
		} else if (size > maxSize) {
			System.out.println("Maximum blur size is " + maxSize);
			size = maxSize;
		}
		// create the blur kernel
//		int numCoords = size * size;
		// float blurFactor = 1.0f / (float) numCoords;
		float[] blurKernel = blurMatrix;
			// new float[numCoords];
//		for (int i = 0; i < numCoords; i++)
//			blurKernel[i] = blurFactor;
		ConvolveOp blurringOp = new ConvolveOp(new Kernel(size, size,
				blurKernel), ConvolveOp.EDGE_NO_OP, null); // leaves edges
															// unaffected
		// ConvolveOp.EDGE_ZERO_FILL, null);
		// edges are filled with black
		g2d.drawImage(im, blurringOp, x, y);
    } 
// end of drawBlurredImage() with size argument
 }

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm67f0]
 */
public class ImageLoader {

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm6504]
 */
    private String IMAGE_PATH = "img/";

/**
 * 
 * 
 * @poseidon-object-id [Im62d1359fm126e672b042mm64fe]
 */
    private String IMAGE_CFG = "image.cfg";
 }
