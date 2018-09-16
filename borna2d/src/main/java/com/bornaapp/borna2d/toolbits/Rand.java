package com.bornaapp.borna2d.toolbits;

import java.util.Date;
import java.util.Random;

/**
 * Created by Hashemi on 24/04/2018.<br>
 * desc:
 * <p>
 * more info:
 */

public class Rand {

    //uses a static field in order to have a consistent seed.
    static private Random rand = new Random(new Date().getTime());

    static public void setSeed(long seed){
        rand.setSeed(seed);
    }

    static public int getInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    static public boolean getBoolean(){
        return rand.nextBoolean();
    }
}
