package com.utsoft.jan.myqqview.douyin.common.preview;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview
 */
public class GLUtils {
    public static int createTextureObject(int textureTarget) {
        //textureTarget gltextureView 地址
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        final int textId = textures[0];
        GLES20.glBindTexture(textureTarget, textId);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        return textId;
    }
}
