package it.reef.ark.element.actor;

import it.reef.ark.element.action.Drawable;
import it.reef.ark.element.action.KeyProcessable;
import it.reef.ark.element.action.SoundsWatcher;
import it.reef.ark.element.action.Updatable;
import it.reef.ark.element.actor.type.ImmagineDinamicaSprite;
import it.reef.ark.main.ArkPanel;
import it.reef.ark.manager.audio.ClipsLoader;
import it.reef.ark.manager.image.ImageSFX;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Worm extends ImmagineDinamicaSprite implements KeyProcessable, Updatable, Drawable, SoundsWatcher{
	private static final Log log = LogFactory.getLog(Worm.class);
	
	public static int SIZE_NORMAL = 1;
	public static int SIZE_LONG = 2;
	public static int SIZE_SMALL = 0;
	
	private int DIRECTION_LEFT = 0;
	private int DIRECTION_RIGHT = 1;
	
	public int SPEED_SLOW=5;
	public int SPEED_NORMAL=10;
	public int SPEED_FAST=20;
	
	private ImageSFX sfx;
	private ClipsLoader cl;
	
	public Worm(int x, int y, ArrayList<BufferedImage> is, ImageObserver io,
			long period, double duration, boolean repeat) {
		super(x, y, is, io, period, duration, repeat);
		// TODO Auto-generated constructor stub
	}
	public Worm(int x, int y, int margineLeft, int marginRight,
			ArrayList<BufferedImage> isNormal,
			ArrayList<BufferedImage> isSmaller,
			ArrayList<BufferedImage> isBigger,
			ImageObserver io,
			long period, double duration, boolean repeat, ImageSFX sfx, ClipsLoader cl) {
		super(x, y, isNormal, io, period, duration, repeat);
		this.sfx=sfx;
		this.cl=cl;
		setSmallerImage(isSmaller);
		setBiggerImage(isBigger);
		setNormalImage(isNormal);
		this.MARGIN_LEFT=margineLeft;
		this.MARGIN_RIGHT=marginRight;
		
		keyState = new boolean[256];
		
		soundSel.put(SPEED_SLOW,"moveNorm");
		cl.setWatcher("moveNorm", this);
		soundSel.put(SPEED_NORMAL,"moveNorm");
		soundSel.put(SPEED_FAST,"moveFast");
		cl.setWatcher("moveFast", this);
		
		this.currSound = soundSel.get(SPEED_NORMAL);

	}

	private ArrayList<BufferedImage>[][] imageSpriteAll = new ArrayList[3][2];
	
	private void setSmallerImage(ArrayList<BufferedImage> isLeft){
		ArrayList<BufferedImage> smallerLeft=isLeft;
		ArrayList<BufferedImage> smallerRight =new ArrayList<BufferedImage>();
		for (int i=0;i<isLeft.size();i++){
			smallerRight.add(sfx.flipImage(isLeft.get(i), ImageSFX.VERTICAL_FLIP));
		}
		imageSpriteAll[SIZE_SMALL][DIRECTION_LEFT]=smallerLeft;
		imageSpriteAll[SIZE_SMALL][DIRECTION_RIGHT]=smallerRight;
		
	}
	private void setNormalImage(ArrayList<BufferedImage> isLeft){
		ArrayList<BufferedImage> normalLeft=isLeft;
		ArrayList<BufferedImage> normalRight=new ArrayList<BufferedImage>();
		for (int i=0;i<isLeft.size();i++){
			normalRight.add(sfx.flipImage(isLeft.get(i), ImageSFX.VERTICAL_FLIP));
		}
		imageSpriteAll[SIZE_NORMAL][DIRECTION_LEFT]=normalLeft;
		imageSpriteAll[SIZE_NORMAL][DIRECTION_RIGHT]=normalRight;
	}
	private void setBiggerImage(ArrayList<BufferedImage> isLeft){
		ArrayList<BufferedImage> biggerLeft=isLeft;
		ArrayList<BufferedImage> biggerRight=new ArrayList<BufferedImage>();
		for (int i=0;i<isLeft.size();i++){
			biggerRight.add(sfx.flipImage(isLeft.get(i), ImageSFX.VERTICAL_FLIP));
		}
		imageSpriteAll[SIZE_LONG][DIRECTION_LEFT]=biggerLeft;
		imageSpriteAll[SIZE_LONG][DIRECTION_RIGHT]=biggerRight;
	}
	
	
	private int currSize = SIZE_NORMAL;
	private int currDirection = DIRECTION_LEFT;
	
	public void setSize(int size){
		this.currSize=size;
		this.changeImageSprite(imageSpriteAll[currSize][currDirection]);
		if (this.getMyForm().getMaxX()>MARGIN_RIGHT) this.setX(MARGIN_RIGHT-this.getWidth());
		if (this.getMyForm().getMinX()<MARGIN_LEFT) this.setX(MARGIN_LEFT+1);
	}
	
	public boolean[] keyState;
	public void handleKeyEvent(KeyEvent e) {
		switch(e.getID())
        {
            case KeyEvent.KEY_PRESSED:
                keyState[e.getKeyCode()] = true;
                break;
            case KeyEvent.KEY_RELEASED:
                keyState[e.getKeyCode()] = false;
                break;
        }
	}

	private boolean soundStartFirst = false;
	private String currSound;
	public void update(){
		super.update();
		
		log.debug("Worm update eseguito super");
		this.resumeAnimation();
		log.debug("Worm update eseguito resumeAnimation");
		if(keyState[KeyEvent.VK_LEFT] && !keyState[KeyEvent.VK_RIGHT]){
            moveLeft();sound();}
		else if(keyState[KeyEvent.VK_RIGHT] && !keyState[KeyEvent.VK_LEFT]){
            moveRight();sound();}
		else
			this.stopAnimation();
		log.debug("Worm update eseguito move");
	}
	
	private void sound(){
		if (!soundStartFirst){
			soundStartFirst=true;
			cl.play(currSound, false);
		}
	}
	
	private int currSpeed=SPEED_NORMAL;
	
	private int MARGIN_LEFT;
	private int MARGIN_RIGHT;
	
	private void moveLeft(){
		if (DIRECTION_LEFT!=currDirection) {
			setImageSprite(imageSpriteAll[currSize][DIRECTION_LEFT]);
			currDirection=DIRECTION_LEFT;
		}
		int x = this.getX();
        x-=currSpeed;
        if(x<MARGIN_LEFT) x = MARGIN_LEFT;
        this.setX(x);
	}
	
	private void moveRight(){
    	int x = this.getX();
    	if (DIRECTION_RIGHT!=currDirection) {
    		this.setImageSprite(imageSpriteAll[currSize][DIRECTION_RIGHT]);
    		currDirection=DIRECTION_RIGHT;
    	}
        x+=currSpeed;
        if(x+getImageSprite().get(0).getWidth() > MARGIN_RIGHT)
            x = MARGIN_RIGHT-getImageSprite().get(0).getWidth();
        this.setX(x);
	}
	
	private Map<Integer,String> soundSel = new HashMap<Integer, String>();
	public void draw(Graphics g){
		super.draw(g);
	}
	public Rectangle2D getMyForm() {
		return new Rectangle(x, y, getImageSprite().get(0).getWidth(),getImageSprite().get(0).getHeight());
	}
	public void atSequenceEnd(String filename, int status) {
		if (status==SoundsWatcher.STOPPED && this.isSpriteActive()){
			cl.play(this.currSound, false);
		}else{
			soundStartFirst=false;
		}
	}
	
	
}
