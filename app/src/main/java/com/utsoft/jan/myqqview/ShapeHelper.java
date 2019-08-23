package com.utsoft.jan.myqqview;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by Administrator on 2019/8/22.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class ShapeHelper {
    private static final String TAG = "ShapeHelper";

    public static int complieVertexShader(String shaderCode) {
        return compilerShader(GL_VERTEX_SHADER, shaderCode);
    }

    private static int compilerShader(int glVertexShader, String shaderCode) {
        final int resId = glCreateShader(glVertexShader);
        if (resId == 0) {
            if (LoggerConfig.ON) {
                Log.i(TAG, "compilerShader: 出错");
            }
            return 0;
        }
        glShaderSource(resId, shaderCode);

        glCompileShader(resId);

        final int[] statusId = new int[1];
        glGetShaderiv(resId, GL_COMPILE_STATUS, statusId, 0);
        if (LoggerConfig.ON) {
            Log.v(TAG, "result" + glGetShaderInfoLog(resId));
        }

        if (statusId[0] == 0) {
            glDeleteShader(resId);
            Log.e(TAG, "compilerShader: compile failed");
            return 0;
        }
        return resId;
    }

    public static int complieFragmentShader(String shaderCode) {
        return compilerShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = glCreateProgram();

        if (programId == 0) {
            if (LoggerConfig.ON) {
                Log.e(TAG, "linkProgram: create not program");
            }

            return 0;
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);

        final int[] statusId = new int[1];

        glGetProgramiv(programId, GL_LINK_STATUS, statusId, 0);
        if (statusId[0] == 0) {
            glDeleteProgram(programId);

            if (LoggerConfig.ON) {
                Log.e(TAG, "linkProgram: link program failed");
            }
            return 0;
        }

        return programId;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] statusId = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, statusId, 0);
        Log.e(TAG, "validateProgram: validate" + statusId[0] + "\nLog" + glGetProgramInfoLog(programObjectId));
        return statusId[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource,
                                   String fragmentShaderSource
                                   ){
        int program;

        final int vertexShader = complieVertexShader(vertexShaderSource);
        final int fragmentShader = complieFragmentShader(fragmentShaderSource);

        program = linkProgram(vertexShader,fragmentShader);

        if (LoggerConfig.ON)
        {
            validateProgram(program);
        }

        return program;
    }
}
