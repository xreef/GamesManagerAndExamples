package it.reef.ark.element.concrete;

import it.reef.ark.element.action.Drawable;
import it.reef.ark.element.actor.type.ImmagineDinamicaSprite;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;

public class Brick extends ImmagineDinamicaSprite implements Drawable {
	
	private int tipo = BrickManager.BRICK_NORMAL;
	
	private Rectangle2D myForm;
	
	private BufferedImage[] imagesType;
	
	public Brick(int x, int y, int tipoBrick, BufferedImage[] imagesBrickType,ImageObserver io,
			long period, double duration, boolean repeat) {
		super(x, y, new ArrayList<BufferedImage>(Arrays.asList(imagesBrickType)), io, period, duration, repeat);
		this.imagesType=imagesBrickType;
		this.setTipo(tipoBrick);
		myForm = new Rectangle(x,y,width,heigth);
	}

	public void setImageSprite(BufferedImage i){
		ArrayList<BufferedImage> is = new ArrayList<BufferedImage>();
		is.add(i);
		this.setImageSprite(is);
	}
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.setImageSprite(imagesType[tipo]);
		this.tipo = tipo;
	}

	public boolean intersect(Ellipse2D ellipse) {
		return ellipse.intersects(myForm);
	}
	public boolean intersect(Rectangle2D rec) {
		return rec.intersects(myForm);
	}

	public Rectangle2D getMyForm() {
		return myForm;
	}
	

}
