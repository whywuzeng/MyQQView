package com.utsoft.jan.myqqview.douyin.common.view.record;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.GroupFilter;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.ImageFilter;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.NoFilter;
import com.utsoft.jan.myqqview.douyin.common.recoder.video.VideoFrameData;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glTexImage2D;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.record
 */
public class RecordRender implements GLSurfaceView.Renderer {

    private final NoFilter showFilter;
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

    private final GroupFilter mBeFilter;

    public void setFrameListener(onFrameAvailableListener mFrameListener) {
        this.mFrameListener = mFrameListener;
    }

    private onFrameAvailableListener mFrameListener;

    public RecordRender(RecordSurfaceView mTarget) {
        this.mTarget = mTarget;
        mBeFilter = new GroupFilter();
        showFilter=new NoFilter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureId = GLUtils.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mTarget.onSurfaceCreated(mSurfaceTexture,EGL14.eglGetCurrentContext());

        //加载 ImageFilter 图片过滤
        if (mOldFilter == null)
        {
            mOldFilter = new ImageFilter();
        }
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

   private int[] fFrame = new int[1];
   private int[] fTexture = new int[1];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
        GLES20.glViewport(0,0,width,height);
        mBeFilter.setSize(width,height);
        //初始化 FBO 为了多画
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
        //1.创建FBO
        GLES20.glGenFramebuffers(1,fFrame,0);
        //3.创建FBO纹理
        fTexture[0] = GLUtils.createTexture();

        //5.给FBO分配内存大小
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
    }

    public void setFilter(ImageFilter filter){
        mBeFilter.addFilter(filter);
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

        //if (mOldFilter!=null)
        //{
        //    mOldFilter.release();
        //    mOldFilter = null;
        //}


        mSurfaceTexture.getTransformMatrix(mMatrix);


        if (mOldFilter!=null)
        {
            mOldFilter.init();
            GLUtils.bindFrameTexture(fFrame[0],fTexture[0]);
            mOldFilter.draw(mTextureId,mMatrix,mCanvasWidth,mCanvasHeight);
            GLUtils.unBindFrameBuffer();

            mBeFilter.init();
            mBeFilter.draw(mMatrix,fTexture[0]);
        }

        /**绘制显示的filter*/
        GLES20.glViewport(0,0,mCanvasWidth,mCanvasHeight);
        showFilter.init();
        showFilter.draw(mBeFilter.getOutputTexture(),mMatrix,mCanvasWidth,mCanvasHeight);
    }
}
