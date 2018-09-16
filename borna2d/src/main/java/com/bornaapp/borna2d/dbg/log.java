package com.bornaapp.borna2d.dbg;

import com.badlogic.gdx.math.Vector2;
import com.bornaapp.borna2d.game.levels.Engine;

/**
 * Created by Hashemi on 9/1/2016.
 * <p/>
 * We need unique and global access to the functions of this class
 * but no need to instantiate, inherit, pass as argument, etc....
 * So, as this class is merely a bunch of functions, we used
 * "Static" data structure instead of "singlton object".
 */

public class log {

    private static final char BACKSLASH = (char) 27;
    // ANSI scape codes
    private static final String ANSI_RESET = "[0m";
    private static final String ANSI_BLACK = "0";
    private static final String ANSI_RED = "1";
    private static final String ANSI_GREEN = "2";
    private static final String ANSI_YELLOW = "3";
    private static final String ANSI_BLUE = "4";
    private static final String ANSI_PURPLE = "5";
    private static final String ANSI_CYAN = "6";
    private static final String ANSI_WHITE = "7";

    //region private methods
    private static void text(String textToPrint, String foreColor) {
        String ansiFormat = "[3" + foreColor + "m";
        System.out.println(BACKSLASH + ansiFormat + textToPrint + BACKSLASH + ANSI_RESET);
    }

    private static void text(String textToPrint, String foreColor, String backColor) {
        String ansiFormat = "[3" + foreColor + ";4" + backColor + "m";
        System.out.println(BACKSLASH + ansiFormat + textToPrint + BACKSLASH + ANSI_RESET);
    }

    private static LogLevel getEngineLogLevel() {
        return Engine.getInstance().getConfig().logLevel;
    }

    private static String getCurrentLevelName() {
        try {
            return Engine.getInstance().getCurrentLevel().getName();
        } catch (Exception e) {
            return "UnknownLevel";
        }
    }

    private static String getCurrentMethodName() {
        try {
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[3];
            //extract class name
            String[] classNameArray = e.getClassName().split("\\.", 0);
            String className = classNameArray[classNameArray.length - 1];
            //extract method name
            String methodName = e.getMethodName();

            return className + "." + methodName + "()";
        } catch (Exception e) {
            return "UnknownMethod";
        }
    }
    //endregion

    //region public methods
    public static void error(String message) {
        if (getEngineLogLevel() == LogLevel.NONE)
            return;
        text(getCurrentLevelName() + "; " + getCurrentMethodName() + ": " + message, ANSI_RED);
    }

    public static void error(boolean value) {
        error(Boolean.toString(value));
    }

    public static void error(float value) {
        error(Float.toString(value));
    }

    public static void error(double value) {
        error(Double.toString(value));
    }

    public static void error(int value) {
        error(Integer.toString(value));
    }

    public static void error(Vector2 value) {
        error("[" + Float.toString(value.x) + ", " + Float.toString(value.y) + "]");
    }

    public static void info(String message) {
        if (getEngineLogLevel() == LogLevel.ERROR || getEngineLogLevel() == LogLevel.NONE)
            return;
        text(getCurrentLevelName() + "; " + getCurrentMethodName() + ": " + message, ANSI_BLUE);
    }

    public static void info(boolean value) {
        info(Boolean.toString(value));
    }

    public static void info(float value) {
        info(Float.toString(value));
    }

    public static void info(double value) {
        info(Double.toString(value));
    }

    public static void info(int value) {
        info(Integer.toString(value));
    }

    public static void info(Vector2 value) {
        info("[" + Float.toString(value.x) + ", " + Float.toString(value.y) + "]");
    }

    public static void debug(String message) {
        if (getEngineLogLevel() == LogLevel.ERROR || getEngineLogLevel() == LogLevel.INFO || getEngineLogLevel() == LogLevel.NONE)
            return;
        text(getCurrentLevelName() + "; " + getCurrentMethodName() + ": " + message, ANSI_BLACK);
    }

    public static void debug(boolean value) {
        debug(Boolean.toString(value));
    }

    public static void debug(float value) {
        debug(Float.toString(value));
    }

    public static void debug(double value) {
        debug(Double.toString(value));
    }

    public static void debug(int value) {
        debug(Integer.toString(value));
    }

    public static void debug(Vector2 value) {
        debug("[" + Float.toString(value.x) + ", " + Float.toString(value.y) + "]");
    }

    public static void raw(String message) {
        text(message, ANSI_GREEN);
    }

    public static void raw(int value) {
        raw(Integer.toString(value));
    }

    public static void raw(float value) {
        raw(Float.toString(value));
    }
    //endregion
}