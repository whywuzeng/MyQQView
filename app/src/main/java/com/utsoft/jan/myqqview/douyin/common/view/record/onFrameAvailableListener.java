package com.utsoft.jan.myqqview.douyin.common.view.record;

import com.utsoft.jan.myqqview.douyin.common.recoder.video.VideoFrameData;

/**
 * Created by Administrator on 2019/9/4.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.record
 */
public interface onFrameAvailableListener {
    void onFrameAvailable(VideoFrameData frameData);
}
