package com.utsoft.jan.myqqview.douyin.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.utsoft.jan.myqqview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/9/8.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view
 */
public class ProgressView extends View {

    //黑色的背景
    public static final int BACKGROUDCOLOR = Color.parseColor("#22000000");
    //红色 淡红色
    public static final int CENTERCOLOR = Color.parseColor("#face15");
    //间隔颜色
    public static final int DIVIDERCOLOR = Color.WHITE;

    public static final int RADIUS = 4;

    public static final int DIVIDER_WIDTH = 2;

    private Paint mPaint;

    private float mRadius;

    private float dividerWidth;
    private float total;
    private List<Integer> progressList = new ArrayList<>();
    //old value
    private float loadingProgress;

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        try {
            mRadius = ta.getDimensionPixelSize(R.styleable.ProgressView_pv_radius, RADIUS);
            dividerWidth = ta.getDimensionPixelSize(R.styleable.ProgressView_pv_divider_width, DIVIDER_WIDTH);
        } finally {
            ta.recycle();
        }
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //        super.onDraw(canvas);
        drawBackgroudColor(canvas);
        drawCenterColor(canvas);
        drawDivider(canvas);
    }

    //画个白色divider
    private void drawDivider(Canvas canvas) {

        mPaint.setColor(DIVIDERCOLOR);
        for (int progress : progressList) {
            float rightWidth = progress * getMeasuredWidth();
            canvas.drawRect(new RectF(rightWidth - dividerWidth, 0, rightWidth, getMeasuredHeight()), mPaint);
        }
    }

    private void drawCenterColor(Canvas canvas) {
        mPaint.setColor(CENTERCOLOR);
        //分百之多少.
        total = 0;
        //先加上以前的progress
        for (int progress : progressList) {
            total += progress;
        }
        //然后 加上 实时变化的loadingProgresss
        total += loadingProgress;
        float rightWidth = getMeasuredWidth() * total;

        canvas.drawRoundRect(new RectF(0, 0, rightWidth, getMeasuredHeight()), mRadius, mRadius, mPaint);

        if (mRadius > rightWidth)
            return;

        canvas.drawRect(new RectF(mRadius, 0, rightWidth, getMeasuredHeight()), mPaint);

    }

    private void drawBackgroudColor(Canvas canvas) {
        mPaint.setColor(BACKGROUDCOLOR);
        canvas.drawRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), mRadius, mRadius, mPaint);
    }

    //这一阶段已经结束
    public void addProgress(int progress){
        //这个值要重置为零
        loadingProgress = 0;
        progressList.add( progress);
        invalidate();
    }

    public void setLoadingProgress(float loadingProgress){
        this.loadingProgress = loadingProgress;
        invalidate();
    }

    public void deleteProgress(int progress){
        //这个值要重置为零
        loadingProgress = 0;
        progressList.remove( progressList.size()-1);
        invalidate();
    }

    public void clear(){
        progressList.clear();
        invalidate();
    }
}
