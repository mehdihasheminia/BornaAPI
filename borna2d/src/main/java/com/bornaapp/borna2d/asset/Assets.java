package com.bornaapp.borna2d.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Json;
import com.bornaapp.borna2d.dbg.log;

/**
 * Created by Mehdi on 08/26/2015.
 * ...
 */
public class Assets {

    private AssetManifest manifest;
    private final AssetManager assetManager = new AssetManager();

    //region Manifest methods
    private boolean ReadAssetManifest(String manifestPath) {

        if (!manifestPath.toLowerCase().endsWith(".json"))
            return false;

        try {
            Json json = new Json();
            FileHandle file = Gdx.files.internal(manifestPath);
            manifest = json.fromJson(AssetManifest.class, file);
            return true;
        } catch (Exception e) {
            log.error("Loading Manifest from JSON failed:" + e.getMessage());
            manifest = null;
            return false;
        }
    }

    private void ListManifestItemsInLoadingQueue() {

        for (String filePath : manifest.textures)
            assetManager.load(filePath, TextureAtlas.class);

        for (String filePath : manifest.images)
            assetManager.load(filePath, Texture.class);

        for (String filePath : manifest.particles)
            assetManager.load(filePath, ParticleEffect.class);

        for (String filePath : manifest.sounds)
            assetManager.load(filePath, Sound.class);

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        for (String filePath : manifest.levels)
            assetManager.load(filePath, TiledMap.class);

        assetManager.setLoader(BitmapFont.class, new BitmapFontLoader(new InternalFileHandleResolver()));
        for (String filePath : manifest.fonts)
            assetManager.load(filePath, BitmapFont.class);
    }
    //endregion

    //region Loading methods

    /**
     * Loads external assets from "assetManifest.json" all at once
     *
     * @param manifestPath path to asset manifest file
     */
    public void LoadAll(String manifestPath) {

        ReadAssetManifest(manifestPath);

        ListManifestItemsInLoadingQueue();

        //AssetManager begins loading all listed assets...
        try {
            while (!assetManager.update()) {
                assetManager.getProgress();
            }
        } catch (Exception e) {
            log.error("Some assets failed to Load: " + e.getMessage());
        }
    }

    /**
     * Loads external assets from "assetManifest.json" progressively
     *
     * @param manifestPath path to asset manifest file
     * @return true if loading is in progress & false if finished or failed
     */
    public boolean LoadByStep(String manifestPath) {

        boolean success = ReadAssetManifest(manifestPath);
        if (!success)
            return false;

        ListManifestItemsInLoadingQueue();

        //AssetManager begins loading one Asset on each call...
        try {
            return !assetManager.update();
        } catch (Exception e) {
            log.error("Some assets failed to Load: " + e.getMessage());
            return false;
        }
    }

    public float getProgress() {
        return assetManager.getProgress();
    }

    //endregion

    //region Methods to retreive an Asset
    public TextureAtlas getTextureAtlas(String name) {
        TextureAtlas textureAtlas = null;
        name = "textures/" + name;

        if (assetManager.isLoaded(name))
            textureAtlas = assetManager.get(name);

        if (textureAtlas == null)
            log.error("Texture-Atlas not found: " + name);

        return textureAtlas;
    }

    public Texture getTexture(String name) {
        Texture img = null;
        name = "images/" + name;

        if (assetManager.isLoaded(name))
            img = assetManager.get(name);

        if (img == null)
            log.error("Texture-Image not found: " + name);

        return img;
    }

    public TiledMap getTiledMap(String name) {
        TiledMap tMap = null;
        name = "levels/" + name;

        if (assetManager.isLoaded(name))
            tMap = assetManager.get(name);

        if (tMap == null)
            log.error("TiledMap not found: " + name);

        return tMap;
    }

    public Sound getSound(String name) {
        Sound sound = null;
        name = "sounds/" + name;

        if (assetManager.isLoaded(name))//todo: sensitive to caps/lower case letters!
            sound = assetManager.get(name);

        if (sound == null)
            log.error("sound not found: " + name);

        return sound;
    }

    public ParticleEffect getParticleEffect(String name) {
        ParticleEffect particleEffect = null;
        name = "particles/" + name;

        if (assetManager.isLoaded(name))//todo: sensitive to caps/lower case letters!
            particleEffect = assetManager.get(name);

        if (particleEffect == null)
            log.error("particleEffect not found: " + name);

        return particleEffect;
    }

    public BitmapFont getBitmapFont(String name) {
        BitmapFont bitmapFont = null;
        name = "fonts/" + name;

        if (assetManager.isLoaded(name))
            bitmapFont = assetManager.get(name);

        if (bitmapFont == null)
            log.error("bitmap-font not found: " + name);

        return bitmapFont;
    }
    //endregion

    public void dispose() {
        assetManager.dispose();
    }
}
