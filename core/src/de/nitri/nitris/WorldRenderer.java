package de.nitri.nitris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import de.nitri.nitris.objects.Tetromino;


/**
 * Created by helfrich on 4/2/15.
 */
public class WorldRenderer implements Disposable {

    private final float gameWidth;
    private final float gameHeight;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private WorldController worldController;
    private GameWorld gameWorld;
    private ShapeRenderer shapeRenderer;

    public static final int FIELD_MARGIN_LEFT = 20;
    public static final int FIELD_MARGIN_TOP = 20;
    private static final int CONTROL_VERTICAL_MARGIN = 5;

    public static final int BLOCK_WIDTH = 20;
    public static final int CONTROL_WIDTH = 100;
    private BitmapFont gameOverFont;

    private static final String GAME_OVER = "GAME OVER";
    private static final String SCORE = "Score";
    private static final String LEVEL = "Level";
    private float centerY;
    private FrameBuffer frameBuffer;
    private SpriteBatch fbBatch;
    private int playfieldWidth;
    private int playfieldHeight;
    private int playfieldCenterX;
    private int playfieldCenterY;
    private int marginRightCenterX;
    private int marginBottomCenterY;
    private BitmapFont scoreFont;
    public int leftArrowX;
    public int leftArrowY;
    public int rightArrowX;
    public int rightArrowY;
    public int rotateArrowX;
    public int rotateArrowY;
    public int downArrowX;
    public int downArrowY;
    public int playX;
    public int playY;
    public int optionsX;
    public int optionsY;
    private GlyphLayout gameOverGlyphLayout;
    private GlyphLayout scoreGlyphLayout;
    private Vector3 worldVector;
    private Vector3 screenVector;
    public float controlScreenWidth;
    public float controlScreenHeight;
    public float playScreenX;
    public float playScreenY;
    public float optionsScreenY;
    public float optionsScreenX;
    public float leftArrowScreenX;
    public float leftArrowScreenY;
    public float rightArrowScreenX;
    public float rightArrowScreenY;
    public float downArrowScreenX;
    public float downArrowScreenY;
    public float rotateArrowScreenX;
    public float rotateArrowScreenY;

    public WorldRenderer(GameWorld gameWorld, WorldController worldController, float gameWidth, float gameHeight) {
        this.gameWorld = gameWorld;
        this.worldController = worldController;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        init();
    }

