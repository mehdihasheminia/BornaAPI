package com.bornaapp.borna2d.game.levels;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Hashemi on 12/10/2016.
 * A collection of SaveSlots. Engine will Read from file
 * to this structure or write this structure to file .
 */
public class SlotCollection {

    public Array<Slot> slots = new Array<Slot>();

    public SlotCollection(){}
}
