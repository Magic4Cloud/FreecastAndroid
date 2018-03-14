package com.cloud4magic.freecast.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Date    2017/6/29
 * Author  xiaomao
 */

public class ToastUtil {

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}
