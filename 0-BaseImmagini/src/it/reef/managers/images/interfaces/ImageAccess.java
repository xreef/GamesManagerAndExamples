package it.reef.managers.images.interfaces;

import java.awt.image.BufferedImage;

public interface ImageAccess {
	public BufferedImage getCurrentImage();
	public int getCurrentPointer();
	public int getMaxPointer();
}
