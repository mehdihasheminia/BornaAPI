package com.bornaapp.borna2d.components;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Mehdi on 12/21/2016.
 */
public abstract class DrawingComponent extends BaseComponent {

    public float scale = 1.0f;

    public boolean flipX = false;
    public boolean flipY = false;

    public float offsetX = 0f;
    public float offsetY = 0f;

    //region Methods

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    protected DrawingComponent() {}

    public abstract void Init(String assetName, float _scale) ;
    public abstract void Init(FileHandle file, float _scale);

    //endregion
}
