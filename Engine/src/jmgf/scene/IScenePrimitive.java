package jmgf.scene;
/**
 * Iterfaccia degli oggetti del gioco
 * 
 * @author Vito Ciullo
 *
 */
public abstract interface IScenePrimitive
{
	public void initialize();
	public void update();
	public ScenePrimitive clone() throws InstantiationException, IllegalAccessException;
}