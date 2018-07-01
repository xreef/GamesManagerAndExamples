package it.reef.ark.element.actor;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import it.reef.ark.element.action.FruitWatcher;
import it.reef.ark.element.actor.type.ImmagineDinamicaSprite;
import it.reef.ark.manager.audio.ClipsLoader;

public class MagicFruit  extends ImmagineDinamicaSprite{

	private Worm worm;
	private Rectangle2D AREA_ATTIVA;
	private int tipo;
	private FruitWatcher fruitWatcher;
	private BallsManager ballManager;
	private ClipsLoader cl;
	
	public MagicFruit(int x, int y, int tipo, ArrayList<BufferedImage> is,
			ImageObserver io, long period, double duration, boolean repeat, Worm worm, BallsManager bm, Rectangle2D areaAttiva, FruitWatcher fw, ClipsLoader cl) {
		super(x, y, is, io, period, duration, repeat);
		this.setX(x-(width/2));
		this.worm=worm;
		this.AREA_ATTIVA=areaAttiva;
		this.tipo=tipo;
		this.ballManager=bm;
		this.fruitWatcher=fw;
		this.cl=cl;
	}
	
	private int FRUIT_SPEED=5;
	private int FRUIT_RANDOM=5;
	public void update(){
		super.update();
		y +=FRUIT_SPEED;
		x += (((int)(Math.random()*FRUIT_RANDOM))-2);
		interceptExternalWall();
		interceptWorm();
	}

	public void interceptWorm(){
		Ellipse2D e2d = new Ellipse2D.Float(this.getX(), this.getY(), this.getWidth(), this.getHeigth());
		Rectangle2D we2d = worm.getMyForm();
		if (e2d.intersects(we2d)){
			this.remove=true;
			switch (this.tipo) {
			case MagicFruitsManager.BANANA:
				worm.setSize(Worm.SIZE_LONG);
				break;
			case MagicFruitsManager.LIMONE:
				worm.setSize(Worm.SIZE_SMALL);
				break;
			case MagicFruitsManager.FRAGOLA:
				fruitWatcher.addBall(this.getX(), this.getY(), (int)(Math.random()*-90)-45);
				break;
			case MagicFruitsManager.ARANCIA:
				ballManager.addSpeed(-10);
				break;
			case MagicFruitsManager.MELA:
				ballManager.resetSpeed();
				worm.setSize(Worm.SIZE_NORMAL);
				break;
			default:
				break;
			}
			cl.play("takeFruit", false);
		}
	}
	
	private boolean remove=false;
	public boolean toRemove(){
		return remove;
	}
	
	public void interceptExternalWall(){
		Ellipse2D e2d = new Ellipse2D.Float(this.getX(), this.getY(), this.getWidth(), this.getHeigth());
		if (e2d.getMinX()<=AREA_ATTIVA.getMinX() || e2d.getMaxX()>=AREA_ATTIVA.getMaxX()){
			this.setX((int)AREA_ATTIVA.getMinX()+1);
		}
		if (e2d.getMaxY()>=AREA_ATTIVA.getMaxY()){
			this.remove=true;
		}
	}
		
}
