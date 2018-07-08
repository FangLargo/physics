package com.mf.physics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mf.physics.Physics;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "physics";
		config.height = 600;
		config.width = 1024;
		
		new LwjglApplication(new Physics(), config);
	}
}
