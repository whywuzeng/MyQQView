package com.utsoft.jan.myqqview.douyin.common.preview;

import android.opengl.GLES20;

/**
 * Created by Administrator on 2019/9/4.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview
 */
public class TextrueProgram {
    public int getmProgramId() {
        return mProgramId;
    }

    private int mProgramId;

    public TextrueProgram(String vertexCode, String fragmentCode) {
        mProgramId = GLUtils.buildProgram(vertexCode, fragmentCode);
    }

    public void useProgram() {
        GLES20.glUseProgram(mProgramId);
    }


}
