package de.nitri.nitris;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
    public GameState gameState;
    public Tetromino nextTetromino;
    private int levelRowsRemoved;
    private Skin skinLibGdx;
    private Window winOptions;
    public Stage windowStage;
    private Preferences prefs;
    private float winScaleFactor;


    public WorldController(NitrisGame game, GameWorld gameWorld) {
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

        windowStage = new Stage();

        skinLibGdx = new Skin(Gdx.files.internal("images/uiskin.json"), new TextureAtlas("images/uiskin.atlas"));

        winOptions = new Window("Nitris", skinLibGdx);

        float width = Gdx.graphics.getWidth();

        winScaleFactor = (1/300f) * width - 1/3f;

        winOptions.setScale(winScaleFactor, winScaleFactor);
        winOptions.add(buildInfoText());
        winOptions.row();
        winOptions.add(buildOptions());
        winOptions.pack();
        winOptions.setColor(1, 1, 1, 0.8f);
        winOptions.setPosition(Gdx.graphics.getWidth() - winOptions.getWidth() * winScaleFactor - 50, 50);
        winOptions.setVisible(false);
        windowStage.addActor(winOptions);

        windowStage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Gdx.app.log("x", String.valueOf(x));
                //Gdx.app.log("y", String.valueOf(y));
                if (x < winOptions.getX() || x > winOptions.getX() + winOptions.getWidth() * winScaleFactor ||
                        y < winOptions.getY() || y > winOptions.getY() + winOptions.getY() + winOptions.getHeight() * winScaleFactor) {
                    winOptions.setVisible(false);
                    Gdx.input.setInputProcessor(null);
                }
            }
        });
    }

    private Table buildOptions() {
        Table tbl = new Table();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        Label lblSound = new Label("Sound", skinLibGdx);

        final CheckBox chkSound = new CheckBox("", skinLibGdx);

        chkSound.setChecked(prefs.getBoolean("sound"));

        chkSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putBoolean("sound", chkSound.isChecked());
                prefs.flush();
            }
        });
        tbl.add(chkSound);
        tbl.add(lblSound);
        return tbl;
    }

    private Table buildInfoText() {
        Table tbl = new Table();
        Label lblText = new Label("Nitris by Pygmalion", skinLibGdx, "default-font", Color.WHITE);
        tbl.add(lblText);
        tbl.row();
        Label lblUrl = new Label("pygmalion.nitri.de", skinLibGdx);
        lblUrl.setColor(0, 0, 1, 1);
        lblUrl.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("http://pygmalion.nitri.de");
            }
        });
        tbl.add(lblUrl);
        tbl.row();
        if (Gdx.app.getType() != Application.ApplicationType.Android && Gdx.app.getType() != Application.ApplicationType.iOS) {
            Label lblHelp = new Label("Use the arrow keys to move the piece ('up' to rotate right, 'down' to accelerate.)", skinLibGdx);
            lblHelp.setWrap(true);
            tbl.add(lblHelp).width(160f);
            tbl.row();
        }
        return tbl;
    }

    public void update(float deltaTime) {

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
                            gy < game.worldRenderer.leftArrowScreenY +  game.worldRenderer.controlScreenWidth) {
                        tetromino.move(-1, 0);
                        moved = true;
                    } else if (gx > game.worldRenderer.rightArrowScreenX &&
                            gx < game.worldRenderer.rightArrowScreenX +  game.worldRenderer.controlScreenWidth &&
                            gy > game.worldRenderer.rightArrowScreenY &&
                            gy < game.worldRenderer.rightArrowScreenY +  game.worldRenderer.controlScreenWidth) {
                        tetromino.move(1, 0);
                        moved = true;
                    } else if (gx > game.worldRenderer.rotateArrowScreenX &&
                            gx < game.worldRenderer.rotateArrowScreenY +  game.worldRenderer.controlScreenWidth &&
                            gy > game.worldRenderer.rotateArrowScreenY &&
                            gy < game.worldRenderer.rotateArrowScreenY +  game.worldRenderer.controlScreenWidth) {
                        tetromino.rotate();
                        moved = true;
                    }
                }
                if ((Gdx.input.justTouched() || Gdx.input.isTouched()) && game.worldRenderer != null) {
                    int gx = Gdx.input.getX();
                    int gy = Gdx.input.getY();
                    if (gx > game.worldRenderer.downArrowScreenX &&
                            gx < game.worldRenderer.downArrowScreenX +  game.worldRenderer.controlScreenWidth &&
                            gy > game.worldRenderer.downArrowScreenY &&
                            gy < game.worldRenderer.downArrowScreenY + game.worldRenderer.controlScreenWidth) {
                        tetromino.fall(true);
                        moved = true;
                    }
                }
                if (!moved)
                    tetromino.fall(false);
                gameWorld.update(deltaTime, tetromino);
                break;
            case GameOver:
                checkMenuControls();
                break;
        }
        windowStage.act();
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
                winOptions.setVisible(true);

                Gdx.input.setInputProcessor(windowStage);
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
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    public void dispose() {
        gameWorld.dispose();
        if (skinLibGdx != null) {
            skinLibGdx.dispose();
        }
        if (windowStage != null) {
            windowStage.dispose();
        }
    }

    static enum GameState {
        Intro, Start, Running, GameOver, Options
    }

}
