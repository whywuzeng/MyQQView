package com.utsoft.jan.common.utils;

import android.util.Log;

/**
 * Created by 薛贤俊 on 2018/3/1.
 */

public class LogUtil {

    private static final String TAG = "MyQQView";

    private static boolean isDebug = true;

    public static void e(String tag, String msg, Throwable e) {
        if (isDebug) {
            Log.e(tag, msg, e);
        }
    }

    public static void e(String tag, Throwable e) {
        if (isDebug) {
            Log.e(tag, "", e);
        }
    }

    public static void e(String msg) {
        if (isDebug) {

            e(TAG, msg);
        }
    }

    public static void e(Throwable e) {
        if (isDebug) {

            e(TAG, e);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {

            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {

            d(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {

            Log.e(tag, msg);
        }
    }
}
