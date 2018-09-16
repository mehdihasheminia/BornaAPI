package com.bornaapp.borna2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.PlayStatus;
import com.bornaapp.borna2d.components.AnimationComponent;
import com.bornaapp.borna2d.components.ParticleComponent;
import com.bornaapp.borna2d.components.TextureAtlasComponent;
import com.bornaapp.borna2d.components.TextureComponent;
import com.bornaapp.borna2d.components.TiledMapLayerComponent;
import com.bornaapp.borna2d.game.levels.Engine;
import com.bornaapp.borna2d.game.levels.LevelBase;

import java.util.Comparator;

/**
 *
 */
public class RenderingSystem extends IteratingSystem {

    Sync sync;
    SpriteBatch batch = Engine.getInstance().getCurrentLevel().getBatch();
    private Array<Entity> renderQueue;

    private ComponentMapper<ParticleComponent> particleMap;
    private ComponentMapper<TextureComponent> texMap;
    private ComponentMapper<TextureAtlasComponent> texAtlasMap;
    private ComponentMapper<AnimationComponent> animMap;
    private ComponentMapper<TiledMapLayerComponent> tileLayerMap;

    //region constructor
    public RenderingSystem(LevelBase level) {
        super(Family.all().get(), level.getSystemPriority());
        renderQueue = new Array<Entity>();
        sync = new Sync();
        particleMap = ComponentMapper.getFor(ParticleComponent.class);
        texMap = ComponentMapper.getFor(TextureComponent.class);
        texAtlasMap = ComponentMapper.getFor(TextureAtlasComponent.class);
        animMap = ComponentMapper.getFor(AnimationComponent.class);
        tileLayerMap = ComponentMapper.getFor(TiledMapLayerComponent.class);
    }
    //endregion

    //region override methods
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        renderQueue.sort(comparator);

        SpriteBatch batch = Engine.getInstance().getCurrentLevel().getBatch();
        if (batch.isDrawing())
            batch.end();
        batch.begin();

        for (Entity entity : renderQueue) {
            //
            renderTexture(entity);
            //
            renderTextureAtlas(entity, deltaTime);
            //
            renderParticle(entity, deltaTime);
            //
            renderTiledMapLayer(entity);
        }
        batch.end();

        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }
    //endregion

    //region Render & Update methods
    private void renderParticle(Entity entity, float deltaTime) {
        if (!particleMap.has(entity))
            return;
        ParticleComponent partComp = particleMap.get(entity);
        partComp.update();
        for (ParticleEffectPool.PooledEffect effect : partComp.activeEffects) {
            effect.draw(batch, deltaTime);
        }
    }

    private void renderTexture(Entity entity) {

        if (!texMap.has(entity))
            return;

        TextureComponent texComp = texMap.get(entity);

        if (!texComp.isVisible)
            return;

        // model origin( (0,0) in model-space ) is bot-left corner by
        // default and rotation and scaling is relative to this point.
        // We change the to the center of texture.
        float w, h, originX, originY, scaleX, scaleY, rot;
        w = texComp.texture.getWidth();
        h = texComp.texture.getHeight();
        originX = texComp.offsetX + w / 2;
        originY = texComp.offsetY + h / 2;
        scaleX = texComp.scale;
        scaleY = texComp.scale;
        rot = sync.getRotation(entity);

        //the portion of texture in texel
        int texOffsetX, texOffsetY, texW, texH;
        texOffsetX = 0;
        texOffsetY = 0;
        texW = texComp.texture.getWidth();
        texH = texComp.texture.getHeight();

        // World transformations
        // taking the new Origin into account
        float x, y;
        x = sync.getX(entity) - originX;
        y = sync.getY(entity) - originY;
        if (x == Float.MAX_VALUE) x = 0;
        if (y == Float.MAX_VALUE) y = 0;

        boolean flipX = texComp.flipX;
        boolean flipY = texComp.flipY;

        //draw texture
        batch.draw(texComp.texture, x, y, originX, originY, w, h, scaleX, scaleY, rot, texOffsetX, texOffsetY, texW, texH, flipX, flipY);
    }

    private void renderTextureAtlas(Entity entity, float deltaTime) {
        if (!texAtlasMap.has(entity))
            return;

        TextureAtlasComponent texComp = texAtlasMap.get(entity);
        TextureRegion region;
        boolean flipX, flipY;

        if (animMap.has(entity)) {
            AnimationComponent animComp = animMap.get(entity);
            animComp.elapsedTime += (animComp.getPlayStatus() == PlayStatus.Playing ? deltaTime : 0.0f);
            if (animComp.getPlayStatus() == PlayStatus.Stopped)
                animComp.elapsedTime = 0f;
            region = (TextureRegion) animComp.getAnimation().getKeyFrame(animComp.elapsedTime); //<---type cast bcs of new version

        } else
            region = texComp.textureAtlas.getRegions().get(0);

        // model origin( (0,0) in model-space ) is bot-left corner by
        // default and rotation and scaling is relative to this point.
        // We change the to the center of texture.
        float x, y, originX, originY, w, h, scaleX, scaleY, rot;
        w = region.getRegionWidth();
        h = region.getRegionHeight();
        originX = texComp.offsetX + w / 2;
        originY = texComp.offsetY + h / 2;
        scaleX = texComp.scale;
        scaleY = texComp.scale;
        rot = sync.getRotation(entity);

        //the portion of texture in texel
        int texOffsetX, texOffsetY, texW, texH;
        texOffsetX = region.getRegionX();
        texOffsetY = region.getRegionY();
        texW = region.getRegionWidth();
        texH = region.getRegionHeight();

        // World transformations
        // taking the new Origin into account
        x = sync.getX(entity) - originX;
        y = sync.getY(entity) - originY;
        if (x == Float.MAX_VALUE) x = 0;
        if (y == Float.MAX_VALUE) y = 0;

        flipX = texComp.flipX;
        flipY = texComp.flipY;

        batch.draw(region.getTexture(), x, y, originX, originY, w, h, scaleX, scaleY, rot, texOffsetX, texOffsetY, texW, texH, flipX, flipY);
    }

    private void renderTiledMapLayer(Entity entity) {
        if (!tileLayerMap.has(entity))
            return;

        TiledMapRenderer tiledMapRenderer = Engine.getInstance().getCurrentLevel().getMap().tiledMapRenderer;
        OrthographicCamera camera = Engine.getInstance().getCurrentLevel().getCamera();

        TiledMapLayerComponent tiledLayerComp = tileLayerMap.get(entity);

        try {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.renderTileLayer(tiledLayerComp.tileLayer);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    //endregion

    //region Sort
    private Comparator<Entity> comparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity e1, Entity e2) {

            return (int) Math.signum(sync.getZ(e2) - sync.getZ(e1));
        }
    };
    //endregion
}
