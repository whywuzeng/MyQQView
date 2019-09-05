package com.utsoft.jan.myqqview.douyin.common.recoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.Handler;
import android.view.Surface;

import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.common.utils.ThreadUtil;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.recoder.video.OffScreenWrapper;
import com.utsoft.jan.myqqview.douyin.common.recoder.video.VideoConfig;
import com.utsoft.jan.myqqview.douyin.common.recoder.video.VideoFrameData;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder
 */
public class VideoRecoder implements Recoder<VideoConfig> {

    private OnRecordFinishListener mListener;
    private VideoConfig configuration;
    private Handler handler;
    private float factor;
    private long duration;
    private long startTimeStamp;
    private boolean started = false;

    private MediaCodec codec;
    private Surface inputSurface;
    private EGLContext glContext;
    private MediaMuxer muxer;
    private OffScreenWrapper offScreen;
    private int track;
    private long mLastTimeStamp;

    public void setProgressListener(OnRecordProgressListener mProgressListener) {
        this.mProgressListener = mProgressListener;
    }

    private OnRecordProgressListener mProgressListener;

    @Override
    public int getDataType() {

        return C.VIDEO;
    }

    @Override
    public void setOnRecordFinishListener(OnRecordFinishListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void configure(VideoConfig config) {
        this.configuration = config;
        handler = ThreadUtil.newHandlerThread("video");
        factor = configuration.getFactor();
        duration = 0;
        startTimeStamp = 0;
    }

    @Override
    public void start() {
        if (started) {
            throw new RuntimeException("videoRecoder is running time");
        }

        if (codec == null) {
            return;
        }

        started = true;

        //延时操作
        handler.post(new Runnable() {
            @Override
            public void run() {
                onStart();
            }
        });
    }

    //延时 start
    private void onStart() {
        offScreen = new OffScreenWrapper(glContext,inputSurface);
        codec.start();
    }

    @Override
    public void stop() {
        if (!started) {
            return;
        }
        started = false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                onStop();
            }
        });
    }

    private void onStop() {
        LogUtil.d("video stop");
        drain(true);
        codec.stop();
        codec.release();
        codec = null;
        muxer.stop();
        muxer.release();
        muxer = null;
        if (mListener != null) {
            mListener.onRecordFinish(new ClipInfo(configuration.getFileName(), duration, getDataType()));
        }
        offScreen.release();
        offScreen = null;
        inputSurface = null;
        glContext = null;
        configuration = null;
        handler.getLooper().quitSafely();
        handler = null;
    }

    @Override
    public void prepareCodec() throws IOException {
        //创建MediaFormat MIME_TYPE = "video/avc"
        final MediaFormat format = MediaFormat.createVideoFormat(C.VideoParams.MIME_TYPE, configuration.getmVideoWidth(), configuration.getmVideoHeight());

        //设置参数
        //这个ColorFormat很重要，这里一定要设置COLOR_FormatSurface
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, configuration.getmBitRate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, C.VideoParams.SAMPLE_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, C.VideoParams.I_FRAME_INTERVAL);

        codec = MediaCodec.createEncoderByType(C.VideoParams.MIME_TYPE);
        //FLAG_ENCODE表示这个是一个编码器
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        //得到对应InputSureface
        inputSurface = codec.createInputSurface();
        glContext = configuration.getContext();

        muxer = new MediaMuxer(configuration.getFileName(),
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    @Override
    public void shutdown() {

        if (handler!=null)
        {
            handler.getLooper().quitSafely();
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public void frameAvailable(final VideoFrameData data){
        if (started)
        {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                onFrameAvailable(data);
            }
        });
    }

    private void onFrameAvailable(VideoFrameData data) {
        if (offScreen == null)
            return;

        offScreen.draw(data.getFilter(),data.getMatrix(),data.getTextureId(),data.getTimeStamp());

        drain(false);
    }

    private void drain(boolean endOfStream) {

        //如果通知编码器结束，就会signalEndOfInputStream
        if (endOfStream) {
            codec.signalEndOfInputStream();
        }

        //得到outputBuffer
        ByteBuffer[] encoderOutputBuffers = codec.getOutputBuffers();

        while (true) {
            //MediaCodec的BufferInfo的缓存。通过这个BufferInfo不断的运输数据。（原始=>编码后的
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            //上面换成的BufferInfo，送入到Encoder中，去查询状态
            int encoderStatus = codec.dequeueOutputBuffer(bufferInfo, C.BUFFER_TIME_OUT);

            //如果时继续等待，就暂时不用处理。大多数情况，都是从这儿跳出循环
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                //没有数据
                if (!endOfStream) {
                    break;
                }
                //outputBuffer发生变化了。就重新去获取
            }else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = codec.getOutputBuffers();
                //格式发生变化。这个第一次configure之后也会调用一次。在这里进行muxer的初始化
            }else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = codec.getOutputFormat();
                track = muxer.addTrack(newFormat);
                muxer.start();
            }else if (encoderStatus < 0) {
                LogUtil.e("unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
            }else {
                //写入数据
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0;
                }

                if (bufferInfo.size != 0) {
                    adaptTimeUs(bufferInfo);
                    //切到对应的位置，进行书写
                    encodedData.position(bufferInfo.offset);
                    encodedData.limit(bufferInfo.offset + bufferInfo.size);
                    //写入
                    muxer.writeSampleData(track, encodedData, bufferInfo);
                    if (mProgressListener != null) {
                        mProgressListener.onRecordProgress(duration);
                    }
                    if (duration >= configuration.getMaxDuration()) {
                        stop();
                    }
                }
                codec.releaseOutputBuffer(encoderStatus, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    //到达最后了，就跳出循环
                    break;
                }
            }
        }
    }

    private void adaptTimeUs(MediaCodec.BufferInfo info) {
        info.presentationTimeUs = (long) (info.presentationTimeUs / factor);
        if (startTimeStamp == 0) {
            startTimeStamp = info.presentationTimeUs;
        } else {
            duration = info.presentationTimeUs - startTimeStamp;
        }
        //        //偶现时间戳错乱，这里做个保护，假设一秒30帧
        //        if (info.presentationTimeUs <= mLastTimeStamp) {
        //            info.presentationTimeUs = (long) (mLastTimeStamp + C.SECOND_IN_US / 30 / factor);
        //        }
        mLastTimeStamp = info.presentationTimeUs;
    }
}
