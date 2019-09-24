package com.utsoft.jan.myqqview.douyin.effect.persenter;

import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;

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

        void addStick(int resId);
    }

    public interface View extends BaseContract.View<Persenter> {

        void onPlayerProgress(float rate, long maxSampleTime);

        void getMaxSampleTime(long maxSampleTime);

        void setSeekBarDrawable(Drawable drawable);
    }
}
