package com.utsoft.jan.myqqview.douyin.effect.persenter;

import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.view.Surface;

import com.utsoft.jan.common.factory.presenter.BasePresenter;
import com.utsoft.jan.common.utils.MutilBitmapUtils;
import com.utsoft.jan.common.widget.Imageview.StickView;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.player.VideoBitmap;
import com.utsoft.jan.myqqview.douyin.common.player.VideoPlayer;

import java.util.List;

/**
 * Created by Administrator on 2019/9/16.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.effect.persenter
 */
public class AfterEffectPresenter extends BasePresenter<AfterEffectContract.View> implements AfterEffectContract.Persenter, VideoPlayer.onPlayerProgressListener {

    private VideoPlayer mVideoPlayer;
    private VideoBitmap videoBitmap;

    public AfterEffectPresenter(AfterEffectContract.View mView,String filePath) {
        super(mView);
        mVideoPlayer = new VideoPlayer(filePath);
        videoBitmap = new VideoBitmap(filePath);
        initBitmapVideo();
        mVideoPlayer.setProgressListener(this);
    }

    private void initBitmapVideo() {
        videoBitmap.initVideoBitmap();
        videoBitmap.setBitmapsCallback(new VideoBitmap.OnBitmapsCallback() {
            @Override
            public void getBitmaps(List<String> bitmaps) {
                final Drawable bitmaps2Drawable = MutilBitmapUtils.Bitmaps2Drawable(bitmaps);
                mView.setSeekBarDrawable(bitmaps2Drawable);
            }
        });
    }

    @Override
    public void initSurface(SurfaceTexture surfaceView) {
        mVideoPlayer.initDecoder(new Surface(surfaceView));
        initGetMaxSampleTime();
    }

    private void initGetMaxSampleTime() {
        final long duration = mVideoPlayer.getDuration();
        if (duration > 0)
        {
            final long second = duration / C.SECOND_IN_US;
            mView.getMaxSampleTime(second);
        }
    }

    @Override
    public void pause() {
        mVideoPlayer.pause();
    }

    @Override
    public void resume() {
        mVideoPlayer.resume();
    }

    @Override
    public void addStick(int resId) {
    }

    @Override
    public void start() {
        super.start();
        mVideoPlayer.start();
    }

    @Override
    public void destory() {
        super.destory();
        mVideoPlayer.stop();
    }

    @Override
    public void onPlayerProgress(long sampleTime) {
        final long duration = mVideoPlayer.getDuration();
        final float rate = (float) sampleTime / duration;
        final float rate2 = rate * 100;
        mView.onPlayerProgress(rate2, sampleTime);
    }
}
