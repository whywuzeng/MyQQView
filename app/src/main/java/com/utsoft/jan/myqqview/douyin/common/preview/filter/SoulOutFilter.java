package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.utsoft.jan.common.utils.LogUtil;
import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexImage2D;
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
                    "    gl_Position = uMvpMatrix * vec4(aPosition,0.1,1.0);\n" +
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
    private int skipFrame = 20;
    private float mProgress = 0;
    private int mFrame = 0;
    private float[] mMvpMatrix = new float[16];
    private int fboId;
    //fbo纹理
    private int fboTextureId;

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
    protected void onInit() {
        super.onInit();
        createFBO();

    }

    private void createFBO() {
        //1.创建FBO
        final int[] ints = new int[1];
        glGenFramebuffers(1, ints, 0);
        fboId = ints[0];


        //2.绑定FBO
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        //3.创建FBO纹理
        fboTextureId = GLUtils.createTexture();

        //4。把纹理绑定到FBO
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTextureId, 0);

        //5.给FBO分配内存大小
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1028, 1920, 0, GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        //6.检查是否绑定成功
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            LogUtil.e("zzz", "glFramebufferTexture2D error");
        }

        //7. 解绑纹理和FBO
        GLES20.glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    protected void onDraw(int textureId, float[] texMatrix) {
        GLES20.glClearColor(0,0,0,0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // 关闭剔除去掉背面
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        // 关闭深度测试
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        //开启混合模式
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_DST_ALPHA);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定渲染纹理
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, fboTextureId);

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
            alpha = 0.4f * (1 - mProgress);
            LogUtil.e("--alpha:"+alpha);
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

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        if (mProgress > 0f) {
            glUniform1f(uAlphaLocation, alpha);
            float scale = 1f + 1f * mProgress*0.6f;
            Matrix.scaleM(mMvpMatrix,0,scale,scale,scale);
            glUniformMatrix4fv(uMvpMatrixLocation,1,false,mMvpMatrix,0);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }

        //GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        //GLES20.glUseProgram(0);

/////////////////////////////////////////////正常渲染///////////////////////////////////////////

        //使用程序
        mProgram.get().useProgram();

//todo 这句注释的含义
//        //这下面的是 正常的渲染
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定好 申请textureId 绑定好纹理类型.已经绑定一次了。

        //绑定fbo
        glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        mRendererInfo.getRectVertex().position(0);
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, mRendererInfo.getRectVertex());
        checkGlError("glVertexAttribPointer");
        mRendererInfo.getTexVertext().position(0);
        glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 0, mRendererInfo.getTexVertext());
        checkGlError("glVertexAttribPointer");

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        //解绑纹理
        GLES20.glBindTexture(GL_TEXTURE_2D, 0);
        //解绑fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        glDisable(GLES20.GL_BLEND);
    }


}
