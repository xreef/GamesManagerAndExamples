package mouse.keyproc;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
    
    
public class AdvancedKeyboard extends JApplet 
                    implements Runnable, KeyListener
{ 
    public void init()
    {
        getContentPane().setLayout(null);
        setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        setIgnoreRepaint(true);
    
        animator = new Animator(new Rectangle(0, 0, DISPLAY_WIDTH,
            DISPLAY_HEIGHT));
    
        backBuffer = new BufferedImage(DISPLAY_WIDTH, DISPLAY_HEIGHT,
            BufferedImage.TYPE_INT_RGB);
        bbGraphics = (Graphics2D)backBuffer.getGraphics();
    
        addKeyListener(this);
        keyProcessor = new KeyProcessor(animator);
    }
    
    public void start()
    {
        requestFocus();
        loop = new Thread(this);
        loop.start();
    }
    
    public void stop()
    {
        loop = null;
    }
    
    public void run()
    {
        long startTime, waitTime, elapsedTime;
        // 1000/25 Frames Per Second = 40 millisecond delay
        int delayTime = 1000/25;
    
        Thread thisThread = Thread.currentThread();
        while(loop==thisThread)
        {
            startTime = System.currentTimeMillis();
    
            // handle mouse events in main loop
            keyProcessor.processKeyEventList();
    
            // handle logic
            animator.animate();
    
            // render to back buffer
            render(bbGraphics);
    
            // render to screen
            Graphics g = getGraphics();
            g.drawImage(backBuffer, 0, 0, null);
            g.dispose();
    
            //  handle frame rate
            elapsedTime = System.currentTimeMillis() - startTime;
            waitTime = Math.max(delayTime - elapsedTime, 5);
              
            try
            { 
                Thread.sleep(waitTime); 
            }
            catch(InterruptedException e) {}
        }
    }
    
    public void render(Graphics g)
    {
        animator.render(g);
    }
    
    public void keyPressed(KeyEvent e)
    {
        keyProcessor.addEvent(e);
    }
    
    public void keyReleased(KeyEvent e)
    {
        keyProcessor.addEvent(e);
    }
    
    public void keyTyped(KeyEvent e)  {}
    
    
    private Animator animator;
    private Thread loop;
    private BufferedImage backBuffer;
    private Graphics2D bbGraphics;
    private KeyProcessor keyProcessor;
    
    private static final int DISPLAY_WIDTH = 400;
    private static final int DISPLAY_HEIGHT = 400;
}