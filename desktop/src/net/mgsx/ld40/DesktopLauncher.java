package net.mgsx.ld40;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.mgsx.ld40.LD40;
import net.mgsx.ld40.model.Rules;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Rules.WIDTH;
		config.height = Rules.HEIGHT;
		new LwjglApplication(new LD40(), config);
	}
}
