package com.bornaapp.borna2d.ai;

import com.badlogic.gdx.ai.pfa.Connection;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * an implementation of a default A* indexed-graph that stores all the possible nodes
 * in tiled-map. A*-Path will be a selection of these nodes.
 */
public class AStarGraph implements IndexedGraph<Node> {

    private Array<Node> nodes = new Array<Node>();

    private GraphType graphType;

    private TiledMapTileLayer tileLayer;

    public AStarGraph(TiledMapTileLayer _tileLayer) {
        tileLayer = _tileLayer;
    }

    public AStarGraph(int width_inTiles, int height_inTiles, int tileWidth_inPixels, int tileHeight_inPixels) {
        tileLayer = new TiledMapTileLayer(width_inTiles, height_inTiles, tileWidth_inPixels, tileHeight_inPixels);
    }

    public void InitAs(GraphType type) {
        graphType = type;
        switch (graphType) {
            case Edge:
                GenerateEdgeGraph();
                break;

            case Diagonal:
                GenerateDiagonalGraph();
                break;
        }
        IndexNodes();
    }

    private void GenerateEdgeGraph() {
        // Loops over the tiles in the map, starting from bottom left corner
        // iterating left to right, then down to up
        for (int y = 0; y < tileLayer.getHeight(); ++y) {
            for (int x = 0; x < tileLayer.getWidth(); ++x) {
                // generate a node for each tile so that they all exist when we create connections
                Node node = new Node();
                node.type = Node.Type.REGULAR;
                nodes.add(node);
            }
        }

        for (int y = 0; y < tileLayer.getHeight(); ++y) {
            for (int x = 0; x < tileLayer.getWidth(); ++x) {
                TiledMapTileLayer.Cell target = tileLayer.getCell(x, y);
                TiledMapTileLayer.Cell up = tileLayer.getCell(x, y + 1);
                TiledMapTileLayer.Cell left = tileLayer.getCell(x - 1, y);
                TiledMapTileLayer.Cell right = tileLayer.getCell(x + 1, y);
                TiledMapTileLayer.Cell down = tileLayer.getCell(x, y - 1);

                //Player can only move where there are not tiles on this layer
                Node targetNode = nodes.get(tileLayer.getWidth() * y + x);
                if (target == null) {
                    if (y != 0 && down == null) {
                        Node downNode = nodes.get(tileLayer.getWidth() * (y - 1) + x);
                        targetNode.createConnection(downNode, 1);
                    }
                    if (x != 0 && left == null) {
                        Node leftNode = nodes.get(tileLayer.getWidth() * y + x - 1);
                        targetNode.createConnection(leftNode, 1);
                    }
                    if (x != tileLayer.getWidth() - 1 && right == null) {
                        Node rightNode = nodes.get(tileLayer.getWidth() * y + x + 1);
                        targetNode.createConnection(rightNode, 1);
                    }
                    if (y != tileLayer.getHeight() - 1 && up == null) {
                        Node upNode = nodes.get(tileLayer.getWidth() * (y + 1) + x);
                        targetNode.createConnection(upNode, 1);
                    }
                }
            }
        }
    }

