package key;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class KeyGrab extends Frame {

	public KeyGrab()
	{
		super("Prova tasti.");
		addWindowListener(new WindowAdapter(){
		       public void windowClosing(WindowEvent we){
		         System.exit(0);
		       }
		     });
		addKeyListener(new KeyGrabPersonalizzato());
		     setSize(400,400);
		     setVisible(true);
		     
		
	}

	public class KeyGrabPersonalizzato extends KeyAdapter { 
		 public void keyPressed(KeyEvent e)
		 {
			 
		   int keyCode = e.getKeyCode();
		   
		   if(keyCode == KeyEvent.VK_UP)
			   System.out.println("You pressed up!");
		   
		   if(keyCode == KeyEvent.VK_RIGHT)
			   System.out.println("You pressed right!");
		 }
		 //This one is empty, but must be defined
		 public void keyReleased() {}
		 public void keyTyped(KeyEvent e)
		 {
			 char key = e.getKeyChar();
			 System.out.println("TASTO --> "+key);
			 switch(key)
			 {
			   case'r':
			   {
			     System.out.println("You typed an R");
			     break;
			    }
			   case'q':
			   {
			     System.out.println("You typed an Q");
			     break;
			    }
			 }

		 }
}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KeyGrab k = new KeyGrab();
	}

}
