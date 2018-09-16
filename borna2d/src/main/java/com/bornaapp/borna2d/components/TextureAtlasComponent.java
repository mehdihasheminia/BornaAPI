package com.bornaapp.borna2d.components;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bornaapp.borna2d.game.levels.Engine;

/**
 * Created by Mehdi on 09/02/2015.
 * ...
 */
public class TextureAtlasComponent extends DrawingComponent {

    public TextureAtlas textureAtlas;

    //region Methods

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    private TextureAtlasComponent() {
    }

    @Override
    public void Init(String assetName, float _scale) {
        textureAtlas = Engine.getInstance().getCurrentLevel().assets.getTextureAtlas(assetName);
        super.scale = _scale;
    }

    @Override
    public void Init(FileHandle file, float _scale) {
        textureAtlas = new TextureAtlas(file);
        super.scale = _scale;
    }

    //endregion

    @Override
    public void dispose() {
        textureAtlas.dispose();
    }
}
