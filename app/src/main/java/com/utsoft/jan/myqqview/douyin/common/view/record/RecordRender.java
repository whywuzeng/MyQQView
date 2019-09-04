package com.utsoft.jan.myqqview.douyin.common.view.record;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.ImageFilter;
import com.utsoft.jan.myqqview.douyin.common.recoder.video.VideoFrameData;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.record
 */
public class RecordRender implements GLSurfaceView.Renderer {

    private RecordSurfaceView mTarget;
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private float[] mMatrix = new float[16];
    private int mCanvasWidth;
    private int mCanvasHeight;
    private ImageFilter imageFilter;
    private ImageFilter mOldFilter;

    public void setFrameListener(onFrameAvailableListener mFrameListener) {
        this.mFrameListener = mFrameListener;
    }

    private onFrameAvailableListener mFrameListener;

    public RecordRender(RecordSurfaceView mTarget) {
        this.mTarget = mTarget;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureId = GLUtils.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mTarget.onSurfaceCreated(mSurfaceTexture,EGL14.eglGetCurrentContext());
        //加载 ImageFilter 图片过滤
        if (imageFilter == null) {
            imageFilter = new ImageFilter();
        }
        else {
            imageFilter.release();
        }
    }

    public void setPreviewSize(int width,int height){
        mPreviewHeight = height;
        mPreviewWidth = width;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
        GLES20.glViewport(0,0,width,height);
    }

    public void setFilter(ImageFilter filter){
        mOldFilter = imageFilter;
        imageFilter = filter;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (imageFilter == null) {
            return;
        }

        float matrix[] = new float[16];
        if (mSurfaceTexture!=null)
        {
            mSurfaceTexture.updateTexImage();
        }

        mSurfaceTexture.getTransformMatrix(matrix);

        if (mFrameListener != null) {
            mFrameListener.onFrameAvailable(new VideoFrameData(imageFilter,
                    matrix, mSurfaceTexture.getTimestamp(), mTextureId));
        }

        imageFilter.init();

        if (mOldFilter!=null)
        {
            mOldFilter.release();
            mOldFilter = null;
        }

        mSurfaceTexture.getTransformMatrix(mMatrix);

        imageFilter.draw(mTextureId,mMatrix,mCanvasWidth,mCanvasHeight);
    }
}
