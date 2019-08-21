package com.utsoft.jan.myqqview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by Administrator on 2019/8/21.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class ApproveLayout extends FrameLayout {

    private static final String TAG = "ApproveLayout";

   private Interpolator lin =  new LinearInterpolator(); //线性


    private int mWidth;
    private int mHeight;
    private LayoutParams lp;
    private Drawable[] mDrawables;
    private Random random = new Random();
    private Interpolator acce;
    private Interpolator dece;
    private Interpolator accelerateInterpolator;
    private Interpolator[] mInterpolators;


    public ApproveLayout(Context context) {
        super(context);
        init();
    }

    public ApproveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ApproveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private void init() {

        acce = new AccelerateDecelerateInterpolator();//加速减速
        dece = new DecelerateInterpolator();//减速
        accelerateInterpolator = new AccelerateInterpolator();//加速

        Drawable blue = getResources().getDrawable(R.mipmap.pl_blue);
        Drawable red = getResources().getDrawable(R.mipmap.pl_red);
        Drawable yellow = getResources().getDrawable(R.mipmap.pl_yellow);
        mDrawables = new Drawable[3];
        mDrawables[0] = blue;
        mDrawables[1] = red;
        mDrawables[2] = yellow;

        int dWidth = blue.getIntrinsicWidth();
        int dHeight = blue.getIntrinsicHeight();

        lp = new LayoutParams(dWidth, dHeight);
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp.leftMargin = 20;

        mInterpolators = new Interpolator[4];
        mInterpolators[0] = lin;
        mInterpolators[1]= acce;
        mInterpolators[2] = dece;
        mInterpolators[3] = accelerateInterpolator;

    }

    private AnimatorSet getEnterAnimator(View target) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alpha, scaleX, scaleY);
        animatorSet.setDuration(1000);
        return animatorSet;
    }

    private ValueAnimator getBezierAnimator(final View target) {
        BezierEvaluator bezierEvaluator = new BezierEvaluator(getPointF(1), getPointF(2));

        ValueAnimator valueAnimator = ValueAnimator.ofObject(bezierEvaluator, new PointF(target.getX(), target.getY()), new PointF(mWidth - random.nextInt(mWidth), 0));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF value = (PointF) animation.getAnimatedValue();
                target.setX(value.x);
                target.setY(value.y);

                target.setAlpha(1 - animation.getAnimatedFraction());
            }
        });
        valueAnimator.setDuration(2000);
        return valueAnimator;
    }

    private void dispatcherAnimator(final View target) {
        target.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AnimatorSet enterAnimator = getEnterAnimator(target);
                ValueAnimator bezierAnimator = getBezierAnimator(target);

                AnimatorSet set = new AnimatorSet();
                set.playSequentially(enterAnimator, bezierAnimator);
                set.setInterpolator(mInterpolators[random.nextInt(4)]);
                set.start();
                set.addListener(new EndAnimatorListener(target));
                target.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private PointF getPointF(int scale) {
        PointF pointF = new PointF();
        pointF.x = random.nextInt(mWidth - 100);
        pointF.y = random.nextInt((mHeight - 100) / scale);
        return pointF;
    }

    public void addHeart() {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(mDrawables[random.nextInt(3)]);
        imageView.setLayoutParams(lp);
        imageView.setClickable(false);
        imageView.setFocusable(false);
        addView(imageView);

        dispatcherAnimator(imageView);
    }

    class EndAnimatorListener implements Animator.AnimatorListener{

        private View mTarget;

        EndAnimatorListener(View mTarget) {
            this.mTarget = mTarget;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (this.mTarget!=null)
            removeView(this.mTarget);

            int childCount = ApproveLayout.this.getChildCount();
            Log.i(TAG, "onAnimationEnd: childCount "+childCount);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
