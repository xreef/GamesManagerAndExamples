package jmgf.scene;

import javax.microedition.lcdui.game.Sprite;

/**
 * Elementi animati della scena 
 * 
 * @author Vito Ciullo
 *
 */
public abstract class AnimatedSceneElement extends SceneElement
{
	protected Sprite element;
	public void setLayer(Sprite l)
	{ 
		super.setLayer(l);
		element = l;
	}

	private int[] frames;

	public AnimatedSceneElement() 
	{ 
		super();
		callingUpdate = true;
	}
	
	public void update() { element.nextFrame(); }
	public void setFrameSequence(int[] frames)
	{ 
		this.frames = frames;
		element.setFrameSequence(frames);
	}
	public int[] getFrameSequence() { return frames; }
}
