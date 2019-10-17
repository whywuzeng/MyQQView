package com.utsoft.jan.myqqview.douyin.common.preview;

import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.utsoft.jan.common.utils.LogUtil;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glValidateProgram;

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
        //n 指定要生成的纹理名称的数量。
        glGenTextures(1, textures, 0);
        final int textId = textures[0];
        //允许您创建或使用命名纹理。
        glBindTexture(textureTarget, textId);

        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER,
                GL_LINEAR);
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER,
                GL_LINEAR);
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S,
                GL_CLAMP_TO_EDGE);
        glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T,
                GL_CLAMP_TO_EDGE);

        return textId;
    }

    public static int createTexture() {
        int[] textureIds = new int[1];
        //创建纹理
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return 0;
        }
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        return textureIds[0];
    }

    public static int buildProgram(String vertexCode, String fragmentCode) {
        int programId;
        final int vertexShaderId = complieShader(GL_VERTEX_SHADER, vertexCode);
        final int fragmentShaderId = complieShader(GL_FRAGMENT_SHADER, fragmentCode);
        if (vertexShaderId == 0 || fragmentShaderId == 0) {
            return 0;
        }
        programId = glCreateProgram();
        if (programId == 0)
        {
            return 0;
        }
        glAttachShader(programId,vertexShaderId);
        glAttachShader(programId,fragmentShaderId);
        glLinkProgram(programId);
        int[] linkStatus = new int[1];
        glGetProgramiv(programId,GL_LINK_STATUS,linkStatus,0);
        if (linkStatus[0] == 0){
            LogUtil.e("Program Log"+glGetProgramInfoLog(programId));
            glDeleteProgram(programId);
            return 0;
        }
        if (!validateProgram(programId))
        {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;
    }

    private static boolean validateProgram(int programId) {
        glValidateProgram(programId);
        int[] valudateResult = new int[1];
        glGetProgramiv(programId,GL_VALIDATE_STATUS,valudateResult,0);
        return valudateResult[0]!= 0;
    }

    private static int complieShader(int type, String shaderCode) {
        final int shaderObjectId = glCreateShader(type);
        checkError();
        if (shaderObjectId == 0) {
            LogUtil.e("『ShaderUtil-compileShader』创建Shader失败");
            return 0;
        }
        //放 shadercode 进入
        glShaderSource(shaderObjectId, shaderCode);
        //生成着色器
        glCompileShader(shaderObjectId);
        int[] completeId = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, completeId, 0);
        if (completeId[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(shaderObjectId));
            LogUtil.e("『ShaderUtil-compileShader』编译Shader失败");
            glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;
    }

    public static void createFBOFrame(int width,int height){
         int[] fFrame = new int[1];
         int[] fTexture = new int[1];

        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
        //1.创建FBO
        GLES20.glGenFramebuffers(1,fFrame,0);
        //3.创建FBO纹理
        fTexture[0] = GLUtils.createTexture();

        //5.给FBO分配内存大小
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 600, 600, 0, GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

    }

    public static void bindFrameTexture(int frameBufferId,int textureId){
        //2.绑定FBO
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        //4。把纹理绑定到FBO
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);

        //6.检查是否绑定成功
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            LogUtil.e("zzz", "glFramebufferTexture2D error");
        }
    }

    public static void unBindFrameBuffer(){
        //7. 解绑纹理和FBO
        GLES20.glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    public static int loadBitmapTexture(Bitmap bitmap) {
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return -1;
        }
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        //根据以上指定的参数，生成一个2D纹理
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureIds[0];
    }

    public static int createFrameBuffer() {
        int[] buffers = new int[1];
        GLES20.glGenFramebuffers(1, buffers, 0);
        checkError();
        return buffers[0];
    }

    private static final String TAG = "GLUtils";

    public static void checkError() {
        if (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "createOutputTexture: " + GLES20.glGetError() );
        }
    }


    public static int createFrameTexture(int width, int height) {
        if (width <= 0 || height <= 0) {
            Log.e(TAG, "createOutputTexture: width or height is 0");
            return -1;
        }
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        if (textures[0] == 0) {
            Log.e(TAG, "createFrameTexture: glGenTextures is 0");
            return -1;
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        checkError();
        //createRenderBuffer(width,height);
        return textures[0];
    }

    public static void createRenderBuffer(int width, int height){
        // 创建RenderBuffer Object并且绑定它
        int[] values = new int[1];
        GLES20.glGenRenderbuffers(1, values, 0);
        int mRenderBuffer = values[0];
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRenderBuffer);

        //为我们的RenderBuffer申请存储空间
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);

        // 将renderBuffer挂载到frameBuffer的depth attachment 上。就上面申请了OffScreenId和FrameBuffer相关联
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRenderBuffer);

        //// 将text2d挂载到frameBuffer的color attachment上
        //GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);

        // 先不使用FrameBuffer，将其切换掉。到开始绘制的时候，在绑定回来
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        /*************************另一种写法******************************************/

        /*************************另一种写法******************************************/
    }
}
