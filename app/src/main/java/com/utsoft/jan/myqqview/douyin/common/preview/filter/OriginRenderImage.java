package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Administrator on 2019/10/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class OriginRenderImage extends BaseRenderImageFilter {

    private int mOutputTextureId;
    private int mInputTextureId;
    private int aPositionLocation;
    private int aTextureCoordLocation;
    private int uTexMatrixLocation;
    private int uTextureLocation;

    @Override
    public void setInputTextureId(int textureId) {
        mInputTextureId = textureId;
    }

    @Override
    public int getOutputTextureId() {
        return mOutputTextureId;
    }

    @Override
    protected String getVertexSource() {
        return VERTEX_CODE;
    }

    @Override
    protected String getFragmentSource() {
        return FRAGMENT_CODE;
    }

    @Override
    protected void onCreated() {

    }

    @Override
    protected void onChanged(int width, int height) {
        mOutputTextureId = GLUtils.createFrameTexture(width, height);

        aPositionLocation = glGetAttribLocation(mProgram, "aPosition");
        aTextureCoordLocation = glGetAttribLocation(mProgram, "aTextureCoord");

        uTexMatrixLocation = glGetUniformLocation(mProgram, "uTexMatrix");

        uTextureLocation = glGetUniformLocation(mProgram, "uTexture");
    }

    protected void enableArguments() {
        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aTextureCoordLocation);
    }

    protected void disableArguments() {
        glDisableVertexAttribArray(aPositionLocation);
        glDisableVertexAttribArray(aTextureCoordLocation);
    }

    @Override
    protected void onDraw(float[] transformMatrix) {

        if (mInputTextureId == 0 || mOutputTextureId == 0) {
            return;
        }

        enableArguments();

        glUniformMatrix4fv(uTexMatrixLocation, 1, false, transformMatrix, 0);
        checkGlError("glUniformMatrix4fv");
        mRendererInfo.getRectVertex().position(0);
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, mRendererInfo.getRectVertex());
        checkGlError("glVertexAttribPointer");
        mRendererInfo.getTexVertext().position(0);
        glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, mRendererInfo.getTexVertext());
        checkGlError("glVertexAttribPointer");

        bindTexture(mInputTextureId);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        unBindTexure();

        disableArguments();
    }

    private void unBindTexure() {
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    private void bindTexture(int mInputTextureId) {

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mInputTextureId);
    }


    public void release() {
        if (mProgram < 0) {
            return;
        }

        glDeleteProgram(mProgram);
    }

    private static final String FRAGMENT_CODE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES uTexture;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = texture2D(uTexture,vTextureCoord);\n" +
                    "}\n";

    private static final String VERTEX_CODE =
            "uniform mat4 uTexMatrix;\n" +
                    "attribute vec2 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = vec4(aPosition,0.0,1.0);\n" +
                    "   vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";
}
