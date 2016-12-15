package de.nitri.nitris;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

/**
 * Created by helfrich on 12/12/2016.
 */

class SettingsScreen implements Screen {

    private final BitmapFont infoFont;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont linkFont;
    private final Preferences prefs;
    private final int checkboxX;
    private final int checkboxY;
    //private final int checkboxWidth;
    // private final int checkboxHeight;
    private int checkboxScreenWidth;
    private int checkboxScreenHeight;
    private int checkboxScreenX;
    private int checkboxScreenY;
    private final int soundLabelX;
    private final int soundLabelY;
    private final int leftArrowX;
    private final int leftArrowY;
    private int leftArrowScreenX;
    private int leftArrowScreenY;
    private final TextureAtlas.AtlasRegion leftArrow;
    private int leftArrowScreenWidth;
    private int leftArrowScreenHeight;
    private final NitrisGame game;
    private Vector3 worldVector;
    private Vector3 screenVector;
    private final float gameWidth;
    private final float gameHeight;
    private float textX;
    private int textY;

    private static String infoText = "Nitris by Pygmalion";
    private static final String linkText = "pygmalion.nitri.de";
    private static final String soundText = "Sound";
    private int linkX;
    private int linkY;
    private int linkWidth;
    private int linkScreenWidth;
    private int linkHeight;
    private int linkScreenHeight;
    private int linkScreenX;
    private int linkScreenY;

    private static final int CHECKBOX_WIDTH = 30;
    private static final int CHECKBOX_SPACE = 12;
    private static final int CONTROL_WIDTH = 100;

