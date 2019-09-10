package com.utsoft.jan.myqqview.douyin.common.view.progressbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * Created by Administrator on 2019/9/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.progressbutton
 */
public class ProgressLayout2 extends FrameLayout implements View.OnClickListener {

    private static final int PLAYCOLOR = Color.parseColor("#34B2ED");
    private static final int TRANSPARENT_COLOR = Color.WHITE;


    private ImageView firstPlayImg;
    private Paint paintPlay;
    private Paint paintPlayCircle;
    private int playColor = PLAYCOLOR;
    private int hookColor = TRANSPARENT_COLOR;
    private int pix;
    private Path playPath;
    private Paint paintPlayHook;
    private ImageView secondPlayImg;
    private ImageView playImg;
    private ImageView squareImg;
    private LoadingImage loadingImage;
    private ImageView fullImg;
    private ImageView hookImg;

    @ProgressLayoutConstant.StatusMode
    private int Mode = ProgressLayoutConstant.First_status;
    //旋转secondPlay
    private ObjectAnimator secondRotation;
    private RectF squareRectF;
    private AnimatorSet animatorSet;
    private Path hookPath;
    private AnimatorSet fullAnimatorSet;

    public void setAddStartLoading(AddStartLoading addStartLoading) {
        this.addStartLoading = addStartLoading;
    }

    private AddStartLoading addStartLoading;


    public ProgressLayout2(Context context) {
        super(context);
        init();
    }

    public ProgressLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setOnClickListener(this);
        initImageView();
        setPaint();
        setDisplayWidth();
        setAnimate();
        iconCreate();

