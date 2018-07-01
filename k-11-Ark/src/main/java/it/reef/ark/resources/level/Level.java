package it.reef.ark.resources.level;

import java.io.InputStream;

public class Level {
	public static InputStream getLevel(int l) {
		return Level.class.getResourceAsStream("livello"+l+".lev");
	} 
}
