package com.utsoft.jan.myqqview;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.utsoft.jan.common.app.PresenterActivity;
import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.myqqview.douyin.common.camera.CameraCompat;
import com.utsoft.jan.myqqview.douyin.common.view.ProgressView;
import com.utsoft.jan.myqqview.douyin.common.view.RecordButton;
import com.utsoft.jan.myqqview.douyin.common.view.progressbutton.LoadingImage;
import com.utsoft.jan.myqqview.douyin.common.view.progressbutton.MasterLayout;
import com.utsoft.jan.myqqview.douyin.common.view.progressbutton.ProgressLayout2;
import com.utsoft.jan.myqqview.douyin.common.view.record.OnSurfaceCreatedCallback;
import com.utsoft.jan.myqqview.douyin.common.view.record.RecordSurfaceView;
import com.utsoft.jan.myqqview.douyin.effect.activity.AfterEffectActivity;
import com.utsoft.jan.myqqview.douyin.recoder.persenter.RecordContract;
import com.utsoft.jan.myqqview.douyin.recoder.persenter.RecordPersenter;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class VideoRecordingActivity extends PresenterActivity<RecordContract.Presenter> implements OnSurfaceCreatedCallback, RecordButton.OnRecordListener, RecordContract.View {

    private RecordSurfaceView glSurfaceView;

    private static final String TAG = "VideoRecordingActivity";
    private boolean isSender;
    private ActivityManager activityManager;
    private CameraCompat cameraCompat;
    private RecordButton recordButton;
    private EGLContext mEGLContext;
    private ProgressView mProgressView;
    public  MasterLayout masterLayout;
    private DownLoadSigTask downLoadSigTask;
    private ProgressLayout2 layProgress;
    private String filePath;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        cameraCompat = CameraCompat.newInstance(this);
        glSurfaceView = findViewById(R.id.sv_record);
        glSurfaceView.setSurfaceCreatedCallback(this);
        recordButton = findViewById(R.id.btn_record);
        mProgressView = findViewById(R.id.pv_progress);
        layProgress = findViewById(R.id.lay_progress);

        recordButton.setOnRecordListener(this);
        initPresenter();
        masterLayout = findViewById(R.id.MasterLayout01);

        masterLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initMaslayout();
            }
        });

        layProgress.setAddStartLoading(new ProgressLayout2.AddStartLoading() {
            DownLoadSigTask downLoadSigTask = null;

            @Override
            public void onStartLoading(LoadingImage loadingImage) {
                //downLoadSigTask = new DownLoadSigTask();
                //downLoadSigTask.setmLoadingImage(loadingImage);
                //if (downLoadSigTask != null) {
                //    downLoadSigTask.execute();
                //}

                mPresenter.startRecording(mEGLContext, cameraCompat.getOutputSize().width, cameraCompat.getOutputSize().height);
            }

            @Override
            public void onEndLoading() {
                //downLoadSigTask.cancel(true);
                mPresenter.stopRecording();
            }
        });

        layProgress.setOnProgressFinish(new ProgressLayout2.onProgressFinishListener() {
            @Override
            public void onProgressFinish() {
                if (TextUtils.isEmpty(filePath))
                {
                    return;
                }
                //点击录制完成之后
                AfterEffectActivity.start(VideoRecordingActivity.this,filePath);
            }
        });

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

    private void initMaslayout() {
        masterLayout.animation(); //Need to call this method for animation and progression

        if (MasterLayout.flg_frmwrk_mode == 1) {

            //Start state. Call any method that you want to execute

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                }
            });

            //downLoadSigTask = new DownLoadSigTask();
            //
            //downLoadSigTask.execute();
        }
        if (MasterLayout.flg_frmwrk_mode == 2) {

            //Running state. Call any method that you want to execute

            downLoadSigTask.cancel(true);
            masterLayout.reset();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                }
            });
        }
        if (MasterLayout.flg_frmwrk_mode == 3) {

            //End state. Call any method that you want to execute.

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                }
            });
        }
    }

     class DownLoadSigTask extends AsyncTask<String, Integer, String> {


         public void setmLoadingImage(LoadingImage mLoadingImage) {
             this.mLoadingImage = mLoadingImage;
         }

         private  LoadingImage mLoadingImage;


         @Override
        protected void onPreExecute() {

        }


        @Override
        protected String doInBackground(final String... args) {

            //Creating dummy task and updating progress

            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(50);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                publishProgress(i);

            }


            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {

            //publishing progress to progress arc

            masterLayout.getCusview().setupprogress(progress[0]);
            if (mLoadingImage!=null)
            {
                mLoadingImage.setProgress(progress[0]);
            }

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
        mPresenter.stopRecord();
        glSurfaceView.onPause();
        cameraCompat.stopPreview(true);
        super.onPause();
    }


    @Override
    public void onSurfaceCreated(final SurfaceTexture texture, final EGLContext context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cameraCompat.setSurfaceTexture(texture);
                cameraCompat.startPreview();
                mEGLContext = context;
            }
        });
    }

    //点击 record按钮录制开始
    @Override
    public void OnRecordStart() {
        mPresenter.startRecording(mEGLContext, cameraCompat.getOutputSize().width, cameraCompat.getOutputSize().height);
    }

    // record 录制结束
    @Override
    public void OnRecordStop() {
        mPresenter.stopRecording();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public RecordSurfaceView getSurface() {
        return glSurfaceView;
    }

    //录制回调progress
    @Override
    public void OnRecordProgress(final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mProgressView.setLoadingProgress( progress);
                LogUtil.e("progress:"+progress);
                layProgress.getLoadingImage().setProgress(progress);
            }
        });
    }

    @Override
    public void addProgress(final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressView.addProgress( progress);
                layProgress.getLoadingImage().setLoadingProgress(progress);
            }
        });
    }

    @Override
    public void onViewStopRecord(String filePath) {
        //线程限制么？
        this.filePath = filePath;

    }
}
