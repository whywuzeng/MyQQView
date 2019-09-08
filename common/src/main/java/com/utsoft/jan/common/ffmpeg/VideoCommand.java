package com.utsoft.jan.common.ffmpeg;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.utsoft.jan.common.utils.FileUtils;
import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.common.utils.StorageUtil;
import com.utsoft.jan.common.utils.VideoUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/9/8.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.ffmpeg
 */
public class VideoCommand {

    public ArrayList<String> mContent = new ArrayList<>();

    //增加命令
    public VideoCommand append(String cmd) {
        mContent.add(cmd);
        return this;
    }

    //增加 long的
    public VideoCommand append(long cmd) {
        return append(String.valueOf(cmd));
    }

    //增加 int的
    public VideoCommand append(int cmd) {
        return append(String.valueOf(cmd));
    }

    //增加  float的
    public VideoCommand append(float cmd) {
        return append(String.valueOf(cmd));
    }

    //to string stirngbuilder
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String s : mContent) {
            stringBuilder.append(s).append(" ");
        }
        return stringBuilder.toString();
    }

    // toarray
    public String[] toArray() {
        final String[] array = new String[mContent.size()];
        mContent.toArray(array);
        LogUtil.d(toString());
        return array;
    }

    /**
     * 无损合并多个视频
     * <p>
     * 注意：此方法要求视频格式非常严格，需要合并的视频必须分辨率相同，帧率和码率也得相同
     */
    public static VideoCommand mergeVideo(List<String> videos, String output) {
        final String appDir = StorageUtil.getExternalStoragePath() + File.separator;
        final String fileName = "ffmpeg_concat.txt";
        FileUtils.writeTxtToFile(videos,appDir,fileName);
        VideoCommand cmd = new VideoCommand();
        cmd.append("ffmpeg").append("-y").append("-f").append("concat").append("-safe")
                .append("0").append("-i").append(appDir + fileName)
                .append("-c").append("copy").append("-threads").append("5").append(output);
        return cmd;
    }


    /**
     * 添加背景音乐
     *
     * @param inputVideo  视频文件
     * @param inputMusic  音频文件
     * @param output      输出路径
     * @param videoVolume 视频原声音音量(例:0.7为70%)
     * @param audioVolume 背景音乐音量(例:1.5为150%)
     */

    public static VideoCommand music(String inputVideo, String inputMusic, String output, float videoVolume, float audioVolume) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(inputVideo);
        } catch (IOException e) {
            return null;
        }
        int at = VideoUtil.selectAudioTrack(mediaExtractor);
        VideoCommand cmd = new VideoCommand();
        cmd.append("ffmpeg").append("-y").append("-i").append(inputVideo);
        if (at == -1) {
            int vt = VideoUtil.selectVideoTrack(mediaExtractor);
            float duration = (float) mediaExtractor.getTrackFormat(vt).getLong(MediaFormat.KEY_DURATION) / 1000 / 1000;
            cmd.append("-ss").append("0").append("-t").append(duration).append("-i").append(inputMusic).append("-acodec").append("copy").append("-vcodec").append("copy");
        } else {
            cmd.append("-i").append(inputMusic).append("-filter_complex")
                    .append("[0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + videoVolume + "[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + audioVolume + "[a1];[a0][a1]amix=inputs=2:duration=first[aout]")
                    .append("-map").append("[aout]").append("-ac").append("2").append("-c:v")
                    .append("copy").append("-map").append("0:v:0");
        }
        cmd.append(output);
        mediaExtractor.release();
        return cmd;
    }

    public static VideoCommand mergeVideoAudio(String inputVideo, String inputMusic, String output) {
        VideoCommand cmd = new VideoCommand();
        //        cmd.append("ffmpeg").append("-i").append(inputVideo).append("-i").append(inputMusic).
        //                append("-c").append("copy").append("-threads").append("5").append(output);
        //        "ffmpeg -i video.mp4 -i audio.aac -map 0:0 -map 1:0 -vcodec copy -acodec copy newvideo.mp4";
        //        cmd.append("ffmpeg").append("-i").append(inputVideo).append("-i").append(inputMusic).append("-map").append("0:0")
        //                .append("-map").append("1:0").append("-vcodec").append("copy").append("-acodec").append("copy").append(output);
        cmd.append("ffmpeg").append("-y").append("-i").append(inputMusic);
        float duration = VideoUtil.getDuration(inputMusic) / 1000000f;
        cmd.append("-ss").append("0").append("-t").append(duration).append("-i").
                append(inputVideo).
                append("-acodec").
                append("copy").
                append("-vcodec").
                append("copy");
        cmd.append(output);
        return cmd;
    }


}
