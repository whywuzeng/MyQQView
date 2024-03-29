package com.utsoft.jan.myqqview.douyin.common.view.record;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;

import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.ImageFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2019/10/8.
 * <p>
 * by author wz
 * 编码的render 最终生成视频，多个过滤
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.record
 */
public class EncodeRender2 implements GLSurfaceView.Renderer {

    private int mTextureId;

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    private SurfaceTexture mSurfaceTexture;
    private int mCanvasWidth;
    private int mCanvasHeight;

    private int mFrameBuffer;

    private ImageFilter imageFilter;

    private static final String TAG = "EncodeRender";

    public EncodeRender2() {
        this.mFrameBuffer = 0;
        this.mTextureId = 0;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //OES展示视频
        mTextureId = GLUtils.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        if (imageFilter == null) {
            imageFilter = new ImageFilter();
        } else {
            imageFilter.release();
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        if (imageFilter == null) {
            return;
        }

        float matrix[] = new float[16];
        if (mSurfaceTexture!=null)
        {
            //从图像流中将纹理图像更新为最近的帧。 就可以消耗这些图像内存
            //可以通过 SurfaceTexture 来创建一个 Surface 然后通过 Surface 向 SurfaceTexture里的 BQ 中提供图像内存
            mSurfaceTexture.updateTexImage();
        }

        mSurfaceTexture.getTransformMatrix(matrix);

        imageFilter.init();

        imageFilter.draw(mTextureId, matrix, mCanvasWidth, mCanvasHeight);

    }
}
