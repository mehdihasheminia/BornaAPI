package com.bornaapp.borna2d.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class Node {
    private Array<Connection<Node>> connections = new Array<Connection<Node>>();
    public int type;
    public int index;

    public int getIndex() {
        return index;
    }

    Array<Connection<Node>> getConnections() {
        return connections;
    }

    void createConnection(Node toNode, float cost) {
        connections.add(new ConnectionImp(this, toNode, cost));
    }

    static class Type {
        static final int REGULAR = 1;
    }
}
