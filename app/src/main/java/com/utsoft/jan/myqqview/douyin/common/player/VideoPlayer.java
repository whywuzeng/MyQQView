package com.utsoft.jan.myqqview.douyin.common.player;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;
import android.view.Surface;

import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.common.utils.ThreadUtil;
import com.utsoft.jan.myqqview.douyin.common.C;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaExtractor.SEEK_TO_CLOSEST_SYNC;

/**
 * Created by Administrator on 2019/9/11.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.player
 */
public class VideoPlayer implements PlayerImpl{

    private final Handler mhandle;
    private MediaExtractor mediaExtractor;

    private String filePath;

    private int videoTrack = -1;

    public long getDuration() {
        return duration;
    }

    private long duration;
    private MediaCodec decoder;
    private MediaCodec.BufferInfo bufferInfo;
    private boolean stopped;
    //当前视频播放的时间
    private long timeLine;
    //上一帧图像的时间
    private long lastSampleTime;
    //是否在消费
    private boolean consumed;

    private onPlayerProgressListener listener;

    public void setProgressListener(onPlayerProgressListener listener) {
        this.listener = listener;
    }

    public VideoPlayer(String filePath) {
        this.filePath = filePath;
        mhandle = ThreadUtil.newHandlerThread("player");
    }

    public void initDecoder(Surface surface){
        mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(filePath);
            initTrack();
            //解码器
            decoder = MediaCodec.createDecoderByType(C.VideoParams.MIME_TYPE);
        } catch (IOException e) {
            return;
        }
        bufferInfo = new MediaCodec.BufferInfo();
        final MediaFormat format = mediaExtractor.getTrackFormat(videoTrack);
        decoder.configure(format,surface,null,0);
        mediaExtractor.selectTrack(videoTrack);
        decoder.start();
        stopped = false;
    }

    private void initTrack() {
        final int numTracks = mediaExtractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            final MediaFormat format = mediaExtractor.getTrackFormat(i);
            final String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.equals(C.VideoParams.MIME_TYPE))
            {
                videoTrack = i;
                duration = format.containsKey(MediaFormat.KEY_DURATION) ? format.getLong(MediaFormat.KEY_DURATION) : 0;

            }
        }
        if (videoTrack == -1)
        {
            LogUtil.e("initTrack Error videoTrack"+videoTrack);
        }
    }

    @Override
    public void start() {
        stopped = false;
        batch();
    }

    private void batch() {
        if (stopped)
        {
            timeLine = 0;
            return;
        }
        //这个提取器一直是当前帧

        final long sampleTime = getNextSampleTime();
        consumeFrame(sampleTime,true);
        mhandle.post(new Runnable() {
            @Override
            public void run() {
                batch();
            }
        });
    }

    private long getNextSampleTime() {
        if (consumed)
        {
            consumed = false;
            mediaExtractor.advance();
        }
        return mediaExtractor.getSampleTime();
    }

    private void consumeFrame(long sampleTime, boolean shouldUpdateProgress) {
        long t = System.nanoTime() / 1000L;
        long duration = t - timeLine;
        //这个逻辑是什么意思
        if(timeLine!=0){
            if (lastSampleTime + duration < sampleTime) {
                return;
            }
        }

        final ByteBuffer[] inputBuffers = decoder.getInputBuffers();
        final int dequeueInputBufferIndex = decoder.dequeueInputBuffer(C.BUFFER_TIME_OUT);
        if (dequeueInputBufferIndex > 0) {
            final ByteBuffer inputBuffer = inputBuffers[dequeueInputBufferIndex];
            final int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
            if (sampleSize <= 0) {
                //没数据sample样式
                lastSampleTime = 0;
                timeLine = 0;
                mediaExtractor.seekTo(0, SEEK_TO_CLOSEST_SYNC);
                decoder.flush();
                return;
            }

            decoder.queueInputBuffer(dequeueInputBufferIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);

            consumed = true;
            timeLine = t;
            lastSampleTime = sampleTime;
            //output release资源
            while (true) {
                final int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, C.BUFFER_TIME_OUT);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED
                        || outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER
                        || outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    continue;
                }
                //这里只是渲染一次
                decoder.releaseOutputBuffer(outputBufferIndex, true);
                //这里渲染成功!
                if (listener != null&&shouldUpdateProgress){
                    listener.onPlayerProgress(sampleTime);
                }
                break;
            }

        }
    }

    @Override
    public void pause() {
        mhandle.post(new Runnable() {
            @Override
            public void run() {
                timeLine = 0;
                stopped = true;
            }
        });
    }

    @Override
    public void stop() {
        mhandle.post(new Runnable() {
            @Override
            public void run() {
                stopped = true;
                timeLine = 0;
                decoder.stop();
                decoder.release();
                decoder = null;
                mediaExtractor.release();
                mediaExtractor = null;
                mhandle.getLooper().quitSafely();
            }
        });
    }

    @Override
    public void seekTo(final long timeUs) {
        mhandle.post(new Runnable() {
            @Override
            public void run() {
                //这里已经跳到这一帧了。
                mediaExtractor.seekTo(timeUs,MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                timeLine = 0;
                consumed = false;
                consumeFrame(getNextSampleTime(),false);
            }
        });
    }

    @Override
    public void resume() {
        mhandle.post(new Runnable() {
            @Override
            public void run() {
                timeLine = 0;
                stopped = false;
                consumed = true;
                batch();
            }
        });
    }

    public interface onPlayerProgressListener {
        void onPlayerProgress(long sampleTime);
    }
}
