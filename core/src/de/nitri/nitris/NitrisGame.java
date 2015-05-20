package de.nitri.nitris;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class NitrisGame extends ApplicationAdapter {

    private static final String TAG = NitrisGame.class.getName();

    private WorldController worldController;
    protected WorldRenderer worldRenderer;

    protected final float gameWidth = 400f;
    private final float gameHeight = 640f;

    public static final String TEXTURE_ATLAS_OBJECTS = "images/helfris.atlas";

    private boolean paused;
    private GameWorld gameWorld;
    private float timeSpent;
    private float fps = 60;
    private FrameBuffer frameBuffer;
    private SpriteBatch fbBatch;
    protected int screenWidth;
    protected int screenHeight;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        gameWorld = new GameWorld();

        worldController = new WorldController(this, gameWorld);
        worldRenderer = new WorldRenderer(gameWorld, worldController, gameWidth, gameHeight);

        paused = false;

    }

    @Override
    public synchronized void render() {
        super.render();

        if (!paused) {
            worldController.update(Gdx.graphics.getDeltaTime());
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
