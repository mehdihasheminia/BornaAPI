package com.bornaapp.borna2d.game.platform;

/**
 * Created by Mehdi on 4/14/2018.
 */

import java.util.Map;

/**
 * methods which are called on platform(e.g. android)
 * but are defined back in the Gdx game
 */
public interface CallbacksToGdx {

    Map<String, String> getGdxData();

    void onMessageReceived(String senderID,  String netMsg);
}
