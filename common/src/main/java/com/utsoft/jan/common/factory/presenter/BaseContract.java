package com.utsoft.jan.common.factory.presenter;

import android.support.annotation.StringRes;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.factory.presenter
 */
public class BaseContract {

   public interface View<T extends Presenter> {
        void showLoading();

        void showErrorMsg(@StringRes int strId);

        void setPresenter(T presenter);
    }

  public   interface Presenter {
        void start();

        void destory();
    }
}
