package com.utsoft.jan.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.utsoft.jan.common.factory.presenter.BaseContract;
import com.utsoft.jan.common.utils.LogUtil;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.app
 */
public abstract class PresenterActivity<Presenter extends BaseContract.Presenter> extends AppCompatActivity implements BaseContract.View<Presenter>{

    protected Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null)
            mPresenter.destory();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showErrorMsg(int strId) {
        LogUtil.e("showErrorMsg");
    }
}
