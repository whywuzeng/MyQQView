package com.utsoft.jan.myqqview.douyin.common.recoder.video;

import com.utsoft.jan.myqqview.douyin.common.preview.filter.ImageFilter;

/**
 * Created by Administrator on 2019/9/4.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder.video
 */
public class VideoFrameData {
    private ImageFilter mFilter;
    private float[] mMatrix;
    private long mTimeStamp;
    private int mTextureId;

    public VideoFrameData(ImageFilter mFilter, float[] mMatrix, long mTimeStamp, int mTextureId) {
        this.mFilter = mFilter;
        this.mMatrix = mMatrix;
        this.mTimeStamp = mTimeStamp;
        this.mTextureId = mTextureId;
    }

    public ImageFilter getFilter() {
        return mFilter;
    }

    public float[] getMatrix() {
        return mMatrix;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public int getTextureId() {
        return mTextureId;
    }
}
