package com.bornaapp.borna2d.math.graph;

/**
 * Created by Mehdi on 3/23/2018.
 * represents a constant functions
 * as ( y = c )
 */

public class Constant extends Curve {
    protected float c;

    public Constant(float _c) {
        c = _c;
    }

    @Override
    public float calculateY(float x) {
        return c;
    }
}
