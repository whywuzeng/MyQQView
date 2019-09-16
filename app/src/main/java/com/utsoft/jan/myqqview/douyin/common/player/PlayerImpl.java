package com.utsoft.jan.myqqview.douyin.common.player;

/**
 * Created by Administrator on 2019/9/15.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.player
 */
public interface PlayerImpl {
    void start();
    void pause();
    void stop();
    void seekTo(long timeUs);
    void resume();
}
