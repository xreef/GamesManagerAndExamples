package it.micra.ark.manager;

import org.junit.Test;

import it.reef.ark.manager.audio.ClipsLoader;
import it.reef.ark.manager.audio.MidisLoader;
import it.reef.ark.manager.image.ImageLoader;
import it.reef.ark.resources.audio.effect.Effects;
import it.reef.ark.resources.audio.music.Musics;
import it.reef.ark.resources.images.Images;


public class TestLoader {
	
	@Test
	public void testImageLoader()
	{
		ImageLoader il = new ImageLoader(Images.class, "images.cfg",null);
	}
	
	@Test
	public void testClipLoader()
	{
		ClipsLoader cl = new ClipsLoader(Effects.class,"sound.cfg",null);
	}
	
	@Test
	public void testMidiLoader()
	{
		MidisLoader ml = new MidisLoader(Musics.class,"midi.cfg",null);
	}
}
