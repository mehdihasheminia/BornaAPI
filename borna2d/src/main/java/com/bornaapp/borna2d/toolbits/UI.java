package com.bornaapp.borna2d.toolbits;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.bornaapp.borna2d.game.levels.Engine;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Hashemi on 10/04/2018.<br>
 * A utility class for actor manipulation
 */

public class UI {

    public static Texture getTestRectangle(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillRectangle(0, 0, width, height);
        Texture pixmapTex = new Texture(pixmap);
        pixmap.dispose();
        return pixmapTex;
    }

    /**
     * sets position of an actor to a
     * percentage of viewport
     *
     * @param percentX
     * @param percentY
     */
    public static void MoveActorTo(Actor actor, float percentX, float percentY) {
        if (actor == null)
            return;
        if (percentX > 1.0f)
            percentX = 1.0f;
        if (percentX < 0.0f)
            percentX = 0.0f;
        if (percentY > 1.0f)
            percentY = 1.0f;
        if (percentY < 0.0f)
            percentY = 0.0f;

        float screenWidth = Engine.getInstance().getCurrentLevel().getViewportWitdh();
        float screenHeight = Engine.getInstance().getCurrentLevel().getViewportHeight();

        float actorWidth = actor.getWidth() * actor.getScaleX();
        float actorHeight = actor.getHeight() * actor.getScaleY();

        float x = percentX * screenWidth - actorWidth / 2f;
        float y = percentY * screenHeight - actorHeight / 2f;

        actor.setPosition(x, y);
    }

    public static void MoveActorBy(Actor actor, float percentX, float percentY) {
        if (actor == null)
            return;
        if (percentX > 1.0f)
            percentX = 1.0f;
        if (percentX < -1.0f)
            percentX = -1.0f;
        if (percentY > 1.0f)
            percentY = 1.0f;
        if (percentY < -1.0f)
            percentY = -1.0f;

        float screenWidth = Engine.getInstance().getCurrentLevel().getViewportWitdh();
        float screenHeight = Engine.getInstance().getCurrentLevel().getViewportHeight();

        float x = actor.getX();
        float y = actor.getY();

        x += percentX * screenWidth;
        y += percentY * screenHeight;

        actor.setPosition(x, y);
    }

    public static void AlignActorX(Actor actor, AlignX alignX, float percentMarginX) {
        if (actor == null)
            return;
        if (percentMarginX > 1.0f)
            percentMarginX = 1.0f;
        if (percentMarginX < 0.0f)
            percentMarginX = 0.0f;

        float screenWidth = Engine.getInstance().getCurrentLevel().getViewportWitdh();
        float actorWidth = actor.getWidth() * actor.getScaleX();

        float x = 0f;
        switch (alignX) {
            case LEFT:
                x = percentMarginX * screenWidth;
                break;

            case CENTER:
                x = (screenWidth - actorWidth) / 2f;
                break;

            case RIGHT:
                x = screenWidth - actorWidth - percentMarginX * screenWidth;
                break;
        }

        actor.setPosition(x, actor.getY());
    }

    public static void AlignActorY(Actor actor, AlignY alignY, float percentMarginY) {
        if (actor == null)
            return;
        if (percentMarginY > 1.0f)
            percentMarginY = 1.0f;
        if (percentMarginY < 0.0f)
            percentMarginY = 0.0f;

        float screenHeight = Engine.getInstance().getCurrentLevel().getViewportHeight();
        float actorHeight = actor.getHeight() * actor.getScaleY();

        float y = 0;
        switch (alignY) {
            case TOP:
                y = screenHeight - actorHeight - percentMarginY * screenHeight;
                break;

            case CENTER:
                y = (screenHeight - actorHeight) / 2f;
                break;

            case BOTTOM:
                y = percentMarginY * screenHeight;
                break;
        }
        actor.setPosition(actor.getX(), y);
    }

    public static void AlignActor(Actor actor, AlignX alignX, AlignY alignY, float percentMarginX, float percentMarginY) {
        AlignActorX(actor, alignX, percentMarginX);
        AlignActorY(actor, alignY, percentMarginY);
    }

    public enum AlignX {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum AlignY {
        TOP,
        CENTER,
        BOTTOM
    }

    public static void ScaleActorInInch(Actor actor, float inches) {
        actor.setScale(inches * 160f * Gdx.graphics.getDensity());
    }

    /**
     * @param actor
     * @param percentScale percentage of screen area if scaling object area is 1 cubic pixel.
     */
    public static void ScaleActor(Actor actor, float percentScale) {
        if (percentScale > 1.0f)
            percentScale = 1.0f;
        if (percentScale < 0.0f)
            percentScale = 0.0f;
        //calculate screen area
        float screenWidth = Engine.getInstance().getCurrentLevel().getViewportWitdh();
        float screenHeight = Engine.getInstance().getCurrentLevel().getViewportHeight();
        float screenArea = screenWidth * screenHeight;
        //calculate actor area before scaling
        float oldActorWidth = actor.getWidth();
        float oldActorHeight = actor.getHeight();
        float oldActorArea = oldActorWidth * oldActorHeight;
        //calculate actor area after scaling
        float newActorArea = percentScale * screenArea;
        //calculate libGdx scaling factor
        actor.setScale((float) Math.sqrt(newActorArea/ oldActorArea ));
    }


}
