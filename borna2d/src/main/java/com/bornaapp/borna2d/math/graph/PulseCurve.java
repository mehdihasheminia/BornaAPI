package com.bornaapp.borna2d.math.graph;

import java.util.Arrays;

/**
 * Created by Mehdi on 4/27/2018.
 */

public class PulseCurve extends com.bornaapp.borna2d.math.graph.Curve {

    private Poly2Curve risingCurve, fallingCurve;
    private float X[] = new float[4];
    private float Y[] = new float[2];

    /**
     * a rectangular pulse curve which starts
     *
     * @param x1 rise starts
     * @param x2 rise ends
     * @param x3 fall starts
     * @param x4 fall ends
     * @param y1 high value (between x2 & x3)
     * @param y2 low value
     */
    public PulseCurve(float x1, float x2, float x3, float x4, float y1, float y2) {
        this.X[0] = x1;
        this.X[1] = x2;
        this.X[2] = x3;
        this.X[3] = x4;
        this.Y[0] = y1;
        this.Y[1] = y2;

        Arrays.sort(X);
        Arrays.sort(Y);

        risingCurve = new Poly2Curve(X[0], Y[0], X[1], Y[1]);
        risingCurve.setLimits(X[0], X[1]);
        risingCurve.setClampType(CLAMP.TO_VALUE);

        fallingCurve = new Poly2Curve(X[3], Y[0], X[2], Y[1]);
        fallingCurve.setLimits(X[2], X[3]);
        fallingCurve.setClampType(CLAMP.TO_VALUE);
    }

    public PulseCurve(float xCenter, float xWidth, float xRiseAndFallPercent, float yHigh) {

        this.X[1] = xCenter - xWidth / 2f;
        this.X[2] = xCenter + xWidth / 2f;
        this.X[0] = X[1] - xWidth * xRiseAndFallPercent;
        this.X[3] = X[2] + xWidth * xRiseAndFallPercent;
        this.Y[0] = 0f;
        this.Y[1] = yHigh;

        risingCurve = new Poly2Curve(X[0], Y[0], X[1], Y[1]);
        risingCurve.setLimits(X[0], X[1]);
        risingCurve.setClampType(CLAMP.TO_VALUE);

        fallingCurve = new Poly2Curve(X[3], Y[0], X[2], Y[1]);
        fallingCurve.setLimits(X[2], X[3]);
        fallingCurve.setClampType(CLAMP.TO_VALUE);
    }

    @Override
    float calculateY(float x) {
        if (x <= X[2])
            return risingCurve.getY(x);
        else
            return fallingCurve.getY(x);
    }
}