package com.utsoft.jan.myqqview.douyin.common.view.record;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.record
 */
public class RecordSurfaceView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    public void setSurfaceCreatedCallback(OnSurfaceCreatedCallback mSurfaceCreatedCallback) {
        this.mSurfaceCreatedCallback = mSurfaceCreatedCallback;
    }

    private OnSurfaceCreatedCallback mSurfaceCreatedCallback;

    public RecordSurfaceView(Context context) {
        super(context);
        setup();
    }

    public RecordSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        //初始化 render
        setEGLContextClientVersion(2);

    }

    void onSurfaceCreated(SurfaceTexture texture, EGLContext context) {
        if (mSurfaceCreatedCallback != null) {
            mSurfaceCreatedCallback.onSurfaceCreated(texture, context);
        }
        texture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }
}
