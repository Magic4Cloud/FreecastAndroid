package com.cloud4magic.freecast.utils;

import android.util.Log;

import com.cloud4magic.freecast.BuildConfig;

/**
 * Date    2017/6/26
 * Author  xiaomao
 */

public class Logger {

    private static String APP_NAME = "FreeStream-";
    private static boolean LOG_E = BuildConfig.DEBUG_LOG;
    private static boolean LOG_W = BuildConfig.DEBUG_LOG;
    private static boolean LOG_I = BuildConfig.DEBUG_LOG;
    private static boolean LOG_D = BuildConfig.DEBUG_LOG;

    public static void e(String tag, String msg) {
        if (LOG_E) {
            tag = APP_NAME + tag;
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (LOG_E) {
            tag = APP_NAME + tag;
            Log.e(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (LOG_W) {
            tag = APP_NAME + tag;
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (LOG_W) {
            tag = APP_NAME + tag;
            Log.w(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (LOG_I) {
            tag = APP_NAME + tag;
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (LOG_I) {
            tag = APP_NAME + tag;
            Log.i(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        if (LOG_D) {
            tag = APP_NAME + tag;
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (LOG_D) {
            tag = APP_NAME + tag;
            Log.d(tag, msg, t);
        }
    }
}
