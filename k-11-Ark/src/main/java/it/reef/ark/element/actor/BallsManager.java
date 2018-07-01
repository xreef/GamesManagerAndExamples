package it.reef.ark.element.actor;

import it.reef.ark.element.action.BallWatcher;
import it.reef.ark.element.action.Drawable;
import it.reef.ark.element.action.Updatable;
import it.reef.ark.element.concrete.BrickManager;
import it.reef.ark.manager.audio.ClipsLoader;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class BallsManager implements Updatable, Drawable {
	
	ArrayList<Ball> lb = new ArrayList<Ball>();
	private Rectangle2D r2d;
	private ArrayList<BufferedImage> is;
	private ImageObserver io;
	private long period;
	private  double duration;
	private  boolean repeat;
	private  ClipsLoader cl;
	private  BrickManager bm;
	private Worm w;
	private BallWatcher bw;
	
	public BallsManager(Rectangle2D r2d, int x, int y, ArrayList<BufferedImage> is, ImageObserver io,
			long period, double duration, boolean repeat, ClipsLoader cl, BrickManager bm, Worm w, BallWatcher bw) {
		lb.add(new Ball(r2d, x, y,  is, io, period, duration, repeat, cl, bm, w, bw));
		this.r2d=r2d;
		this.is=is;
		this.io=io;
		this.period=period;
		this.duration=duration;
		this.repeat=repeat;
		this.cl=cl;
		this.bm=bm;
		this.w=w;
		this.bw=bw;
	}

	public void addBall(int x, int y, int angle){
		lb.add(new Ball(r2d, x, y,  is, io, period, duration, repeat, cl, bm, w, bw));
	}
	
	public void draw(Graphics g) {
		for (Ball b : lb) b.draw(g);
	}

	public void update() {
		if (lb!=null){
			for (int i=0;i<lb.size();i++) {
				lb.get(i).update();
				// if (lb.get(i).toRemove()) lb.remove(i);
			}
		}
	}
	
	public void addSpeed(int percIncremento){
		for (Ball b:lb) {
			int newStep = b.getSTEP()*(100+percIncremento)/100;
			if (newStep<3) newStep = 3; 
			b.setSTEP(newStep);
		}
	}
	public void resetSpeed(){
		for (Ball b:lb) {
			b.setSTEP(b.STEP_NORMAL);
		}
	}
	
	public void checkBallToRemove(){
		for (int i=0;i<lb.size();i++) {
			if (lb.get(i).toRemove()) lb.remove(i);
		}
	}
	
	public int getNumBall(){
		if (lb!=null) return lb.size();
		return 0;
	}
}
