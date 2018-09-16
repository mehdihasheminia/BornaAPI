package com.bornaapp.borna2d.game.maps;

/**
 * Created by Mehdi on 8/26/2015.
 * ...
 */

public class MapParameters {
    public String  collisionLayerName;   //each object: property x & y & width & height(Float)
    public String areaLayerName;      //each object: property x & y & width & height(Float)
    public String  lightsLayerName;      //each object: property x & y(Float), custom property"distance"(String) & "color"(String[4])
    public String  particlesLayerName;   //each object: property x & y(Float)
    public String  checkpointsLayerName; //each object: custom property"name"(String), property x & y(Float)
    public String  pathLayerName;
    public String  propertyKey_Color;
    public String  propertyKey_Distance;
}
