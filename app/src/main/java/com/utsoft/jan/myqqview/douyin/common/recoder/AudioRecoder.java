package com.utsoft.jan.myqqview.douyin.common.recoder;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Handler;

import com.utsoft.jan.common.utils.FileUtils;
import com.utsoft.jan.common.utils.ThreadUtil;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.recoder.audio.AudioConfig;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.utsoft.jan.myqqview.douyin.common.C.BUFFER_TIME_OUT;


/**
 * Created by Administrator on 2019/9/6.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder
 */
public class AudioRecoder implements Recoder<AudioConfig>, Runnable {

    private OnRecordFinishListener recordListener;

    private AudioRecord mAudioRecord;
    private AudioConfig mAudioConfig;
    private MediaCodec encoder;
    private MediaCodec.BufferInfo bufferInfo;
    private boolean isStarted;
    private BufferedOutputStream bufferedOutputStream;
    private Handler recordHandler;
    private Handler encodeHandler;
    private long maxDuration;
    private int duration;
    private float preFrameDuration;

    public AudioRecoder() {

    }

    @Override
    public int getDataType() {
        return C.AUDIO;
    }

    @Override
    public void setOnRecordFinishListener(OnRecordFinishListener mListener) {
        this.recordListener = mListener;
    }

    @Override
    public void configure(AudioConfig config) {
        this.mAudioConfig = config;

        final int minBufferSize = AudioRecord.getMinBufferSize(config.getSampleRate(), C.AudioParams.CHANNEL, C.AudioParams.BITS_PER_SAMPLE);

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                C.AudioParams.SAMPLE_RATE, C.AudioParams.CHANNEL, C.AudioParams.BITS_PER_SAMPLE, minBufferSize);

        recordHandler = ThreadUtil.newHandlerThread("record");
        encodeHandler = ThreadUtil.newHandlerThread("encode");
        maxDuration = config.getMaxDuration();
        duration = 0;
        preFrameDuration = C.AudioParams.framePreTime;
    }

    private void loop() {
        recordHandler.post(this);
    }

    @Override
    public void start() {
        mAudioRecord.startRecording();
        encoder.start();

        loop();
        isStarted = true;
    }

    @Override
    public void stop() {
        if (recordHandler == null)
        {
            return;
        }

        if (!isStarted)
        {
            return;
        }

        isStarted = false;
        //结束录制线程 和 audioRecord
        recordHandler.post(new Runnable() {
            @Override
            public void run() {
                onStop();
            }
        });
    }

    private void onStop() {
        if (mAudioRecord == null)
        {
            return;
        }
        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord =null;
    }

    @Override
    public void prepareCodec() throws IOException {
        final MediaFormat audioFormat = MediaFormat.createAudioFormat(C.AudioParams.MIME_TYPE, this.mAudioConfig.getSampleRate(), C.AudioParams.CHANNEL_COUNT);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, C.AudioParams.CHANNEL);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, C.AudioParams.BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, C.AudioParams.CHANNEL_COUNT);
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 4);

        encoder = MediaCodec.createEncoderByType(C.AudioParams.MIME_TYPE);

        encoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        //bufferInfo = new MediaCodec.BufferInfo();

        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(mAudioConfig.getFileName()));

    }

    @Override
    public void shutdown() {
        if (recordHandler != null) {
            recordHandler.getLooper().quitSafely();
        }

        if (encodeHandler != null) {
            encodeHandler.getLooper().quitSafely();
        }
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
    //结束编码任务
    private void stopEncode(){
        encodeHandler.post(new Runnable() {
            @Override
            public void run() {
                if (encoder == null)
                {
                    return;
                }
                encoder.stop();
                encoder.release();
                encoder = null;
                FileUtils.closeSafely(bufferedOutputStream);
                bufferedOutputStream = null;
                if (recordListener!=null){
                    //模拟 duration 大小
                    recordListener.onRecordFinish(new ClipInfo(mAudioConfig.getFileName(),100000,getDataType()));
                }

                encodeHandler.getLooper().quitSafely();
                encodeHandler =null;
            }
        });
    }

    //录制线程
    @Override
    public void run() {
        //判断是否为空  一旦record停了 ，编码线程也要跟着停掉
        if (mAudioRecord == null) {
            //结束录制音频
            stopEncode();
            recordHandler.getLooper().quitSafely();
            recordHandler =null;
            return;
        }
        //要多大byte数组 怎么计算的 字节, 每帧20ms数据
        int length = (int) ((C.AudioParams.SAMPLE_RATE * 0.02 * 16) / 8);
        byte[] buffer = new byte[length];
        final int bytes = mAudioRecord.read(buffer, 0, length);
        if (bytes > 0) {
            encode(buffer, bytes);
        }
        loop();
    }

    private void encode(final byte[] buffer, final int bytes) {
        encodeHandler.post(new Runnable() {
            @Override
            public void run() {
                onEncode(buffer, bytes);
                drain();
            }
        });
    }

    private long getTimeUs() {
        return System.nanoTime() / 1000L;
    }

    private void onEncode(byte[] buffer, int bytes) {
        final ByteBuffer[] inputBuffers = encoder.getInputBuffers();
        while (true) {
            final int inputBufferIndex = encoder.dequeueInputBuffer(BUFFER_TIME_OUT);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.position(0);
                if (buffer != null) {
                    inputBuffer.put(buffer, 0, bytes);
                }

                if (bytes <= 0) {
                    encoder.queueInputBuffer(inputBufferIndex, 0, 0, getTimeUs(),
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                }
                else {
                    encoder.queueInputBuffer(inputBufferIndex, 0, bytes,
                            getTimeUs(), 0);
                }
                break;
            }
        }
    }

    private void drain() {
        bufferInfo = new MediaCodec.BufferInfo();
        final ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        int encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, C.BUFFER_TIME_OUT);
        while (encoderStatus >= 0) {
            final ByteBuffer ecoderData = encoderOutputBuffers[encoderStatus];
            final int outSize = bufferInfo.size;
            ecoderData.position(bufferInfo.offset);
            ecoderData.limit(bufferInfo.offset+outSize);
            final byte[] data = new byte[outSize + 7];
            addADTSHeader(data,outSize+7);
            ecoderData.get(data,7,outSize);

            try {
                bufferedOutputStream.write(data,0,data.length);
                bufferedOutputStream.flush();
                //时间累积加
                duration+=preFrameDuration;
            } catch (IOException e) {
                e.printStackTrace();

            }
            if (duration>=maxDuration)
            {
                stop();
            }
            //释放 outputbuffer 状态
            encoder.releaseOutputBuffer(encoderStatus,false);
            //重新取这个 outputbuffer 状态
            encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, C.BUFFER_TIME_OUT);
        }
    }

    private void addADTSHeader(byte[] packet, int length) {
        int profile = 2; // AAC LC
        int freqIdx = 4; // 44.1KHz
        int chanCfg = 1; // CPE
        // fill in A D T S data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (length >> 11));
        packet[4] = (byte) ((length & 0x7FF) >> 3);
        packet[5] = (byte) (((length & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}
