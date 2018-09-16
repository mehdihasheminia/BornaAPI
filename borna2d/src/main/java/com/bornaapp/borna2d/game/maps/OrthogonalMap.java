package com.bornaapp.borna2d.game.maps;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.bornaapp.borna2d.game.levels.Engine;

/**
 * Created by Hashemi on 11/15/2016.
 */
public class OrthogonalMap extends Map {

    //Loads using asset manager & related asset manifest file
    @Override
    protected void Init(String assetName, float _mapScale) {
        mapScale = _mapScale;
        tiledMap = Engine.getInstance().getCurrentLevel().assets.getTiledMap(assetName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, mapScale, Engine.getInstance().getCurrentLevel().getBatch());
    }

    //Loads directly from file
    @Override
    protected void Init(FileHandle fileHandle, float _mapScale) {
        mapScale = _mapScale;
        tiledMap = new TmxMapLoader().load(fileHandle.path());
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, mapScale, Engine.getInstance().getCurrentLevel().getBatch());
    }
}
