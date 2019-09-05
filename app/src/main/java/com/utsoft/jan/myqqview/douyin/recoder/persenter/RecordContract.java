package com.utsoft.jan.myqqview.douyin.recoder.persenter;

import android.opengl.EGLContext;

import com.utsoft.jan.common.factory.presenter.BaseContract;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.recoder.persenter
 */
public class RecordContract {

    public interface View extends BaseContract.View<Presenter> {

    }

    public interface Presenter extends BaseContract.Presenter {
        void startRecording(EGLContext mEGLContext, int width, int height);

        void stopRecording();
    }
}
