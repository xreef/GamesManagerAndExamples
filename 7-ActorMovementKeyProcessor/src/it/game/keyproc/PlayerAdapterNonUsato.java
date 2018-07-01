//package it.game.keyproc;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.util.LinkedList;
//    
//public class PlayerAdapterNonUsato extends KeyAdapter {
//	private KeyProcessable player;
//	private LinkedList<KeyEvent> keyEventList;
//	
//	public PlayerAdapterNonUsato(KeyProcessable p) {
//		player = p;
//		keyEventList = new LinkedList<KeyEvent>();
//	}
//
//    
//	public void keyPressed(KeyEvent e) {
//		synchronized(e)
//        {
//			if (keyEventList.size()==0 || keyEventList.getLast()!=e) keyEventList.add(e);
//        }
//	}
//	public void keyReleased(KeyEvent e) {
//		synchronized(e)
//        {
//			if (keyEventList.size()==0 || keyEventList.getLast()!=e) keyEventList.add(e);
//        }
//	}
////    public void addEvent(KeyEvent event)
////    {   
////        synchronized(keyEventList)
////        {
////            keyEventList.add(event);
////        }
////    }
//  
//    public void processKeyEventList()
//    {
//        KeyEvent event;
//   
//        while(keyEventList.size()>0)
//        {
//            synchronized(keyEventList)
//            {
//                event = (KeyEvent)keyEventList.removeFirst();
//            }
//            player.handleKeyEvent(event);
//        }
//    }
//}