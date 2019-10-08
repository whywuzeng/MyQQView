package com.utsoft.jan.myqqview.douyin.common.recoder.video;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;

import com.utsoft.jan.common.utils.ThreadUtil;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.recoder.OnRecordFinishListener;
import com.utsoft.jan.myqqview.douyin.common.recoder.Recoder;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2019/9/27.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder.video
 */
public class VideoCipple implements Recoder<VideoConfig> {


    private MediaCodec decoderByType;
    private MediaCodec encoderByType;
    private VideoConfig mConfig;
    private String mDataSource;
    private MediaExtractor mediaExtractor;
    private int mtrackIndex;
    private long videoDuration;
    private Handler mHandler;

    public void init() throws IOException {
        mHandler = ThreadUtil.newHandlerThread("DecodeAndEncoder");
    }


    @Override
    public int getDataType() {

        return C.VIDEO;
    }

    @Override
    public void setOnRecordFinishListener(OnRecordFinishListener mListener) {

    }

    @Override
    public void configure(VideoConfig config) {
        this.mConfig = config;

    }

    @Override
    public void start() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startEncodeAndDecode();
            }
        });
    }

    //开始编解码
    private void startEncodeAndDecode() {
        final ByteBuffer[] decoderBuffers = decoderByType.getInputBuffers();
        final ByteBuffer[] encoderBuffers = encoderByType.getInputBuffers();

        //解码
        final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        //编码用
        final MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        //先解码
        boolean isDone = false;
        boolean isInputDone = false; //解码标识位
        boolean isOutputDone = false; // 编码标识位

        while (!isDone) {
            if (!isInputDone) {
                final int inputIndex = decoderByType.dequeueInputBuffer(C.BUFFER_TIME_OUT);
                int sampleSize = 0;
                if (inputIndex > 0) {
                    final ByteBuffer decoderBuffer = decoderBuffers[inputIndex];
                    decoderBuffer.clear();
                    sampleSize = mediaExtractor.readSampleData(decoderBuffer, 0);
                    if (sampleSize >= 0) {
                        //根据时间来判断是否要 装数据.这里如果不根据呢
                        decoderByType.queueInputBuffer(inputIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                        mediaExtractor.advance();
                    }
                    else {
                        decoderByType.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isInputDone = true;
                    }
                }

            }

                if (!isOutputDone)
                {

                    final int OutputBufIndex = decoderByType.dequeueOutputBuffer(bufferInfo, C.BUFFER_TIME_OUT);
                    if (OutputBufIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED
                            || OutputBufIndex == MediaCodec.INFO_TRY_AGAIN_LATER
                            || OutputBufIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        continue;
                    }else {
                        decoderByType.releaseOutputBuffer(OutputBufIndex,true);
                        //结束这个
                        isInputDone = true;

                        //这里来绘制。inputsurface 和  outputface传递

                    }

                }

                //

            }



    }

    @Override
    public void stop() {

    }

    @Override
    public void prepareCodec() throws IOException {
        //初始化  编码
        final MediaFormat videoFormat = MediaFormat.createVideoFormat(C.VideoParams.MIME_TYPE, mConfig.getmVideoWidth(), mConfig.getmVideoHeight());
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //采样率
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mConfig.getmBitRate());
        //帧率
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, C.VideoParams.SAMPLE_RATE);
        //todo what
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, C.VideoParams.I_FRAME_INTERVAL);

        //编码
        encoderByType = MediaCodec.createEncoderByType(C.VideoParams.MIME_TYPE);
        encoderByType.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);


        //初始化解码
        mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(mDataSource);
        initTrack();

        //解码
        decoderByType = MediaCodec.createDecoderByType(C.VideoParams.MIME_TYPE);
        final MediaFormat videoDecoderFormat = mediaExtractor.getTrackFormat(mtrackIndex);

        final OutputSurface outputSurface = new OutputSurface();
        //取得outputsurface
        decoderByType.configure(videoDecoderFormat, outputSurface.getSurface(), null, 0);

        //初始化inputsurface
        final InputSurface inputSurface = new InputSurface(encoderByType.createInputSurface());
        inputSurface.MCurrent();

        decoderByType.start();
        encoderByType.start();
    }

    //拉取视频轨道信息
    private void initTrack() {
        final int trackCount = mediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            final MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
            final String formatstring = trackFormat.getString(MediaFormat.KEY_MIME);
            if (formatstring.equals(C.VideoParams.MIME_TYPE)) {
                mtrackIndex = i;
                videoDuration = trackFormat.containsKey(MediaFormat.KEY_DURATION) ? trackFormat.getLong(MediaFormat.KEY_DURATION) : 0;

            }
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
