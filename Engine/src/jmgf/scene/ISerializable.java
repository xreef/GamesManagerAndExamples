package jmgf.scene;

/**
 * Interfaccia di serializzazione di scene
 * 
 * @author Vito Ciullo
 *
 */

public interface ISerializable
{
	public void serialize();
	
	public SceneElement[] unserialize();
}
