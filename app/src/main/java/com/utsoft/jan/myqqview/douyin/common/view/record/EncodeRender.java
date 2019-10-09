package com.utsoft.jan.myqqview.douyin.common.view.record;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.BaseRenderImageFilter;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.OriginRenderImage;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.RecordRenderDrawer;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.WaterMarkRenderDrawer;

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
public class EncodeRender implements GLSurfaceView.Renderer {

    private int mTextureId;

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    private SurfaceTexture mSurfaceTexture;
    private int mCanvasWidth;
    private int mCanvasHeight;

    private int mFrameBuffer;

    private OriginRenderImage originRenderImage;
    private WaterMarkRenderDrawer mWaterMarkDrawer;
    private RecordRenderDrawer mRecordDrawer;

    private static final String TAG = "EncodeRender";

    public EncodeRender(Context context) {
        originRenderImage = new OriginRenderImage();
        mWaterMarkDrawer = new WaterMarkRenderDrawer(context);
        mRecordDrawer = new RecordRenderDrawer();
        this.mFrameBuffer = 0;
        this.mTextureId = 0;
    }

    //设置最开始 纹理ID
    public void setInputTexture(int texture) {
        this.mTextureId = texture;
    }

    public void bindFrameBuffer(int textureId) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
    }

    public void unBindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void deleteFrameBuffer() {
        GLES30.glDeleteFramebuffers(1, new int[]{mFrameBuffer}, 0);
        GLES30.glDeleteTextures(1, new int[]{mTextureId}, 0);
    }

    public void create(){
        originRenderImage.create();
        mWaterMarkDrawer.create();
        mRecordDrawer.create();
    }

    public void surfaceChangedSize(int width, int height) {
        mFrameBuffer = GLUtils.createFrameBuffer();
        originRenderImage.surfaceChangedSize(width, height);
        mWaterMarkDrawer.surfaceChangedSize(width,height);
        mRecordDrawer.surfaceChangedSize(width,height);

        this.originRenderImage.setInputTextureId(mTextureId);
        final int outputTextureId = this.originRenderImage.getOutputTextureId();
        mWaterMarkDrawer.setInputTextureId(outputTextureId);
        mRecordDrawer.setInputTextureId(outputTextureId);
    }

    public void drawRender(BaseRenderImageFilter drawer, boolean useFrameBuffer, long timestamp, float[] transformMatrix) {
        if (useFrameBuffer) {
            bindFrameBuffer(drawer.getOutputTextureId());
        }
        drawer.draw(timestamp, transformMatrix);
        if (useFrameBuffer) {
            unBindFrameBuffer();
        }
    }

    public void draw(long timestamp, float[] transformMatrix) {
        if (mTextureId == 0 || mFrameBuffer == 0) {
            Log.e(TAG, "draw: mInputTexture or mFramebuffer or list is zero");
            return;
        }
        drawRender(originRenderImage, true, timestamp, transformMatrix);
        // 绘制顺序会控制着 水印绘制哪一层
        drawRender(mWaterMarkDrawer, true, timestamp, transformMatrix);
        drawRender(mRecordDrawer, false, timestamp, transformMatrix);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //OES展示视频
        mTextureId = GLUtils.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        setInputTexture(mTextureId);
        create();
        mSurfaceTexture = new SurfaceTexture(mTextureId);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
        surfaceChangedSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {


        float matrix[] = new float[16];
        if (mSurfaceTexture!=null)
        {
            //从图像流中将纹理图像更新为最近的帧。 就可以消耗这些图像内存
            //可以通过 SurfaceTexture 来创建一个 Surface 然后通过 Surface 向 SurfaceTexture里的 BQ 中提供图像内存
            mSurfaceTexture.updateTexImage();
        }

        mSurfaceTexture.getTransformMatrix(matrix);

        draw(0,matrix);

    }
}
