package com.bornaapp.borna2d.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bornaapp.borna2d.game.levels.Engine;
import com.bornaapp.borna2d.game.levels.LevelBase;

/**
 * Created by Mehdi on 11/27/2016.
 */
public class Background {

    private LevelBase currentLevel = Engine.getInstance().getCurrentLevel();

    private Texture texture;
    private boolean wrap;

    public Background(String Path, boolean wrap) {
        texture = currentLevel.assets.getTexture(Path);
        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        this.wrap = wrap;
    }

    public void render() {
        float texW = (wrap ? currentLevel.getViewportWitdh() : texture.getWidth());
        float texH = (wrap ? currentLevel.getViewportHeight() : texture.getHeight());

        currentLevel.getBatch().draw(texture, 0, 0, texW, texH);
    }
}
