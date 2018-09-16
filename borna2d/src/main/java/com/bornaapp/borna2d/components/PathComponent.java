package com.bornaapp.borna2d.components;

import com.badlogic.gdx.math.Vector2;
import com.bornaapp.borna2d.ai.AStarGraph;
import com.bornaapp.borna2d.ai.AStarPath;
import com.bornaapp.borna2d.ai.Node;

/**
 * Created by Mehdi on 8/29/2015.
 * ...
 */
public class PathComponent extends BaseComponent {

    public AStarPath path;
    public AStarGraph aStarGraph;

    private Vector2 currentPosition;
    private Vector2 calculatedVelocity;

    private float travelVelocity = 1.0f;
    private float reachTolerance = 8.0f;

    private int currentNodeIndex = 0;

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    private PathComponent() {
    }

    public void Init(Vector2 _startingPosition, AStarGraph _graph) {
        path = new AStarPath();
        aStarGraph = _graph;
        currentPosition = _startingPosition;
        calculatedVelocity = new Vector2();
    }

    public void setDestination(Vector2 destination) {
        path = aStarGraph.CalculatePath(currentPosition, destination);
        if (isPathValid())
            currentNodeIndex = 0;
        else
            cancelDestination();
    }

    public void cancelDestination() {
        path.clear();
        calculatedVelocity.x = 0;
        calculatedVelocity.y = 0;
    }

    public boolean isPathValid() {
        try {
            return (path.getCount() > 1);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean reachedDestination() {

        return (currentNodeIndex >= path.getCount());
    }

    public boolean reachedTargetNode() {
        Vector2 nextNodeXY = aStarGraph.getNodeXY(getTargetNode());
        float distance = currentPosition.cpy().sub(nextNodeXY).len();
        return (distance < reachTolerance);
    }

    private Node getTargetNode() {
        return path.get(currentNodeIndex);
    }

    public void gotoNextNode() {
        if (currentNodeIndex < path.getCount() - 1)
            currentNodeIndex++;
        else
            cancelDestination();
    }

    public void CalculateVelocityToTargetNode(float deltaTime) {
        calculatedVelocity = aStarGraph.getNodeXY(getTargetNode());
        calculatedVelocity.sub(currentPosition).nor().scl(travelVelocity);
        calculatedVelocity.mulAdd(calculatedVelocity, deltaTime).limit(travelVelocity);
    }

    public void CalculateBounceVelocity(float deltaTime) {
        int destIndex = path.getCount() - 1;
        if (destIndex < 0)
            calculatedVelocity = new Vector2(0f, 0f); //<------WHAT COLLIDES?
        else {
            calculatedVelocity = aStarGraph.getNodeXY(path.get(destIndex));
            calculatedVelocity.sub(currentPosition).nor().scl(travelVelocity);
            calculatedVelocity.mulAdd(calculatedVelocity, deltaTime).limit(travelVelocity);
        }
    }

    //region properties
    public void SetPosition(Vector2 pos) {
        SetPosition(pos.x, pos.y);
    }

    public void SetPosition(float posX, float posY) {
        currentPosition.x = posX;
        currentPosition.y = posY;
    }

    public Vector2 getVelocity() {
        return calculatedVelocity;
    }

    public void setTravelVelocity(float desiredVelocity) {
        this.travelVelocity = desiredVelocity;
    }

    //endregion
    @Override
    public void dispose() {
        path.clear();
    }
}
