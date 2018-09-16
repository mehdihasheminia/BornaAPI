package com.bornaapp.borna2d.math.graph;

/**
 * Created by Mehdi on 3/23/2018.
 * represents a Linear Curve
 * as ( y = mx + c )
 */

public class LinCurve extends com.bornaapp.borna2d.math.graph.Curve {
    float m, c;

    public LinCurve(float _m, float _c) {
        m = _m;
        c = _c;
    }

    public LinCurve(float x1, float y1, float x2, float y2) {
        m = (y2 - y1) / (x2 - x1);
        c = y1 - m * x1;
    }

    @Override
    float calculateY(float x) {
        return m * x + c;
    }
}
