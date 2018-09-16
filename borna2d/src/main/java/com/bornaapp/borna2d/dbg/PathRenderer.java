package com.bornaapp.borna2d.dbg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.borna2d.ai.AStarGraph;
import com.bornaapp.borna2d.ai.AStarPath;
import com.bornaapp.borna2d.ai.Node;
import com.bornaapp.borna2d.game.levels.Engine;

import java.util.Iterator;

public class PathRenderer {

    private Color color = Color.RED;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public void drawPath(AStarGraph graph, AStarPath path) {

        if (Engine.getInstance().getConfig().logLevel == LogLevel.NONE)
            return;

        Iterator<Node> pathIterator = path.iterator();
        Node node1, node2 = null;

        while (pathIterator.hasNext()) {

            node1 = pathIterator.next();
            Vector2 p1 = graph.getNodeXY(node1);

            shapeRenderer.setProjectionMatrix(Engine.getInstance().getCurrentLevel().getCamera().combined);
            shapeRenderer.setColor(color);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(p1.x, p1.y, 4);
            shapeRenderer.end();

            if (node2 != null) {
                Vector2 p2 = graph.getNodeXY(node2);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.line(p1.x, p1.y, p2.x, p2.y);
                shapeRenderer.end();
            }
            node2 = node1;
        }
    }
}
