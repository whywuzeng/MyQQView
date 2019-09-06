package com.utsoft.jan.myqqview.douyin.common.recoder.audio;

import com.utsoft.jan.myqqview.douyin.common.C;

/**
 * Created by Administrator on 2019/9/6.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder.audio
 */
public class AudioConfig {
    //采样率
    private int sampleRate;

    //采样率每帧
    private int samplePerFrame;

    private @C.SpeedMode int speedMode;

    private String fileName;

    private long maxDuration;

    public AudioConfig(int sampleRate, int samplePerFrame, String fileName) {
        this.sampleRate = sampleRate;
        this.samplePerFrame = samplePerFrame;
        this.fileName = fileName;
    }

    public int getSpeedMode() {
        return speedMode;
    }

    public void setSpeedMode(int speedMode) {
        this.speedMode = speedMode;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getSamplePerFrame() {
        return samplePerFrame;
    }

    public String getFileName() {
        return fileName;
    }
}
