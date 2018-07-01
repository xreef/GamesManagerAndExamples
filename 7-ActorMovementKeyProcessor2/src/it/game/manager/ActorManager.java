package it.game.manager;

import it.game.people.interf.Drawable;
import it.game.people.interf.Updater;

import java.awt.Window;
import java.util.LinkedList;

public class ActorManager {
	public LinkedList<Object> actorList;

	public ActorManager() {
		// make a linked list to hold all the actors.
		actorList = new LinkedList<Object>();
	}

	public void createEntity(String s, int number) {
		for (int i = 0; i < number; i++) {
			actorList.add(entityFactory(s));
		}
	}

	Object entityFactory(String name) {
		Class temp;
		Object targetClass = null;
		try {
			// Find the class in question
			temp = (Class) Class.forName(name);
			// Make an instance of the desired class
			targetClass = temp.newInstance();
		} catch (Exception e) {
			System.out.println("Cannot create insance of " + name);
		}
		System.out.println("Created the " + name + "!");
		return targetClass;
	}
	
	public void clearEntities() {
		for (int i = 0; i < actorList.size(); i++) {
			actorList.remove(i);
		}
		System.out.println("The entity list has been cleared");
	}

	public void updateAll() {
		Updater u;
		for (int i = 0; i < actorList.size(); i++) {
			u = (Updater) actorList.get(i);
			u.update();
		}
	}

	public void drawAll(Window w) {
		Drawable d;
		for (int i = 0; i < actorList.size(); i++) {
			d = (Drawable) actorList.get(i);
			if (d.isVisible() == true)
				d.draw(w);
		}
	}
	
	public void loadAllImage(Window w){
		Drawable d;
		for (int i = 0; i < actorList.size(); i++) {
			d = (Drawable) actorList.get(i);
			d.loadImage(w);
		}
	}
}// end ActorManager.java
