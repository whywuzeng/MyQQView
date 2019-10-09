package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.utsoft.jan.myqqview.douyin.common.preview.RendererInfo;
import com.utsoft.jan.myqqview.douyin.common.preview.TextrueProgram;

/**
 * Created by Administrator on 2019/10/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public abstract class BaseRenderImageFilter {

    //数据顶点
    protected RendererInfo mRendererInfo = new RendererInfo();

    //程序点
    protected int mProgram;

    protected int width;
    protected int height;

    //获得顶点数据
    private MultiRenderInfo multiRenderInfo;

    public void create() {

        multiRenderInfo = new MultiRenderInfo();

        //初始化 mpro
        mProgram = new TextrueProgram(getVertexSource(), getFragmentSource()).getmProgramId();
        onCreated();
    }

    public void surfaceChangedSize(int width, int height) {
        this.width = width;
        this.height = height;
        onChanged(width, height);
    }

    public void draw(long timestamp, float[] transformMatrix) {
        clear();
        useProgram();
        viewPort(0, 0, width, height);
        onDraw(transformMatrix);
    }

    protected void viewPort(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

    protected void useProgram() {
        GLES20.glUseProgram(mProgram);
    }

    protected void clear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        GLES20.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
    }

    public abstract void setInputTextureId(int textureId);

    public abstract int getOutputTextureId();

    protected abstract String getVertexSource();

    protected abstract String getFragmentSource();

    protected abstract void onCreated();

    protected abstract void onChanged(int width, int height);

    protected abstract void onDraw(float[] transformMatrix);

    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e("GLError", msg);
        }
    }
}
