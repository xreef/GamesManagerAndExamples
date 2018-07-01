/**
 * 
 */
package it.reef.image;

import it.reef.image.loader.ImageLoader;
import it.reef.image.loader.img.Images;
import junit.framework.TestCase;

/**
 * @author reef
 *
 */
public class ImageLoaderTest extends TestCase {

	/**
	 * @param name
	 */
	public ImageLoaderTest(String name) {
		super(name);
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMain() {
		ImageLoader il = new ImageLoader(Images.class,"image.cfg", null);
	}

}
