import gestori.ActorManager;


public class mainGame {

	public static void main(String args[])
	{
		ActorManager am = new ActorManager();
//		am.createEntity("Player",1);
//		am.createEntity("Enemy",10);
		am.createEntity("modelli.Player",1);
		am.createEntity("modelli.Enemy",10);
		am.updateAll();
		am.drawAll();
	}

}
