import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class GraphEnv {

	public static DisplayMode findRequestedMode() {
		DisplayMode[] requestedDisplayModes = new DisplayMode[] {
				new DisplayMode(1280, 1024, 32,
						DisplayMode.REFRESH_RATE_UNKNOWN),
				new DisplayMode(1024, 768, 32, DisplayMode.REFRESH_RATE_UNKNOWN),
				new DisplayMode(800, 600, 16, DisplayMode.REFRESH_RATE_UNKNOWN),
				new DisplayMode(640, 480, 16, DisplayMode.REFRESH_RATE_UNKNOWN),
				new DisplayMode(1434, 834, -1, DisplayMode.REFRESH_RATE_UNKNOWN)
		};

		GraphicsEnvironment gfxEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice defaultDevice = gfxEnv.getDefaultScreenDevice();
		DisplayMode[] displayModes = defaultDevice.getDisplayModes();

		DisplayMode best = null;
		// loop through each of our requested modes
		for (int rIndex = 0; rIndex < requestedDisplayModes.length; rIndex++) {
			// loop through each of the available modes
			for (int mIndex = 0; mIndex < displayModes.length; mIndex++) {
				if (displayModes[mIndex].getWidth() == requestedDisplayModes[rIndex].getWidth()
						&& displayModes[mIndex].getHeight() == requestedDisplayModes[rIndex].getHeight()
						&& displayModes[mIndex].getBitDepth() == requestedDisplayModes[rIndex].getBitDepth()) {
					// We found a resolution match
					if (best == null) {
						// if the refresh rate was specified try to match that
						// as well
						if (requestedDisplayModes[rIndex].getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN) {
							if (displayModes[mIndex].getRefreshRate() != requestedDisplayModes[rIndex].getRefreshRate()) {
								best = displayModes[mIndex];
								return best;
							}
						} else {
							best = displayModes[mIndex];
							return best;
						}
					}
				}
			}
		}
		// no matching modes so we return null
		return best;
	}

	public static void main(String[] args) {
		DisplayMode newMode = null;
		// we need to make sure the system default display can
		// support full screen mode, if it can’t we will run
		// in windowed mode
		boolean fullScreenMode = false;

		GraphicsEnvironment gfxEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreenDevice = gfxEnv.getDefaultScreenDevice();

		if (defaultScreenDevice.isFullScreenSupported()) {
			fullScreenMode = true;
			// try to get one of the modes we really want
			newMode = findRequestedMode();
			// if the mode doesn’t exist then go into windowed mode
			if (newMode == null)
				fullScreenMode = false;
		} else
			System.out.println("full screen mode unsupported.");
		// FullScreenFrame1 myFrame = null;
		// if(fullScreenMode && !forceWindowedMode)
		// myFrame = new FullScreenFrame1("FullScreenFrame1 Full Screen Mode",
		// newMode);
		// else
		// myFrame = new
		// FullScreenFrame1("FullScreenFrame1 Windowed Mode",false);
		// myFrame.initToScreen();
		// }
		
		// varie();
		
		DisplayMode d = findRequestedMode();
		if (d!=null){
			System.out.println("WIDTHxHEIGHT " + d.getWidth()+ "x" + d.getHeight());
			System.out.println("Refresh Rate "+ d.getRefreshRate() + "Hz");
			System.out.println("BitDepth " + d.getBitDepth()+ "bpp");
		}else{
			System.out.println("Nessuna risoluzione è adatta");
		}
		
		System.out.println("Memoria video accelerata per buffer "+defaultScreenDevice.getAvailableAcceleratedMemory()+"bytes.");
	}

	/**
	 * @param args
	 */
	public static void varie() {
		// TODO Auto-generated method stub
		GraphicsEnvironment gfxEnv = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		System.out.println("gfxEnv.getMaximumWindowBounds().height "
				+ gfxEnv.getMaximumWindowBounds().height);
		System.out.println("gfxEnv.getMaximumWindowBounds().width "
				+ gfxEnv.getMaximumWindowBounds().width);

		System.out.println("gfxEnv.getMaximumWindowBounds().getHeight() "
				+ gfxEnv.getMaximumWindowBounds().getHeight());
		System.out.println("gfxEnv.getMaximumWindowBounds().getWidth() "
				+ gfxEnv.getMaximumWindowBounds().getWidth());

		System.out.println("gfxEnv.getMaximumWindowBounds().getgetMaxX() "
				+ gfxEnv.getMaximumWindowBounds().getMaxX());
		System.out.println("gfxEnv.getMaximumWindowBounds().getMinX() "
				+ gfxEnv.getMaximumWindowBounds().getMinX());
		System.out.println("gfxEnv.getMaximumWindowBounds().getCenterX() "
				+ gfxEnv.getMaximumWindowBounds().getCenterX());
		System.out.println("-----------------------------------------");
		GraphicsDevice[] screenDevList = gfxEnv.getScreenDevices();
		GraphicsDevice defaultDevice = gfxEnv.getDefaultScreenDevice();
		for (int i = 0; i < screenDevList.length; i++) {
			DisplayMode[] displayModes = screenDevList[i].getDisplayModes();
			if (defaultDevice.equals(screenDevList[i])) {
				System.out.print(" DEFAULT ");
			}
			for (int d = 0; d < displayModes.length; d++) {

				System.out.println("----------------------- " + i + " - " + d
						+ " ----------------------------");
				System.out.println("WIDTHxHEIGHT " + displayModes[d].getWidth()
						+ "x" + displayModes[d].getHeight());
				System.out.println("Refresh Rate "
						+ displayModes[d].getRefreshRate() + "Hz");
				System.out.println("BitDepth " + displayModes[d].getBitDepth()
						+ "bpp");

			}
		}
	}

}
