package com.utsoft.jan.myqqview.douyin.effect.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.utsoft.jan.common.app.PresenterActivity;
import com.utsoft.jan.myqqview.R;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.SoulOutFilter;
import com.utsoft.jan.myqqview.douyin.common.view.record.OnSurfaceCreatedCallback;
import com.utsoft.jan.myqqview.douyin.common.view.record.RecordSurfaceView;
import com.utsoft.jan.myqqview.douyin.effect.persenter.AfterEffectContract;
import com.utsoft.jan.myqqview.douyin.effect.persenter.AfterEffectPresenter;

/**
 * Created by Administrator on 2019/9/11.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.effect.activity
 */
public class AfterEffectActivity extends PresenterActivity<AfterEffectContract.Persenter> implements AfterEffectContract.View,OnSurfaceCreatedCallback {

    private static final String key_path_final = "key:path";

    private RecordSurfaceView surfaceView;
    private String filePath;

    public static void start(Activity from, String fileName) {
        final Intent intent = new Intent(from, AfterEffectActivity.class);
        intent.putExtra(key_path_final, fileName);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_effect);
        final Intent intent = getIntent();
        filePath = intent.getExtras().getString(key_path_final);
        surfaceView = findViewById(R.id.sv_record);
        surfaceView.setSurfaceCreatedCallback(this);
        surfaceView.setFilter(new SoulOutFilter());
        initPresenter();

    }

    @Override
    protected void initPresenter() {
        mPresenter = new AfterEffectPresenter(this,filePath);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void onSurfaceCreated(SurfaceTexture texture, EGLContext context) {
        //需不要UI线程
        mPresenter.initSurface(texture);
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
