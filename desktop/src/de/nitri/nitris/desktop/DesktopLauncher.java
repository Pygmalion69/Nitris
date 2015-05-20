package de.nitri.nitris.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import de.nitri.nitris.NitrisGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

        boolean rebuildAtlas = true;

        if (rebuildAtlas) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.pot = false;
            settings.edgePadding = false;
            settings.duplicatePadding = true;
            settings.debug = false;
            TexturePacker.process(settings, "assets-raw/images", "images", "helfris.atlas");
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 400;
        config.height = 640;
        config.resizable = false;
        config.vSyncEnabled = true;
		new LwjglApplication(new NitrisGame(), config);
	}
}
