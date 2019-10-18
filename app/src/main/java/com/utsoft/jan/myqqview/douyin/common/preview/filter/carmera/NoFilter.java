package com.utsoft.jan.myqqview.douyin.common.preview.filter.carmera;

import android.opengl.GLES20;

import com.utsoft.jan.myqqview.douyin.common.preview.filter.BaseRenderImageFilter;

/**
 * Created by Administrator on 2019/10/18.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter.carmera
 */
public class NoFilter extends BaseRenderImageFilter {

    @Override
    public int getOutputTextureId() {
        return mTextureId;
    }

    @Override
    protected String getVertexSource() {

        return "attribute vec4 vPosition;\n" +
                "attribute vec2 vCoord;\n" +
                "uniform mat4 vMatrix;\n" +
                "\n" +
                "varying vec2 textureCoordinate;\n" +
                "\n" +
                "void main(){\n" +
                "    gl_Position = vMatrix*vPosition;\n" +
                "    textureCoordinate = vCoord;\n" +
                "}";
    }

    @Override
    protected String getFragmentSource() {

        return "precision mediump float;\n" +
                "varying vec2 textureCoordinate;\n" +
                "uniform sampler2D vTexture;\n" +
                "void main() {\n" +
                "    gl_FragColor = texture2D( vTexture, textureCoordinate );\n" +
                "}";
    }

    @Override
    protected void onChanged(int width, int height) {

    }

    //默认为黑色
    @Override
    protected void clear() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
