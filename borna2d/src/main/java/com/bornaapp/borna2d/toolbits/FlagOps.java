package com.bornaapp.borna2d.toolbits;

public class FlagOps {

    public static int add(int flag, int bitIndex) {
        int value = 1 << bitIndex;
        flag |= value;
        return flag;
    }

    public static int remove(int flag, int bitIndex) {
        int value = 1 << bitIndex;
        flag &= ~value;
        return flag;
    }

    public static boolean contains(int flag, int bitIndex) {
        int value = 1 << bitIndex;
        return 0 != (flag & value);
    }
}
