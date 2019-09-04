package com.utsoft.jan.myqqview.douyin.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2019/9/4.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view
 */
public class RecordButton extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;

    private static final int BACKGROUND_COLOR = Color.parseColor("#fe2c55");

    private int mBackgroundColor = BACKGROUND_COLOR;

    private OnRecordListener mOnRecordListener;

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackground(null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBackgroundColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
                getMeasuredWidth() / 2, mPaint);
        super.onDraw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mOnRecordListener ==null)
        {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startAnimation();
                mOnRecordListener.OnRecordStart();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopAnimation();
                mOnRecordListener.OnRecordStop();
                break;
        }
        return true;
    }

    private void stopAnimation() {

    }

    private void startAnimation() {

    }

    public void setOnRecordListener(OnRecordListener mOnRecordListener) {
        this.mOnRecordListener = mOnRecordListener;
    }

    public interface OnRecordListener{
        void OnRecordStart();
        void OnRecordStop();
    }
}
