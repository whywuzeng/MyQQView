package com.utsoft.jan.myqqview.douyin.common.recoder;

import android.opengl.EGLContext;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.utsoft.jan.common.utils.FileUtils;
import com.utsoft.jan.common.utils.ThreadUtil;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.recoder.audio.AudioConfig;
import com.utsoft.jan.myqqview.douyin.common.recoder.video.VideoConfig;
import com.utsoft.jan.myqqview.douyin.common.recoder.video.VideoFrameData;
import com.utsoft.jan.myqqview.douyin.common.view.record.onFrameAvailableListener;

import java.io.IOException;

import static com.utsoft.jan.myqqview.douyin.common.C.VIDEO;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder
 */
public class MediaRecoder implements onFrameAvailableListener, OnRecordFinishListener {

    private final OnRecordFinishListener mFinishListener;
    private  int remainDuration;
    private final int maxDuration;
    private VideoRecoder videoRecoder;
    private AudioRecoder audioRecoder;

    private Handler mHandler;

    public MediaRecoder(int seconds, @NonNull OnRecordFinishListener listener)
    {
        mFinishListener = listener;
        videoRecoder = new VideoRecoder();

        audioRecoder = new AudioRecoder();
        audioRecoder.setOnRecordFinishListener(this);
        remainDuration = seconds * C.SECOND_IN_US;
        maxDuration = remainDuration;
        videoRecoder.setOnRecordFinishListener(this);
        mHandler = ThreadUtil.newHandlerThread("MediaRecoder");
    }

    public boolean startHandle(final EGLContext context, final int width, final int height, @C.SpeedMode final int mode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                start(context, width, height, mode);
            }
        });
        return true;
    }

    public boolean start(EGLContext context, int width, int height, @C.SpeedMode int mode){
        if (remainDuration<=0){
            return  false;
        }

        FileUtils.createFile(C.VIDEO_TEMP_FILE_NAME);
        final VideoConfig video = new VideoConfig(context, width, height, C.VideoParams.BIT_RATE,C.VIDEO_TEMP_FILE_NAME);
        video.setFactor(MediaConfig.getSpeedFactor(mode));
        video.setMaxDuration(remainDuration);
        videoRecoder.configure(video);

        FileUtils.createFile(C.AUDIO_TEMP_FILE_NAME);
        final AudioConfig audioConfig = new AudioConfig(C.AudioParams.SAMPLE_RATE, C.AudioParams.SAMPLE_PER_FRAME, C.AUDIO_TEMP_FILE_NAME);
        audioConfig.setMaxDuration(remainDuration);
        audioConfig.setSpeedMode(mode);
        audioRecoder.configure(audioConfig);

        try {
            videoRecoder.prepareCodec();
            audioRecoder.prepareCodec();
        } catch (IOException e) {
            e.printStackTrace();
            videoRecoder.shutdown();
            audioRecoder.shutdown();
            return false;
        }
        audioRecoder.start();

        videoRecoder.start();
        return true;

    }

    @Override
    public void onFrameAvailable(VideoFrameData frameData) {
        videoRecoder.frameAvailable(frameData);
    }

    public void setProgressListener(OnRecordProgressListener listener){
        videoRecoder.setProgressListener(listener);
    }

    public void stopHandle() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        });
    }

    public void stop() {
        audioRecoder.stop();
        videoRecoder.stop();
    }

    @Override
    public void onRecordFinish(ClipInfo info) {
        if (info.getType() == VIDEO) {
            remainDuration -= info.getDuration();
        }

        if (mFinishListener!=null)
        {
            mFinishListener.onRecordFinish(info);
        }
    }

    public float getMaxDuration() {
        return maxDuration;
    }
}
