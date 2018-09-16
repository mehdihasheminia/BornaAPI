package com.bornaapp.borna2d.game.levels;

/**
 * Created by Hashemi on 12/10/2016.
 * A basic structure which creates a framework for saving game data on file.
 * user must inherit a custom child class and add it to list of slots in saveInterface.
 */
public class Slot {

    public String name;
    public int param1;

    public Slot() {
    }
}
