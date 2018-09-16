package com.bornaapp.borna2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.bornaapp.borna2d.dbg.PathRenderer;
import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.components.PathComponent;
import com.bornaapp.borna2d.game.levels.Engine;
import com.bornaapp.borna2d.game.levels.LevelBase;

/**
 * Created by Mehdi on 08/30/2015.
 * ...
 */
public class PathFindingSystem extends IteratingSystem {

    private Sync sync;
    private PathRenderer pathRenderer = null;
    private ComponentMapper<PathComponent> pathMap;

    public PathFindingSystem(LevelBase level) {
        super(Family.all(PathComponent.class).get(), level.getSystemPriority());
        sync = new Sync();
        pathMap = ComponentMapper.getFor(PathComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PathComponent pathComp = pathMap.get(entity);

        pathComp.SetPosition(sync.getX(entity), sync.getY(entity));

        if (pathComp.isPathValid()) {
            //
            if (pathComp.reachedDestination()) {
                //
                pathComp.cancelDestination();
                //
            } else {
                //
                if (pathComp.reachedTargetNode())
                    pathComp.gotoNextNode();
                else
                    pathComp.CalculateVelocityToTargetNode(deltaTime);
            }
        }
        sync.setVelocity(entity, pathComp.getVelocity());
    }

    public Array<PathComponent> getPathComponents() {
        //get all entities having path component
        PooledEngine ashleyEngine = Engine.getInstance().getCurrentLevel().getAshleyEngine();
        Family family = Family.all(PathComponent.class).get();
        ImmutableArray<Entity> entities = ashleyEngine.getEntitiesFor(family);
        //filling the path array
        Array<PathComponent> pathArray = new Array<PathComponent>();
        for (Entity entity : entities) {
            try {
                pathArray.add(pathMap.get(entity));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return pathArray;
    }
}
