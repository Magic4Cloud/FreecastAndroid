package com.cloud4magic.freecast.jni;

import com.cloud4magic.freecast.utils.Logger;

/**
 * Date    2017/7/18
 * Author  xiaomao
 */
public class RecordVideo {

    public static native boolean mp4init(String path, int width, int height, int fps, int type);

    public static native void mp4packVideo(byte[] data, int size, int keyFrame);

    public static native void mp4packAudio(byte[] data, int size);

    public static native void mp4close();

    static {
        Logger.e("NativeClass", "before load library");
        System.loadLibrary("CameraShooting");
        Logger.e("NativeClass", "after load library");
    }
}
