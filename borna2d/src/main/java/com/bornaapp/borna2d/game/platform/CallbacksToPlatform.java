/*
 * Copyright (c) 2017.
 *  s. Mehdi HashemiNia
 *  All Rights Reserved.
 */

package com.bornaapp.borna2d.game.platform;

/**
 * Created by s. Mehdi HashemiNia on 1/19/2017.
 * <p>
 * <p>
 * methods which are called in Gdx game
 * but are defined back in the platform(e.g. android)
 */
public interface CallbacksToPlatform {

    void setCallbacksToGdx(CallbacksToGdx callbacksToGdx);

    //----platform data--------
    int getGameProgress();

    void setGameProgress(int progress);

    boolean isMultiplayer();

    /**
     * @param sMessage               json-formatted string message
     * @param isReliable true for sending non-time-sensitive data.
     */
    void BroadcastMessage(String sMessage, boolean isReliable);

    //----platform navigation---

    String getOpponentName();

    byte[] getOpponentImageBytes();

    void gotoMainMenu();

    void GoToResultPage(boolean iWon,
                        float myRecordTime, float myMaxSpeed, float myAveSpeed,
                        float opponentRecordTime, float opponentMaxSpeed, float opponentAveSpeed);

    //----platform purchase---
    int getNumCoins();

    void ConsumeCoin();

    int getSkins();

    void Purchase(int id);

    long getTime();
}
