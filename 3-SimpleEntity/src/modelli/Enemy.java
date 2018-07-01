package modelli;

public class Enemy extends Actor
{
	public Enemy(){}

	public void update()
	{
		
	System.out.println("The Enemy has been updated");
	}
	
	public void draw()
	{
	 System.out.println("The Enemy has been drawn");
	}
	public void logDebugString(String s)
	{
		System.out.println(s);
	}
}