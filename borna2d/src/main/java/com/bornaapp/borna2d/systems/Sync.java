package com.bornaapp.borna2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.borna2d.components.BodyComponent;
import com.bornaapp.borna2d.components.ParticleComponent;
import com.bornaapp.borna2d.components.PositionComponent;
import com.bornaapp.borna2d.components.ZComponent;
import com.bornaapp.borna2d.dbg.log;

/**
 * Created by Hashemi on 12/13/2016.
 * <p/>
 * Because there are some parameters (like position) that are repeated across
 * different components, systems need a way to decide which one to use.The Sync
 * class works as an abstraction layer and is a way to prioritize common parameters.
 */
public class Sync {

    private ComponentMapper<PositionComponent> posMap = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<ZComponent> zMap = ComponentMapper.getFor(ZComponent.class);
    private ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<ParticleComponent> particleMap = ComponentMapper.getFor(ParticleComponent.class);

    /**
     * Extracts X from entities, based on components they have
     *
     * @param entity Ahley entity
     * @return "X" of entity in pixels
     */
    public float getX(Entity entity) {

        if (bodyMap.has(entity)) {
            BodyComponent bodyComp = bodyMap.get(entity);
            return bodyComp.body.getPosition().x;
        }

        if (posMap.has(entity)) {
            PositionComponent posComp = posMap.get(entity);
            return posComp.x;
        }

        if (particleMap.has(entity)) {
            ParticleComponent partComp = particleMap.get(entity);
            return partComp.getPosition().x;
        }

        return Float.MAX_VALUE;
    }

    /**
     * Extracts Y from entities, based on components they have
     *
     * @param entity Ahley entity
     * @return "Y" of entity in pixels
     */
    public float getY(Entity entity) {

        if (bodyMap.has(entity)) {
            BodyComponent bodyComp = bodyMap.get(entity);
            return bodyComp.body.getPosition().y;
        }

        if (posMap.has(entity)) {
            PositionComponent posComp = posMap.get(entity);
            return posComp.y;
        }

        if (particleMap.has(entity)) {
            ParticleComponent partComp = particleMap.get(entity);
            return partComp.getPosition().y;
        }

        return Float.MAX_VALUE;
    }

    public float getZ(Entity entity) {

        if (zMap.has(entity)) {
            ZComponent zComp = zMap.get(entity);
            return zComp.z;
        }

        return Integer.MAX_VALUE;
    }

    /**
     * Extracts rotation angle from entities, based on components they have
     *
     * @param entity Ahley entity
     * @return rotation angle of entity in Degrees
     */
    public float getRotation(Entity entity) {

        if (bodyMap.has(entity)) {
            BodyComponent bodyComp = bodyMap.get(entity);
            return bodyComp.body.getAngle() * (180 / (float) Math.PI); //radian(Box2D) to Degree(LibGdx)
        }

        return 0.0f;
    }

    /**
     * Extracts velocity from entities, based on components they have
     *
     * @param entity Ahley entity
     * @return velocity
     */
    public Vector2 getLinearVelocity(Entity entity) {

        if (bodyMap.has(entity)) {
            BodyComponent bodyComp = bodyMap.get(entity);
            return bodyComp.body.getLinearVelocity();
        }

        return new Vector2();
    }

    /**
     * Sets velocity into entity, based on components it has
     *
     * @param entity Ahley entity
     * @return rotation angle of entity in Degrees
     */
    public void setVelocity(Entity entity, Vector2 vel) {

        if (bodyMap.has(entity)) {
            BodyComponent bodyComp = bodyMap.get(entity);
            bodyComp.body.setLinearVelocity(vel);
            return;
        }
    }

    //endregion
}
