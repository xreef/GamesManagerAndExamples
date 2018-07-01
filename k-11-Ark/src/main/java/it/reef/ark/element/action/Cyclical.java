package it.reef.ark.element.action;

public interface Cyclical {
	public abstract void restartAt(int imPosn);
	public abstract void resume();
}
