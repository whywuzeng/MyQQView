package com.utsoft.jan.common.widget.Imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.utsoft.jan.common.R;
import com.utsoft.jan.common.utils.ScreenUtil;

/**
 * Created by Administrator on 2019/9/24.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.widget.Imageview
 */
public class StickView extends android.support.v7.widget.AppCompatImageView implements StickViewImpl {

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

    private static final float scaleIconRate = 1.4f;
    private float MIN_SCALE;
    private float MAX_SCALE;
    private float bitmapInitWidth;

    private onDeleteView<StickView> mOnDeleteView;

    public void setOnDeleteView(onDeleteView<StickView> mOnDeleteView) {
        this.mOnDeleteView = mOnDeleteView;
    }

    public StickView(Context context, StickOption mOption) {
        super(context);
        init(mOption);
    }

    public StickView(Context context, AttributeSet attrs, StickOption mOption) {
        super(context, attrs);
        init(mOption);
    }

    public StickView(Context context, AttributeSet attrs, int defStyleAttr, StickOption mOption) {
        super(context, attrs, defStyleAttr);
        init(mOption);
    }

    private void init(StickOption mOption) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2.0f);
        mPaint.setColor(Color.WHITE);

        deleteRectF = new RectF();
        scaleRectF = new RectF();

        valueFloat = new float[9];

        this.mOption = mOption;

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

    private void initScaleRate() {
        //当图片的宽比高大时 按照宽计算 缩放大小根据图片的大小而改变 最小为图片的1/8 最大为屏幕宽
        final float minWidth = mOption.getWidth() / 8.0f;
        final float minHeight = mOption.getHeight() / 8.0f;
        if (mBitmap.getWidth() > mBitmap.getHeight()) {
            if (mBitmap.getWidth() < minWidth) {
                MIN_SCALE = 1.0f;
            }
            else {
                MIN_SCALE = 1.0f * minWidth / mBitmap.getWidth();
            }
            MAX_SCALE = 1.0f * ScreenUtil.getScreenWidth() / mBitmap.getWidth();
        }
        else {
            if (mBitmap.getHeight() < minHeight) {
                MIN_SCALE = 1.0f;
            }
            else {
                MIN_SCALE = 1.0f * minHeight / mBitmap.getHeight();
            }
            MAX_SCALE = 1.0f * mOption.getHeight() / mBitmap.getHeight();
        }

        bitmapInitWidth = (float) Math.hypot(mBitmap.getWidth(), mBitmap.getHeight()) / 2;
    }


    public void setBitmap(Bitmap bitmap) {
        mOption.getMatrix().reset();
        mBitmap = bitmap;
        final int dx = (mOption.getWidth() - bitmap.getWidth()) / 2;
        final int dy = (mOption.getHeight() - bitmap.getHeight()) / 2;
        initScaleRate();
        final float scaleRate = (MIN_SCALE + MAX_SCALE) / 2;
        mOption.getMatrix().postTranslate(dx, dy);
        mOption.getMatrix().postScale(scaleRate, scaleRate, dx + mBitmap.getWidth() / 2, dy + mBitmap.getHeight() / 2);
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

        if (mBitmap == null)
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

        scaleRectF.left = x4 - bitmapRotateWidth / 2;
        scaleRectF.right = x4 + bitmapRotateWidth / 2;
        scaleRectF.top = y4 - bitmapRotateHeight / 2;
        scaleRectF.bottom = y4 + bitmapRotateHeight / 2;

        if (mOption.isEdit()) {
            canvas.drawLine(x1, y1, x2, y2, mPaint);
            canvas.drawLine(x2, y2, x4, y4, mPaint);
            canvas.drawLine(x4, y4, x3, y3, mPaint);
            canvas.drawLine(x3, y3, x1, y1, mPaint);
            canvas.drawBitmap(bitmapDelete, null, deleteRectF, null);
            canvas.drawBitmap(bitmapRotate, null, scaleRectF, null);
        }

        canvas.restore();
    }

    float mLastY = 0;
    float mLastX = 0;

    boolean isResize = false;
    boolean inSide = false;
    boolean inDelete = false;
    private float lastDegress = 0;
    private PointF mid = new PointF();

    private float lastmidDp = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        boolean handle = true;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //3 种清况
                if (isInBitmap(event)) {
                    inSide = true;
                    isResize = false;
                    inDelete = false;
                    mOption.setEdit(true);
                    mLastX = event.getX();
                    mLastY = event.getY();
                }
                else if (isInResize(event)) {
                    isResize = true;
                    inSide = false;
                    inDelete = false;
                    lastDegress = startRotation(event);
                    midStartPoint(event);
                    lastmidDp = scaleMidPoint(event);
                }
                else if (isInDelete(event)) {
                    inDelete = true;
                    isResize = false;
                    inSide = false;
                    if (mOnDeleteView != null) {
                        mOnDeleteView.deleteClick(StickView.this);
                    }

                }else {
                    handle = true;
                    mOption.setEdit(false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (inSide) {
                    final float x = event.getX();
                    final float y = event.getY();
                    final float minX = Math.min(x, mOption.getWidth());
                    final float minY = Math.min(y, mOption.getHeight());

                    mOption.getMatrix().postTranslate(minX - mLastX, minY - mLastY);
                    invalidate();
                    mLastX = minX;
                    mLastY = minY;
                }
                else if (isResize) {
                    //这里还没有旋转，还是以前的样子  所以旋转的中心应该为 down记录mid
                    mOption.getMatrix().postRotate((startRotation(event) - lastDegress)*2, mid.x, mid.y);
                    lastDegress = startRotation(event);

                    final float newScalePoint = scaleMidPoint(event);

                     float scale = newScalePoint / lastmidDp;

                    if (newScalePoint / bitmapInitWidth <= MIN_SCALE && scale < 1 || newScalePoint / bitmapInitWidth >= MAX_SCALE && scale > 1) {
                        scale = 1;
                        if (!isInResize(event))
                        {
                            //要是不设置 这个会怎么样 旋转会飘走？
                            isResize = false;
                        }
                    }
                    else {
                        lastmidDp = scaleMidPoint(event);
                    }

                    //todo 需要更新mid 点么?
                    //midStartPoint(event);
                    mOption.getMatrix().postScale(scale,scale,mid.x,mid.y);
                    mOption.setScale(scale);
                    invalidate();
                }


                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                inSide = false;
                isResize = false;
                inDelete = false;
                invalidate();
                break;

        }
        return handle;
        //return super.onTouchEvent(event);
    }

    private boolean isInDelete(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float v1 = deleteRectF.left - bitmapDeleteWidth / 2;
        final float v2 = deleteRectF.right + bitmapDeleteWidth / 2;

        final float v3 = deleteRectF.top - bitmapDeleteHeight / 2;
        final float v4 = deleteRectF.bottom + bitmapDeleteHeight / 2;

        if (v1 < x && x < v2 && v3 < y && y < v4)
            return true;

        return false;
    }

    //得到 event 到 mid 距离
    private float scaleMidPoint(MotionEvent event) {
        return (float) Math.hypot(event.getX() - mid.x, event.getY() - mid.y);
    }

    //获取中间点坐标
    private void midStartPoint(MotionEvent event) {
        mOption.getMatrix().getValues(valueFloat);
        float x1 = valueFloat[0] * 0 + valueFloat[1] * 0 + valueFloat[2];
        float y1 = valueFloat[3] * 0 + valueFloat[4] * 0 + valueFloat[5];

        final float f3 = x1 + event.getX();
        final float f4 = y1 + event.getY();
        mid.set(f3 / 2, f4 / 2);
    }

    //求这个点的 反正切角度    根据左上角
    private float startRotation(MotionEvent event) {
        mOption.getMatrix().getValues(valueFloat);
        float x1 = valueFloat[0] * 0 + valueFloat[1] * 0 + valueFloat[2];
        float y1 = valueFloat[3] * 0 + valueFloat[4] * 0 + valueFloat[5];

        final double v = Math.atan2(event.getY() - y1,event.getX() - x1);
        return (float) Math.toDegrees(v);
    }

    private boolean isInResize(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float v1 = scaleRectF.left - bitmapRotateWidth / 2;
        final float v2 = scaleRectF.right + bitmapRotateWidth / 2;

        final float v3 = scaleRectF.top - bitmapRotateHeight / 2;
        final float v4 = scaleRectF.bottom + bitmapRotateHeight / 2;

        if (v1 < x && x < v2 && v3 < y && y < v4)
            return true;

        return false;
    }

    /**
     * 点击 在bitmap rect里
     *
     * @param event
     * @return
     */
    private boolean isInBitmap(MotionEvent event) {

        mOption.getMatrix().getValues(valueFloat);
        // 图片4个顶点的坐标
        //左上
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

        final float[] arrayXfloats = new float[4];
        final float[] arrayYfloats = new float[4];

        arrayXfloats[0] = x1;
        arrayXfloats[1] = x2;
        arrayXfloats[2] = x4;
        arrayXfloats[3] = x3;

        arrayYfloats[0] = y1;
        arrayYfloats[1] = y2;
        arrayYfloats[2] = y4;
        arrayYfloats[3] = y3;

        return pointInRect(arrayXfloats, arrayYfloats, event.getY(), event.getX());
    }

    private boolean pointInRect(float[] arrayXfloats, float[] arrayYfloats, float y, float x) {
        //四条边 长度
        final double topV = Math.hypot(arrayXfloats[1] - arrayXfloats[0], arrayYfloats[1] - arrayYfloats[0]);
        final double rightV = Math.hypot(arrayXfloats[2] - arrayXfloats[1], arrayYfloats[2] - arrayYfloats[1]);
        final double bottomV = Math.hypot(arrayXfloats[3] - arrayXfloats[2], arrayYfloats[3] - arrayYfloats[2]);
        final double leftV = Math.hypot(arrayXfloats[0] - arrayXfloats[3], arrayYfloats[0] - arrayYfloats[3]);

        //到左上点距离线
        final double leftLine = Math.hypot(x - arrayXfloats[0], y - arrayYfloats[0]);
        //右上点
        final double rightLine = Math.hypot(x - arrayXfloats[1], y - arrayYfloats[1]);
        //右下角
        final double rightBottomLine = Math.hypot(x - arrayXfloats[2], y - arrayYfloats[2]);
        //左下角
        final double leftBottomLine = Math.hypot(x - arrayXfloats[3], y - arrayYfloats[3]);

        //海伦公式
        final double p1 = (leftLine + rightLine + topV) / 2;
        final double s1 = Math.sqrt(p1 * (p1 - leftLine) * (p1 - rightLine) * (p1 - topV));

        final double p2 = (leftLine + leftBottomLine + leftV) / 2;
        final double s2 = Math.sqrt(p2 * (p2 - leftLine) * (p2 - leftBottomLine) * (p2 - leftV));

        final double p3 = (leftBottomLine + rightBottomLine + bottomV) / 2;
        final double s3 = Math.sqrt(p3 * (p3 - leftBottomLine) * (p3 - rightBottomLine) * (p3 - bottomV));

        final double p4 = (rightBottomLine + rightLine + rightV) / 2;
        final double s4 = Math.sqrt(p4 * (p4 - rightBottomLine) * (p4 - rightLine) * (p4 - rightV));

        final double rectS = topV * rightV;

        return Math.abs(rectS - (s1 + s2 + s3 + s4)) < 0.5;
    }

    @Override
    public void setEditable() {
        mOption.setEdit(true);
        invalidate();
    }

    @Override
    public void setNoEditable() {
        mOption.setEdit(false);
        invalidate();
    }


}
