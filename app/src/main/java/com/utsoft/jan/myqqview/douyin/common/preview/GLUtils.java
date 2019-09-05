package com.utsoft.jan.myqqview.douyin.common.preview;

import android.opengl.GLES11Ext;

import com.utsoft.jan.common.utils.LogUtil;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
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
            LogUtil.d(glGetProgramInfoLog(programId));
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
            LogUtil.e("『ShaderUtil-compileShader』编译Shader失败");
            glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;
    }
}
