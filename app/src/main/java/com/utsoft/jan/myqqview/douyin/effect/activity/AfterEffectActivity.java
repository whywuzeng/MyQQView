package com.utsoft.jan.myqqview.douyin.effect.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.utsoft.jan.common.app.PresenterActivity;
import com.utsoft.jan.common.utils.ScreenUtil;
import com.utsoft.jan.common.widget.popup.PopPasterView;
import com.utsoft.jan.myqqview.R;
import com.utsoft.jan.myqqview.douyin.common.C;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.SoulOutFilter;
import com.utsoft.jan.myqqview.douyin.common.view.record.OnSurfaceCreatedCallback;
import com.utsoft.jan.myqqview.douyin.common.view.record.RecordSurfaceView;
import com.utsoft.jan.myqqview.douyin.effect.persenter.AfterEffectContract;
import com.utsoft.jan.myqqview.douyin.effect.persenter.AfterEffectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/9/11.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.effect.activity
 */
public class AfterEffectActivity extends PresenterActivity<AfterEffectContract.Persenter> implements AfterEffectContract.View, OnSurfaceCreatedCallback, View.OnClickListener {

    private static final String key_path_final = "key:path";
    @BindView(R.id.tv_add_sticker)
    TextView tvAddSticker;
    @BindView(R.id.tv_add_subtitle)
    TextView tvAddSubtitle;

    private RecordSurfaceView surfaceView;
    private String filePath;
    private ImageButton btnPlay;
    private ImageButton btnBack;
    private ImageButton btnEffect;
    private LinearLayout layEffectPanel;
    private TextView tvCurrentSecond;
    private SeekBar mSeekBar;
    private TextView tvMaxSecond;
    private PopPasterView popPasterView;

    public static void start(Activity from, String fileName) {
        final Intent intent = new Intent(from, AfterEffectActivity.class);
        intent.putExtra(key_path_final, fileName);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_effect);
        ButterKnife.bind(this);
        final Intent intent = getIntent();
        filePath = intent.getExtras().getString(key_path_final);

        surfaceView = findViewById(R.id.sv_record);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnEffect = (ImageButton) findViewById(R.id.btn_effect);
        layEffectPanel = (LinearLayout) findViewById(R.id.lay_effect_panel);
        layEffectPanel.setVisibility(View.GONE);
        tvCurrentSecond = (TextView) findViewById(R.id.tv_current_second);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        tvMaxSecond = (TextView) findViewById(R.id.tv_max_second);

        btnEffect.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        surfaceView.setSurfaceCreatedCallback(this);
        surfaceView.setFilter(new SoulOutFilter());
        initPresenter();

    }

    @Override
    protected void initPresenter() {
        mPresenter = new AfterEffectPresenter(this, filePath);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_effect:
                btnEffectClick();
                break;
            case R.id.btn_play:
                bntPlayClick();
                break;
            case R.id.btn_back:
                backClick();
                break;
            default:
                break;
        }
    }

    private void backClick() {
        surfaceViewAnimBig();
        layEffectPanel.setVisibility(View.GONE);
        btnPlay.setAlpha(0.0f);
        mPresenter.resume();
    }

    private void bntPlayClick() {
        if (btnPlay.getAlpha() > 0) {
            btnPlay.setAlpha(0.0f);
            mPresenter.resume();
        }
        else {
            btnPlay.setAlpha(1.0f);
            mPresenter.pause();
        }
    }

    //特效点击
    private void btnEffectClick() {
        //surface缩小
        surfaceViewAnim();
        //显示布局
        showlayView();

    }

    private void showlayView() {
        layEffectPanel.setVisibility(View.VISIBLE);
        btnPlay.setAlpha(1.0f);
        mPresenter.pause();
    }

    private void surfaceViewAnim() {
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(surfaceView, "scaleX", 1.0f, 0.56f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(surfaceView, "scaleY", 1.0f, 0.56f);

        surfaceView.setPivotX(ScreenUtil.getScreenWidth() / 2);
        surfaceView.setPivotY(0);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private void surfaceViewAnimBig() {
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(surfaceView, "scaleX", 1.0f, 1.44f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(surfaceView, "scaleY", 1.0f, 1.44f);
        surfaceView.setPivotX(ScreenUtil.getScreenWidth() / 2);
        surfaceView.setPivotY(0);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    @Override
    public void onPlayerProgress(float rate, long sampleTime) {
        mSeekBar.setProgress((int) rate);
        final long second = sampleTime / C.SECOND_IN_US;
        setTvCurrentSecond((int) second);
    }

    @Override
    public void getMaxSampleTime(long maxSampleTime) {
        mSeekBar.setMax(100);
        setTvMaxSecond((int) maxSampleTime);
    }

    @Override
    public void setSeekBarDrawable(final Drawable drawable) {
        if (drawable != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSeekBar.setProgressDrawable(drawable);
                }
            });
        }
    }

    public void setTvCurrentSecond(int second) {
        if (second < 10) {
            tvCurrentSecond.setText("00:0" + second);
        }
        else {
            tvCurrentSecond.setText("00:" + second);
        }
    }

    public void setTvMaxSecond(int second) {
        if (second < 10) {
            tvMaxSecond.setText("00:0" + second);
        }
        else {
            tvMaxSecond.setText("00:" + second);
        }
    }

    @OnClick(R.id.tv_add_sticker)
    public void onTvAddStickerClicked() {
        if (popPasterView == null) {
            popPasterView = new PopPasterView(AfterEffectActivity.this);
        }
        popPasterView.show();
    }

    @OnClick(R.id.tv_add_subtitle)
    public void onTvAddSubtitleClicked() {

    }
}
