package com.utsoft.jan.myqqview.douyin.recoder.persenter;

import android.Manifest;
import android.opengl.EGLContext;
import android.widget.Toast;

import com.utsoft.jan.common.app.AppProfile;
import com.utsoft.jan.common.factory.presenter.BasePresenter;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.recoder.ClipInfo;
import com.utsoft.jan.myqqview.douyin.common.recoder.MediaRecoder;
import com.utsoft.jan.myqqview.douyin.common.recoder.OnRecordFinishListener;
import com.utsoft.jan.myqqview.douyin.common.recoder.OnRecordProgressListener;
import com.utsoft.jan.myqqview.douyin.permission.PermissionManager;

import static com.utsoft.jan.myqqview.douyin.common.C.MODE_NORMAL;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.recoder.persenter
 */
public class RecordPersenter extends BasePresenter<RecordContract.View>
        implements RecordContract.Presenter,OnRecordFinishListener, OnRecordProgressListener {

    private MediaRecoder mediaRecoder;

    /**
     * 初始速度1.0f
     */
    private @C.SpeedMode
    int mMode = MODE_NORMAL;
    private boolean mStarted;

    public RecordPersenter(RecordContract.View mView) {
        super(mView);

        init(mView);
    }

    private void init(RecordContract.View mView) {
        PermissionManager.instance().checkPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new RecordPermissionGrantedListener());
        mediaRecoder = new MediaRecoder(3, this);
        mediaRecoder.setProgressListener(this);
    }

    //录制结束
    @Override
    public void onRecordFinish(ClipInfo info) {

    }

    @Override
    public void onRecordProgress(long duration) {

    }

    class RecordPermissionGrantedListener implements PermissionManager.OnPermissionGrantedListener {

        @Override
        public void onPermissionGranted(String permission) {

        }

        @Override
        public void onPermissionDenied(String permission) {

        }
    }

    /**
     * 开始录制
     * @param mEGLContext
     * @param width
     * @param height
     */
    @Override
    public void startRecording(EGLContext mEGLContext, int width, int height) {
        if (!mediaRecoder.start(mEGLContext,width,height,mMode)) {
            Toast.makeText(AppProfile.getContext(), "视频已达到最大长度", Toast.LENGTH_SHORT).show();
            return;
        }

        mStarted = true;
        mView.getSurface().setFrameListener(mediaRecoder);
    }

    @Override
    public void stopRecording() {
        if (!mStarted)
            return;

        mediaRecoder.stop();
    }

}
