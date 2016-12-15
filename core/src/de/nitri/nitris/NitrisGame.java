package de.nitri.nitris;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class NitrisGame extends Game {

    private static final String TAG = NitrisGame.class.getName();

    WorldController worldController;
    WorldRenderer worldRenderer;

    static final String TEXTURE_ATLAS_OBJECTS = "images/nitris.atlas";

    //private int screenWidth;
    //private int screenHeight;

    GameScreen gameScreen;
    float gameHeight;
    float gameWidth;
    SettingsScreen settingsScreen;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        gameWidth = 400f;
        gameHeight = 640f;

        //screenWidth = Gdx.graphics.getWidth();
        //screenHeight = Gdx.graphics.getHeight();

        GameWorld gameWorld = new GameWorld();

        worldController = new WorldController(this, gameWorld);

        worldRenderer = new WorldRenderer(gameWorld, worldController, gameWidth, gameHeight);

        gameScreen = new GameScreen(this);

        settingsScreen = new SettingsScreen(this);

        //TODO:
        //setScreen(settingsScreen);
        setScreen(gameScreen);

    }

    @Override
    public synchronized void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        worldRenderer.dispose();
        worldController.dispose();
        gameScreen.dispose();
    }

}