    private void init() {
        camera = new OrthographicCamera();
        camera.setToOrtho(true, gameWidth, gameHeight);

        worldVector = new Vector3();
        screenVector = new Vector3();

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        Assets.instance.init(new AssetManager());

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("zorque.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.flip = true;
        gameOverFont = generator.generateFont(parameter);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("kenvector_future.ttf"));
        parameter.size = 24;
        parameter.flip = true;
        scoreFont = generator.generateFont(parameter);

        centerY = gameHeight / 2;
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) gameWidth, (int) gameHeight, false);
        fbBatch = new SpriteBatch();

        playfieldWidth = BLOCK_WIDTH * 10;
        playfieldHeight = BLOCK_WIDTH * 20;

        playfieldCenterX = FIELD_MARGIN_LEFT + playfieldWidth / 2;
        playfieldCenterY = FIELD_MARGIN_TOP + playfieldHeight / 2;

        marginRightCenterX = (int) (FIELD_MARGIN_LEFT + playfieldWidth + ((gameWidth - playfieldWidth - FIELD_MARGIN_LEFT) / 2));

        marginBottomCenterY = (int) (gameHeight - ((gameHeight - (FIELD_MARGIN_TOP + playfieldHeight)) / 2));



        leftArrowX = (int) (gameWidth / 4) - (CONTROL_WIDTH / 2);
        leftArrowY = marginBottomCenterY - (CONTROL_WIDTH / 2);

        rightArrowX = (int) ((gameWidth / 4) * 3) - (CONTROL_WIDTH / 2);
        rightArrowY = leftArrowY;

        rotateArrowX = (int) (gameWidth / 2) - (CONTROL_WIDTH / 2);
        rotateArrowY = marginBottomCenterY - CONTROL_WIDTH - CONTROL_VERTICAL_MARGIN;

        downArrowX = rotateArrowX;
        downArrowY = marginBottomCenterY + CONTROL_VERTICAL_MARGIN;

        playX = (int) (gameWidth / 3) - (CONTROL_WIDTH / 2);
        playY = leftArrowY;

        optionsX = (int) ((gameWidth / 3) * 2) - (CONTROL_WIDTH / 2);
        optionsY = playY;

        gameOverGlyphLayout = new GlyphLayout();
        scoreGlyphLayout = new GlyphLayout();



    }

    public void render() {

        renderWorld();
        camera.update();

    }

    private synchronized void renderWorld() {

        // playfield
        frameBuffer.begin();

        Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff / 255.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.rect(0, 0, gameWidth, gameHeight, Color.CYAN, Color.CYAN, Color.BLUE, Color.BLUE);

        shapeRenderer.setColor(0 / 255f, 0 / 255f, 0 / 255f, 1f);
        shapeRenderer.rect(FIELD_MARGIN_LEFT, FIELD_MARGIN_TOP,
                playfieldWidth, playfieldHeight);
        shapeRenderer.end();

        batch.begin();

        renderPlayfield();

        if (worldController.gameState == WorldController.GameState.Running || worldController.gameState == WorldController.GameState.GameOver)
            renderNextTetromino();

        renderScore();

        renderControls();

        if (worldController.gameState == WorldController.GameState.GameOver) {
            //float textWidth = gameOverFont.getBounds(GAME_OVER).width;
            //float textHeight = gameOverFont.getBounds(GAME_OVER).height;
            gameOverGlyphLayout.setText(gameOverFont, GAME_OVER);
            float textWidth = gameOverGlyphLayout.width;
            float textHeight = gameOverGlyphLayout.height;
            float textX = playfieldCenterX - textWidth / 2;
            float textY = playfieldCenterY - textHeight / 2;
            gameOverFont.setColor(Color.BLACK);
            gameOverFont.draw(batch, GAME_OVER, textX + 2, textY - 2);
            gameOverFont.setColor(Color.RED);
            gameOverFont.draw(batch, GAME_OVER, textX, textY);
        }


        batch.end();

        frameBuffer.end();

        fbBatch.begin();
        fbBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        fbBatch.end();

        if (worldController.windowStage != null) {

            worldController.windowStage.draw();

        }

    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void renderControls() {
        switch (worldController.gameState) {
            case Running:
                batch.draw(Assets.instance.controls.arrowLeft, leftArrowX, leftArrowY, CONTROL_WIDTH, CONTROL_WIDTH);
                batch.draw(Assets.instance.controls.arrowRight, rightArrowX, rightArrowY, CONTROL_WIDTH, CONTROL_WIDTH);
                batch.draw(Assets.instance.controls.arrowRotate, rotateArrowX, rotateArrowY, CONTROL_WIDTH, CONTROL_WIDTH);
                batch.draw(Assets.instance.controls.arrowDown, downArrowX, downArrowY, CONTROL_WIDTH, CONTROL_WIDTH);
                break;
            default:
                batch.draw(Assets.instance.controls.play, playX, playY, CONTROL_WIDTH, CONTROL_WIDTH);
                batch.draw(Assets.instance.controls.options, optionsX, optionsY, CONTROL_WIDTH, CONTROL_WIDTH);

        }
    }

    private void renderScore() {
        scoreGlyphLayout.setText(scoreFont, SCORE);
        float textWidth = scoreGlyphLayout.width;
        float textHeight = scoreGlyphLayout.height;
        //float textWidth = scoreFont.getBounds(SCORE).width;
        //float textHeight = scoreFont.getBounds(SCORE).height;
        float textX = marginRightCenterX - textWidth / 2;
        float textY = playfieldCenterY / 2 - textHeight / 2;
        scoreFont.setColor(Color.WHITE);

        scoreFont.draw(batch, SCORE, textX, textY);

        String score = String.valueOf(gameWorld.score);
        //textWidth = scoreFont.getBounds(score).width;
        scoreGlyphLayout.setText(scoreFont, score);
        textWidth = scoreGlyphLayout.width;
        textX = marginRightCenterX - textWidth / 2;
        textY += textHeight * 1.3f;
        scoreFont.draw(batch, score, textX, textY);

        //textWidth = scoreFont.getBounds(LEVEL).width;
        scoreGlyphLayout.setText(scoreFont, LEVEL);
        textWidth = scoreGlyphLayout.width;
        textX = marginRightCenterX - textWidth / 2;
        textY += textHeight * 2.6f;
        scoreFont.draw(batch, LEVEL, textX, textY);

        String level = String.valueOf(gameWorld.level);
        //textWidth = scoreFont.getBounds(level).width;
        scoreGlyphLayout.setText(scoreFont, level);
        textWidth = scoreGlyphLayout.width;
        textX = marginRightCenterX - textWidth / 2;
        textY += textHeight * 1.3f;
        scoreFont.draw(batch, level, textX, textY);

    }

    private void renderNextTetromino() {
        if (worldController.nextTetromino != null && worldController.nextTetromino.type != 0) {
            for (int i = 0; i < worldController.nextTetromino.grid.length; i++) {
                for (int j = 0; j < worldController.nextTetromino.grid[0].length; j++) {
                    if (worldController.nextTetromino.grid[i][j]) {
                        int x = (marginRightCenterX - (worldController.nextTetromino.grid[0].length / 2) * BLOCK_WIDTH) + (j * BLOCK_WIDTH);
                        int y = FIELD_MARGIN_TOP + (i * BLOCK_WIDTH);
                        drawBlock(worldController.nextTetromino.type, x, y);
                    }
                }
            }
        }
    }

    public synchronized void renderPlayfield() {

        //logIntArray(playfield, "playfield");

        for (int i = 2; i < gameWorld.playfield.length; i++) {
            for (int j = 0; j < gameWorld.playfield[0].length; j++) {
                if (gameWorld.playfield[i][j] > 0) {
                    int x = WorldRenderer.FIELD_MARGIN_LEFT + j * WorldRenderer.BLOCK_WIDTH;
                    int y = WorldRenderer.FIELD_MARGIN_TOP + (i - 2) * WorldRenderer.BLOCK_WIDTH;
                    //Gdx.app.debug(TAG, "i = " + i);
                    drawBlock(gameWorld.playfield[i][j], x, y);
                }
            }
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void drawBlock(int type, int x, int y) {
        switch (type) {
            case Tetromino.I:
                batch.draw(Assets.instance.tetromino.elementCyanSquare, x, y, WorldRenderer.BLOCK_WIDTH, WorldRenderer.BLOCK_WIDTH);
                break;
            case Tetromino.O:
                batch.draw(Assets.instance.tetromino.elementYellowSquare, x, y, WorldRenderer.BLOCK_WIDTH, WorldRenderer.BLOCK_WIDTH);
                break;
            case Tetromino.T:
                batch.draw(Assets.instance.tetromino.elementPurpleSquare, x, y, WorldRenderer.BLOCK_WIDTH, WorldRenderer.BLOCK_WIDTH);
                break;
            case Tetromino.S:
                batch.draw(Assets.instance.tetromino.elementGreenSquare, x, y, WorldRenderer.BLOCK_WIDTH, WorldRenderer.BLOCK_WIDTH);
                break;
            case Tetromino.Z:
                batch.draw(Assets.instance.tetromino.elementRedSquare, x, y, WorldRenderer.BLOCK_WIDTH, WorldRenderer.BLOCK_WIDTH);
                break;
            case Tetromino.J:
                batch.draw(Assets.instance.tetromino.elementBlueSquare, x, y, WorldRenderer.BLOCK_WIDTH, WorldRenderer.BLOCK_WIDTH);
                break;
            case Tetromino.L:
                batch.draw(Assets.instance.tetromino.elementOrangeSquare, x, y, WorldRenderer.BLOCK_WIDTH, WorldRenderer.BLOCK_WIDTH);
                break;
        }
    }

    private float toScreenX(int worldX) {
        worldVector.x = worldX;
        worldVector.y = 0;
        worldVector.z = 0;
        screenVector = camera.project(worldVector);
        return screenVector.x;
    }

    private float toScreenY(int worldY) {
        worldVector.x = 0;
        worldVector.y = gameHeight - worldY;
        worldVector.z = 0;
        screenVector = camera.project(worldVector);
        return screenVector.y;
    }

    public void resize(int width, int height) {
        controlScreenWidth = toScreenX(CONTROL_WIDTH);
        controlScreenHeight = toScreenY(CONTROL_WIDTH);
        playScreenX = toScreenX(playX);
        playScreenY = toScreenY(playY);
        optionsScreenX = toScreenX(optionsX);
        optionsScreenY = toScreenY(optionsY);
        leftArrowScreenX = toScreenX(leftArrowX);
        leftArrowScreenY = toScreenY(leftArrowY);
        rightArrowScreenX = toScreenX(rightArrowX);
        rightArrowScreenY = toScreenY(rightArrowY);
        downArrowScreenX = toScreenX(downArrowX);
        downArrowScreenY = toScreenY(downArrowY);
        rotateArrowScreenX = toScreenX(rotateArrowX);
        rotateArrowScreenY = toScreenY(rotateArrowY);
    }

    @Override
    public void dispose() {
        if (null != frameBuffer)
            frameBuffer.dispose();
        if (null != batch)
            batch.dispose();
        if (null != fbBatch)
            fbBatch.dispose();

    }
}
