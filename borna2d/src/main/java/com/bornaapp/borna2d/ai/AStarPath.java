package com.bornaapp.borna2d.ai;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * a portion of A*-Graph that is consist of selected nodes.
 */
public class AStarPath implements GraphPath<Node> {
    private Array<Node> nodes = new Array<Node>();

    public AStarPath() {}

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    @Override
    public int getCount() {
        return nodes.size;
    }

    @Override
    public Node get(int i) {
        return nodes.get(i);
    }

    public Node removeIndex(int index) {
        return nodes.removeIndex(index);
    }

    @Override
    public void add(Node node) {
        nodes.add(node);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public void reverse() {
        nodes.reverse();
    }
}
