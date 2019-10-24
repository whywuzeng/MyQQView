package com.utsoft.jan.myqqview.utils;

import android.content.res.AssetManager;
import android.support.annotation.Keep;
import android.view.Surface;

/**
 * Created by Administrator on 2019/10/21.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.utils
 */
@Keep
public class OpenGLJinLib {

    static {
        System.loadLibrary("native-lib");
    }

    @Keep
    public static native String sayHello();

    @Keep
    public static native String stringHello();

    @Keep
    public static native int cameroInit(Surface surface, int width, int height, AssetManager assetManager);

    @Keep
    public static native void cameroDraw(float[] matrix);

    @Keep
    public static native void camereRelease();


    @Keep
    public static native int cameraFilterInite(Surface surface, int width, int height, AssetManager assetManager);

    @Keep
    public static native void cameraFilterDraw(float[] matrix);

    @Keep
    public static native void cameraFilterRelease();
}
