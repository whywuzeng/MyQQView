package com.utsoft.jan.myqqview.douyin.common.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.utsoft.jan.myqqview.douyin.common.C;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2019/9/17.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.player
 */
public class VideoBitmap implements VideoImpl{

    private long duration;

    @Override
    public MediaExtractor initMediaExtractor(File path) throws IOException {
        MediaExtractor extractor =null;
        extractor = new MediaExtractor();
        extractor.setDataSource(path.toString());
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
    public Bitmap getBitmapBySec(MediaExtractor extractor, MediaFormat format, MediaCodec codec, long sec) {
        final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        //视频定位到上一帧
        extractor.seekTo(sec,MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

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
                //能够有效输出
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0 | presentationTimeUs >= sec) {
                    //时间是指定时间或者已经是视频结束时间，停止循环
                    sawOutputEOS = true;
                    boolean doRender = (bufferInfo.size != 0);
                    if (doRender) {
                        //获取指定时间解码出来的Image对象。
                        image = codec.getOutputImage(outputBufferId);
                        //将Image转换成Bimap
                        YuvImage yuvImage = new YuvImage(YUV_420_888toNV21(image), ImageFormat.NV21, width, height, null);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
                        bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                        try {
                            stream.close();
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

}
