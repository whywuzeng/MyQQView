package com.utsoft.jan.myqqview;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utsoft.jan.myqqview.douyin.common.camera.CameraCompat;
import com.utsoft.jan.myqqview.douyin.common.view.record.OnSurfaceCreatedCallback;
import com.utsoft.jan.myqqview.douyin.common.view.record.RecordSurfaceView;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class VideoRecordingActivity extends AppCompatActivity implements OnSurfaceCreatedCallback {

    private RecordSurfaceView glSurfaceView;

    private static final String TAG = "VideoRecordingActivity";
    private boolean isSender;
    private ActivityManager activityManager;
    private CameraCompat cameraCompat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        cameraCompat = CameraCompat.newInstance(this);
        glSurfaceView = findViewById(R.id.sv_record);
        glSurfaceView.setSurfaceCreatedCallback(this);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        final boolean isSupportEs2 = info.reqGlEsVersion >= 0x20000;
        if (isSupportEs2) {

            isSender = true;
        }
        else {
            Log.i(TAG, "onCreate: 不支持");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        cameraCompat.startPreview();
    }

    @Override
    protected void onPause() {
        glSurfaceView.onPause();
        cameraCompat.onStopPreview();
        super.onPause();
    }


    @Override
    public void onSurfaceCreated(final SurfaceTexture texture, EGLContext context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cameraCompat.setSurfaceTexture(texture);
                cameraCompat.startPreview();
            }
        });
    }

}
