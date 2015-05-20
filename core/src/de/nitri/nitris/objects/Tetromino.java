package de.nitri.nitris.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;

import de.nitri.nitris.GameWorld;
import de.nitri.nitris.WorldController;

/**
 * Created by helfrich on 4/2/15.
 */
public class Tetromino {

    //types
    public final static int I = 1;
    public final static int O = 2;
    public final static int T = 3;
    public final static int S = 4;
    public final static int Z = 5;
    public final static int J = 6;
    public final static int L = 7;
    private static Tetromino _instance;

    public int type;

    public static final String TAG = Tetromino.class.getName();

    private boolean[][] gridI = {{false, false, false, false},
            {true, true, true, true},
            {false, false, false, false},
            {false, false, false, false}};

    private boolean[][] gridO = {{false, true, true, false},
            {false, true, true, false},
            {false, false, false, false}};

    private boolean[][] gridT = {{false, true, false},
            {true, true, true},
            {false, false, false}};

    private boolean[][] gridS = {{false, true, true},
            {true, true, false},
            {false, false, false}};

    private boolean[][] gridZ = {{true, true, false},
            {false, true, true},
            {false, false, false}};

    private boolean[][] gridJ = {{true, false, false},
            {true, true, true},
            {false, false, false}};

    private boolean[][] gridL = {{false, false, true},
            {true, true, true},
            {false, false, false}};

    public TextureRegion[][] blockTextureRegions;

    public boolean[][] grid;

    private boolean[][] rotatedGrid;

    private boolean[][] rotatedGrid3 = new boolean[3][3];

    private boolean[][] rotatedGrid4 = new boolean[4][4];

    private int oldPosX;
    private int oldPosY;
    public int posX;
    public int posY;
    private GameWorld gameWorld;
    private WorldController worldController;
    public boolean moved;

    public boolean isFalling() {
        return falling;
    }

    private boolean falling;

    public int delay = 800;

    public long getLastFallTime() {
        return lastFallTime;
    }

    public void setLastFallTime(long lastFallTime) {
        this.lastFallTime = lastFallTime;
    }

    public long lastFallTime;

    public Tetromino(GameWorld gameWorld, WorldController worldController) {
        this.gameWorld = gameWorld;
        this.worldController = worldController;

    }

    public synchronized void init(int type) {
        this.type = type;
        this.falling = true;

        delay = 800 - (gameWorld.level * 33);
        if (delay < 33) {
            delay = 33;
        }

        switch (type) {
            case I:
                grid = new boolean[4][4];
                grid = deepCopy(gridI);
                break;
            case O:
                grid = new boolean[3][4];
                grid = deepCopy(gridO);
                break;
            case T:
                grid = new boolean[3][3];
                grid = deepCopy(gridT);
                break;
            case S:
                grid = new boolean[3][3];
                grid = deepCopy(gridS);
                break;
            case Z:
                grid = new boolean[3][3];
                grid = deepCopy(gridZ);
                break;
            case J:
                grid = new boolean[3][3];
                grid = deepCopy(gridJ);
                break;
            case L:
                grid = new boolean[3][3];
                grid = deepCopy(gridL);
                break;
        }
        //logBooleanArray(grid, "grid");
        gameWorld.setTetrominoGrid(grid);
        posX = gameWorld.playfield[0].length / 2 - grid[0].length / 2;
        if (grid[0].length < 4) posX--;
        posY = 0;
/*
        blockTextureRegions = new TextureRegion[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j]) {
                    switch (type) {
                        case I:
                            blockTextureRegions[i][j] = Assets.instance.tetromino.elementCyanSquare;
                            break;
                        case O:
                            blockTextureRegions[i][j] = Assets.instance.tetromino.elementYellowSquare;
                            break;
                        case J:
                            blockTextureRegions[i][j] = Assets.instance.tetromino.elementBlueSquare;
                            break;
                    }
                }
            }
        }
        */
    }

    public void fall(boolean force) {
        if (force || System.currentTimeMillis() - lastFallTime >= delay) {
            boolean possible = true;
            falling = true;
            if (!force)
                lastFallTime = System.currentTimeMillis();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    if (grid[i][j]) {
                        int blockRow = posY + i;
                        int blockCol = posX + j;
                        if (blockRow >= gameWorld.blocks.length - 1) {
                            possible = false;
                        } else if (gameWorld.blocks[blockRow + 1][blockCol]) {
                            possible = false;
                        }
                    }
                }
            }
            if (possible) {
                posY++;
                moved = true;
            } else {
                //gameWorld.resetPositionTracking();
                worldController.tetrominoSpawned = false;
                falling = false;
                if (posY == 0) {
                    worldController.gameOver();
                }
            }
        }

    }

    public synchronized void move(int deltaX, int deltaY) {
        int newPosX = posX + deltaX;
        int newPosY = posY + deltaY;
        boolean possible = true;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j]) {
                    int blockRow = newPosY + i;
                    int blockCol = newPosX + j;
                    if (blockRow >= gameWorld.blocks.length || blockCol < 0 || blockCol >= gameWorld.blocks[0].length) {
                        possible = false;
                    } else if (gameWorld.blocks[blockRow][blockCol]) {
                        possible = false;
                    }
                }
            }
        }
        if (possible) {
            posX = newPosX;
            posY = newPosY;
        }
    }

    public synchronized void rotate() {
        //Gdx.app.debug(TAG, "rotate");
        if (grid.length == grid[0].length) {
            // rotatable
            int size = grid.length;
/*            if (size == 4)
                rotatedGrid = Arrays.copyOf(rotatedGrid4, 4);
            else
                rotatedGrid = Arrays.copyOf(rotatedGrid3, 3);*/
            rotatedGrid = new boolean[size][size];

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    //left
                    //rotatedGrid[size - 1 - j][i] = grid[i][j];
                    //right
                    rotatedGrid[j][size - 1 - i] = grid[i][j];
                }
            }
            boolean possible = true;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (rotatedGrid[i][j]) {
                        int blockRow = posY + i;
                        int blockCol = posX + j;
                        if (blockRow >= gameWorld.blocks.length || blockCol < 0 || blockCol >= gameWorld.blocks[0].length) {
                            possible = false;
                        } else if (gameWorld.blocks[blockRow][blockCol]) {
                            possible = false;
                        }
                    }
                }
            }
            // if (possible) grid = rotatedGrid;
            if (possible) {
                grid = Arrays.copyOf(rotatedGrid, rotatedGrid.length);
                moved = true;
            }
        }
    }

    public void render(float deltaTime) {

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

    public static boolean[][] deepCopy(boolean[][] original) {
        if (original == null) {
            return null;
        }

        final boolean[][] result = new boolean[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
            // For Java versions prior to Java 6 use the next:
            // System.arraycopy(original[i], 0, result[i], 0, original[i].length);
        }
        return result;
    }
}
