package com.utsoft.jan.myqqview;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.utsoft.jan.common.app.PresenterActivity;
import com.utsoft.jan.myqqview.douyin.common.camera.CameraCompat;
import com.utsoft.jan.myqqview.douyin.common.view.RecordButton;
import com.utsoft.jan.myqqview.douyin.common.view.record.OnSurfaceCreatedCallback;
import com.utsoft.jan.myqqview.douyin.common.view.record.RecordSurfaceView;
import com.utsoft.jan.myqqview.douyin.recoder.persenter.RecordContract;
import com.utsoft.jan.myqqview.douyin.recoder.persenter.RecordPersenter;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class VideoRecordingActivity extends PresenterActivity<RecordContract.Presenter> implements OnSurfaceCreatedCallback, RecordButton.OnRecordListener,RecordContract.View {

    private RecordSurfaceView glSurfaceView;

    private static final String TAG = "VideoRecordingActivity";
    private boolean isSender;
    private ActivityManager activityManager;
    private CameraCompat cameraCompat;
    private RecordButton recordButton;
    private EGLContext mEGLContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        cameraCompat = CameraCompat.newInstance(this);
        glSurfaceView = findViewById(R.id.sv_record);
        glSurfaceView.setSurfaceCreatedCallback(this);
        recordButton = findViewById(R.id.btn_record);

        recordButton.setOnRecordListener(this);

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
    protected void initPresenter() {
        mPresenter = new RecordPersenter(this);
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
    public void onSurfaceCreated(final SurfaceTexture texture, final EGLContext context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cameraCompat.startPreview();
                cameraCompat.setSurfaceTexture(texture);
                mEGLContext = context;
            }
        });
    }

    //点击 record按钮录制开始
    @Override
    public void OnRecordStart() {
        mPresenter.startRecording(mEGLContext,cameraCompat.getOutputSize().width,cameraCompat.getOutputSize().height);
    }

    // record 录制结束
    @Override
    public void OnRecordStop() {

    }

    @Override
    public void showLoading() {

    }

}
