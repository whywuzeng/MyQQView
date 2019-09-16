package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DST_ALPHA;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Administrator on 2019/9/16.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class SoulOutFilter extends ImageFilter {

    //顶点数据
    private static final String VERTEX =
            "uniform mat4 uTexMatrix;\n" +
                    "attribute vec2 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform mat4 uMvpMatrix;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMvpMatrix * vec4(aPosition,0.0,1.0);\n" +
                    "   vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";
    //片元数据
    private static final String FRAGMENT =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES uTexture;\n" +
                    "uniform float uAlpha;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = vec4(texture2D(uTexture,vTextureCoord).rgb,uAlpha);\n" +
                    "}\n";
    private int uMvpMatrixLocation;
    private int uAlphaLocation;
    private int sMaxFrame = 15;
    private int skipFrame = 14;
    private float mProgress = 0;
    private int mFrame = 0;
    private float[] mMvpMatrix = new float[16];

    @Override
    protected String getFragmentCode() {
        return FRAGMENT;
    }

    @Override
    protected String getVertexCode() {
        return VERTEX;
    }

    @Override
    protected void initVertexArgument() {
        super.initVertexArgument();
        uMvpMatrixLocation = glGetUniformLocation(getProgram(), "uMvpMatrix");
    }

    @Override
    protected void initFragmentArgument() {
        super.initFragmentArgument();
        uAlphaLocation = glGetUniformLocation(getProgram(), "uAlpha");
    }

    @Override
    protected void onDraw(int textureId, float[] texMatrix) {
        GLES20.glClear(GL_COLOR_BUFFER_BIT);
        //开启混合模式
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);

        mProgress = (float) mFrame / sMaxFrame;
        if (mProgress > 1f) {
            mProgress = 0f;
        }

        mFrame++;
        if (mFrame > sMaxFrame + skipFrame) {
            mFrame = 0;
        }

        setVertexAttrs();
        setFragmentAttrs();
        Matrix.setIdentityM(mMvpMatrix, 0);
        glUniformMatrix4fv(uMvpMatrixLocation, 1, false, mMvpMatrix, 0);
        float backAlpha = 1f;
        float alpha = 0f;
        if (mProgress > 0f) {
            alpha = 0.2f / (1 - mProgress);
            backAlpha = 1 - alpha;
        }
        glUniform1f(uAlphaLocation, backAlpha);
        glUniformMatrix4fv(uTexMatrixLocation, 1, false, texMatrix, 0);
        checkGlError("glUniformMatrix4fv");
        mRendererInfo.getRectVertex().position(0);
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, mRendererInfo.getRectVertex());
        checkGlError("glVertexAttribPointer");
        mRendererInfo.getTexVertext().position(0);
        glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, mRendererInfo.getTexVertext());
        checkGlError("glVertexAttribPointer");

        glActiveTexture(GL_TEXTURE0);
        //绑定好 申请textureId 绑定好纹理类型.已经绑定一次了。
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        if (mProgress > 0f) {
            glUniform1f(uMvpMatrixLocation, alpha);
            float scale = 1f + 1f * mProgress*0.6f;
            Matrix.scaleM(mMvpMatrix,0,scale,scale,scale);
            glUniformMatrix4fv(uMvpMatrixLocation,1,false,mMvpMatrix,0);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);

        glDisable(GL_BLEND);
    }
}
