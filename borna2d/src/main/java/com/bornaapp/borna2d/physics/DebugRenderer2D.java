package com.bornaapp.borna2d.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.bornaapp.borna2d.dbg.LogLevel;
import com.bornaapp.borna2d.game.levels.Engine;
import com.bornaapp.borna2d.iDispose;

public class DebugRenderer2D implements iDispose {

    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    public void render(World world, OrthographicCamera camera) {
        if (Engine.getInstance().getConfig().logLevel == LogLevel.NONE)
            return;
        Matrix4 debugMatrix = camera.combined.cpy().scale(1, 1, 0);
        debugRenderer.setDrawBodies(true);
        debugRenderer.setDrawVelocities(true);
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }
}
