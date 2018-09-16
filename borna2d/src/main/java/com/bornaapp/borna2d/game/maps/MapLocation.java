package com.bornaapp.borna2d.game.maps;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mehdi on 11/18/2016.
 */
public class MapLocation {

    public String name;
    public Vector2 position;

    public MapLocation(String _name, float x, float y) {
        name = _name;
        position = new Vector2();
        position.x = x;
        position.y = y;
    }
}
