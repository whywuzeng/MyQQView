package Jni;

import android.support.annotation.Keep;

import com.utsoft.jan.common.ffmpeg.VideoCmdCallback;
import com.utsoft.jan.common.utils.LogUtil;

/**
 * Created by Administrator on 2019/9/8.
 * <p>
 * by author wz
 * <p>
 * jni
 */
//不会被混淆
@Keep
public class FFmpegCmd {
    public static Object sLock = new Object();

    /**
     * 加载所有相关的库
     */
    static {
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("swresample");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("avfilter");
        System.loadLibrary("avdevice");
        System.loadLibrary("ffmpeg");
    }

    /**
     * 调用底层执行
     *
     * @param argc
     * @param argv
     */
    @Keep
    public static native int exec(int argc, String[] argv);

    @Keep
    public static native void exit();

    //唤醒这个锁
    @Keep
    public static void onExecuted(int ret) {
        synchronized (sLock) {
            //唤醒
            sLock.notify();
        }
    }

    @Keep
    public static void onProgress(float progress) {
        LogUtil.d("progress is " + progress);
    }

    /**
     * 执行ffmpeg命令
     */

    @Keep
    public static void exec(String[] cmd, VideoCmdCallback callback) {
        final long t1 = System.currentTimeMillis();
        int ret = exec(cmd.length, cmd);
        synchronized (sLock) {
            try {
                sLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (callback != null) {
            callback.onCommandFinish(ret == 0);
        }
        final long t2 = System.currentTimeMillis();

        LogUtil.e("ffmpeg" + cmd.toString() + "exec 执行的时间为" + (t2 - t1));

    }
}
