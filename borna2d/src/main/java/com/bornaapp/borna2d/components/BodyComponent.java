package com.bornaapp.borna2d.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.bornaapp.borna2d.game.levels.Engine;
import com.bornaapp.borna2d.physics.BoxDef;
import com.bornaapp.borna2d.physics.CapsuleDef;
import com.bornaapp.borna2d.physics.CircleDef;
import com.bornaapp.borna2d.physics.CollisionEvent;
import com.bornaapp.borna2d.physics.LineDef;
import com.bornaapp.borna2d.physics.Material;
import com.bornaapp.borna2d.physics.PolygonDef;
import com.bornaapp.borna2d.physics.ShapeDef;

/**
 * Created by Mehdi on 08/29/2015.<p>
 * "Box2D is tuned for MKS units. Keep the size of moving objects roughly between 0.1 and 10 meters.
 * You'll need to use some scaling system when you render your environment and actors.
 * The Box2D testbed does this by using an OpenGL viewport transform."
 * Use the engine examples eg. HelloWorld to reference your own units against.
 */
public class BodyComponent extends BaseComponent {

    public Body body;

    //Filter definitions
    public final static short FILTER_CATEGORY_DEFAULT = (short) 0x0001;
    public final static short FILTER_GROUP_NO_CONTACT_WITH_LIGHTS = (short) -5;
    public final static short FILTER_MASK_DEFAULT = (short) 0xFFFF;


    //protected constructor, as components must only
    //be created using Ashley Engine or child classes.
    protected BodyComponent() {
    }

    //region Core Body & Fixture initialization methods
    public void CreateBody(BodyDef.BodyType bodyType, float x, float y, boolean fixedRotation) {
        //body properties
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.fixedRotation = fixedRotation;
        // Box2D position is relative to center of body.
        bodyDef.position.set(x, y);
        body = Engine.getInstance().getCurrentLevel().getWorld().createBody(bodyDef);
    }

