package it.reef.ark.element.actor;

import it.reef.ark.element.action.Drawable;
import it.reef.ark.element.action.FruitWatcher;
import it.reef.ark.element.action.Updatable;
import it.reef.ark.manager.audio.ClipsLoader;
import it.reef.ark.manager.image.ImageLoader;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class MagicFruitsManager implements Updatable, Drawable {
	ArrayList<MagicFruit> lmf = new ArrayList<MagicFruit>();
	
	private String[] immagini;
	private ImageLoader il;
	private ImageObserver io;
	private long period;
	private Worm worm;
	private BallsManager ballManager;
	private FruitWatcher fruitWatcher;
	private Rectangle2D AREA_ATTIVA;
	private ClipsLoader cl;
	
	public MagicFruitsManager(ImageLoader il, ImageObserver io, long period, Worm worm, BallsManager bm, Rectangle2D areaAttiva, FruitWatcher fw,ClipsLoader cl){
		immagini = new String[TIPI_FRUTTA];
		immagini[MELA] = "mela";
		immagini[ARANCIA] = "arancia";
		immagini[LIMONE] = "limone";
		immagini[BANANA] = "banana";
		immagini[FRAGOLA] = "fragola";
		this.il=il;
		this.io=io;
		this.worm=worm;
		this.AREA_ATTIVA=areaAttiva;
		this.ballManager=bm;
		this.fruitWatcher=fw;
		this.cl=cl;
	}
	
	public void draw(Graphics g) {
		for (MagicFruit mf : lmf) mf.draw(g);
	}

	public void update() {
		for (MagicFruit mf : lmf) {
			mf.update();
		}
		
		boolean notAllRemoved = true;
		while (notAllRemoved){
			notAllRemoved=false;
			for (int i=0;i<lmf.size();i++) {
				if (lmf.get(i).toRemove()) {
					lmf.remove(i);
					notAllRemoved = true;
				}
			}
		}
	}
	
	public final static int TIPI_FRUTTA=5;
	public final static int MELA=0;
	public final static int ARANCIA=1;
	public final static int LIMONE=2;
	public final static int BANANA=3;
	public final static int FRAGOLA=4;
	
	public void addFruit(int x, int y, int tipo){
		MagicFruit mf = new MagicFruit(x, y, tipo, il.getImages(immagini[tipo]), io, period, 1, true, worm, ballManager, AREA_ATTIVA, fruitWatcher, cl);
		lmf.add(mf);
	}
}
