package com.utsoft.jan.myqqview.douyin.common.player;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;

import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.myqqview.douyin.common.C;

import java.io.IOException;

/**
 * Created by Administrator on 2019/9/11.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.player
 */
public class VideoPlayer {

    private MediaExtractor mediaExtractor;

    private String filePath;

    private int videoTrack = -1;
    private long duration;
    private MediaCodec decoder;
    private MediaCodec.BufferInfo bufferInfo;

    public VideoPlayer(String filePath) {
        this.filePath = filePath;
    }

    private void initDecoder(Surface surface){
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
        decoder.configure(format,surface,null,);

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
}
