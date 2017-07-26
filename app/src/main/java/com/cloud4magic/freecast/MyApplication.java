package com.cloud4magic.freecast;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Date   2017/7/5
 * Editor  Misuzu
 */

public class MyApplication extends Application {

    public static MyApplication INSTANCE ;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
