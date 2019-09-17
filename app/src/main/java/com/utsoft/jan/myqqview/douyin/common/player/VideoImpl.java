package com.utsoft.jan.myqqview.douyin.common.player;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2019/9/17.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.player
 */
public interface VideoImpl {

    MediaExtractor initMediaExtractor(File path) throws IOException;

    MediaFormat initMediaFormat(String path,MediaExtractor extractor);

    MediaCodec initMediaCodec(MediaFormat format) throws IOException;

    int initTrack(MediaExtractor extractor);

    //编译出图片
    Bitmap getBitmapBySec(MediaExtractor extractor,MediaFormat format,MediaCodec codec,long sec);

}