    public void AddFixture(CircleDef circleDef, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {
        AddFixture(circleDef, new Material(), offsetX, offsetY, isSensor, event);
    }

    public void AddFixture(CircleDef circleDef, Material material, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {

        float r = circleDef.r;

        if (r <= 0.0f) r = 1.0f;

        FixtureDef fixDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(r);
        shape.setPosition(new Vector2(offsetX, offsetY));
        fixDef.shape = shape;
        fixDef.isSensor = isSensor;
        fixDef.density = material.density;
        fixDef.restitution = material.elasticity;
        fixDef.friction = material.friction;
        if(isSensor) {
            fixDef.filter.categoryBits = FILTER_CATEGORY_DEFAULT;
            fixDef.filter.groupIndex = FILTER_GROUP_NO_CONTACT_WITH_LIGHTS;
            fixDef.filter.maskBits = FILTER_MASK_DEFAULT;
        }
        //apply to body
        body.createFixture(fixDef).setUserData(event);
        //deallocate unnecessary  elements
        shape.dispose();
    }

    public void AddFixture(BoxDef boxDef, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {
        AddFixture(boxDef, new Material(), offsetX, offsetY, isSensor, event);
    }

    public void AddFixture(BoxDef boxDef, Material material, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {

        float width = boxDef.width;
        float height = boxDef.height;

        if (width <= 0.0f) width = 0.1f;
        if (height <= 0.0f) height = 0.1f;

        FixtureDef fixDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2, new Vector2(offsetX, offsetY), 0f);
        fixDef.shape = shape;
        fixDef.isSensor = isSensor;
        fixDef.density = material.density;
        fixDef.restitution = material.elasticity;
        fixDef.friction = material.friction;
        if(isSensor) {
            fixDef.filter.categoryBits = FILTER_CATEGORY_DEFAULT;
            fixDef.filter.groupIndex = FILTER_GROUP_NO_CONTACT_WITH_LIGHTS;
            fixDef.filter.maskBits = FILTER_MASK_DEFAULT;
        }
        // applying properties to body
        body.createFixture(fixDef).setUserData(event);
        body.setTransform(body.getPosition(),(float) Math.toRadians(boxDef.angle)); //todo:not tested! doesnt it affect previous works?
        //deallocate unnecessary  elements
        shape.dispose();
    }

    public void AddFixture(PolygonDef polygonDef, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {
        AddFixture(polygonDef, new Material(), offsetX, offsetY, isSensor, event);
    }

    public void AddFixture(PolygonDef polygonDef, Material material, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {

        //Box2D only supports convex polygons of 8 vertices max
        Vector2[] vertices = polygonDef.vertices;

        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector2(vertices[i].x + offsetX, vertices[i].y + offsetY);
        }

        FixtureDef fixDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        fixDef.shape = shape;
        fixDef.isSensor = isSensor;
        fixDef.density = material.density;
        fixDef.restitution = material.elasticity;
        fixDef.friction = material.friction;
        if(isSensor) {
            fixDef.filter.categoryBits = FILTER_CATEGORY_DEFAULT;
            fixDef.filter.groupIndex = FILTER_GROUP_NO_CONTACT_WITH_LIGHTS;
            fixDef.filter.maskBits = FILTER_MASK_DEFAULT;
        }
        // applying properties to body
        body.createFixture(fixDef).setUserData(event);
        //deallocate unnecessary  elements
        shape.dispose();
    }

    public void AddFixture(LineDef lineDef, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {
        AddFixture(lineDef, new Material(), offsetX, offsetY, isSensor, event);
    }

    public void AddFixture(LineDef lineDef, Material material, float offsetX, float offsetY, boolean isSensor, CollisionEvent event) {

        Vector2 p1 = lineDef.point1.cpy();
        Vector2 p2 = lineDef.point2.cpy();

        p1.x = p1.x + offsetX;
        p1.y = p1.y + offsetY;
        p2.x = p2.x + offsetX;
        p2.y = p2.y + offsetY;

        FixtureDef fixDef = new FixtureDef();
        Shape shape = new EdgeShape();
        ((EdgeShape) shape).set(p1, p2);
        fixDef.shape = shape;
        fixDef.isSensor = isSensor;
        fixDef.density = material.density;
        fixDef.restitution = material.elasticity;
        fixDef.friction = material.friction;
        if(isSensor) {
            fixDef.filter.categoryBits = FILTER_CATEGORY_DEFAULT;
            fixDef.filter.groupIndex = FILTER_GROUP_NO_CONTACT_WITH_LIGHTS;
            fixDef.filter.maskBits = FILTER_MASK_DEFAULT;
        }
        // applying properties to body
        body.createFixture(fixDef).setUserData(event);
        //deallocate unnecessary  elements
        shape.dispose();
    }

    public void AddFixture(CapsuleDef capsuleDef, float offsetX, float offsetY, boolean isSensor, CollisionEvent headEvent, CollisionEvent trunkEvent, CollisionEvent footEvent) {
        AddFixture(capsuleDef, new Material(), offsetX, offsetY, isSensor, headEvent, trunkEvent, footEvent);
    }

    public void AddFixture(CapsuleDef capsuleDef, Material material, float offsetX, float offsetY, boolean isSensor, CollisionEvent headEvent, CollisionEvent trunkEvent, CollisionEvent footEvent) {

        float r = capsuleDef.r;
        float h = capsuleDef.h;

        //central rectangle(trunk) fixture
        AddFixture(new BoxDef(2 * r, h), material, offsetX, offsetY, isSensor, trunkEvent);

        //top circle fixture
        AddFixture(new CircleDef(r), material, offsetX, offsetY + h / 2, isSensor, headEvent);

        //bottom circle fixture
        AddFixture(new CircleDef(r), material, offsetX, offsetY - h / 2, isSensor, footEvent);
    }
    //endregion

    //region properties

    public void setMaterial(float density, float restitution, float friction) {
        // restitution value must be between 0.0f & 1.0f
        if (restitution > 1.0f) restitution = 1.0f;
        if (restitution < 0.0f) restitution = 0.0f;
        // friction value must be between 0.0f & 1.0f
        if (friction > 1.0f) friction = 1.0f;
        if (friction < 0.0f) friction = 0.0f;

        //set one material to all fixtures
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setDensity(density);
            fixture.setRestitution(restitution);
            fixture.setFriction(friction);
        }
    }
    //endregion

    //region Utilities
    public boolean ContainsPointOfScreenCoord(float screenX, float screenY) {

        //convert mouse pointer coordinates from screen-space to world-space
        Vector3 worldCoord = Engine.getInstance().getCurrentLevel().getCamera().unproject(new Vector3(screenX, screenY, 0));

        //check if this point is in contact with any fixtures
        for (Fixture fixture : body.getFixtureList()) {
            if (fixture.testPoint(worldCoord.x, worldCoord.y))
                return true;
        }
        return false;
    }

    public boolean ContainsPoint(float x, float y) {
        //check if this point is in contact with any fixtures
        for (Fixture fixture : body.getFixtureList()) {
            if (fixture.testPoint(x, y))
                return true;
        }
        return false;
    }

    //endregion

    //--------------------------------- new methods ------------------------------------------------
    public Vector2 getLongitudinalVec() {
        return body.getWorldVector(new Vector2(0f, 1f)).nor();
    }

    public float getLongitudinalSpeed() {
        return getLongitudinalVec().dot(body.getLinearVelocity()); //todo: suspicious!
    }

    public Vector2 getLongitudinalVelocity() {
        Vector2 longVec = getLongitudinalVec();
        return new Vector2(longVec.scl(longVec.dot(body.getLinearVelocity())));
    }

    public Vector2 getRightVec() {
        return body.getWorldVector(new Vector2(1f, 0f)).nor();
    }

    public float getLateralSpeed() {
        return getRightVec().dot(body.getLinearVelocity());
    }

    public Vector2 getLateralVelocity() {
        Vector2 rightVec = getRightVec();
        return new Vector2(rightVec.scl(rightVec.dot(body.getLinearVelocity())));
    }

    @Override
    public void dispose(){
    }
}