package de.nitri.nitris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.Arrays;

import de.nitri.nitris.objects.Tetromino;

/**
 * Created by helfrich on 4/2/15.
 */
public class GameWorld {

    public static final String TAG = GameWorld.class.getName();
    private final FrameBuffer frameBuffer;
    private final SpriteBatch fbBatch;
    public int[][] playfield = new int[22][10]; //used for rendering
    public boolean[][] blocks = new boolean[22][10];  // used for object detection

    private boolean[][] oldGrid;

    private int oldPosX;
    private int oldPosY;

    public int score = 0;
    public int level = 0;

    public GameWorld() {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 400, 600, false);
        fbBatch = new SpriteBatch();

    }

    public void setTetrominoGrid(boolean[][] tetrominoGrid) {
        this.tetrominoGrid = tetrominoGrid;
        oldGrid = null;
    }

    public void reset() {
        score = 0;
        level = 0;
        playfield = new int[22][10]; //used for rendering
        blocks = new boolean[22][10];  // used for object detection
    }

    private boolean[][] tetrominoGrid;

    public synchronized void update(float deltaTime, Tetromino tetromino) {
        if (null != tetromino) {
            int posX = tetromino.posX;
            int posY = tetromino.posY;
            tetrominoGrid = tetromino.grid.clone();
            int nRows = tetrominoGrid.length;
            int nCols = tetrominoGrid[0].length;
            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    if (oldGrid != null && tetromino.isFalling()) {
                        // erase
                        if (oldGrid[i][j]) {
                            playfield[oldPosY + i][oldPosX + j] = 0;
                        }
                    }
                    if (tetrominoGrid[i][j]) {
                        playfield[posY + i][posX + j] = tetromino.type;
                        if (!tetromino.isFalling()) {
                            blocks[posY + i][posX + j] = true;
                        }
                    }
                }
            }

            oldGrid = Arrays.copyOf(tetrominoGrid, tetrominoGrid.length);

            oldPosX = posX;
            oldPosY = posY;
        }

    }

    private void logIntArray(int[][] ar, String name) {
        Gdx.app.log(TAG, "");
        Gdx.app.log(TAG, name);
        for (int i = 0; i < ar.length; i++) {
            String row = "";
            for (int j = 0; j < ar[0].length; j++) {
                row += " " + ar[i][j];
            }
            row += " [" + i + "]";
            Gdx.app.log(TAG, row);
        }
        Gdx.app.log(TAG, "");
    }

    private void logBooleanArray(boolean[][] ar, String name) {
        Gdx.app.log(TAG, "");
        Gdx.app.log(TAG, name);
        for (int i = 0; i < ar.length; i++) {
            String row = "";
            for (int j = 0; j < ar[0].length; j++) {
                row += " " + ar[i][j];
            }
            row += " [" + i + "]";
            Gdx.app.log(TAG, row);
        }
        Gdx.app.log(TAG, "");
    }

    public void dispose() {
    }
}
