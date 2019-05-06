package com.elektra.typhoon.database;

import android.database.CursorWindow;

import java.lang.reflect.Field;

public class CursorWindowFixer {
    public static void fix() {
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 40485760);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
