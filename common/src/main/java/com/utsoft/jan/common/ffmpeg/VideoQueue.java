package com.utsoft.jan.common.ffmpeg;


import android.os.Handler;
import android.os.HandlerThread;

import Jni.FFmpegCmd;

/**
 * Created by Administrator on 2019/9/8.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.ffmpeg
 */
public class VideoQueue {
    //handle
    private Handler mHandler;

    //构造 就来用 handlethread getlooper
    public VideoQueue() {
        HandlerThread handlerThread = new HandlerThread("ffmpeg");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    //release 方法 释放handle
    public void release(){
        if (mHandler == null)
            return;

        mHandler.getLooper().quitSafely();
        mHandler =null;
    }

    //execCommand 执行ffmpeg方法
    public void execCommand(final String[] cmd, final VideoCmdCallback callback){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                FFmpegCmd.exec(cmd,callback);
            }
        });
    }

    public void execCommand(final String[] cmd){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                FFmpegCmd.exec(cmd,null);
            }
        });
    }
}
