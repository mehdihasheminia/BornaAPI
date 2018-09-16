package com.bornaapp.borna2d.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * heuristic enables path-finding algorithms to choose the node that is most likely to lead to the optimal path. in
 * this implementation we used "Manhattan distance formula" to estimate the cost of moving from one node to another.
 */
public class ManhattanHeuristic implements Heuristic<Node> {

    int Width_inTiles;

    public ManhattanHeuristic(AStarGraph graph) {
        Width_inTiles = graph.getTileLayer().getWidth();
    }

    @Override
    public float estimate(Node startNode, Node endNode) {
        int startIndex = startNode.getIndex();
        int endIndex = endNode.getIndex();

        int startY = startIndex / Width_inTiles;
        int startX = startIndex % Width_inTiles;

        int endY = endIndex / Width_inTiles;
        int endX = endIndex % Width_inTiles;

        // returns cost as magnitude of differences on both axes ( Manhattan distance formula (not ideal))
        return Math.abs(startX - endX) + Math.abs(startY - endY);
    }
}
