package com.bornaapp.borna2d.toolbits;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Mehdi on 5/25/2018.
 */

public class AABB {

    static public Rectangle fromPolygon(PolygonShape polygonShape) {
        if (polygonShape == null)
            return new Rectangle(0f, 0f, 0f, 0f);

        float xMin = 0f, xMax = 0f, yMin = 0f, yMax = 0f;
        for (int i = 0; i < polygonShape.getVertexCount(); i++) {
            Vector2 vertex = new Vector2();
            polygonShape.getVertex(i, vertex);
            if (vertex.x > xMax) xMax = vertex.x;
            else if (vertex.x < xMin) xMin = vertex.x;
            if (vertex.y > yMax) yMax = vertex.y;
            else if (vertex.y < yMin) yMin = vertex.y;
        }
        float w = xMax - xMin;
        float h = yMax - yMin;
        return new Rectangle(xMin, yMin, w, h);
    }
}
