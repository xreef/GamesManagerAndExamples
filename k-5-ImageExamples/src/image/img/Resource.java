package image.img;



import java.io.InputStream;

public class Resource {
	public static InputStream getBall(){
		return Resource.class.getResourceAsStream("ball.png");
	}
}
