package com.utsoft.jan.myqqview;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class VideoRecordingActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    private static final String TAG = "VideoRecordingActivity";
    private boolean isSender;
    private ActivityManager activityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        final boolean isSupportEs2 = info.reqGlEsVersion >= 0x20000;
        final VideoRecodingRenderer airRenderer = new VideoRecodingRenderer(this);
        if (isSupportEs2) {
            glSurfaceView.setEGLContextClientVersion(2);

            glSurfaceView.setRenderer(airRenderer);
            isSender = true;
        }
        else {
            Log.i(TAG, "onCreate: 不支持");
        }

        setContentView(R.layout.activity_video);
    }

    class VideoRecodingRenderer implements GLSurfaceView.Renderer{


        public VideoRecodingRenderer(VideoRecordingActivity videoRecordingActivity) {

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //GLUtils.
            //new SurfaceTexture()
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }
}
