package mouse;

import java.awt.*;
import javax.swing.*;
    
public class SimpleMouse extends JFrame
{
    public SimpleMouse()
    {
        super("Simple Mouse Example");
        getContentPane().setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
    
        MouseMat mouseMat = new MouseMat(new Rectangle(10, 10, 
            380, 380));
        getContentPane().add(mouseMat);
    
        showToInternalSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        validate();
    }
    
    
    public Insets showToInternalSize(int internalWidth, int
        internalHeight)
    {
        setVisible(true);
        Insets insets = getInsets();
    
        final int newWidth = internalWidth + insets.left +
            insets.right;
        final int newHeight = internalHeight + insets.top +
            insets.bottom;
        try
        {
           EventQueue.invokeAndWait(new Runnable()
           {
              public void run()
              {
                 setSize(newWidth, newHeight);
              }
          });
       }
       catch(Exception e)
       {
          System.out.println(e);
       }
    
       return insets;
    }
    
    
    public static void main(String[] args)
    {
       new SimpleMouse();
    }
    
    private static final int DISPLAY_WIDTH = 400;
    private static final int DISPLAY_HEIGHT = 400;
}
