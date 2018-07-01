package it.reef.legend.resources.images.actors;

import java.io.InputStream;

public class ImagesActors {
	public static InputStream getImage(String i) {
		return ImagesActors.class.getResourceAsStream(i);
	} 
}
