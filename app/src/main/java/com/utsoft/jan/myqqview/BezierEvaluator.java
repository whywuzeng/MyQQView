package com.utsoft.jan.myqqview;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by Administrator on 2019/8/21.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {

    private static final String TAG = "BezierEvaluator";

    private PointF mStartP;
    private PointF mEndP;

    public BezierEvaluator(PointF mStartP, PointF mEndP) {
        this.mStartP = mStartP;
        this.mEndP = mEndP;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        Log.i(TAG, "evaluate: " + fraction);
        PointF pointF = new PointF();
        float time = 1 - fraction;
        pointF.x = (float) (Math.pow(time, 3) * startValue.x + 3 * mStartP.x * fraction * Math.pow(time, 2) + 3 * mEndP.x * Math.pow(fraction, 2) * time + endValue.x * Math.pow(fraction, 3));

        pointF.y = (float) (Math.pow(time, 3) * startValue.y + 3 * mStartP.y * fraction * Math.pow(time, 2) + 3 * mEndP.y * Math.pow(fraction, 2) * time + endValue.y * Math.pow(fraction, 3));
        return pointF;
    }
}
