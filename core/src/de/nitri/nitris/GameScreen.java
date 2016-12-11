package de.nitri.nitris;

import com.badlogic.gdx.Screen;

/**
 * Created by helfrich on 11/12/2016.
 */

class GameScreen implements Screen {

    private final NitrisGame game;
    private boolean paused;

    GameScreen(final NitrisGame game) {
        this.game = game;
        paused = false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        if (!paused) {
            game.worldController.update();
        }

        game.worldRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        game.worldRenderer.resize(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
