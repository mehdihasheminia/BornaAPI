package com.bornaapp.borna2d.physics;

/**
 * Created by Mehdi on 2/9/2018.
 */

public class Material {
    public float density = 1.0f;
    public float elasticity = 0.05f;
    public float friction = 0f;

    public Material(float _density, float _elasticity, float _friction) {
        density = _density;
        elasticity = _elasticity;
        friction = _friction;
    }

    public Material() {
        density = 1.0f;
        elasticity = 0.1f;
        friction = 1.0f;
    }
}
