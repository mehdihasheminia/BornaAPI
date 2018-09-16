/*
 * Copyright (c) 2017.
 *  s. Mehdi HashemiNia
 *  All Rights Reserved.
 */

package com.bornaapp.borna2d;

import java.util.EnumSet;

/**
 * Created by s. Mehdi HashemiNia on 1/3/2017.
 */
public class Flags<E extends Enum<E>> {
    private EnumSet<E> enumSet;

    public Flags(Class<E> elementType) {
        enumSet = EnumSet.noneOf(elementType);
    }

    public void set(E e) {
        enumSet.add(e);
    }

    public void clear(E e) {
        enumSet.remove(e);
    }

    public void toggle(E e) {
        if (enumSet.contains(e))
            enumSet.remove(e);
        else
            enumSet.add(e);
    }

    public boolean contains(E e) {
        return enumSet.contains(e);
    }
}
