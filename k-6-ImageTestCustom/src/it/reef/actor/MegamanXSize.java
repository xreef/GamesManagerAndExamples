package it.reef.actor;

import it.reef.engine.effect.ImageSFX;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class MegamanXSize extends ImmagineDinamicaSprite {

	ArrayList<BufferedImage> originalImage;
	ImageSFX isfx =  new ImageSFX();
	private double resizeFactor=1;
	
	private int xOriginale;
	private int yOriginale;
	
	public MegamanXSize(int x, int y, ArrayList<BufferedImage> is,
			ImageObserver io, long period, double duration, boolean repeat) {
		super(x, y, is, io, period, duration, repeat);
		originalImage=new ArrayList<BufferedImage>();
		for (int i=0;i<is.size();i++) originalImage.add(null);
		
		xOriginale=x;
		yOriginale=y;
	}

	public double getResizeFactor() {
		return resizeFactor;
	}

	public void setResizeFactor(double resizeFactor) {
		this.resizeFactor = resizeFactor;
	}

	boolean allImageResized=false;
	
	public void update(){
		super.update();
		if (!allImageResized){
			double widthChange;
			if (imagePointer<Math.round(imageSprite.size()/2)){
				widthChange= (imagePointer+1)*resizeFactor;
			}else{
				widthChange= (imageSprite.size()-imagePointer)*resizeFactor;
			}
	
			double heightChange = widthChange;
			originalImage.set(imagePointer, isfx.resizeImage(imageSprite.get(imagePointer), widthChange, heightChange));
			if (imagePointer==imageSprite.size()-1) allImageResized=true;
		}
		x=isfx.getNewX(xOriginale, imageSprite.get(imagePointer), originalImage.get(imagePointer), ImageSFX.CENTRE);
		y=isfx.getNewY(yOriginale, imageSprite.get(imagePointer), originalImage.get(imagePointer), ImageSFX.DOWN);
	}
	
	@Override
	public void draw(Graphics g) {
		if (originalImage.get(imagePointer) == null) {
	         g.setColor(Color.yellow);
	         g.fillRect(x, y, 20, 20);
	         g.setColor(Color.black);
	         g.drawString("??", x+10, y+10);
	       }
	       else
	         g.drawImage(originalImage.get(imagePointer), x, y, iobs);
	}
	
}
