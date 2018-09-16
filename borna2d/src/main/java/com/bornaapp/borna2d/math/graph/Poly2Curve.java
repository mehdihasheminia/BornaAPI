package com.bornaapp.borna2d.math.graph;

/**
 * Created by Mehdi on 2/11/2018.
 * represents a curve for a Polynomial functions
 * as ( y = ax^2 + bx + c )
 */

public class Poly2Curve extends com.bornaapp.borna2d.math.graph.Curve {

    protected float a, b, c;

    public Poly2Curve(float _a, float _b, float _c) {
        a = _a;
        b = _b;
        c = _c;
    }

    public Poly2Curve(float x, float y, float h, float k) {
        // variables "x" and "y" are the coordinates of a point on parabola
        // variables "h" and "k" are the coordinates of the parabola's vertex
        // y = a(x-h)^2+k
        a = (y - k) / (float) Math.pow(x - h, 2);
        b = -2 * a * h;
        c = a * (float) Math.pow(h, 2) + k;
    }

    @Override
    float calculateY(float x) {
        return a * (x * x) + b * x + c;
    }
}
