package de.nitri.nitris;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class NitrisGame extends ApplicationAdapter {

    private static final String TAG = NitrisGame.class.getName();

    WorldController worldController;
    WorldRenderer worldRenderer;

    final float gameWidth = 400f;
    final float gameHeight = 640f;

    static final String TEXTURE_ATLAS_OBJECTS = "images/nitris.atlas";

    private boolean paused;
    protected int screenWidth;
    protected int screenHeight;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        GameWorld gameWorld = new GameWorld();

        worldController = new WorldController(this, gameWorld);
        worldRenderer = new WorldRenderer(gameWorld, worldController, gameWidth, gameHeight);

        paused = false;

    }

    @Override
    public synchronized void render() {
        super.render();

        if (!paused) {
            worldController.update();
        }

        worldRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldRenderer.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
        paused = true;
    }

    @Override
    public void resume() {
        super.resume();
        paused = false;
    }

    @Override
    public void dispose() {
        super.dispose();
        worldRenderer.dispose();
        worldController.dispose();
    }

}
