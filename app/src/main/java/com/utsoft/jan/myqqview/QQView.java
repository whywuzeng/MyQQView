package com.utsoft.jan.myqqview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

/**
 * Created by Administrator on 2019/8/19.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class QQView extends View {
    private static final String TAG = "QQView";

    private RectF mArcRect;
    private Paint mArcPaint;
    private Paint mBarPaint;

    private static final float mRate = 530.f / 625.f;
    private int mWith;
    private int mHeight;
    private int mBelowBackgroundColor;
    //角度
    private int belowCorner;
    private Paint p;
    private Paint mTextPaint;
    private int mArcCenterX;
    private int mArcCenterY;
    private String countStep = "5623";
    private Paint mLinePaint;

    private int[] mSteps = {3251, 5685, 5795, 7666, 2000, 6500, 5777};
    private Path mBelPath;
    private Paint bgPaint;
    private Path mUpPath;
    private String step = "25";
    private float percent;
    private AnimatorSet animatorSet;
    private Path mWavePath;
    private Paint mWavePaint;
    private float mOffsetWith;
    private int mOffsetHeight;
    private AnimatorSet animatorSetTwo;
    private ValueAnimator mLightWaveAnimator;
    private long mLightWaveAnimTime = 4000L;
    private SweepGradient mSweepGradient;

    public QQView(Context context) {
        super(context);
        init();
    }

    public QQView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QQView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mAllPointCount;
    private int waveWidth;
    private int waveNum;
    private int mHalfPointCount;
    private int mWaveHeight;
    //所有的波浪点
    private Point[] points;

    private void initWave() {
        //中心点
        // 高度 274
        int centerY = (int) (mArcCenterY + 274.f / 506.f * mHeight);
        int centerX = 0;

        mAllPointCount=9+8+8;
        waveWidth = (int) (260.f/ 435.f * mWith);
        waveNum= 0;
        mWaveHeight = (int) (18.f/506.f*mHeight);

        points = getPoints(centerY, centerX);

    }

    private Point[] getPoints(int centerY, int centerX) {
        Point centerPoint = new Point(centerX, centerY);
        mHalfPointCount = mAllPointCount / 2;
        Point[] points = new Point[mAllPointCount];
        waveNum = (mHalfPointCount)/4;
        points[mHalfPointCount] = centerPoint;

        for (int i = mHalfPointCount + 1; i < mAllPointCount; i += 4) {
            int width = points[mHalfPointCount].x + waveWidth * (i / 4 - waveNum);
            points[i] = new Point(waveWidth / 4 + width, centerPoint.y + mWaveHeight);
            points[i + 1] = new Point(waveWidth / 2 + width, centerPoint.y);
            points[i + 2] = new Point(waveWidth * 3 / 4 + width, centerPoint.y - mWaveHeight);
            points[i + 3] = new Point(width + waveWidth, centerPoint.y);
        }

        //屏幕外
        for (int i = 0; i < mHalfPointCount; i++) {
            //y 周的點
            //points[mHalfPointCount+i].x
            //points[i] = new Point( -(i/4*waveWidth+waveWidth/(1+i%4)),points[mHalfPointCount+i].y);
            int index = mHalfPointCount - i - 1;
            if (index % 2 != 0) {
                if (points[mHalfPointCount + i + 1].y > centerPoint.y) {
                    points[index] = new Point(-points[mHalfPointCount + i + 1].x, points[mHalfPointCount + i + 1].y - 2 * mWaveHeight);
                }
                else if (points[mHalfPointCount + i + 1].y < centerPoint.y) {
                    points[index] = new Point(-points[mHalfPointCount + i + 1].x, points[mHalfPointCount + i + 1].y + 2 * mWaveHeight);
                }
            }
            else {
                points[index] = new Point(-points[mHalfPointCount + i + 1].x, points[mHalfPointCount + i + 1].y);
            }
        }

        for (int i = 0; i < points.length; i++) {
            Log.i(TAG, "getPoints: "+points[i].toString());
        }

        return points;
    }

    private void drawWave(Canvas canvas,float offsetWidth,int offsetHeight){
        mWavePath.reset();
        //mBelPath.reset();
        mWavePath.moveTo(points[0].x+offsetWidth,points[0].y+offsetHeight);
        for (int i = 1;i<mAllPointCount;i+=2)
        {
            mWavePath.quadTo(points[i].x+offsetWidth,points[i].y+offsetHeight,
                    points[i+1].x+offsetWidth,points[i+1].y+offsetHeight);
        }

        mWavePath.lineTo(points[mAllPointCount-1].x+offsetWidth,points[mAllPointCount-1].y+offsetHeight);

        mWavePath.lineTo(mWith , mHeight- 18.f / 435.f * mWith);
        mWavePath.quadTo(mWith, mHeight, mWith - (int) 18.f / 435.f * mWith, mHeight );

        mWavePath.lineTo((int) 18.f / 435.f * mWith, mHeight);
        mWavePath.quadTo(0, mHeight, 0, mHeight-(int) 18.f / 435.f * mWith);
        //mWavePath.lineTo(mWith,mHeight);
        //mWavePath.lineTo(0,mHeight);
        //mWavePath.lineTo(0,mHeight);
        mWavePath.close();
        //mWavePath.op(mBelPath,Path.Op.INTERSECT);
        canvas.drawPath(mWavePath,mWavePaint);
    }



    private void init() {
        mBelowBackgroundColor = Color.argb(200, 47, 194, 255);
        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        //38 184 240
        mArcPaint.setColor(Color.rgb(38, 184, 240));

        mTextPaint = new Paint();

        mBarPaint = new Paint();
        mBarPaint.setColor(Color.rgb(38, 184, 240));
        mBarPaint.setAntiAlias(true);
        mBarPaint.setStrokeCap(Paint.Cap.ROUND);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.parseColor("#c1c1c1"));
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{8, 4}, 0));

        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(mBelowBackgroundColor);

        p = new Paint();//这个是画矩形的画笔，方便大家理解这个圆弧
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);

        mWavePath = new Path();
        mWavePaint = new Paint();
        mWavePaint.setColor(mBelowBackgroundColor);
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setStrokeWidth(1);

        mBelPath = new Path();

        animatorSet = new AnimatorSet();

        ValueAnimator stepValueAnimator = ValueAnimator.ofInt(0, mSteps[mSteps.length - 1]);
        stepValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                step = String.valueOf(animation.getAnimatedValue());
                invalidate();
            }
        });

        ValueAnimator percentAnimator = ValueAnimator.ofFloat(0, 1);
        percentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animatorSet.setDuration(1000);
        animatorSet.playTogether(stepValueAnimator, percentAnimator);
        animatorSet.start();


        animatorSetTwo = new AnimatorSet();
        ValueAnimator waveOffsetValueAnimator = ValueAnimator.ofInt(5, 6,10,30);
        waveOffsetValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetWith = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator waveHeightValueAnimator = ValueAnimator.ofInt(-120, 120);
        waveHeightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //mOffsetHeight = (int) animation.getAnimatedValue();
                //invalidate();
            }
        });
        //waveOffsetValueAnimator.setDuration(4000);
        //waveOffsetValueAnimator.setRepeatCount(999);
        //waveOffsetValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        //waveOffsetValueAnimator.start();

        //animatorSetTwo.setDuration(1000000);
        //animatorSetTwo.playTogether(waveOffsetValueAnimator, waveHeightValueAnimator);
        //animatorSetTwo.start();


    }

    private void updateArcPaint() {
        // 设置渐变
        int[] mGradientColors = {Color.parseColor("#c1c1c1"), Color.rgb(38, 184, 240), Color.BLUE};
        mSweepGradient = new SweepGradient(mArcCenterX, mArcCenterY, mGradientColors, null);
        mArcPaint.setShader(mSweepGradient);
    }

    private void startLightWaveAnimator() {
        if (mLightWaveAnimator != null && mLightWaveAnimator.isRunning()) {
            return;
        }
        mLightWaveAnimator = ValueAnimator.ofFloat(0,  waveWidth);
        mLightWaveAnimator.setDuration(mLightWaveAnimTime);
        mLightWaveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mLightWaveAnimator.setInterpolator(new LinearInterpolator());
        mLightWaveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetWith = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mLightWaveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mOffsetWith = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mLightWaveAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultWith = Integer.MAX_VALUE;
        int with;
        int height;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST) {
            with = size;
        }
        else {
            with = defaultWith;
        }
        height = (int) (with / mRate);
        setMeasuredDimension(with, height);
        Log.e(TAG, "onMeasure: with:" + with + "height:" + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆弧的中心

        mWith = w;
        mHeight = h;
        mArcCenterX = w / 2; //265
        mArcCenterY = (int) (162.f / 506.f * mHeight);

        mArcRect = new RectF();

        //mArcRect.left = mArcCenterX - 125.f/450.f*mWith;  //120
        //mArcRect.top = mArcCenterY - 125.f/525.f*mHeight; //412
        //mArcRect.right = mArcCenterX + 125.f/450.f*mWith; //44
        //mArcRect.bottom = mArcCenterY + 125.f/525.f*mHeight; //335

        mArcRect.left = mArcCenterX - 125.f / 435.f * mWith;  //120
        mArcRect.top = mArcCenterY - 125.f / 506.f * mHeight; //412
        mArcRect.right = mArcCenterX + 125.f / 435.f * mWith; //44
        mArcRect.bottom = mArcCenterY + 125.f / 506.f * mHeight; //335

        //mArcRect.left = (int) (250.f/510.f*mArcCenterX);  //120
        //mArcRect.right = (int) (776.f/510.f*mArcCenterX); //412
        //mArcRect.top = (int) (84.f/362.f*mArcCenterY); //44
        //mArcRect.bottom = (int) (637.f/362.f*mArcCenterY); //335

        int mArcWith = (int) (17.f / 532.f * mWith);

        mArcPaint.setStrokeWidth(mArcWith);
        mBarPaint.setStrokeWidth(mArcWith);
        updateArcPaint();
        initWave();
        startLightWaveAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制下面元素
        drawBelowBackground(0, 0, mWith, mHeight, mBelowBackgroundColor, canvas, belowCorner);
        DrawUpBg(canvas);

        canvas.drawArc(mArcRect, 120, 300 * percent, false, mArcPaint);
        //canvas.drawRect(mArcRect, p);

        int sposX = (int) (mArcCenterX);
        int sposY = (int) (mArcCenterY - 33.f / 506.f * mHeight);
        String text = "截至22.50已走";
        mTextPaint.setColor(Color.parseColor("#c1c1c1"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(16.f / 506.f * mHeight);
        //绘制圆弧里的文字
        canvas.drawText(text, sposX, sposY, mTextPaint);

        int sposXbig = (int) (mArcCenterX);
        int sposYbig = (int) (mArcCenterY + 17.f / 506.f * mHeight);
        String text2 = "6294";
        mTextPaint.setColor(Color.rgb(38, 184, 240));
        mTextPaint.setTextSize(40.f / 506.f * mHeight);
        canvas.drawText(step, sposXbig, sposYbig, mTextPaint);

        sposXbig = (int) (mArcCenterX);
        sposYbig = (int) (mArcCenterY + 50.f / 506.f * mHeight);
        text2 = "好友平均3369步";
        mTextPaint.setColor(Color.parseColor("#c1c1c1"));
        mTextPaint.setTextSize(16.f / 506.f * mHeight);
        canvas.drawText(text2, sposXbig, sposYbig, mTextPaint);

        sposXbig = (int) (mArcCenterX - 36.f / 435.f * mWith);
        sposYbig = (int) (mArcCenterY + 117.f / 506.f * mHeight);
        text2 = "第";
        mTextPaint.setColor(Color.parseColor("#c1c1c1"));
        mTextPaint.setTextSize(14.f / 506.f * mHeight);
        canvas.drawText(text2, sposXbig, sposYbig, mTextPaint);

        sposXbig = (int) (mArcCenterX + 36.f / 435.f * mWith);
        sposYbig = (int) (mArcCenterY + 117.f / 506.f * mHeight);
        text2 = "名";
        canvas.drawText(text2, sposXbig, sposYbig, mTextPaint);

        mTextPaint.setColor(Color.rgb(38, 184, 240));
        mTextPaint.setTextSize(24.f / 506.f * mHeight);
        canvas.drawText("10", mArcCenterX, sposYbig, mTextPaint);

        sposXbig = (int) (mArcCenterX - 174.f / 435.f * mWith);
        sposYbig = (int) (mArcCenterY + 163.f / 506.f * mHeight);
        text2 = "最近7天";
        mTextPaint.setColor(Color.parseColor("#c1c1c1"));
        mTextPaint.setTextSize(14.f / 506.f * mHeight);
        canvas.drawText(text2, sposXbig, sposYbig, mTextPaint);

        sposXbig = (int) (mArcCenterX + 174.f / 435.f * mWith);
        sposYbig = (int) (mArcCenterY + 163.f / 506.f * mHeight);
        text2 = "平均" + countStep + "步";
        mTextPaint.setColor(Color.parseColor("#c1c1c1"));
        mTextPaint.setTextSize(14.f / 506.f * mHeight);
        canvas.drawText(text2, sposXbig, sposYbig, mTextPaint);

        int startX = (int) (mArcCenterX - 201.f / 435.f * mWith);
        int startY = (int) (mArcCenterY + 182.f / 506.f * mHeight);
        int stopX = (int) (startX + 383.f / 435.f * mWith);
        int stopY = startY;
        canvas.drawLine(startX, startY, stopX, stopY, mLinePaint);

        for (int i = 0; i < mSteps.length; i++) {
            //高度193  宽度172
            int startX1 = (int) ((mArcCenterX - 172.f / 435.f * mWith) + i * (58.f / 435.f * mWith));
            int stopX1 = startX1;
            int stopY1 = (int) (mArcCenterY + (228.f / 506.f * mHeight));

            float numh = mSteps[i] / Float.valueOf(countStep) * 40.f; //平均值
            if (numh < 40.f) {
                mBarPaint.setColor(Color.parseColor("#c1c1c1"));
            }
            else {
                mBarPaint.setColor(Color.rgb(38, 184, 240));
            }
            int stepHeight = (int) (numh / 506.f * mHeight);
            int startY1 = (int) (stopY1 - stepHeight);

            canvas.drawLine(startX1, startY1, stopX1, stopY1, mBarPaint);
            canvas.drawText(i + "天", stopX1 + 1 / 435.f * mWith, stopY1 + 20.f / 506.f * mHeight, mTextPaint);
        }

        drawWave(canvas,mOffsetWith,mOffsetHeight);
        //drawWave(canvas,mOffsetWith+4,mOffsetHeight+6);

    }

    private void drawBelowBackground(int left, int i, int mWith, int mHeight, int mBelowBackgroundColor, Canvas canvas, int belowCorner) {
        bgPaint.setColor(mBelowBackgroundColor);
        float heightY = mHeight - 73.f / 506.f * mHeight;
        mBelPath.moveTo(left, heightY);
        mBelPath.lineTo(left, heightY + 63.f / 506.f * mHeight);
        RectF rectF = new RectF();
        rectF.left = 0;
        rectF.right = 2 * 18.f / 435.f * mWith;
        rectF.top = mHeight - 2 * 18.f / 435.f * mWith;
        rectF.bottom = mHeight;
        //canvas.drawRect(rectF,bgPaint);
        //mBelPath.arcTo(rectF,90,90,true);
        mBelPath.quadTo(0, mHeight, (int) 18.f / 435.f * mWith, mHeight);
        mBelPath.lineTo(mWith - 18.f / 435.f * mWith, mHeight);

        rectF.left = mWith - 2 * 18.f / 435.f * mWith;
        rectF.right = mWith;
        rectF.top = mHeight - 2 * 18.f / 435.f * mWith;
        rectF.bottom = mHeight;
        //canvas.drawRect(rectF,bgPaint);
        //mBelPath.arcTo(rectF,0,90,true);
        mBelPath.quadTo(mWith, mHeight, mWith, mHeight - (int) 18.f / 435.f * mWith);

        mBelPath.lineTo(mWith, heightY);
        //mBelPath.lineTo(left,heightY);
        //mBelPath.close();

        //canvas.drawPath(mBelPath, bgPaint);
    }

    private void DrawUpBg(Canvas canvas) {

        bgPaint.setColor(Color.WHITE);
        bgPaint.setShadowLayer(15,10,10,Color.GRAY);

        mUpPath = new Path();
        float heightY = mHeight - 73.f / 506.f * mHeight;
        mUpPath.moveTo(0, heightY);

        mUpPath.lineTo(0, 18.f / 506.f * mHeight);
        mUpPath.quadTo(0, 0, (int) 18.f / 435.f * mWith, 0);
        mUpPath.lineTo(mWith - 18.f / 435.f * mWith, 0);

        mUpPath.quadTo(mWith, 0, mWith, (int) 18.f / 435.f * mWith);

        mUpPath.lineTo(mWith, heightY);
        //mBelPath.lineTo(left,heightY);
        mUpPath.close();

        canvas.drawPath(mUpPath, bgPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        RectF rectF = new RectF();
        rectF.top = mWith;
        rectF.left = 380.f / 450.f * mWith;
        rectF.right = mWith;
        rectF.bottom = mHeight;
        if (rectF.contains(event.getX(), event.getY())) {//当前点击的坐标在右下角的范围内
            //在这里可以做点击事件的监听
            Toast.makeText(this.getContext(), "Click", Toast.LENGTH_SHORT).show();
            if (!animatorSet.isStarted()) {
                animatorSet.resume();
                animatorSet.start();
            }
            return false;
        }
        else {
            return super.onTouchEvent(event);
        }
    }
}
