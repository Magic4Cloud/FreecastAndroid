package com.cloud4magic.freecast;

import android.app.Application;

/**
 * Date   2017/7/5
 * Editor  Misuzu
 */

public class MyAplication extends Application {

    public static MyAplication INSTANCE ;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
