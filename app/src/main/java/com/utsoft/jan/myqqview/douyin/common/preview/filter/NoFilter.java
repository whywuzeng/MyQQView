package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES20;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Administrator on 2019/10/7.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class NoFilter extends ImageFilter{

    private static final String FRAGMENT_CODE =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D( uTexture, vTextureCoord );\n" +
                    "}";


    private static final String VERTEX_CODE =
            "attribute vec2 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "uniform mat4 uTexMatrix;\n" +
                    "\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_Position = vec4(aPosition,0.0,1.0);\n" +
                    "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}";

    private int mTextureId;


    @Override
    protected String getFragmentCode() {
        return FRAGMENT_CODE;
    }

    @Override
    protected String getVertexCode() {
        return VERTEX_CODE;
    }

    @Override
    protected void onInit() {
        super.onInit();
    }

    @Override
    protected void initVertexArgument() {
        super.initVertexArgument();
    }

    @Override
    protected void initFragmentArgument() {
        super.initFragmentArgument();
    }

    @Override
    protected void onDraw(int textureId, float[] texMatrix,int width,int height) {

        glUniformMatrix4fv(uTexMatrixLocation,1,false,texMatrix,0);
        checkGlError("glUniformMatrix4fv");
        mRendererInfo.getRectVertex().position(0);
        glVertexAttribPointer(aPositionLocation,2,GL_FLOAT,false,0,mRendererInfo.getRectVertex());
        checkGlError("glVertexAttribPointer");
        mRendererInfo.getTexVertext().position(0);
        glVertexAttribPointer(aTextureCoordLocation,2,GL_FLOAT,false,0,mRendererInfo.getTexVertext());
        checkGlError("glVertexAttribPointer");
        glActiveTexture(GL_TEXTURE0);
        //绑定好 申请textureId 绑定好纹理类型.已经绑定一次了。
        glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLE_STRIP,0,4);
    }
}
