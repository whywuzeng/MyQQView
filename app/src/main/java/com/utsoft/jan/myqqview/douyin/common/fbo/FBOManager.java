package com.utsoft.jan.myqqview.douyin.common.fbo;

import android.graphics.Bitmap;

import com.utsoft.jan.common.utils.LogUtil;

import static android.opengl.GLES10.GL_UNSIGNED_BYTE;
import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glTexImage2D;

/**
 * Created by Administrator on 2019/9/16.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.fbo
 */
public class FBOManager {

    //位置
    private int fboId;
    private int fboTextureId;
    private Bitmap bitmap;

    public void initFBO() {
        //1.创建FBO
        final int[] ints = new int[1];
        glGenFramebuffers(1, ints, 0);
        fboId = ints[0];

        //2.绑定FBO
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        //3.创建FBO纹理
        fboTextureId = createTexture();

        //4。把纹理绑定到FBO
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTextureId, 0);

        //5.给FBO分配内存大小
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

        //6.检查是否绑定成功
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            LogUtil.e("zzz", "glFramebufferTexture2D error");
        }
        Unbunding();
    }

    private void Unbunding() {
        //7. 解绑纹理和FBO
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private int createTexture() {

        return 0;
    }



}
