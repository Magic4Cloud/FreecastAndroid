package com.cloud4magic.freecast;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.cloud4magic.freecast.utils.Logger;

import java.security.MessageDigest;

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
    }

    public static void getKeyHash(Context context){
        // Add code to print out the key hash
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.cloud4magic.freecast",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Logger.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Logger.d("KeyHash", e.getMessage());
        }
    }
}
