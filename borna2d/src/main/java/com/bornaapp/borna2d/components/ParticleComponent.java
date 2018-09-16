package com.bornaapp.borna2d.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.game.levels.Engine;

import java.util.ArrayList;

/**
 * uses ashley-infra-structure
 */
public class ParticleComponent extends BaseComponent {

    private ParticleEffectPool poolOfEffects;
    public Array<ParticleEffectPool.PooledEffect> activeEffects = new Array<ParticleEffectPool.PooledEffect>();
    private final int effectsMaxCapacity = 200;

    private float spawnRate = 10f; //adding particle-effects per second
    private long lastSpawned = 0;

    private Vector2 position;

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    private ParticleComponent() {
    }

    public void init(String assetName, float scale) {//todo: this causes scaling problem in multiplayer mode

        //template particle effect
        ParticleEffect templateParticle = Engine.getInstance().getCurrentLevel().assets.getParticleEffect(assetName);
        templateParticle.scaleEffect(scale);
//        particleEffect.setEmittersCleanUpBlendFunction(false);

        //sets up a pool for effects to avoid redundant allocations
        poolOfEffects = new ParticleEffectPool(templateParticle, 50, effectsMaxCapacity);

//        recycledEffect = poolOfEffects.obtain();
//        currentLevel.effects.add(recycledEffect);///
        position = new Vector2(0f, 0f);
    }

    public void init(FileHandle fileHandle, float scale) { //todo: doesn't dispose

        //template particle effect
        ParticleEffect templateParticle = new ParticleEffect();
        templateParticle.load(fileHandle, Gdx.files.internal("particles/"));
        templateParticle.scaleEffect(scale);
//        particleEffect.setEmittersCleanUpBlendFunction(false);

        //sets up a pool for effects to avoid redundant allocations
        poolOfEffects = new ParticleEffectPool(templateParticle, 50, effectsMaxCapacity);

//        recycledEffect = poolOfEffects.obtain();
//        currentLevel.effects.add(recycledEffect);///
        position = new Vector2(0f, 0f);
    }

    public void spawn() {
        //Spawn limit
        long currentTime = System.nanoTime();
        if (currentTime - lastSpawned < 1e9 / spawnRate)
            return;
        else
            lastSpawned = currentTime;

        ParticleEffectPool.PooledEffect recycledEffect = poolOfEffects.obtain();
        recycledEffect.setPosition(position.x, position.y);
        if (recycledEffect.isComplete())
            recycledEffect.reset();//todo necessary?
        recycledEffect.start();
        activeEffects.add(recycledEffect);
    }

    public void update() {
        //marked finished effects
        ArrayList<ParticleEffectPool.PooledEffect> markedForRemoval = new ArrayList<ParticleEffectPool.PooledEffect>();
        for (ParticleEffectPool.PooledEffect effect : activeEffects) {
            if (effect.isComplete()) {
                effect.free();
                //poolOfEffects.free(effect); //equivalent of above?
                markedForRemoval.add(effect);
            }
        }

        //remove finished effects
        for (ParticleEffectPool.PooledEffect effect : markedForRemoval) {
            activeEffects.removeValue(effect, true);
        }

        //clamp size of active effects to max capacity
        if (activeEffects.size > effectsMaxCapacity) {
            int diff = activeEffects.size - effectsMaxCapacity;
            activeEffects.removeRange(0, diff);
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public void setSpawnRate(float effectsPerSecond) {
        if (effectsPerSecond <= 0f)
            effectsPerSecond = 10f;
        spawnRate = effectsPerSecond;
    }

    @Override
    public void dispose() {
        for (ParticleEffectPool.PooledEffect effect : activeEffects) {
            effect.dispose();
        }
    }

}
