package it.reef.ark.element.actor;

import it.reef.ark.element.action.BallWatcher;
import it.reef.ark.element.actor.type.ImmagineDinamicaSprite;
import it.reef.ark.element.concrete.Brick;
import it.reef.ark.element.concrete.BrickManager;
import it.reef.ark.main.Ark;
import it.reef.ark.manager.audio.ClipsLoader;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Ball extends ImmagineDinamicaSprite {

	private static final Log log = LogFactory.getLog(Ball.class);
	
	BallWatcher ballWatcher = null;
	
	private ClipsLoader cl;
	private int BALL_SIZE;
	private Rectangle2D AREA_ATTIVA;
	
	private BrickManager brickm;
	private Worm worm;
	
	public Ball(Rectangle2D r2d, int x, int y, ArrayList<BufferedImage> is, ImageObserver io,
			long period, double duration, boolean repeat, ClipsLoader cl, BrickManager bm, Worm w,BallWatcher bw) {
		super(x, y, is, io, period, duration, repeat);
		this.cl=cl;
//		this.speedX=-STEP_NORMAL^2/2;
//		this.speedY=this.speedX;
		this.STEP=this.STEP_NORMAL;
		this.BALL_SIZE=this.getImageSprite().get(0).getHeight();
		
		this.brickm = bm;
		this.worm=w;
		this.AREA_ATTIVA=r2d;
		this.ballWatcher=bw;
	}
	
	public Ball(int x, int y, ArrayList<BufferedImage> is, ImageObserver io,
			long period, double duration, boolean repeat) {
		super(x, y, is, io, period, duration, repeat);
		// TODO Auto-generated constructor stub
	}

	
	public int STEP_NORMAL=Ark.BALL_STEP;
	public int STEP_SLOW=(int)(Ark.BALL_STEP*1.5);
	public int STEP_FAST=Ark.BALL_STEP*2;
	
	private int radius = -45;
	private int currStep = STEP_NORMAL;
	
//	private int speedX=currStep;
//	private int speedY=-currStep;
	
	private int STEP=STEP_NORMAL;
	public int getSpeedX(){
		// x = cx + r * cos(a)
		int sx = (int)(STEP * Math.cos(Math.toRadians(radius)));
		log.debug("SX "+sx);
		if (sx==0)
			if ((radius>=90 && radius<=270) || (radius<=-90 && radius>=-270) ){
				sx=-1;
			}else if ((radius<=90 && radius>=270) || (radius>=-90 && radius<=-270) ){
				 sx=1;
			}
		log.debug("Recalculating SX "+sx);
		if (sx==0){
			sx+=1;
		}
		return  sx;
	}
	public int getSpeedY(){
		// y = cy + r * sin(a)
		int sy = (int)(STEP * Math.sin(Math.toRadians(radius)));
		log.debug("SY "+sy);
		if (sy==0)
			if ((radius>=0 && radius<=180) || (radius<=-180 && radius>=-360) ){
				sy=-1;
			}else if ((radius>=180 && radius<=360)||(radius<=0 && radius>=-180) ){
				 sy=1;
			}
		log.debug("Recalculating SY "+sy);
		
		if (sy==0){
			sy+=1;
		}
		return  sy;
	}
	
	
	private String[] soundBrickCollision = new String[]{null,"ballWall","ballBrick","ballBrickIron"};
	
	public void update(){
		this.setX(this.getX()+this.getSpeedX());
		this.setY(this.getY()+this.getSpeedY());
		
		interceptBrick();
		interceptWorm();
		interceptExternalWall();

		super.update();
	}
	
	
	private boolean remove=false;
	public boolean toRemove(){
		return remove;
	}
	public void interceptExternalWall(){
		Ellipse2D e2d = new Ellipse2D.Float(this.getX(), this.getY(), BALL_SIZE, BALL_SIZE);
		if (e2d.getMinX()<=AREA_ATTIVA.getMinX()){
			log.debug("Collisione parete laterale sinistra");
        	// this.speedX *= -1;
        	this.radius = 180 - this.radius;
        	this.setX((int)AREA_ATTIVA.getMinX());
		}
		if ( e2d.getMaxX()>=AREA_ATTIVA.getMaxX() ){
			log.debug("Collisione parete laterale destra");
        	// this.speedX *= -1;
        	this.radius = 180 - this.radius;
        	this.setX((int)AREA_ATTIVA.getMaxX()-(int)e2d.getWidth());
		}
		if (e2d.getMinY()<=AREA_ATTIVA.getMinY()){
			log.debug("Collisione pareti sopra");
			this.radius *= -1;
			this.setY((int)AREA_ATTIVA.getMinY());
        	// this.speedY *= -1;
		}
		if (e2d.getMaxY()>=AREA_ATTIVA.getMaxY()){
			log.debug("Collisione pareti sotto");
			//System.exit(0);
			// this.radius *= -1;
			this.remove=true;
			ballWatcher.checkVita(true);
			
		}
	}
	//      -270|90
	//          |
	//          |
	//  180     |      +0
	//	-----------------		
	// -180     |	   -0
	//          |
	//          |
	//       270|-90
	
	public void interceptWorm(){
		Ellipse2D e2d = new Ellipse2D.Float(this.getX(), this.getY(), BALL_SIZE, BALL_SIZE);
		Rectangle2D we2d = worm.getMyForm();
		if (e2d.intersects(we2d)){
			while (this.radius>360) this.radius-=360;
			while (this.radius<-360) this.radius+=360;
			
			log.debug("Collisione verme");
			int discostamentoImpattoDaCentro = (int)(we2d.getCenterX()-e2d.getCenterX());
			int baseDiImpatto = (int)((we2d.getWidth()/2)+e2d.getWidth());
			
			// |---------------------------|  Verme
			//     ^         C			
			//  Collisione
			//			   Centro
			// 	   >--------<     Discostamento
			// >------------<     Base impatto
			
			int percentualeDiscostamento = Math.abs(discostamentoImpattoDaCentro)*100/Math.abs(baseDiImpatto);
			
			this.radius *= -1;
			
			int change =(int) Math.random()*10;
				
			int angolo = 90*percentualeDiscostamento/100;
			if (e2d.getCenterX()<we2d.getCenterX()){
				this.radius-=angolo;
			}else if (e2d.getCenterX()>we2d.getCenterX()){
				this.radius+=angolo;
			}
			
			this.radius+= change-5; 
			
		
//			if ((this.radius>175 && this.radius<270)||(this.radius>-185 && this.radius<-90))  this.radius=170;
//			if ((this.radius<5 && this.radius>-90) || (this.radius<365 && this.radius>270)) this.radius=10;
			
			if (this.getSpeedY()>0) this.radius *= -1;
			
			this.setY((int)(we2d.getMinY()-(e2d.getHeight()+1)));
			
//			int change =(int) Math.random()*10;
//			this.radius *= -1;
//			int discostamento = (int) (we2d.getCenterX() - e2d.getCenterX());
//			int halfSize = (int)we2d.getWidth()/2;
//			// halfSize:discostamento=45:x
//			// int percDisc = halfSize/100*discostamento;
//			if (e2d.getCenterX()<we2d.getCenterX())
//			{
//				this.radius += discostamento*90/halfSize;
//			}else{
//				this.radius -= discostamento*90/halfSize;
//			}
//			this.radius += change-5;
//			if (this.getSpeedY()>0) this.radius = 180 - this.radius;
//			
//			this.setY((int)(we2d.getMinY()-e2d.getHeight()));
		}
	}
	
	public void interceptBrick(){
		
		Ellipse2D.Double e2d = new Ellipse2D.Double(this.getX(), this.getY(), BALL_SIZE, BALL_SIZE);
		
		List<Brick> lb = brickm.getListBrick();
		for (int i=0;i<lb.size();i++){
			Brick b = lb.get(i);
			if (e2d.intersects(b.getMyForm())){
				log.debug("Collisione mattoncino in posizione X: "+b.getX()+" Y: "+b.getY());
				cl.play(soundBrickCollision[b.getTipo()], false);
				
				Rectangle2D br2d = b.getMyForm();
				
				if (b.getTipo()==BrickManager.BRICK_SOLID){
					bounce(br2d.getBounds(), e2d);
					break;
				}else if (b.getTipo()==BrickManager.BRICK_NORMAL || b.getTipo()==BrickManager.BRICK_HARD){
					bounce(br2d.getBounds(), e2d);
					if (b.getTipo()==BrickManager.BRICK_NORMAL){
						lb.remove(i);
						ballWatcher.addPointsToScore(5);
						ballWatcher.checkFineLev(brickm.remainingBrick());

						ballWatcher.brickRemoved((int)b.getMyForm().getCenterX(), (int)b.getMyForm().getCenterY());
					}else if (b.getTipo()==BrickManager.BRICK_HARD){
						ballWatcher.addPointsToScore(10);
						b.setTipo(BrickManager.BRICK_NORMAL);
					}
				}
			}
		}
	}
	
	private void bounce(Rectangle r, Ellipse2D.Double e) {
        double cx = e.getCenterX();
        double cy = e.getCenterY();
        int outcode = r.outcode(cx, cy);
        switch(outcode) {
            case Rectangle.OUT_TOP:
            	//this.setY((r.y-this.heigth)-1);
            	if (this.getSpeedY()>0) {
	            	this.setY((r.y-this.heigth));
	            	this.radius *= -1;
            	}
            	// this.speedY *= -1;
            	break;
            case Rectangle2D.OUT_BOTTOM:
            	//this.setY(r.y+r.height+1);
            	if (this.getSpeedY()<0){
	            	this.setY(r.y+r.height);
	            	this.radius *= -1;
            	}
                //this.speedY *= -1;
                break;
            case Rectangle2D.OUT_TOP + Rectangle2D.OUT_LEFT:
            	//this.setY((r.y-this.heigth)-1);
            	if (this.getSpeedY()>0) {
	            	this.setY((r.y-this.heigth));
	            	this.radius *= -1;
            	}
            	// this.speedY *= -1;
            	//this.setX((r.x-this.width)-1);
            	
            	// this.speedX *= -1;
            	if (this.getSpeedX()>0) {
            		this.setX((r.x-this.width));
            		this.radius = 180 - this.radius;
            	}
                break;
            case Rectangle2D.OUT_LEFT + Rectangle2D.OUT_BOTTOM:
            	//this.setX((r.x-this.width)-1);
            	if (this.getSpeedX()>0) {
	            	this.setX((r.x-this.width));
	            	this.radius = 180 - this.radius;
            	}
            	// this.speedX *= -1;
            	//this.setY(r.y+r.height+1);
            	if (this.getSpeedY()<0){
	            	this.setY(r.y+r.height);
	            	this.radius *= -1;
            	}
                //this.speedY *= -1;
                break;
            case Rectangle2D.OUT_BOTTOM + Rectangle.OUT_RIGHT:
            	//this.setX(r.x+r.width+1);
            	if (this.getSpeedX()<0) {
	            	this.setX(r.x+r.width);
	            	this.radius = 180 - this.radius;
            	}
            	// this.speedX *= -1;
            	//this.setY(r.y+r.height+1);
            	if (this.getSpeedY()<0){
	            	this.setY(r.y+r.height);
	            	this.radius *= -1;
        		}
                // this.speedY *= -1;
                break;
            case Rectangle2D.OUT_RIGHT + Rectangle2D.OUT_TOP:
            	//this.setX(r.x+r.width+1);
            	if (this.getSpeedX()<0) {
	            	this.setX(r.x+r.width);
	            	this.radius = 180 - this.radius;
            	}
            	// this.speedX *= -1;
            	//this.setY((r.y-this.heigth)-1);
            	if (this.getSpeedY()>0){
            		this.setY((r.y-this.heigth));
            	 	this.radius *= -1;
        		}
                // this.speedY *= -1;
            	break;
            case Rectangle2D.OUT_LEFT:
            	//this.setX((r.x-this.width)-1);
            	if (this.getSpeedX()>0) {
            		this.setX((r.x-this.width));
            		this.radius = 180 - this.radius;
            	}
            	// this.speedX *= -1;
                break;
            case Rectangle2D.OUT_RIGHT:
            	//this.setX(r.x+r.width+1);
            	if (this.getSpeedX()<0) {
	            	this.setX(r.x+r.width);
	            	this.radius = 180 - this.radius;
            	}
            	// this.speedX *= -1;
                break;
            default:
            {
            	System.out.println("CENTER: " + outcode);
            	int sy = this.getSpeedY();
           	 	this.radius = 180 - this.radius;
            	if (sy>0) this.setY(r.y+r.height+1);
            	if (sy<0) this.setY((r.y-this.heigth)-1);
            } 
        }
        this.radius+=Math.random()*10;
    }
	
	public int getSTEP() {
		return STEP;
	}

	public void setSTEP(int sTEP) {
		STEP = sTEP;
		STEP_NORMAL = sTEP;
		STEP_FAST = sTEP*2;
		STEP_SLOW = (int)(sTEP*0.7);
	}

	
	
}
