package com.bornaapp.borna2d.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.game.levels.Engine;

import java.io.Serializable;

/**
 * Created by Mehdi on 9/2/2015.
 * ...
 */
public class SoundComponent extends BaseComponent {
    private long id;
    private Sound sound;
    private boolean is3D, isLooping, isStopped;
    private float volume, pan, pitch;
    private boolean volNeedsCommit, panNeedsCommit, pitchNeedsCommit;

    //----------------------
    String tmp;
    static int numSounds = 0;

    //private constructor, as components must be created
    //using Ashley Engine and initialize afterwards.
    private SoundComponent() {
        id = -1;
        is3D = false;
        isLooping = false;
        isStopped = true;
        volume = 1.0f;
        pan = 0f;
        pitch = 1f;
        volNeedsCommit = false;
        panNeedsCommit = false;
        pitchNeedsCommit = false;
    }

    public void init(String assetName, boolean is3D, boolean isLooping) {
        sound = Engine.getInstance().getCurrentLevel().assets.getSound(assetName);
        this.is3D = is3D;
        this.isLooping = isLooping;
        this.volume = 1.0f;
        //
        tmp = assetName;
        numSounds++;
    }

    public void init(FileHandle fileHandle, boolean is3D, boolean isLooping) {
        sound = Gdx.audio.newSound(fileHandle);
        this.is3D = is3D;
        this.isLooping = isLooping;
        this.volume = 1.0f;
        //
        tmp = fileHandle.path();
        numSounds++;
    }

    //---------------------------------- PLAY BACK -------------------------------------------------
    public void play(float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
        play();
    }

    public void play() {
        //only one instance of a unique-sound can be played
        //for simultaneous play, different SoundComponents must be instantiated
        if (isStopped) {
            //id of a unique-sound is saved for later access
            if (isLooping)
                id = sound.loop(volume);
            else
                id = sound.play(volume);
            isStopped = false;
        }
    }

    public void stop() {
        if (!isStopped) {
            sound.stop();
            isStopped = true;
        }
    }

    //this solves the problem of "silent sounds at the beginning" in some devices.
    public void refreshID() {
        //calling this is necessary in some devices like Samsung A3
        //but nor very important on some others like galaxy Alpha
        if (id < 0) {
            stop();
            play();
        }
    }

    //--------------------------------- SOUND PROPERTIES -------------------------------------------

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        float tmpVolume = MathUtils.clamp(volume, 0f, 1f);
        if (this.volume != tmpVolume) {
            //save value
            this.volume = tmpVolume;
            //schedule to commitChanges
            volNeedsCommit = true;
        }
    }

    public void setPosition(Vector2 srcPosition) {

        if (id < 0 || !is3D || volume == 0f || isStopped)
            return;

        //reference to camera and it's boundaries for easy access
        Camera camera = Engine.getInstance().getCurrentLevel().getCamera();
        float halfViewWidth = camera.viewportWidth / 2f;
        float halfViewHeight = camera.viewportHeight / 2f;
        //our micro-phone is attached to the camera & srcPosition is the position of source of sound
        //if we map srcPosition from world space to view space, it's value will be relative to camera
        //including zooming and rotation
        Vector3 vSrcPos = camera.project(new Vector3(srcPosition.x, srcPosition.y, 0));
        //change coordinate origin to center of screen instead of bottom-left.
        vSrcPos.x -= halfViewWidth;
        vSrcPos.y -= halfViewHeight;
        //normalize values to be independent of resolution
        vSrcPos.x /= halfViewWidth;
        vSrcPos.y /= halfViewHeight;
        //distance is the diagonal of vSrcPos from center of screen(current coordinate origin)
        float distance = (float) Math.sqrt(Math.pow(vSrcPos.x, 2) + Math.pow(vSrcPos.y, 2));
        float maxDistance = 3f;
        float distVol = 1f - MathUtils.clamp(distance / maxDistance, 0f, 1f);
        float alteredVolume = MathUtils.clamp(distVol * volume, 0f, volume);
        setVolume(alteredVolume);
        //calculate left/right pan
        float tmpPan = MathUtils.clamp(vSrcPos.x, -1f, 1f);
        if (this.pan != tmpPan) {
            //save value
            this.pan = tmpPan;
            //schedule to commitChanges
            panNeedsCommit = true;
        }
    }

    public void setPitch(float pitch) {
        if (id < 0 || volume == 0f || isStopped)
            return;
        float tmpPitch = MathUtils.clamp(pitch, 0.5f, 2f);
        if (this.pitch != tmpPitch) {
            //save value
            this.pitch = tmpPitch;
            //schedule to commitChanges
            pitchNeedsCommit = true;
        }
    }

    public void commitChanges() {
        if (volNeedsCommit && !panNeedsCommit)
            sound.setVolume(id, volume);
        if (panNeedsCommit)
            sound.setPan(id, pan, volume);
        if (pitchNeedsCommit)
            sound.setPitch(id, pitch);

        volNeedsCommit = false;
        panNeedsCommit = false;
        pitchNeedsCommit = false;
    }

    @Override
    public void dispose() {
        sound.dispose();
    }
}