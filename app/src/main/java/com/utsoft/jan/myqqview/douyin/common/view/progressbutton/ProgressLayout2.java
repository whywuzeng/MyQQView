package com.utsoft.jan.myqqview.douyin.common.view.progressbutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Administrator on 2019/9/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.progressbutton
 */
public class ProgressLayout2 extends FrameLayout {

    private static final int PLAYCOLOR = Color.parseColor("#34B2ED");

    private ImageView firstPlayImg;
    private Paint paintPlay;
    private int playColor = PLAYCOLOR;
    private int pix;
    private Path playPath;

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

        final Config config = Config.ARGB_8888;
        final Bitmap playBitmap = Bitmap.createBitmap(pix, pix, config);
        final Canvas playcanvas = new Canvas(playBitmap);
        playcanvas.drawPath(playPath, paintPlay);

        firstPlayImg.setImageBitmap(playBitmap);

        addView(firstPlayImg, lp);

    }

    //创建图标
    private void iconCreate() {
        playPath = new Path();
        //paintPlay
        playPath.lineTo(pix * 0.39f, pix * 0.39f);
        playPath.moveTo(pix * 0.70f, pix * 0.55f);
        playPath.moveTo(pix * 0.39f, pix * 0.70f);
        playPath.moveTo(pix * 0.39f, pix * 0.39f);

    }

    //初始化所有的动画
    private void setAnimate() {

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

    }

    private void initImageView() {
        firstPlayImg = new ImageView(getContext());
        firstPlayImg.setClickable(false);
    }
}
