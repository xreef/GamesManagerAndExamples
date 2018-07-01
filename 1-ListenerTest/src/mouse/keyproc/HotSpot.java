package mouse.keyproc;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
    
public class HotSpot
{
    public HotSpot(Point pos, int diameter, Color col)
    {
        bounds = new Rectangle(pos.x, pos.y, diameter, diameter);
        image = new BufferedImage(diameter, diameter,
             BufferedImage.TYPE_INT_ARGB);
    
        Graphics2D g2D = (Graphics2D)image.getGraphics();
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, 
             diameter, diameter);
        g2D.setColor(col);
        g2D.fill(circle);
    
        g2D.dispose();
    }
    
    public void render(Graphics g)
    {
        g.drawImage(image, bounds.x, bounds.y, null);
    }
    
    public Rectangle bounds;
    private BufferedImage image;
}