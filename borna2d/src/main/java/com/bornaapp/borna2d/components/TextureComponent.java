package com.bornaapp.borna2d.components;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.bornaapp.borna2d.game.levels.Engine;

/**
 * Created by Hashemi on 11/7/2016.
 */
public class TextureComponent extends DrawingComponent {

    public Texture texture;
    public boolean isVisible = true;

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    private TextureComponent() {
    }

    @Override
    public void Init(String assetName, float _scale) {
        texture = Engine.getInstance().getCurrentLevel().assets.getTexture(assetName);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        super.scale = _scale;
    }

    @Override
    public void Init(FileHandle file, float _scale) {
        texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        super.scale = _scale;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}


