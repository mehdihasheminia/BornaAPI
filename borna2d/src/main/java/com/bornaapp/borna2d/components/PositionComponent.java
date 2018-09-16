package com.bornaapp.borna2d.components;

public class PositionComponent extends BaseComponent {

    // normally we should consider x & y as integers but we used float
    // because of its compatibility with Vector2 class
    public float x, y;

    //region Methods

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    private PositionComponent() {
    }

    public void Init(float _x, float _y) {
        x = _x;
        y = _y;
    }

    @Override
    public void dispose(){
    }
}
