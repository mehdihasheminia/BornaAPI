package com.bornaapp.borna2d.physics;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Hashemi on 12/19/2016.
 */
public class PolygonDef extends ShapeDef {
    public Vector2[] vertices;

    public PolygonDef(Vector2[] _vertices){
        vertices = _vertices;
    }
}
