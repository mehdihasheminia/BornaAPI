package com.bornaapp.borna2d.physics;

/**
 * Created by Hashemi on 12/19/2016.
 */
public class CircleDef extends ShapeDef {
    public float r; //<--------------------------in pixels

    public CircleDef(float _r) {
        r = _r;
    }

    public CircleDef() {
        r = 0f;
    }
}
