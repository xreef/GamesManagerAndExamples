import java.text.SimpleDateFormat;

public class Bloop {

	int playerlives = 10;

	/*
	 * long begintime = System.currentTimeMillis();
	 * //time something out in between
	 * long endtime = System.currentTimeMillis() – begintime;
	 */
	
	public void gameMain() {
		System.out.println("The Game Main is executing");
		while (playerlives >= 0) {
			System.out.println("Darn, you died!"+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSSSSS").format(new java.util.Date()));
			playerlives -= 1;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Someone Interrupted my Thread!");
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Bloop b = new Bloop();
		b.gameMain();

	}

}
