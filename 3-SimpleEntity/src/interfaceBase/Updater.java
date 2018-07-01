package interfaceBase;



public interface Updater {
	public boolean collidesWith(Updater other);

	public void handleCollisions();

	public void update();
}
