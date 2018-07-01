package mouse;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MouseGrab extends Frame {
	public MouseGrab(){
		super("Prova mouse.");
		addWindowListener(new WindowAdapter(){
		       public void windowClosing(WindowEvent we){
		         System.exit(0);
		       }
		     });
			addMouseListener(new MouseGrabPersonalizzato());
		     setSize(400,400);
		     setVisible(true);
	}
	
	public class MouseGrabPersonalizzato extends MouseAdapter {
		public void mouseClicked(MouseEvent e)
		{
			if(e.getButton()== MouseEvent.BUTTON1_MASK);
			{
				System.out.println("Pigiato il bottone1");
			}
		}
	
		public void mousePressed(MouseEvent e)
		{
			System.out.println("The mouse is at "+e.getX()+" "+e.getY());
		}

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MouseGrab k = new MouseGrab();
	}
}
