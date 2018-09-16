package com.bornaapp.borna2d.components;

import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.game.levels.Engine;

/**
 * Created by Hashemi on 12/18/2016.
 * Higher Z means farther from camera
 */
public class ZComponent extends BaseComponent {

    public int z;

    public void Init(int _z) {
        z = _z;
    }

    public void Init() {
        z = Engine.getInstance().getCurrentLevel().getDefaultZ();
    }

    @Override
    public void dispose() {
    }
}
