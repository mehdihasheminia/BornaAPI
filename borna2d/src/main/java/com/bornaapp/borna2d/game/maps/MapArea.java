package com.bornaapp.borna2d.game.maps;

import com.bornaapp.borna2d.components.BodyComponent;
import com.bornaapp.borna2d.physics.CollisionEvent;

/**
 * Created by Hashemi on 12/8/2016.
 */
public class MapArea {

    public String name;
    public BodyComponent bodyComponent;

    public MapArea(String name, BodyComponent bodyComponent){
        this.name = name;
        this.bodyComponent = bodyComponent;
        this.bodyComponent.body.setUserData(this);
        bodyComponent.addToNewEntity();
    }

    public void SetCollisionEvent(CollisionEvent collisionEvent){
        this.bodyComponent.body.getFixtureList().get(0).setUserData(collisionEvent);
    }

}
