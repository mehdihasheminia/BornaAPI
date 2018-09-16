package com.bornaapp.borna2d.math.graph;

import com.bornaapp.borna2d.physics.Material;

/**
 * Created by Mehdi on 2/11/2018.
 */

public abstract class Curve {
    float xlimitLow = -Float.MAX_VALUE;
    float xlimitHigh = +Float.MAX_VALUE;

    public enum CLAMP {
        TO_ZERO,
        TO_VALUE
    }

    CLAMP clampType = CLAMP.TO_ZERO;

    public void setLimits(float _xlimitLow, float _xlimitHigh) {
        // just in case!
        float max = Math.max(_xlimitLow, _xlimitHigh);
        float min = Math.min(_xlimitLow, _xlimitHigh);

        xlimitLow = min;
        xlimitHigh = max;
    }

    public void setClampType(CLAMP _clampType) {
        clampType = _clampType;
    }

    abstract float calculateY(float x);

    public float getY(float x) {
        if (xIsTooHigh(x)) {
            if (clampType == CLAMP.TO_ZERO)
                return 0;
            if (clampType == CLAMP.TO_VALUE)
                return calculateY(xlimitHigh);
        }
        if (xIsTooLow(x)) {
            if (clampType == CLAMP.TO_ZERO)
                return 0;
            if (clampType == CLAMP.TO_VALUE)
                return calculateY(xlimitLow);
        }
        return calculateY(x);
    }

    private boolean xIsTooHigh(float x) {
        return (x > xlimitHigh);
    }

    private boolean xIsTooLow(float x) {
        return (x < xlimitLow);
    }
}
