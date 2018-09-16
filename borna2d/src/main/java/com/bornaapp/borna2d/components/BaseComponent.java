package com.bornaapp.borna2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.bornaapp.borna2d.game.levels.Engine;

/**
 * Created by Hashemi on 13/06/2018.<br>
 * desc:
 * <p>
 * more info:
 */

public abstract class BaseComponent implements Component {

    public void addTo(Entity entity){
        entity.add(this);
    }

    //as multiple instances of same component cannot be added to one entity,
    //we create different entities to handle this ashley-engine limitation
    public void addToNewEntity(){
        PooledEngine ashleyEngine = Engine.getInstance().getCurrentLevel().getAshleyEngine();
        //create new entity
        Entity entity = ashleyEngine.createEntity();
        ashleyEngine.addEntity(entity);
        //add this component to new entity
        addTo(entity);
    }

    public abstract void dispose();
}
