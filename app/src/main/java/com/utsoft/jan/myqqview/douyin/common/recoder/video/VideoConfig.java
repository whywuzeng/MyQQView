package com.utsoft.jan.myqqview.douyin.common.recoder.video;


import android.opengl.EGLContext;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder.video
 */
public class VideoConfig {

    private EGLContext context;

    private int mVideoWidth;

    private int mVideoHeight;

    private int mBitRate;

    private String fileName;

    private float factor;

    private long maxDuration;

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public VideoConfig(EGLContext context, int mVideoWidth, int mVideoHeight, int mBitRate, String fileName) {
        this.context = context;
        this.mVideoWidth = mVideoWidth;
        this.mVideoHeight = mVideoHeight;
        this.mBitRate = mBitRate;
        this.fileName = fileName;
    }

    public EGLContext getContext() {
        return context;
    }

    public int getmVideoWidth() {
        return mVideoWidth;
    }

    public int getmVideoHeight() {
        return mVideoHeight;
    }

    public int getmBitRate() {
        return mBitRate;
    }

    public String getFileName() {
        return fileName;
    }

    public float getFactor() {
        return factor;
    }

    public long getMaxDuration() {
        return maxDuration;
    }
}
