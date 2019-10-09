package com.utsoft.jan.myqqview.douyin.common.recoder.video;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;

import com.utsoft.jan.common.utils.ThreadUtil;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.recoder.OnRecordFinishListener;
import com.utsoft.jan.myqqview.douyin.common.recoder.Recoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private OutputSurface outputSurface;
    private InputSurface inputSurface;

    MediaMuxer mMediaMuxer;
    //
    private int muxVideoTrack = -1;

    //防止开头的编码不正确
    boolean muxStarted = false;
    final Object lock = new Object();

    static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void init(String dataSource, String outputSource) throws IOException {
        mHandler = ThreadUtil.newHandlerThread("DecodeAndEncoder");
        this.mDataSource = dataSource;
        mMediaMuxer = new MediaMuxer(outputSource, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        decoderByType = MediaCodec.createDecoderByType(C.VideoParams.MIME_TYPE);
        //编码
        encoderByType = MediaCodec.createEncoderByType(C.VideoParams.MIME_TYPE);

        //初始化解码
        mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(mDataSource);
        initTrack();

    }


    private void startMux(MediaFormat mediaFormat, int flag) {
        if (flag == 0) {
            muxVideoTrack = mMediaMuxer.addTrack(mediaFormat);
        }
        synchronized (lock) {
            if (muxVideoTrack != -1 && !muxStarted) {
                mMediaMuxer.start();
                muxStarted = true;
                lock.notify();
            }
        }
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
        //mHandler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        startEncode();
        //    }
        //});

        executorService.execute(videoCliper);
    }

    private Runnable videoCliper = new Runnable() {
        @Override
        public void run() {
            mediaExtractor.selectTrack(mtrackIndex);
            prepareCodec();
            startEncode();
        }
    };

    private void startEncode() {
        //这个作用
        startEncodeAndDecode();
    }

    //开始编解码
    private void startEncodeAndDecode() {
        ByteBuffer[] decoderBuffers = decoderByType.getInputBuffers();
        ByteBuffer[] encoderBuffers = encoderByType.getOutputBuffers();

        //解码
        final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        //编码用
        final MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        //先解码
        boolean isDone = false;
        boolean isInputDone = false; //解码标识位
        boolean decodeDone = false; //解码结束标识位
        boolean isOutputDone = false; // 编码标识位

        while (!isDone) {
            if (!isInputDone) {
                final int inputIndex = decoderByType.dequeueInputBuffer(C.BUFFER_TIME_OUT);
                int sampleSize = 0;
                if (inputIndex > 0) {
                    final ByteBuffer decoderBuffer = decoderBuffers[inputIndex];
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

            if (!decodeDone) {

                final int OutputBufIndex = decoderByType.dequeueOutputBuffer(bufferInfo, C.BUFFER_TIME_OUT);
                if (OutputBufIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED
                        || OutputBufIndex == MediaCodec.INFO_TRY_AGAIN_LATER
                        || OutputBufIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED
                        || OutputBufIndex < 0) {

                }
                else {

                    final boolean doRender = bufferInfo.size != 0;

                    decoderByType.releaseOutputBuffer(OutputBufIndex, doRender);

                    if (doRender) {

                        //这里来绘制。inputsurface 和  outputface传递
                        outputSurface.awaitNewImage();

                        //手动调用ondrawframe 取消阻塞
                        outputSurface.drawImage(bufferInfo.presentationTimeUs / 1000);

                        inputSurface.setPresentationTime(bufferInfo.presentationTimeUs * 1000);

                        //传送图像数据给 surface
                        inputSurface.swapBuf();
                    }

                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        //通知编码结束
                        encoderByType.signalEndOfInputStream();
                        //结束这个
                        decodeDone = true;
                    }
                }
            }

            //编码可行性
            while (true) {
                final int encodeStatus = encoderByType.dequeueOutputBuffer(info, C.BUFFER_TIME_OUT);
                if (encodeStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    break;
                }
                else if (encodeStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    encoderBuffers = encoderByType.getOutputBuffers();
                }
                else if (encodeStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    final MediaFormat outputFormat = encoderByType.getOutputFormat();
                    startMux(outputFormat, 0);
                }
                else if (encodeStatus < 0) {
                }
                else {
                    final ByteBuffer encoderData = encoderBuffers[encodeStatus];

                    if (encoderData == null) {
                        throw new RuntimeException("encoderData " + encodeStatus +
                                " was null");
                    }

                    if ((info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        info.size = 0;
                    }

                    isDone = (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (isDone) {
                        break;
                    }

                    if (info.presentationTimeUs == 0 && !isDone) {
                        continue;
                    }

                    if (info.size != 0) {
                        encoderData.position(info.offset);
                        encoderData.limit(info.offset + info.size);
                        if (!muxStarted) {
                            synchronized (lock) {
                                if (!muxStarted) {
                                    try {
                                        lock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        //LogUtil.d("startEncodeAndDecode: " + info.presentationTimeUs);
                        mMediaMuxer.writeSampleData(muxVideoTrack, encoderData, info);
                    }
                    encoderByType.releaseOutputBuffer(encodeStatus, false);
                }

            }

        }



    }

    @Override
    public void stop() {
        mediaExtractor.release();

        mMediaMuxer.stop();
        mMediaMuxer.release();

        if (inputSurface != null) {
            inputSurface.release();
        }

        decoderByType.stop();
        decoderByType.release();
        encoderByType.stop();
        encoderByType.release();
    }

    @Override
    public void prepareCodec() {
        //初始化  编码
        final MediaFormat videoFormat = MediaFormat.createVideoFormat(C.VideoParams.MIME_TYPE, mConfig.getmVideoWidth(), mConfig.getmVideoHeight());
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        //采样率
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mConfig.getmBitRate());
        //帧率
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, C.VideoParams.SAMPLE_RATE);
        //todo what
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, C.VideoParams.I_FRAME_INTERVAL);

        encoderByType.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        //解码
        final MediaFormat videoDecoderFormat = mediaExtractor.getTrackFormat(mtrackIndex);

        final int videoWidth = videoDecoderFormat.getInteger(MediaFormat.KEY_WIDTH);
        final int videoHeight = videoDecoderFormat.getInteger(MediaFormat.KEY_HEIGHT);

        outputSurface = new OutputSurface();
        outputSurface.setup(videoWidth, videoHeight);
        //取得outputsurface
        decoderByType.configure(videoDecoderFormat, outputSurface.getSurface(), null, 0);

        //初始化inputsurface
        inputSurface = new InputSurface(encoderByType.createInputSurface());
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
