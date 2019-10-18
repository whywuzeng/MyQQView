package com.utsoft.jan.myqqview.douyin.common.view.record;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.BaseRenderImageFilter2;
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
    private int mRenderBuffer;

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
    private RecordRenderDrawer mEndRecord;

    private static final String TAG = "EncodeRender";

    public EncodeRender(Context context) {
        originRenderImage = new OriginRenderImage();
        mWaterMarkDrawer = new WaterMarkRenderDrawer(context);
        mRecordDrawer = new RecordRenderDrawer();
        mEndRecord = new RecordRenderDrawer();
        this.mFrameBuffer = 0;
        this.mTextureId = 0;
    }

    //设置最开始 纹理ID
    public void setInputTexture(int texture) {
        this.mTextureId = texture;
    }

    // 挂载mFrameBuffer 容器，
    public void bindFrameBuffer(int textureId) {

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer);
        //为FrameBuffer挂载Texture[1]来存储颜色
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);

        // 将renderBuffer挂载到frameBuffer的depth attachment 上。就上面申请了OffScreenId和FrameBuffer相关联
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRenderBuffer);
    }

    public void unBindFrameBuffer() {
        //解绑FrameBuffer
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

        //结束record
        mEndRecord.create();
    }

    private int createRenderBuffer(int width, int height){
        int[] values = new int[1];
        GLES20.glGenRenderbuffers(1, values, 0);
        int mRenderBuffer = values[0];
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRenderBuffer);

        //为我们的RenderBuffer申请存储空间
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);

        return values[0];
    }

    public void surfaceChangedSize(int width, int height) {
        mFrameBuffer = GLUtils.createFrameBuffer();
        mRenderBuffer = createRenderBuffer(width,height);

        originRenderImage.surfaceChangedSize(width, height);
        mWaterMarkDrawer.surfaceChangedSize(width,height);
        mRecordDrawer.surfaceChangedSize(width,height);
        mEndRecord.surfaceChangedSize(width, height);

        this.originRenderImage.setInputTextureId(mTextureId);
        final int outputTextureId = this.originRenderImage.getOutputTextureId();
        mWaterMarkDrawer.setInputTextureId(outputTextureId);
        mRecordDrawer.setInputTextureId(outputTextureId);
        mEndRecord.setInputTextureId(outputTextureId);
    }

    public void drawRender(BaseRenderImageFilter2 drawer, boolean useFrameBuffer, long timestamp, float[] transformMatrix) {
        if (useFrameBuffer) {
            bindFrameBuffer(drawer.getOutputTextureId());
        }
        if (useFrameBuffer) {
            if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
                    == GLES20.GL_FRAMEBUFFER_COMPLETE) {
                drawer.draw(timestamp, transformMatrix);
            }
        }
        else {
            drawer.draw(timestamp, transformMatrix);
        }
        if (useFrameBuffer) {
            unBindFrameBuffer();
        }
    }

    public void draw(long timestamp, float[] transformMatrix) {
        if (mTextureId == 0 || mFrameBuffer == 0) {
            Log.e(TAG, "draw: mInputTexture or mFramebuffer or list is zero");
            return;
        }
        //drawRender(originRenderImage, true, timestamp, transformMatrix);
        //drawRender(mRecordDrawer, false, timestamp, transformMatrix);
        // 绘制顺序会控制着 水印绘制哪一层
        drawRender(mWaterMarkDrawer, false, timestamp, transformMatrix);
        //drawRender(mEndRecord,false,timestamp,transformMatrix);
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
