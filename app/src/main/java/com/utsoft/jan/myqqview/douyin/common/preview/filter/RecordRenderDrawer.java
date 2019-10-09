package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created By Chengjunsen on 2018/9/21
 */
public class RecordRenderDrawer extends BaseRenderImageFilter {
    // 绘制的纹理 ID
    private int mTextureId;

    private int av_Position;
    private int af_Position;
    private int s_Texture;

    private static final String TAG = "RecordRenderDrawer";

    public RecordRenderDrawer() {
        this.mTextureId = 0;
    }

    @Override
    public void setInputTextureId(int textureId) {
        this.mTextureId = textureId;
        Log.d(TAG, "setInputTextureId: " + textureId);
    }

    @Override
    public int getOutputTextureId() {
        return mTextureId;
    }

    @Override
    public void create() {

    }

    @Override
    public void surfaceChangedSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(long timestamp, float[] transformMatrix) {
        drawFrame(timestamp,transformMatrix);
    }


    private void drawFrame(long timeStamp, float[] transformMatrix) {
        onDraw(transformMatrix);
    }

    private void updateChangedSize(int width, int height) {
        onChanged(width, height);
    }


    @Override
    protected void onCreated() {
        av_Position = GLES20.glGetAttribLocation(mProgram, "av_Position");
        af_Position = GLES20.glGetAttribLocation(mProgram, "af_Position");
        s_Texture = GLES20.glGetUniformLocation(mProgram, "s_Texture");
        Log.d(TAG, "onCreated: av_Position " + av_Position);
        Log.d(TAG, "onCreated: af_Position " + af_Position);
        Log.d(TAG, "onCreated: s_Texture " + s_Texture);
        Log.e(TAG, "onCreated: error " + GLES20.glGetError());
    }

    @Override
    protected void onChanged(int width, int height) {

    }

    @Override
    protected void onDraw(float[] texMatrix) {
        clear();
        useProgram();
        viewPort(0, 0, width, height);

        GLES20.glEnableVertexAttribArray(av_Position);
        GLES20.glEnableVertexAttribArray(af_Position);
//        GLES20.glVertexAttribPointer(av_Position, CoordsPerVertexCount, GLES20.GL_FLOAT, false, VertexStride, mVertexBuffer);
//        GLES20.glVertexAttribPointer(af_Position, CoordsPerTextureCount, GLES20.GL_FLOAT, false, TextureStride, mDisplayTextureBuffer);
        mRendererInfo.getRectVertex().position(0);
        GLES20.glVertexAttribPointer(av_Position, 2, GLES20.GL_FLOAT, false, 0, mRendererInfo.getRectVertex());
        checkGlError("avPosition");

        mRendererInfo.getTexVertext().position(0);
        GLES20.glVertexAttribPointer(af_Position, 2, GLES20.GL_FLOAT, false, 0, mRendererInfo.getTexVertext());
        checkGlError("afPosition");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(s_Texture, 0);
        // 绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(av_Position);
        GLES20.glDisableVertexAttribArray(af_Position);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    protected String getVertexSource() {
        final String source = "attribute vec4 av_Position; " +
                "attribute vec2 af_Position; " +
                "varying vec2 v_texPo; " +
                "void main() { " +
                "    v_texPo = af_Position; " +
                "    gl_Position = av_Position; " +
                "}";
        return source;
    }

    @Override
    protected String getFragmentSource() {
        final String source = "precision mediump float;\n" +
                "varying vec2 v_texPo;\n" +
                "uniform sampler2D s_Texture;\n" +
                "void main() {\n" +
                "   vec4 tc = texture2D(s_Texture, v_texPo);\n" +
                "   gl_FragColor = texture2D(s_Texture, v_texPo);\n" +
                "}";
        return source;
    }
}