    private void GenerateDiagonalGraph() {
        // Loops over the tiles in the map, starting from bottom left corner
        // iterating left to right, then down to up
        for (int y = 0; y < tileLayer.getHeight(); ++y) {
            for (int x = 0; x < tileLayer.getWidth(); ++x) {
                // generate a node for each tile so that they all exist when we create connections
                Node node = new Node();
                node.type = Node.Type.REGULAR;
                nodes.add(node);
            }
        }

        for (int y = 0; y < tileLayer.getHeight(); ++y) {
            for (int x = 0; x < tileLayer.getWidth(); ++x) {

                TiledMapTileLayer.Cell target = tileLayer.getCell(x, y);
                TiledMapTileLayer.Cell up = tileLayer.getCell(x, y + 1);
                TiledMapTileLayer.Cell upLeft = tileLayer.getCell(x - 1, y + 1);
                TiledMapTileLayer.Cell upRight = tileLayer.getCell(x + 1, y + 1);
                TiledMapTileLayer.Cell left = tileLayer.getCell(x - 1, y);
                TiledMapTileLayer.Cell right = tileLayer.getCell(x + 1, y);
                TiledMapTileLayer.Cell down = tileLayer.getCell(x, y - 1);
                TiledMapTileLayer.Cell downLeft = tileLayer.getCell(x - 1, y - 1);
                TiledMapTileLayer.Cell downRight = tileLayer.getCell(x + 1, y - 1);

                Node targetNode = nodes.get(tileLayer.getWidth() * y + x);
                if (target == null) {
                    if (y != 0 && down == null) {
                        Node downNode = nodes.get(tileLayer.getWidth() * (y - 1) + x);
                        targetNode.createConnection(downNode, 1);
                    }
                    if (x != 0 && y != 0 && downLeft == null) {
                        Node downLeftNode = nodes.get(tileLayer.getWidth() * (y - 1) + (x - 1));
                        targetNode.createConnection(downLeftNode, 1.7f);
                    }
                    if (x != tileLayer.getWidth() - 1 && y != 0 && downRight == null) {
                        Node downRightNode = nodes.get(tileLayer.getWidth() * (y - 1) + (x + 1));
                        targetNode.createConnection(downRightNode, 1.7f);
                    }
                    if (x != 0 && left == null) {
                        Node leftNode = nodes.get(tileLayer.getWidth() * y + x - 1);
                        targetNode.createConnection(leftNode, 1);
                    }
                    if (x != tileLayer.getWidth() - 1 && right == null) {
                        Node rightNode = nodes.get(tileLayer.getWidth() * y + x + 1);
                        targetNode.createConnection(rightNode, 1);
                    }
                    if (y != tileLayer.getHeight() - 1 && up == null) {
                        Node upNode = nodes.get(tileLayer.getWidth() * (y + 1) + x);
                        targetNode.createConnection(upNode, 1);
                    }
                    if (x != 0 && y != tileLayer.getHeight() - 1 && upLeft == null) {
                        Node upLeftNode = nodes.get(tileLayer.getWidth() * (y + 1) + (x - 1));
                        targetNode.createConnection(upLeftNode, 1.7f);
                    }
                    if (x != tileLayer.getWidth() - 1 && y != tileLayer.getHeight() - 1 && upRight == null) {
                        Node upRightNode = nodes.get(tileLayer.getWidth() * (y + 1) + (x + 1));
                        targetNode.createConnection(upRightNode, 1.7f);
                    }
                }
            }
        }
    }

    private void IndexNodes() {
        //indexing nodes
        for (int x = 0; x < nodes.size; ++x) { // speedier than indexOf()
            nodes.get(x).index = x;
        }
    }

    public GraphType getGraphType() {
        return graphType;
    }

    public AStarPath CalculatePath(Vector2 origin, Vector2 destination) {

        //Validation of origin and destination
        //
        Node startNode;
        int startX = (int) origin.x;
        int startY = (int) origin.y;
        if (isNodeAvailableAt(startX, startY))
            startNode = getNodeByXY(startX, startY);
        else
            return null;

        Node endNode;
        int endX = (int) destination.x;
        int endY = (int) destination.y;
        if (isNodeAvailableAt(endX, endY))
            endNode = getNodeByXY(endX, endY);
        else
            return null;

        //Calculate A*-path
        //
        IndexedAStarPathFinder<Node> pathFinder = new IndexedAStarPathFinder<Node>(this, false);
        AStarPath path = new AStarPath();
        pathFinder.searchNodePath(startNode, endNode, new ManhattanHeuristic(this), path);
        return path;
    }

    //region nodes
    public boolean isNodeAvailableAt(int x, int y) {

        int modX = x / ((int) tileLayer.getTileWidth());
        int modY = y / ((int) tileLayer.getTileHeight());

        int index = tileLayer.getWidth() * modY + modX;

        return (index >= 0 && index < nodes.size);
    }

    public Node getNodeByXY(int x, int y) {

        int modX = x / ((int) tileLayer.getTileWidth());
        int modY = y / ((int) tileLayer.getTileHeight());

        return nodes.get(tileLayer.getWidth() * modY + modX);
    }

    public TiledMapTileLayer getTileLayer() {
        return tileLayer;
    }

    public Vector2 getNodeXY(Node node) {
        int nodeIndex = node.getIndex();
        Vector2 position = new Vector2();
        position.x = tileLayer.getTileWidth() / 2 + (nodeIndex % tileLayer.getWidth()) * tileLayer.getTileWidth();
        position.y = tileLayer.getTileHeight() / 2 + (nodeIndex / tileLayer.getWidth()) * tileLayer.getTileHeight();
        return position;
    }
    //endregion

    @Override
    public int getIndex(Node node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<Node>> getConnections(Node fromNode) {
        return fromNode.getConnections();
    }
}