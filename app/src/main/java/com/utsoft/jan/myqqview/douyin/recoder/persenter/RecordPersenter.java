package com.utsoft.jan.myqqview.douyin.recoder.persenter;

import android.Manifest;
import android.opengl.EGLContext;
import android.widget.Toast;

import com.utsoft.jan.common.app.AppProfile;
import com.utsoft.jan.common.factory.presenter.BasePresenter;
import com.utsoft.jan.common.ffmpeg.VideoCmdCallback;
import com.utsoft.jan.common.ffmpeg.VideoCommand;
import com.utsoft.jan.common.ffmpeg.VideoQueue;
import com.utsoft.jan.common.utils.FileUtils;
import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.common.utils.StorageUtil;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.recoder.ClipInfo;
import com.utsoft.jan.myqqview.douyin.common.recoder.MediaRecoder;
import com.utsoft.jan.myqqview.douyin.common.recoder.OnRecordFinishListener;
import com.utsoft.jan.myqqview.douyin.common.recoder.OnRecordProgressListener;
import com.utsoft.jan.myqqview.douyin.permission.PermissionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.utsoft.jan.myqqview.douyin.common.C.MODE_NORMAL;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.recoder.persenter
 */
public class RecordPersenter extends BasePresenter<RecordContract.View>
        implements RecordContract.Presenter, OnRecordFinishListener, OnRecordProgressListener {

    private MediaRecoder mediaRecoder;

    /**
     * 初始速度1.0f
     */
    private @C.SpeedMode
    int mMode = MODE_NORMAL;
    private boolean mStarted;
    private ClipInfo videoInfo;
    private ClipInfo audioInfo;
    private VideoQueue mQueue;
    private List<String> videoList = new ArrayList<>();

    public RecordPersenter(RecordContract.View mView) {
        super(mView);

        init(mView);
    }

    private void init(RecordContract.View mView) {
        PermissionManager.instance().checkPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new RecordPermissionGrantedListener());
        mediaRecoder = new MediaRecoder(5, this);
        mediaRecoder.setProgressListener(this);
        mQueue = new VideoQueue();
    }

    //录制结束
    @Override
    public void onRecordFinish(ClipInfo info) {
        //计算当前的进度条
        switch (info.getType()) {
            case C.VIDEO:
                videoInfo = info;
                LogUtil.e("C.VIDEO");
                break;
            case C.AUDIO:
                audioInfo = info;
                LogUtil.e("C.AUDIO");
                break;
        }
        LogUtil.e("第一次video录制回调进入方法! info:打印"+info.getType());
        mergeVideoAudio();
    }

    //合并Video Audio
    private void mergeVideoAudio() {
        if (audioInfo == null || videoInfo == null)
            return;
        final String currentFile = generateFileName();
        final VideoCommand videoCommand = VideoCommand.mergeVideoAudio(videoInfo.getFileName(), audioInfo.getFileName(), currentFile);
        LogUtil.e("第一次video录制回调进入方法!");

        mQueue.execCommand(videoCommand.toArray(), new VideoCmdCallback() {
            @Override
            public void onCommandFinish(boolean success) {
                LogUtil.e("第一次录制有没有成功!");
                LogUtil.e("横杠百分比:" + videoInfo.getDuration() * 1.0f / mediaRecoder.getMaxDuration());
                mView.addProgress(videoInfo.getDuration() * 1.0f / mediaRecoder.getMaxDuration());
                videoList.add(currentFile);
                LogUtil.e("videoList"+videoList.toString());
                final long tmpDuration = videoInfo.getDuration();
                mergeVideoList(tmpDuration);
                FileUtils.deleteFile(audioInfo.getFileName());
                FileUtils.deleteFile(videoInfo.getFileName());
                //这里置空 前面两个线程回调才能成功.
                RecordPersenter.this.audioInfo = null;
                RecordPersenter.this.videoInfo = null;
            }
        });
    }

    private void mergeVideoList(final long videoInfo) {
        LogUtil.e("videoInfo " + videoInfo);
        final String mergeVideo = generateFileName("mergeVideo");
        LogUtil.e("mergeVideo :" + mergeVideo);
        FileUtils.createFile(mergeVideo);
        final VideoCommand videoCommand = VideoCommand.mergeVideo(videoList, mergeVideo);
        mQueue.execCommand(videoCommand.toArray(), new VideoCmdCallback() {
            @Override
            public void onCommandFinish(boolean success) {
                LogUtil.e("mediaRecoder.getMaxDuration() :" + mediaRecoder.getMaxDuration());
                long maxDur = (long) mediaRecoder.getMaxDuration();
                LogUtil.e("mediaRecoder.maxDur :" + maxDur);
                if (videoInfo >= maxDur)
                {
                    for (String fName : videoList) {
                        LogUtil.e("fname" + fName);
                        FileUtils.deleteFile(fName);
                    }
                    mView.onViewStopRecord(mergeVideo);
                }
            }
        });
    }

    @Override
    public void onRecordProgress(long duration) {
        //每次变化
        float progress = duration * 1.0f / mediaRecoder.getMaxDuration();
        mView.OnRecordProgress(progress);
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
     *
     * @param mEGLContext
     * @param width
     * @param height
     */
    @Override
    public void startRecording(EGLContext mEGLContext, int width, int height) {
        //todo 这里不应该在主线程开始 有问题
        //todo 怀疑是play线程有问题
        if (!mediaRecoder.start(mEGLContext, width, height, mMode)) {
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

    //屏幕一黑强制停止
    @Override
    public void stopRecord(){
        mediaRecoder.stop();
    }

    private String generateFileName() {
        return StorageUtil.getExternalStoragePath() + File.separator + "temp" + videoList.size() + ".mp4";
    }

    private String generateFileName(String fileName) {
        return StorageUtil.getExternalStoragePath() + File.separator + fileName + ".mp4";
    }

}
