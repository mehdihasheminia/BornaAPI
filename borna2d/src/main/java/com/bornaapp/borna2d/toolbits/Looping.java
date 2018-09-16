package com.bornaapp.borna2d.toolbits;

/**
 * Created by Mehdi on 6/28/2018.<br>
 * desc:
 * <p>
 * more info:
 */
public class Looping {

    private long lastTime = 0;
    private boolean didOnce = false;

    /**
     * checks if its time to do something
     *
     * @param ms time in milli-seconds
     * @return true if it's time to repeat a task
     */
    public boolean isTime(float ms) { ///<--------------------todo: can u use it twice in one level?
        long currentTime = System.nanoTime();
        if ((currentTime - lastTime) * 1e-6f < ms) {
            //return false to alert user it's not time yet!
            return false;
        }
        lastTime = currentTime;
        return true;
    }

    /**
     * We use this function if we want to do a task
     * in a loop ONLY ONCE and not repeat it
     *
     * @return true if a task is not done yet
     */
    public boolean isNotDoneYet() {
        if (!didOnce) {
            didOnce = true;
            return true;
        }
        return false;
    }
}
