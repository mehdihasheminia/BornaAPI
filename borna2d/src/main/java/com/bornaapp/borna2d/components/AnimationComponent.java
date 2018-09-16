package com.bornaapp.borna2d.components;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bornaapp.borna2d.PlayStatus;

/**
 * Created by Mehdi on 09/02/2015.
 * ...
 */
public class AnimationComponent extends BaseComponent {
    private Animation<TextureRegion> animation; //<---type bcs of new version
    public float elapsedTime = 0;

    private PlayStatus playStatus = PlayStatus.Playing;

    //region Methods

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    private AnimationComponent() {
    }

    public void Init(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        if (this.animation == animation)
            return;
        this.animation = animation;
        elapsedTime = 0;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setPlayStatus(PlayStatus status){
        playStatus = status;
    }

    public PlayStatus getPlayStatus(){
        return playStatus;
    }

    //endregion

    @Override
    public void dispose(){
    }
}
