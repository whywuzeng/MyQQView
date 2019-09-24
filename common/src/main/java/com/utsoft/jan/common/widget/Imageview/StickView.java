package com.utsoft.jan.common.widget.Imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.utsoft.jan.common.R;

/**
 * Created by Administrator on 2019/9/24.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.widget.Imageview
 */
public class StickView extends android.support.v7.widget.AppCompatImageView {

    /**
     * 边界画笔
     */
    private Paint mPaint;
    private RectF deleteRectF;
    private RectF scaleRectF;

    private StickOption mOption;
    private Bitmap mBitmap;
    private float[] valueFloat;
    private Bitmap bitmapDelete;
    private Bitmap bitmapRotate;
    private float bitmapDeleteWidth;
    private float bitmapDeleteHeight;
    private float bitmapRotateWidth;
    private float bitmapRotateHeight;

    private static final float scaleIconRate = 0.7f;

    public StickView(Context context) {
        super(context);
        init();
    }

    public StickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setColor(Color.WHITE);

        deleteRectF = new RectF();
        scaleRectF = new RectF();
        mOption = new StickOption();

        valueFloat = new float[9];

        initBitmap();
    }

    private void initBitmap() {
        //把图片加载
        bitmapDelete = BitmapFactory.decodeResource(getResources(), R.mipmap.camera_delete);
        bitmapRotate = BitmapFactory.decodeResource(getResources(), R.mipmap.camera_rotate);
        //得到所有图片的宽度

        bitmapDeleteWidth = bitmapDelete.getWidth() * scaleIconRate;
        bitmapDeleteHeight = bitmapDelete.getHeight() * scaleIconRate;

        bitmapRotateWidth = bitmapRotate.getWidth() * scaleIconRate;
        bitmapRotateHeight = bitmapRotate.getHeight() * scaleIconRate;

    }



    public void setBitmap(Bitmap bitmap) {
        mOption.getMatrix().reset();
        mBitmap = bitmap;
        invalidate();
    }

    //这个view就不能imageResource
    @Override
    public void setImageResource(int resId) {
        //super.setImageResource(resId);
        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    //公式
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final Matrix matrix = mOption.getMatrix();
        matrix.getValues(valueFloat);

        if (mBitmap != null)
            return;

        canvas.save();

        canvas.drawBitmap(mBitmap, mOption.getMatrix(), null);
        // 图片4个顶点的坐标
        float x1 = valueFloat[0] * 0 + valueFloat[1] * 0 + valueFloat[2];
        float y1 = valueFloat[3] * 0 + valueFloat[4] * 0 + valueFloat[5];
        //右上
        float x2 = valueFloat[0] * mBitmap.getWidth() + valueFloat[1] * 0 + valueFloat[2];
        float y2 = valueFloat[3] * mBitmap.getWidth() + valueFloat[4] * 0 + valueFloat[5];
        //左下
        float x3 = valueFloat[0] * 0 + valueFloat[1] * mBitmap.getHeight() + valueFloat[2];
        float y3 = valueFloat[3] * 0 + valueFloat[4] * mBitmap.getHeight() + valueFloat[5];
        //右下
        float x4 = valueFloat[0] * mBitmap.getWidth() + valueFloat[1] * mBitmap.getHeight() + valueFloat[2];
        float y4 = valueFloat[3] * mBitmap.getWidth() + valueFloat[4] * mBitmap.getHeight() + valueFloat[5];

        deleteRectF.left = x2 - bitmapDeleteWidth / 2;
        deleteRectF.right = x2 + bitmapDeleteWidth / 2;
        deleteRectF.top = y2 - bitmapDeleteHeight / 2;
        deleteRectF.bottom = y2 + bitmapDeleteHeight / 2;

        if (mOption.isEdit()) {
            canvas.drawLine(x1, y1, x2, y2, mPaint);
            canvas.drawLine(x2, y2, x3, y3, mPaint);
            canvas.drawLine(x3, y3, x4, y4, mPaint);
            canvas.drawLine(x4, y4, x1, y1, mPaint);
        }

        canvas.drawBitmap(bitmapDelete, null, deleteRectF, null);

        canvas.restore();
    }
}
