package com.bornaapp.borna2d.physics;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Hashemi on 12/19/2016.
 */
public class LineDef extends ShapeDef {
    public Vector2 point1;
    public Vector2 point2;

    public LineDef(Vector2 p1, Vector2 p2) {
        point1 = p1;
        point2 = p2;
    }

    public LineDef(float x1, float y1, float x2, float y2) {
        point1 = new Vector2(x1, y1);
        point2 = new Vector2(x2, y2);
    }
}