        addToLayout();
    }

    //把imageview加入到layout
    private void addToLayout() {
        final LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;

        final Config config = Config.ARGB_8888;
        final Bitmap playBitmap = Bitmap.createBitmap(pix, pix, config);
        final Canvas playcanvas = new Canvas(playBitmap);
        playcanvas.drawPath(playPath, paintPlay);


        final Bitmap secondPlayBitmap = Bitmap.createBitmap(pix, pix, config);
        final Canvas secondPlaycanvas = new Canvas(secondPlayBitmap);

        final Bitmap firstPlayBitmap = Bitmap.createBitmap(pix, pix, config);
        final Canvas firstPlaycanvas = new Canvas(firstPlayBitmap);

        final Bitmap squarePlayBitmap = Bitmap.createBitmap(pix, pix, config);
        final Canvas squarePlaycanvas = new Canvas(squarePlayBitmap);

        final Bitmap fullPlayBitmap = Bitmap.createBitmap(pix, pix, config);
        final Canvas fullPlaycanvas = new Canvas(fullPlayBitmap);

        final Bitmap hookPlayBitmap = Bitmap.createBitmap(pix, pix, config);
        final Canvas hookPlaycanvas = new Canvas(hookPlayBitmap);


        //圆的间隔
        int left = (int) (pix * 0.05f);
        int top = (int) (pix * 0.05f);
        int right = (int) (pix * 0.95f);
        int bottom = (int) (pix * 0.95f);

        //圆的间隔
        final RectF arcDivideRect = new RectF(left, top, right, bottom);

        firstPlaycanvas.drawArc(arcDivideRect, 0, 360, false, paintPlayCircle);

        //画个大圆小一个弧度
        secondPlaycanvas.drawArc(arcDivideRect, -80, 340, false, paintPlayCircle);

        //画方形
        squarePlaycanvas.drawRect(squareRectF, paintPlay);

        //画个实心圆
        fullPlaycanvas.drawArc(arcDivideRect, 0, 360, true, paintPlay);

        //画勾
        hookPlaycanvas.drawPath(hookPath, paintPlayHook);


        firstPlayImg.setImageBitmap(firstPlayBitmap);
        secondPlayImg.setImageBitmap(secondPlayBitmap);
        playImg.setImageBitmap(playBitmap);
        squareImg.setImageBitmap(squarePlayBitmap);
        fullImg.setImageBitmap(fullPlayBitmap);
        hookImg.setImageBitmap(hookPlayBitmap);

        addView(firstPlayImg, lp);
        addView(secondPlayImg, lp);
        addView(playImg, lp);
        addView(squareImg, lp);
        addView(loadingImage, lp);
        addView(fullImg, lp);
        addView(hookImg, lp);
    }

    //创建图标
    private void iconCreate() {
        playPath = new Path();
        //paintPlay
        playPath.moveTo(pix * 0.39f, pix * 0.39f);
        playPath.lineTo(pix * 0.70f, pix * 0.55f);
        playPath.lineTo(pix * 0.39f, pix * 0.70f);
        playPath.lineTo(pix * 0.39f, pix * 0.39f);

        squareRectF = new RectF(pix * 50f / 127f, pix * 50f / 127f, pix * 50f / 127f + pix * 35f / 127f, pix * 50f / 127f + pix * 35f / 127f);

        hookPath = new Path();
        hookPath.moveTo(pix * 42f / 127f, pix * 67f / 127f);
        hookPath.lineTo(pix * 61f / 127f, pix * 85f / 127f);
        hookPath.lineTo(pix * 87f / 127f, pix * 45f / 127f);
    }

    //初始化所有的动画
    private void setAnimate() {
        secondRotation = ObjectAnimator.ofFloat(secondPlayImg, "rotation", 0, 360);
        secondRotation.setDuration(600);
        secondRotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //开始方形 loading状态
                animatorSet.start();

            }
        });

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(playImg, "alpha", 1, 0);
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(playImg, "scaleX", 1, 3);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(playImg, "scaleY", 1, 3);

        animatorSet = new AnimatorSet().setDuration(300);
        animatorSet.playTogether(alpha, scaleX, scaleY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                squareImg.setVisibility(VISIBLE);
                playImg.setVisibility(GONE);
                firstPlayImg.setVisibility(VISIBLE);
                secondPlayImg.setVisibility(GONE);
                loadingImage.setVisibility(VISIBLE);
                playImg.setAlpha(1.0f);
                playImg.setScaleX(1.0f);
                playImg.setScaleY(1.0f);
                Mode = ProgressLayoutConstant.LOADING_STATUS;
                if (addStartLoading != null) {
                    addStartLoading.onStartLoading(loadingImage);
                }
            }
        });


        final ObjectAnimator fullScaleX = ObjectAnimator.ofFloat(fullImg, "scaleX", 0, 1);
        final ObjectAnimator fullScaleY = ObjectAnimator.ofFloat(fullImg, "scaleY", 0, 1);
        fullAnimatorSet = new AnimatorSet().setDuration(500);
        fullAnimatorSet.playTogether(fullScaleX, fullScaleY);
        fullAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hookImg.setVisibility(VISIBLE);
            }
        });

    }

    private void setDisplayWidth() {
        final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

        int sizearea = width * height;

        pix = (int) Math.sqrt(sizearea * 0.0208);
    }

    private void setPaint() {
        paintPlay = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPlay.setColor(playColor);
        //填充
        paintPlay.setStyle(Paint.Style.FILL);

        paintPlayCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPlayCircle.setColor(playColor);
        paintPlayCircle.setStyle(Paint.Style.STROKE);
        paintPlayCircle.setStrokeWidth(5);

        paintPlayHook = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPlayHook.setColor(hookColor);
        paintPlayHook.setStyle(Paint.Style.STROKE);
        paintPlayHook.setStrokeWidth(10);

    }

    private void initImageView() {
        firstPlayImg = new ImageView(getContext());
        firstPlayImg.setClickable(false);

        secondPlayImg = new ImageView(getContext());
        secondPlayImg.setClickable(false);

        playImg = new ImageView(getContext());
        playImg.setClickable(false);

        squareImg = new ImageView(getContext());
        squareImg.setVisibility(GONE);
        squareImg.setClickable(false);

        loadingImage = new LoadingImage(getContext());
        loadingImage.setClickable(false);
        loadingImage.setVisibility(GONE);

        loadingImage.setmAddLoadingFinish(new LoadingImage.addLoadingFinish() {
            @Override
            public void onLoadingFinish() {
                fullImg.setVisibility(VISIBLE);
                loadingImage.reset();
                if (addStartLoading != null) {
                    addStartLoading.onEndLoading();
                }
                fullAnimatorSet.start();
                Mode = ProgressLayoutConstant.LOAD_FINISH;
            }

            @Override
            public void onLoadingPause() {
                loadingImage.setVisibility(GONE);
                squareImg.setVisibility(GONE);
                firstPlayImg.setVisibility(VISIBLE);
                playImg.setVisibility(VISIBLE);
                Mode = ProgressLayoutConstant.First_status;
                if (addStartLoading != null) {
                    addStartLoading.onEndLoading();
                }
            }
        });


        fullImg = new ImageView(getContext());
        fullImg.setClickable(false);
        fullImg.setVisibility(GONE);

        hookImg = new ImageView(getContext());
        hookImg.setClickable(false);
        hookImg.setVisibility(GONE);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (Mode) {
            case ProgressLayoutConstant.First_status:
                clickFirstStatus();
                break;
            case ProgressLayoutConstant.Second_status:
                clickSecondStatus();
                break;
            case ProgressLayoutConstant.LOADING_STATUS:
                clickLoading();
                break;
            case ProgressLayoutConstant.LOAD_FINISH:
                clickFinish();
                break;
            default:
                break;
        }
    }

    private void clickFinish() {
        fullImg.setVisibility(GONE);
        hookImg.setVisibility(GONE);
        playImg.setVisibility(View.VISIBLE);
        firstPlayImg.setVisibility(View.VISIBLE);
        Mode = ProgressLayoutConstant.First_status;
    }

    private void clickLoading() {
        loadingImage.onPause();
        Mode = ProgressLayoutConstant.First_status;
        squareImg.setVisibility(View.GONE);
        loadingImage.setVisibility(GONE);
        playImg.setVisibility(View.VISIBLE);
        firstPlayImg.setVisibility(View.VISIBLE);
    }

    private void clickSecondStatus() {
        Mode = ProgressLayoutConstant.First_status;
        firstPlayImg.setVisibility(View.VISIBLE);
        secondPlayImg.setVisibility(View.GONE);
        playImg.setVisibility(View.VISIBLE);
        squareImg.setVisibility(View.GONE);
        //停止动画
        secondRotation.cancel();
    }

    private void clickFirstStatus() {
        Mode = ProgressLayoutConstant.Second_status;
        firstPlayImg.setVisibility(View.GONE);
        secondPlayImg.setVisibility(View.VISIBLE);
        //开始动画
        secondRotation.start();
    }

    public LoadingImage getLoadingImage() {
        return loadingImage;
    }

    public interface AddStartLoading {

        void onStartLoading(LoadingImage loadingImage);

        void onEndLoading();
    }
}
