package com.bornaapp.borna2d.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class CollisionEvent {

    public Object owner = null;

    public CollisionEvent(Object owner) {
        this.owner = owner;
    }

    public abstract void onBeginContact(Object collidedObject, Body collidedBody, Fixture collidedFixture);

    public void onEndContact(Object collidedObject, Body collidedBody, Fixture collidedFixture){};
}
