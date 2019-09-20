package com.utsoft.jan.myqqview.douyin.effect.persenter;

import android.graphics.SurfaceTexture;

import com.utsoft.jan.common.factory.presenter.BaseContract;

/**
 * Created by Administrator on 2019/9/16.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.effect.persenter
 */
public class AfterEffectContract {

    public interface Persenter extends BaseContract.Presenter {
        void initSurface(SurfaceTexture surfaceView);

        void pause();

        void resume();
    }

    public interface View extends BaseContract.View<Persenter> {

        void onPlayerProgress(float rate, long maxSampleTime);

        void getMaxSampleTime(long maxSampleTime);
    }
}
