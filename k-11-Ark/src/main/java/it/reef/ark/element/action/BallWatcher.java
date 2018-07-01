package it.reef.ark.element.action;

public interface BallWatcher {
	public void checkVita(boolean isDead);
	public void checkFineLev(int remaningBrick);
	public void addPointsToScore(int points);
	public void brickRemoved(int x, int y);
}
