package de.nitri.nitris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;

class Assets implements Disposable, AssetErrorListener {

    static final String TAG = Assets.class.getName();

    static final Assets instance = new Assets();

    private AssetManager assetManager;

    AssetTetromino tetromino;

    AssetControls controls;
    AssetSounds sounds;

    AssetSettings settings;

    private Assets() {
    }

     class AssetTetromino {

        final AtlasRegion elementBlueSquare;
        final AtlasRegion elementCyanSquare;
        final AtlasRegion elementGreenSquare;
        final AtlasRegion elementGreySquare;
        final AtlasRegion elementOrangeSquare;
        final AtlasRegion elementPurpleSquare;
        final AtlasRegion elementRedSquare;
        final AtlasRegion elementYellowSquare;

        AssetTetromino(TextureAtlas atlas) {
            elementBlueSquare = atlas.findRegion("element_blue_square");
            elementBlueSquare.flip(false, true);
            elementCyanSquare = atlas.findRegion("element_cyan_square");
            elementCyanSquare.flip(false, true);
            elementGreenSquare = atlas.findRegion("element_green_square");
            elementGreenSquare.flip(false, true);
            elementGreySquare = atlas.findRegion("element_grey_square");
            elementGreySquare.flip(false, true);
            elementOrangeSquare = atlas.findRegion("element_orange_square");
            elementOrangeSquare.flip(false, true);
            elementPurpleSquare = atlas.findRegion("element_purple_square");
            elementPurpleSquare.flip(false, true);
            elementRedSquare = atlas.findRegion("element_red_square");
            elementRedSquare.flip(false, true);
            elementYellowSquare = atlas.findRegion("element_yellow_square");
            elementYellowSquare.flip(false, true);
        }

    }

     class AssetControls {
        final AtlasRegion arrowLeft;
        final AtlasRegion arrowRight;
        final AtlasRegion arrowDown;
        final AtlasRegion arrowRotate;
        final AtlasRegion play;
        final AtlasRegion options;

        AssetControls(TextureAtlas atlas) {
            arrowLeft = atlas.findRegion("arrow_left");
            arrowLeft.flip(false, true);
            arrowRight = atlas.findRegion("arrow_right");
            arrowLeft.flip(false, true);
            arrowDown = atlas.findRegion("arrow_down");
            arrowDown.flip(false, true);
            arrowRotate = atlas.findRegion("arrow_rotate");
            arrowRotate.flip(true, true);
            play = atlas.findRegion("play");
            options = atlas.findRegion("options");
        }
    }

    class AssetSettings {
        final AtlasRegion elementGreyBox;
        final AtlasRegion elementBlueCheckmark;

        AssetSettings(TextureAtlas atlas) {
            elementGreyBox = atlas.findRegion("grey_box");
            elementGreyBox.flip(false, true);
            elementBlueCheckmark = atlas.findRegion("blue_checkmark");
            elementBlueCheckmark.flip(false, true);
        }
    }

    void init(AssetManager assetManager) {
        this.assetManager = assetManager;
        // set asset manager error handler
        assetManager.setErrorListener(this);
        // load texture atlas
        assetManager.load(NitrisGame.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);


        assetManager.load("sounds/level_up.wav", Sound.class);
        assetManager.load("sounds/row_cleared.wav", Sound.class);
        assetManager.load("sounds/game_over.wav", Sound.class);

        // start loading assets and wait until finished
        assetManager.finishLoading();

        Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames()) {
            Gdx.app.debug(TAG, "asset: " + a);
        }

        TextureAtlas atlas = assetManager.get(NitrisGame.TEXTURE_ATLAS_OBJECTS);

        // enable texture filtering for pixel smoothing
        for (Texture t : atlas.getTextures()) {
            t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }

        // create game resource objects
        tetromino = new AssetTetromino(atlas);

        controls = new AssetControls(atlas);

        settings = new AssetSettings(atlas);

        sounds = new AssetSounds();

    }

     class AssetSounds {

        final Sound levelUp;
        final Sound rowCleared;
        final Sound gameOver;

        AssetSounds() {
            levelUp = assetManager.get("sounds/level_up.wav", Sound.class);
            rowCleared = assetManager.get("sounds/row_cleared.wav", Sound.class);
            gameOver = assetManager.get("sounds/game_over.wav", Sound.class);
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Couldn't load asset '" + asset.toString() + "'", throwable);

    }

    @Override
    public void dispose() {
        assetManager.dispose();

    }

}
