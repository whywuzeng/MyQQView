package com.utsoft.jan.myqqview.douyin.common.recoder.video;

import android.view.Surface;

/**
 * Created by Administrator on 2019/9/26.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder.video
 * 接受解码的buffer
 */
public class InputSurface {
    private final WindowSurface windowSurface;
    private GLCore mGLCore;

    public InputSurface(Surface surface) {
        mGLCore = new GLCore(null, GLCore.FLAG_RECORDABLE);
        windowSurface = new WindowSurface(mGLCore, surface, true);
        windowSurface.makeCurrent();
    }

    public void MCurrent(){
        windowSurface.makeCurrent();
    }

    public void swapBuf(){
        windowSurface.swapBuffers();
    }
}
