package it.reef.ark.resources.images;

import java.io.InputStream;

public class Images {
	public static InputStream getImage(String i) {
		return Images.class.getResourceAsStream(i);
	} 
}
