package com.utsoft.jan.myqqview.douyin.common.preview.filter.carmera;

import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.utsoft.jan.common.app.AppProfile;
import com.utsoft.jan.myqqview.R;
import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.BaseRenderImageFilter;


/**
 * draw并不执行父类的draw方法,所以矩阵对它无效
 * Description:
 */
public class ProcessFilter2 extends BaseRenderImageFilter {

    //private final WaterMarkFilter drawer;
    //创建离屏buffer

    private final com.utsoft.jan.myqqview.douyin.common.preview.filter.carmera.WaterMarkFilter drawer ;
    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int[] fTexture = new int[1];

    private int width;
    private int height;
    private final float[] matrix;


    public ProcessFilter2() {
        matrix = MatrixUtils.getOriginalMatrix();
        MatrixUtils.flip(matrix, false, true);//矩阵上下翻转

        drawer = new WaterMarkFilter();
        drawer.setWaterMark(BitmapFactory.decodeResource(AppProfile.getContext().getResources(), R.mipmap.bufuhanzhe));
        drawer.setPosition(0, 70, 0, 0);
    }

    //单纯的方法
    public void onCreated() {
        drawer.create();
    }

    //暴露这个方法给调用 不要调用 create
    @Override
    public void draw() {
        boolean b = GLES20.glIsEnabled(GLES20.GL_CULL_FACE);
        if (b) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
        GLES20.glViewport(0, 0, width, height);
        GLUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);

        drawer.setInputTextureId(mTextureId);
        drawer.draw();
        GLUtils.unBindFrameBuffer();
        if (b) {
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteRenderbuffers(1, fRender, 0);
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }

    @Override
    public int getOutputTextureId() {
        return fTexture[0];
    }

    @Override
    protected String getVertexSource() {
        return null;
    }

    @Override
    protected String getFragmentSource() {
        return null;
    }

    //这个也看作单纯的方法
    public void onChanged(int width, int height) {
        if (this.width != width && this.height != height) {
            this.width = width;
            this.height = height;
            //mFilter.surfaceChangedSize(width, height);
            drawer.surfaceChangedSize(width,height);
            deleteFrameBuffer();
            GLES20.glGenFramebuffers(1, fFrame, 0);
            GLES20.glGenRenderbuffers(1, fRender, 0);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                    width, height);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, fRender[0]);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
            fTexture[0] = GLUtils.createFrameTexture(width, height);
        }
    }
}
