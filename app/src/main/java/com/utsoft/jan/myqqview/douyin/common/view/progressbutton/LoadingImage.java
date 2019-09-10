package com.utsoft.jan.myqqview.douyin.common.view.progressbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Administrator on 2019/9/10.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.progressbutton
 */
public class LoadingImage extends View {

    private int pix;
    //有间隔的圆
    private RectF arcDivideRect;
    private float sweepAngle;
    private Paint paint;

    public static final int START_STATUS = 0;

    public static final int PLAYING_STATUS = 3;

    public static final int PAUSE_STATUS = 1;

    public static final int RESET_STATUS = 2;

    private int mStatus;

    private addLoadingFinish mAddLoadingFinish;
    private float mLoadingProgress;

    public LoadingImage(Context context) {
        super(context);
        init();
    }

    public LoadingImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initDisplayMetrics();
        initPoint();
        setPaint();
        reset();
    }

    private void setPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(7);
        paint.setColor(Color.rgb(60, 63, 65));
        paint.setStyle(Paint.Style.STROKE);
    }

    private void initPoint() {
        //圆的间隔
        int left = (int) (pix * 0.05f);
        int top = (int) (pix * 0.05f);
        int right = (int) (pix * 0.95f);
        int bottom = (int) (pix * 0.95f);

        //圆的间隔
        arcDivideRect = new RectF(left, top, right, bottom);
    }

    private void initDisplayMetrics() {
        final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        final int widthPixels = displayMetrics.widthPixels;
        final int heightPixels = displayMetrics.heightPixels;
        final int areaSize = widthPixels * heightPixels;
        pix = (int) Math.sqrt(areaSize * 0.0208);
    }

    public void setLoadingProgress(float loadingProgress) {
        this.mLoadingProgress = loadingProgress;
        sweepAngle += mLoadingProgress;
    }

    public void setProgress(float progress) {
        sweepAngle += progress * 3.6f;
        mStatus = PLAYING_STATUS;
        invalidate();
        if (sweepAngle >= 360) {
            if (this.mAddLoadingFinish != null) {
                this.mAddLoadingFinish.onLoadingFinish();
            }
        }
    }

    public void onPause() {
        mStatus = PAUSE_STATUS;
        if (this.mAddLoadingFinish != null) {
            this.mAddLoadingFinish.onLoadingFinish();
        }
    }

    public void reset() {
        mStatus = START_STATUS;
        sweepAngle = 0;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int finalWidth = 0;
        int finalHeight = 0;

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            finalWidth = width;
            finalHeight = height;
        }
        else if (widthMode == MeasureSpec.AT_MOST) {
            finalWidth = Math.min(pix, width);
            finalHeight = Math.min(pix, height);
        }
        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(arcDivideRect, -80, sweepAngle, false, paint);
    }

    public interface addLoadingFinish {
        void onLoadingFinish();
    }

    public void setmAddLoadingFinish(addLoadingFinish mAddLoadingFinish) {
        this.mAddLoadingFinish = mAddLoadingFinish;
    }
}