    @SuppressWarnings("SuspiciousNameCombination")
    SettingsScreen(NitrisGame game) {

        this.game = game;
        this.gameWidth = game.gameWidth;
        this.gameHeight = game.gameHeight;

        prefs = Gdx.app.getPreferences("Nitris");

        // TODO: get camera and other objects from the game instance

        camera = new OrthographicCamera();
        camera.setToOrtho(true, gameWidth, gameHeight);

        worldVector = new Vector3();
        screenVector = new Vector3();
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("kenvector_future.ttf"));
        parameter.size = 16;
        parameter.flip = true;
        infoFont = generator.generateFont(parameter);
        linkFont = generator.generateFont(parameter);
        linkFont.setColor(Color.BLUE);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);


        if (Gdx.app.getType() != Application.ApplicationType.Android && Gdx.app.getType() != Application.ApplicationType.iOS) {
            infoText += "\n\nUse the arrow keys to move\nthe piece ('up' to rotate\nright, 'down' to accelerate).";
        }
        GlyphLayout infoGlyphLayout = new GlyphLayout(infoFont, infoText, Color.WHITE, gameWidth * .8f, Align.left, true);
        GlyphLayout linkGlyphLayout = new GlyphLayout(linkFont, linkText, Color.BLUE, gameWidth * .8f, Align.left, true);

        float textWidth = infoGlyphLayout.width;
        textX = (gameWidth - textWidth) / 2;
        textY = 100;

        linkWidth = (int) linkGlyphLayout.width;
        linkHeight = (int) linkGlyphLayout.height;
        linkX = (int) (gameWidth - linkWidth) / 2;
        linkY = (int) (gameHeight + linkHeight) / 2;

        linkScreenWidth = toScreenX(linkWidth);
        linkScreenHeight = toScreenY(linkHeight);
        linkScreenX = toScreenX(linkX);
        linkScreenY = toScreenY(linkY);

        GlyphLayout soundGlyphLayout = new GlyphLayout(infoFont, soundText);

        //checkboxWidth = greyBox.getRegionWidth();
        //checkboxHeight = greyBox.getRegionHeight();
        checkboxX = (int) ((gameWidth - CHECKBOX_WIDTH - CHECKBOX_SPACE - soundGlyphLayout.width) / 2);
        checkboxY = (int) (gameHeight * (2f / 3f) - CHECKBOX_WIDTH / 2);

        checkboxScreenWidth = toScreenX(CHECKBOX_WIDTH);
        checkboxScreenHeight = toScreenY(CHECKBOX_WIDTH);
        checkboxScreenX = toScreenX(checkboxX);
        checkboxScreenY = toScreenY(checkboxY);


        soundLabelX = checkboxX + CHECKBOX_WIDTH + CHECKBOX_SPACE;
        soundLabelY = (int) ((checkboxY + (CHECKBOX_WIDTH / 2f)) - soundGlyphLayout.height / 2f);

        leftArrow = Assets.instance.controls.arrowLeft;

        leftArrowX = (int) ((gameWidth - CONTROL_WIDTH) / 2);
        leftArrowY = (int) (gameHeight - 6 - CONTROL_WIDTH);

        leftArrowScreenX = toScreenX(leftArrowX);
        leftArrowScreenY = toScreenY(leftArrowY);
        leftArrowScreenWidth = toScreenX(CONTROL_WIDTH);
        leftArrowScreenHeight = toScreenY(CONTROL_WIDTH);
    }

    @Override
    public void show() {

    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff / 255.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.rect(0, 0, gameWidth, gameHeight, Color.CYAN, Color.CYAN, Color.BLUE, Color.BLUE);

        shapeRenderer.setColor(0 / 255f, 0 / 255f, 0 / 255f, 1f);
        shapeRenderer.end();

        batch.begin();

        //infoGlyphLayout.setText(infoFont, infoText);
        //infoGlyphLayout.setText(infoFont, infoText, Color.WHITE, gameWidth * .8f, Align.left, true);

        infoFont.draw(batch, infoText, textX, textY);

        linkFont.draw(batch, linkText, linkX, linkY);

        infoFont.draw(batch, soundText, soundLabelX, soundLabelY);

        if (prefs.getBoolean("sound", false)) {
            batch.draw(Assets.instance.settings.elementBlueCheckmark, checkboxX, checkboxY, CHECKBOX_WIDTH, CHECKBOX_WIDTH);
        } else {
            batch.draw(Assets.instance.settings.elementGreyBox, checkboxX, checkboxY, CHECKBOX_WIDTH, CHECKBOX_WIDTH);
        }

        batch.draw(leftArrow, leftArrowX, leftArrowY, CONTROL_WIDTH, CONTROL_WIDTH);

        batch.end();

        if (Gdx.input.justTouched()) {
            int gx = Gdx.input.getX();
            int gy = Gdx.input.getY();
            if (gx > linkScreenX && gx < linkScreenX + linkScreenWidth && gy > linkScreenY &&
                    gy < linkScreenY + linkScreenHeight) {
                Gdx.net.openURI("http://pygmalion.nitri.de/");
            }
            if (gx > checkboxScreenX && gx < checkboxScreenX + checkboxScreenWidth &&
                    gy > checkboxScreenY && gy < checkboxScreenY + checkboxScreenHeight) {
                prefs.putBoolean("sound", !prefs.getBoolean("sound", false));
                prefs.flush();
            }
            if (gx > leftArrowScreenX && gx < leftArrowScreenX + leftArrowScreenWidth &&
                    gy > leftArrowScreenY && gy < leftArrowScreenY + leftArrowScreenHeight) {
                game.setScreen(game.gameScreen);
            }
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void resize(int width, int height) {
        linkScreenWidth = toScreenX(linkWidth);
        linkScreenHeight = toScreenY(linkHeight);
        linkScreenX = toScreenX(linkX);
        linkScreenY = toScreenY(linkY);

        checkboxScreenWidth = toScreenX(CHECKBOX_WIDTH);
        checkboxScreenHeight = toScreenY(CHECKBOX_WIDTH);
        checkboxScreenX = toScreenX(checkboxX);
        checkboxScreenY = toScreenY(checkboxY);

        leftArrowScreenX = toScreenX(leftArrowX);
        leftArrowScreenY = toScreenY(leftArrowY);
        leftArrowScreenWidth = toScreenX(CONTROL_WIDTH);
        leftArrowScreenHeight = toScreenY(CONTROL_WIDTH);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private int toScreenX(int worldX) {
        worldVector.x = worldX;
        worldVector.y = 0;
        worldVector.z = 0;
        screenVector = camera.project(worldVector);
        return (int) screenVector.x;
    }

    private int toScreenY(int worldY) {
        worldVector.x = 0;
        worldVector.y = gameHeight - worldY;
        worldVector.z = 0;
        screenVector = camera.project(worldVector);
        return (int) screenVector.y;
    }
}
