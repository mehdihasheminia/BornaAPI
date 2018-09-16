package com.bornaapp.borna2d.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {

        //Checking the validity of parameters and extracting them
        //
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        CollisionEvent eventA = null, eventB = null;
        boolean eventA_valid, eventB_valid;

        try {
            eventA = (CollisionEvent) fixtureA.getUserData();
            eventA_valid = (eventA != null);
        } catch (Exception e) {
            eventA_valid = false;
        }

        try {
            eventB = (CollisionEvent) fixtureB.getUserData();
            eventB_valid = (eventB != null);
        } catch (Exception e) {
            eventB_valid = false;
        }

        if (eventA_valid && eventB_valid) {
            eventA.onBeginContact(eventB.owner, fixtureB.getBody(), fixtureB);
            eventB.onBeginContact(eventA.owner, fixtureA.getBody(), fixtureA);
        }
    }

    @Override
    public void endContact(Contact contact) {

        //Checking the validity of parameters and extracting them
        //
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        CollisionEvent eventA = null, eventB = null;
        boolean eventA_valid, eventB_valid;

        try {
            eventA = (CollisionEvent) fixtureA.getUserData();
            eventA_valid = (eventA != null);
        } catch (Exception e) {
            eventA_valid = false;
        }

        try {
            eventB = (CollisionEvent) fixtureB.getUserData();
            eventB_valid = (eventB != null);
        } catch (Exception e) {
            eventB_valid = false;
        }

        if (eventA_valid && eventB_valid) {
            eventA.onEndContact(eventB.owner, fixtureB.getBody(), fixtureB);
            eventB.onEndContact(eventA.owner, fixtureA.getBody(), fixtureA);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
