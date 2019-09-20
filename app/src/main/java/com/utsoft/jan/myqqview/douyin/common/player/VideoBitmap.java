package com.utsoft.jan.myqqview.douyin.common.player;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.os.Handler;

import com.utsoft.jan.common.utils.FileUtils;
import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.common.utils.ThreadUtil;
import com.utsoft.jan.myqqview.douyin.common.C;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/9/17.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.player
 */
public class VideoBitmap implements VideoImpl{

    private long duration;
    private Handler mHandler;
    private List<String> bitmaps = new ArrayList<>();


    @Override
    public MediaExtractor initMediaExtractor(String path) throws IOException {
        MediaExtractor extractor =null;
        extractor = new MediaExtractor();
        extractor.setDataSource(path);
        return extractor;
    }

    @Override
    public MediaFormat initMediaFormat(String path, MediaExtractor extractor) {
        final int videoTrack = initTrack(extractor);
        if (videoTrack<0)
        {
            throw new RuntimeException("No video track found in "+path);
        }
        extractor.selectTrack(videoTrack);
        final MediaFormat trackFormat = extractor.getTrackFormat(videoTrack);
        return trackFormat;
    }

    @Override
    public MediaCodec initMediaCodec(MediaFormat format) throws IOException {
        MediaCodec codec =null;

        final String MIME = format.getString(MediaFormat.KEY_MIME);
        codec = MediaCodec.createDecoderByType(MIME);
        //todo 这里decodeformat 是什么?
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,1);
        return codec;
    }

    @Override
    public int initTrack(MediaExtractor extractor) {
        final int trackCount = extractor.getTrackCount();
        int videoTrack = -1;
        for (int i = 0; i < trackCount; i++) {
            final MediaFormat trackFormat = extractor.getTrackFormat(i);
            final String mime = trackFormat.getString(MediaFormat.KEY_MIME);
            if (mime.equals(C.VideoParams.MIME_TYPE))
            {
                videoTrack = i;
                duration = trackFormat.containsKey(MediaFormat.KEY_DURATION) ? trackFormat.getLong(MediaFormat.KEY_DURATION) : 0;
                return videoTrack;
            }
        }
        return videoTrack;
    }

    @Override
    public Bitmap getBitmapBySec(MediaExtractor extractor, MediaFormat format, MediaCodec codec, long[] sec) {
        final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        //视频定位到上一帧
        extractor.seekTo(sec[0], MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

        long presentationTimeUs = 0L;
        boolean sawOutputEOS = false;
        boolean stopDecode = false;
        boolean sawInputEOS = false;
        final int width = format.getInteger(MediaFormat.KEY_WIDTH);
        final int height = format.getInteger(MediaFormat.KEY_HEIGHT);
        int outputBufferId;
        Image image = null;
        Bitmap bitmap = null;

        while (!sawOutputEOS&&!stopDecode){
            if (!sawInputEOS)
            {
                int inputBufferId = codec.dequeueInputBuffer(-1);
                if (inputBufferId >= 0) {
                    ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
                    int sampleSize = extractor.readSampleData(inputBuffer, 0);
                    if (sampleSize < 0) {
                        codec.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        sawInputEOS = true;
                    }else {
                        //获取定位的帧的时间
                        presentationTimeUs = extractor.getSampleTime();
                        //把定位的帧压入队列
                        codec.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0);
                        //跳到下一帧
                        extractor.advance();
                    }
                }

            }

            outputBufferId = codec.dequeueOutputBuffer(bufferInfo, C.BUFFER_TIME_OUT);
            if (outputBufferId>0)
            {
                boolean enablePresentation = false;
                LogUtil.e("presentationTimeUs:" + presentationTimeUs);
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    sawOutputEOS = true;
                    //这里回调
                    if (mBitmapsCallback!=null)
                    {
                        mBitmapsCallback.getBitmaps(bitmaps);
                    }
                }
                for (int i = 0; i < sec.length; i++) {
                    if (sec[i] != 0 && Math.abs(presentationTimeUs - sec[i]) < 567411) {
                        enablePresentation = true;
                        sec[i] = 0;
                        break;
                    }
                }
                //能够有效输出
                if (enablePresentation) {
                    //时间是指定时间或者已经是视频结束时间，停止循环
                    boolean doRender = (bufferInfo.size != 0);
                    if (doRender) {
                        //获取指定时间解码出来的Image对象。
                        image = codec.getOutputImage(outputBufferId);
                        //将Image转换成Bimap
                        YuvImage yuvImage = new YuvImage(YUV_420_888toNV21(image), ImageFormat.NV21, width, height, null);
                        //ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        final String pathFile = Environment.getExternalStorageDirectory().getPath() + File.separator + System.currentTimeMillis() + "temp.png";

                        FileUtils.createFile(pathFile);

                        FileOutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(pathFile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        final boolean isSuccessful = yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, outputStream);

                        //bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                        if (isSuccessful) {
                            LogUtil.e("保存成功了");
                        }
                        bitmaps.add(pathFile);

                        try {
                            //stream.close();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        image.close();
                    }
                }
                codec.releaseOutputBuffer(outputBufferId, true);
            }
        }

        return bitmap;
    }


    private static byte[] YUV_420_888toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        //if (VERBOSE) Log.v("YUV_420_888toNV21", "get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;

                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;

                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            // if (VERBOSE) Log.v("", "Finished reading data from plane " + i);
        }
        return data;
    }


    MediaExtractor extractor = null;
    MediaFormat mediaFormat = null;
    MediaCodec decoder = null;

    private String mPath;

    public VideoBitmap(String mPath) {
        this.mPath = mPath;
        mHandler = ThreadUtil.newHandlerThread("videobitmap");
    }

    public void initVideoBitmap() {

        try {
            if (extractor == null) {
                extractor = initMediaExtractor(this.mPath);
            }
            if (mediaFormat == null) {
                mediaFormat = initMediaFormat(this.mPath, extractor);
            }
            if (decoder == null) {
                decoder = initMediaCodec(mediaFormat);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //初始化解码配置
        decoder.configure(mediaFormat, null, null, 0);
        decoder.start();
        int second = (int) (duration / 1000000L);
        final long[] seconds = new long[second+1];
        seconds[0]=0;
        for (int i = 0; i < second; i++) {
            seconds[i+1] = (i+1)*888888L;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //开始解码
                getBitmapBySec(extractor, mediaFormat, decoder, seconds);
            }
        });
    }

    private OnBitmapsCallback mBitmapsCallback;

    public void setBitmapsCallback(OnBitmapsCallback mBitmapsCallback) {
        this.mBitmapsCallback = mBitmapsCallback;
    }

    public interface OnBitmapsCallback{
        void getBitmaps(List<String> bitmaps);
    }
}
