package com.utsoft.jan.myqqview.douyin.effect.persenter;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.utsoft.jan.common.factory.presenter.BasePresenter;
import com.utsoft.jan.myqqview.douyin.common.player.VideoBitmap;
import com.utsoft.jan.myqqview.douyin.common.player.VideoPlayer;

/**
 * Created by Administrator on 2019/9/16.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.effect.persenter
 */
public class AfterEffectPresenter extends BasePresenter<AfterEffectContract.View> implements AfterEffectContract.Persenter {

    private VideoPlayer mVideoPlayer;
    private VideoBitmap videoBitmap;

    public AfterEffectPresenter(AfterEffectContract.View mView,String filePath) {
        super(mView);
        mVideoPlayer = new VideoPlayer(filePath);
        videoBitmap = new VideoBitmap(filePath);
        initBitmapVideo();
    }

    private void initBitmapVideo() {
        videoBitmap.initVideoBitmap();
    }

    @Override
    public void initSurface(SurfaceTexture surfaceView) {
        mVideoPlayer.initDecoder(new Surface(surfaceView));
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
}
