package com.example.taobaounion.utils;

import android.util.Log;

public class LogUtils {
    private static int currentLev = 4;
    private static int debugLev = 4;
    private static int infoLev = 3;
    private static int warningLev = 2;
    private static int errorLev = 1;

    public static void d(Object obj, String log) {
        if (currentLev >= debugLev) {
            Log.d(obj.getClass().getSimpleName(), log);
        }
    }

    public static void i(Object obj, String log) {
        if (currentLev >= infoLev) {
            Log.i(obj.getClass().getSimpleName(), log);
        }
    }

    public static void w(Object obj, String log) {
        if (currentLev >= warningLev) {
            Log.w(obj.getClass().getSimpleName(), log);
        }
    }

    public static void e(Object obj, String log) {
        if (currentLev >= errorLev) {
            Log.e(obj.getClass().getSimpleName(), log);
        }
    }
}
