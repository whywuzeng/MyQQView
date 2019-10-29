package com.utsoft.jan.mp3encoder;

/**
 * Created by Administrator on 2019/10/29.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.mp3encoder
 */
public class Mp3Encoder {

    static {
        System.loadLibrary("mp3encoder");
    }

    public static native int init(String pcmPath, int audioChannels, int bitRate, int sampleRate, String mp3Path);
    public static native void encode();
    public static native void destroy();

}
