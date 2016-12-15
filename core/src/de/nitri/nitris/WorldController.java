package de.nitri.nitris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Random;

import de.nitri.nitris.objects.Tetromino;

/**
 * Created by helfrich on 4/2/15.
 */
public class WorldController {

    private final NitrisGame game;
    private String TAG = WorldController.class.getName();

    private GameWorld gameWorld;
    public boolean tetrominoSpawned = false;
    private Tetromino tetromino;
    GameState gameState;
    Tetromino nextTetromino;
    private int levelRowsRemoved;
    private Skin skinLibGdx;
    private Preferences prefs;

    WorldController(NitrisGame game, GameWorld gameWorld) {
        this.game = game;
        this.gameWorld = gameWorld;
        init();
    }

    private void init() {
        //TODO:

        prefs = Gdx.app.getPreferences("Nitris");

        gameState = GameState.Start;
        tetromino = new Tetromino(gameWorld, this);
        nextTetromino = new Tetromino(gameWorld, this);
        levelRowsRemoved = 0;

        skinLibGdx = new Skin(Gdx.files.internal("images/uiskin.json"), new TextureAtlas("images/uiskin.atlas"));

    }

    void update() {

        switch (gameState) {
            case Start:
                checkMenuControls();
                break;
            case Running:
                checkRows();
                boolean moved = false;
                if (!tetrominoSpawned) spawnTetromino();
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    tetromino.rotate();
                    moved = true;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    tetromino.move(-1, 0);
                    moved = true;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    tetromino.move(1, 0);
                    moved = true;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    //tetromino.move(0, 1);
                    tetromino.fall(true);
                    moved = true;
                }
                if (Gdx.input.justTouched() && game.worldRenderer != null) {
                    int gx = Gdx.input.getX();
                    int gy = Gdx.input.getY();
                    if (gx > game.worldRenderer.leftArrowScreenX &&
                            gx < game.worldRenderer.leftArrowScreenX + game.worldRenderer.controlScreenWidth &&
                            gy > game.worldRenderer.leftArrowScreenY &&
                            gy < game.worldRenderer.leftArrowScreenY + game.worldRenderer.controlScreenWidth) {
                        tetromino.move(-1, 0);
                        moved = true;
                    } else if (gx > game.worldRenderer.rightArrowScreenX &&
                            gx < game.worldRenderer.rightArrowScreenX + game.worldRenderer.controlScreenWidth &&
                            gy > game.worldRenderer.rightArrowScreenY &&
                            gy < game.worldRenderer.rightArrowScreenY + game.worldRenderer.controlScreenWidth) {
                        tetromino.move(1, 0);
                        moved = true;
                    } else if (gx > game.worldRenderer.rotateArrowScreenX &&
                            gx < game.worldRenderer.rotateArrowScreenY + game.worldRenderer.controlScreenWidth &&
                            gy > game.worldRenderer.rotateArrowScreenY &&
                            gy < game.worldRenderer.rotateArrowScreenY + game.worldRenderer.controlScreenWidth) {
                        tetromino.rotate();
                        moved = true;
                    }
                }
                if ((Gdx.input.justTouched() || Gdx.input.isTouched()) && game.worldRenderer != null) {
                    int gx = Gdx.input.getX();
                    int gy = Gdx.input.getY();
                    if (gx > game.worldRenderer.downArrowScreenX &&
                            gx < game.worldRenderer.downArrowScreenX + game.worldRenderer.controlScreenWidth &&
                            gy > game.worldRenderer.downArrowScreenY &&
                            gy < game.worldRenderer.downArrowScreenY + game.worldRenderer.controlScreenWidth) {
                        tetromino.fall(true);
                        moved = true;
                    }
                }
                if (!moved)
                    tetromino.fall(false);
                gameWorld.update(tetromino);
                break;
            case GameOver:
                checkMenuControls();
                break;
        }
    }

    private void checkMenuControls() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            int gx = Gdx.input.getX();
            int gy = Gdx.input.getY();
            if (gx > game.worldRenderer.playScreenX &&
                    gx < game.worldRenderer.playScreenX + game.worldRenderer.controlScreenWidth &&
                    gy > game.worldRenderer.playScreenY &&
                    gy < game.worldRenderer.playScreenY + game.worldRenderer.controlScreenWidth) {
                gameWorld.reset();
                gameState = GameState.Running;
            }
            if (gx > game.worldRenderer.optionsScreenX &&
                    gx < game.worldRenderer.optionsScreenX + game.worldRenderer.controlScreenWidth &&
                    gy > game.worldRenderer.optionsScreenY &&
                    gy < game.worldRenderer.optionsScreenY + game.worldRenderer.controlScreenWidth) {
                //winOptions.setVisible(true);
                //Gdx.input.setInputProcessor(windowStage);
                game.setScreen(game.settingsScreen);
            }
        }
    }

    private void checkRows() {
        int rowsRemoved = 0;
        if (null == tetromino || System.currentTimeMillis() - tetromino.lastFallTime >= tetromino.delay) {
            boolean checkAgain = false;
            for (int i = 0; i < gameWorld.blocks.length; i++) {
                if (checkAgain) {
                    i -= 1;
                }
                boolean full = true;
                for (int j = 0; j < gameWorld.blocks[0].length; j++) {
                    if (!gameWorld.blocks[i][j]) {
                        full = false;
                    }
                }
                if (full) {
                    removeRow(i);
                    rowsRemoved++;
                    levelRowsRemoved++;
                    checkAgain = true;
                } else {
                    checkAgain = false;
                }
            }
        }

        if (rowsRemoved > 0) {
            play(Assets.instance.sounds.rowCleared);
        }

        switch (rowsRemoved) {
            case 1:
                gameWorld.score += 40 * (gameWorld.level + 1);
                break;
            case 2:
                gameWorld.score += 100 * (gameWorld.level + 1);
                break;
            case 3:
                gameWorld.score += 300 * (gameWorld.level + 1);
                break;
            case 4:
                gameWorld.score += 1200 * (gameWorld.level + 1);
                break;
        }
        if (levelRowsRemoved >= 10) {
            gameWorld.level++;
            levelRowsRemoved = 0;
            play(Assets.instance.sounds.levelUp);
        }
    }

    private void removeRow(int row) {
        for (int j = 0; j < gameWorld.blocks[0].length; j++) {
            gameWorld.blocks[row][j] = false;
            gameWorld.playfield[row][j] = 0;
        }
        for (int i = row; i > 0; i--) {
            for (int j = 0; j < gameWorld.blocks[0].length; j++) {
                if (gameWorld.blocks[i - 1][j]) {
                    gameWorld.blocks[i - 1][j] = false;
                    gameWorld.blocks[i][j] = true;
                    gameWorld.playfield[i][j] = gameWorld.playfield[i - 1][j];
                    gameWorld.playfield[i - 1][j] = 0;
                }
            }
        }
    }

    public void gameOver() {
        gameState = GameState.GameOver;
        play(Assets.instance.sounds.gameOver);
    }

    private void spawnTetromino() {

        if (System.currentTimeMillis() - tetromino.lastFallTime >= tetromino.delay) {
            if (nextTetromino.type == 0) {
                tetromino.init(randInt(1, 7));
            } else {
                tetromino.init(nextTetromino.type);
            }
            nextTetromino.init(randInt(1, 7));
            tetrominoSpawned = true;
        }
    }

    private void play(Sound sound) {
        if (prefs.getBoolean("sound", false)) {
            sound.play();
        }
    }

    private static Random rand = new Random();

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    private static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    void dispose() {
        gameWorld.dispose();
        if (skinLibGdx != null) {
            skinLibGdx.dispose();
        }
    }

    enum GameState {
        Start, Running, GameOver
    }

}
