package com.elektra.typhoon.database;

import android.database.CursorWindow;

import java.lang.reflect.Field;

public class CursorWindowFixer {
    public static void fix() {
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 10485760);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
