package it.reef.legend.resources;

import it.reef.legend.manager.audio.ClipsLoader;
import it.reef.legend.manager.audio.MidisLoader;
import it.reef.legend.manager.image.ImageLoader;
import it.reef.legend.resources.audio.effects.Effects;
import it.reef.legend.resources.audio.musics.Musics;
import it.reef.legend.resources.images.actors.ImagesActors;
import it.reef.legend.resources.images.backgrounds.ImagesBackgrounds;

import org.junit.Test;

public class TestLoader {
	
	@Test
	public void testImageLoader()
	{
		@SuppressWarnings("unused")
		ImageLoader il = new ImageLoader(ImagesActors.class, "imagesActors.cfg",null);
	}

	@Test
	public void testImageBackgroundLoader()
	{
		@SuppressWarnings("unused")
		ImageLoader il = new ImageLoader(ImagesBackgrounds.class, "imagesBackgrounds.cfg",null);
	}

	@Test
	public void testClipLoader()
	{
		@SuppressWarnings("unused")
		ClipsLoader cl = new ClipsLoader(Effects.class,"effects.cfg",null);
	}
	
	@Test
	public void testMidiLoader()
	{
		@SuppressWarnings("unused")
		MidisLoader ml = new MidisLoader(Musics.class,"musics.cfg",null);
	}
}
